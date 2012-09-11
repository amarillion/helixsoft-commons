package nl.helixsoft.recordstream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * I implemented this utility method because the SPARQL GROUP BY implementation of virtuoso sucks.
 * (AVG and SUM cast double to int, CONCAT just takes the first value, etc...)
 * Instead, just do the query without a group clause, but with output sorted on the grouping variable.
 * Then pass the result to this function to aggregate the results by group.
 * @throws SQLException 
 * @throws IllegalAccessException 
 * @throws InstantiationException 
 */
public class Reducer implements RecordStream
{
	private final RecordStream parent;
	private final Map<String, GroupFunc> accumulator;
	private final Map<String, Integer> idxByKey;
	private final String groupKey;
	private final List<String> dataHeaders;
	private Object prevValue = null;
	private Record row;
	private int idxGroupVar;
	
	Reducer (RecordStream parent, String groupVar, Map<String, GroupFunc> accumulator) throws RecordStreamException
	{
		this.parent = parent;
		this.accumulator = accumulator; //TODO: should really make a defensive copy
		
		idxByKey = new HashMap<String, Integer>();
		for (int i = 0; i < parent.getNumCols(); ++i)
		{
			idxByKey.put (parent.getColumnName(i), i);
		}
		dataHeaders = new ArrayList<String>(accumulator.keySet());
		this.groupKey = groupVar;
		idxGroupVar = idxByKey.get(groupVar);
		
		row = parent.getNext();
		prevValue = row.getValue(idxGroupVar);
		// Reset the accumulator at start
		resetAccumulator();
	}
	
	@Override
	public int getNumCols() 
	{
		return dataHeaders.size() + 1;
	}

	@Override
	public String getColumnName(int i) 
	{
		if (i == 0) return groupKey; else return dataHeaders.get(i - 1);
	}

	private Record writeAccumulator() 
	{
		Object[] vals = new Object[getNumCols()];
		vals[0] = prevValue;
		for (int i = 0; i < dataHeaders.size(); ++i)
		{
			vals[i+1] = accumulator.get(dataHeaders.get(i)).getResult();
		}
		return new DefaultRecord(this, vals);
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
	public Record getNext() throws RecordStreamException 
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
			if (!(row.getValue(idxGroupVar).toString().equals(prevValue.toString())))
			{
				Record result = writeAccumulator();
				// start with a fresh accumulator
				prevValue = row.getValue(idxGroupVar);
				resetAccumulator();
				return result;
			}

		} 		
	}

	public static class Count implements GroupFunc
	{
		private int count = 0;
		public void accumulate (Record val) { count++; }
		public Object getResult() { return count; }
		public void clear() { count = 0; }
	}

	public static class LogAverageFloat extends AbstractGroupFunc
	{
		public LogAverageFloat(String col) { super(col); }
		private int count = 0;
		private float sum = 0;
		public void accumulate (Record val) { int i = getIdx(val); count++; sum += (Float)val.getValue(i); }
		public Object getResult() { 
			double result = Math.log (sum / count); 
			return result;
		}
		public void clear() { count = 0; sum = 0; }
	}

	public interface GroupFunc
	{
		public void accumulate(Record val);
		public Object getResult();
		public void clear();
		

	}

	public static class AverageFloat extends AbstractGroupFunc
	{
		public AverageFloat(String col) { super(col); }
		private int count = 0;
		private float sum = 0;
		public void accumulate (Record val) { int i = getIdx(val); count++; sum += (Float)val.getValue(i); }
		public Object getResult() { return sum / count; }
		public void clear() { count = 0; sum = 0; }
	}

	public static class SumFloat extends AbstractGroupFunc
	{
		public SumFloat(String col) { super(col); }
		private float sum = 0;
		public void accumulate (Record val) { int i = getIdx(val); sum += (Float)val.getValue(i); }
		public Object getResult() { return sum; }
		public void clear() { sum = 0; }
	}

	public static class Concatenate extends AbstractGroupFunc
	{
		private final String sep;
		public Concatenate(String colName, String sep) { super(colName); this.sep = sep; }
		private boolean first = true;
		private StringBuilder builder = new StringBuilder();
		public void accumulate (Record val) { int i = getIdx(val); if (first) first = false; else builder.append(sep); builder.append(val.getValue(i).toString()); }
		public Object getResult() { return builder.toString(); }			
		public void clear() { first = true; builder = new StringBuilder(); }
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
				RecordStream rs = val.getParent();
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
}