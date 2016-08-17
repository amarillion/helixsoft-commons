package nl.helixsoft.recordstream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Transforms a {@link RecordStream} into another RecordStream by applying an aggregate functions to records that group together.
 * <p>
 * Usage examples:
 * <ul>
 * <li>Calculate the sum, maximum, or average for each group
 * <li>Flatten a gene annotation map (for example GO annotation). If you start with a gene + GO table, which each row containing only one GO identifier, and a single gene occurring in multiple rows, you can transform this 
 * into a table where each gene is in only one row, and the GO annotations are concatenated with a delimiter.
 * </ul>
 * <p>
 * A Reducer has the following parameters:
 * <ul>
 * <li>A parent RecordStream, which will be the input before transformation
 * <li>A grouping column. Two consecutive records are grouped together if they have the same value in the grouping column
 * <li>An accumulator, which is a map of (column, {@link GroupFunc}) pairs. The GroupFunc is applied to each value of that column in the group, and accumulates the result. Grouping functions can do things like: calculate the min, max, sum or average. 
 * Concatenate values with a separator. Or just return the first item.    
 * </ul>
 * 
 * <p>
 * I started writing this utility class to work around limitations of the SPARQL GROUP BY implementation of Virtuoso 6.
 * (AVG and SUM cast double to int, CONCAT just takes the first value or complains about maximum row length in temp table, etc...)
 */
public class Reducer extends AbstractRecordStream
{
	private final RecordStream parent;
	private final Map<String, GroupFunc> accumulator;
	private Object prevValue = null;
	private Record row;
	private int idxGroupVar;
	private final RecordMetaData rmd;
	
	/**
	 * 
	 * @param parent The recordstream that we want to reduce
	 * @param groupVar
	 * @param accumulator
	 * @throws StreamException
	 */
	public Reducer (RecordStream parent, String groupVar, Map<String, GroupFunc> accumulator) throws StreamException
	{
		this.parent = parent;
		this.accumulator = accumulator; //TODO: should really make a defensive copy
		
		List<String> outHeaders = new ArrayList<String>();
		outHeaders.add (groupVar);
		for (String header : accumulator.keySet())
		{
			outHeaders.add(header);
		}
		idxGroupVar = parent.getMetaData().getColumnIndex(groupVar);
		
		row = parent.getNext();
		prevValue = row.get(idxGroupVar);
		// Reset the accumulator at start
		resetAccumulator();
		rmd = new DefaultRecordMetaData(outHeaders);
	}
	
	private Record writeAccumulator() 
	{
		Object[] vals = new Object[rmd.getNumCols()];
		vals[0] = prevValue;
		for (int i = 1; i < rmd.getNumCols(); ++i)
		{
			String colName = rmd.getColumnName(i);
			vals[i] = accumulator.get(colName).getResult();
		}
		return new DefaultRecord(rmd, vals);
	}

	private void resetAccumulator() 
	{
		for (String h : accumulator.keySet())
		{
			accumulator.get(h).clear();
		}
	}

	private void accumulate()
	{
		// accumulate a row
		for (String h : accumulator.keySet())
		{
			accumulator.get(h).accumulate(row);
		}
	}
	
	@Override
	public Record getNext() throws StreamException 
	{
		if (row == null) return null; // nothing more to return
		
		while (true)
		{
			accumulate();

			row = parent.getNext();

			// are we at the end?
			if (row == null)
			{
				return writeAccumulator();
			}
			
			// has groupVar changed?
			if (!(row.get(idxGroupVar).toString().equals(prevValue.toString())))
			{
				Record result = writeAccumulator();
				// start with a fresh accumulator
				prevValue = row.get(idxGroupVar);
				resetAccumulator();
				return result;
			}

		} 		
	}

	public static class GenericGroupFunc<T, U> extends AbstractGroupFunc
	{
		final BiFunction<T, U, T> function;
		
		GenericGroupFunc (String colName, T initial, BiFunction<T, U, T> function)
		{
			super(colName);
			this.function = function;
			this.initial = initial;
		}
		
		private T chain;
		private final T initial;
		
		public void accumulate (Record val) 
		{ 
			int i = getIdx(val); 
			U more = (U)val.get(i); // can't check at compile time but will throw ClassCastException at runtime if value is not the expected type.
			chain = function.apply (chain, more); 
		}
		
		public T getResult() { 
			return chain;
		}
		
		public void clear() { chain = initial; }

	}
	
	/** Count the items in the group */
	public static class Count implements GroupFunc
	{
		private int count = 0;
		public void accumulate (Record val) { count++; }
		public Object getResult() { return count; }
		public void clear() { count = 0; }
	}

	/** Calculate the log(average) of floating point values. */
	public static class LogAverageFloat extends AbstractGroupFunc
	{
		public LogAverageFloat(String col) { super(col); }
		private int count = 0;
		private float sum = 0;
		public void accumulate (Record val) { int i = getIdx(val); count++; sum += (Float)val.get(i); }
		public Object getResult() { 
			double result = Math.log (sum / count); 
			return result;
		}
		public void clear() { count = 0; sum = 0; }
	}

	//TODO: replace with "Reduce". // requires splitting Accumulator...
	/** Function to a apply to a group of values from a RecordStream. {@link #accumulate} is invoked with each element of the group in turn. The class should hold a running tally of the elements seen. After the last
	 * element, {@link #getResult} is called followed by {@link #clear} to prepare for the next group. Implementations are used in {@link Reducer}, there are several canned implementations available. */
	public interface GroupFunc
	{
		/** Accumulate another value */
		public void accumulate(Record val);
		/** Get the result so far */
		public Object getResult();
		/** Reset the grouping function to the starting state, to prepare it for the next group. */
		public void clear();
	}

	/** Calculate the average of a group of Float values */
	public static class AverageFloat extends AbstractGroupFunc
	{
		public AverageFloat(String col) { super(col); }
		private int count = 0;
		private float sum = 0;
		public void accumulate (Record val) { int i = getIdx(val); count++; sum += (Float)val.get(i); }
		public Object getResult() { return sum / count; }
		public void clear() { count = 0; sum = 0; }
	}

	/** Calculate the sum of a group of Float values */
	public static class SumFloat extends AbstractGroupFunc
	{
		public SumFloat(String col) { super(col); }
		private float sum = 0;
		public void accumulate (Record val) { int i = getIdx(val); sum += (Float)val.get(i); }
		public Object getResult() { return sum; }
		public void clear() { sum = 0; }
	}

	/** Concatenate a group of String values with a separator between them. */
	public static class Concatenate extends AbstractGroupFunc
	{
		private final String sep;
		public Concatenate(String colName, String sep) { super(colName); this.sep = sep; }
		private boolean first = true;
		private StringBuilder builder = new StringBuilder();
		public void accumulate (Record val) { int i = getIdx(val); if (first) first = false; else builder.append(sep); builder.append(val.get(i).toString()); }
		public Object getResult() { return builder.toString(); }			
		public void clear() { first = true; builder = new StringBuilder(); }
	}
	
	/** put the group of objects in a {@link List} */
	public static class AsList extends AbstractGroupFunc
	{
		public AsList(String col) { super(col); }
		private List<Object> list;
		public void accumulate (Record val) { int i = getIdx(val); list.add (val.get(i)); }
		public Object getResult() { return list; }
		public void clear() { list = new ArrayList<Object>(); }
	}

	/** put the group of objects in a {@link Set} */
	public static class AsSet extends AbstractGroupFunc
	{
		private Set<Object> set;		
		public AsSet(String col) { super(col); }
		public void accumulate (Record val) { int i = getIdx(val); set.add (val.get(i)); }
		public Object getResult() { return set; }
		public void clear() { set = new HashSet<Object>(); }
	}

	public static abstract class AbstractGroupFunc implements GroupFunc
	{
		protected int idx = -1;
		private final String colName;
		public AbstractGroupFunc (String col) { this.colName = col; }
		
		protected int getIdx (Record val)
		{
			if (idx < 0)
			{
				// lazy initialization of idx
				RecordMetaData rs = val.getMetaData();
				for (int col = 0; col < rs.getNumCols(); ++col)
					if (rs.getColumnName(col).equals(colName))
					{
						idx = col;
						break;
					}
			}
			return idx;
		}
	}

	@Override
	public RecordMetaData getMetaData() 
	{
		return rmd;
	}

	@Override
	public void close() 
	{
		parent.close();
	}
}