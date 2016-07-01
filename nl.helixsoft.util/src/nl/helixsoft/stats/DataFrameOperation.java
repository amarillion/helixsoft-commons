package nl.helixsoft.stats;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.table.TableModel;

import com.google.common.collect.HashMultimap;

import nl.helixsoft.recordstream.BiFunction;
import nl.helixsoft.recordstream.Record;
import nl.helixsoft.recordstream.RecordStream;
import nl.helixsoft.recordstream.ReduceFunctions;
import nl.helixsoft.util.ObjectUtils;

/**
 * A collection of static functions to perform complex transformations on DataFrames.
 * <p>
 * Examples: join two dataframes (left, right, full join, inner join), 
 * convert a dataframe from long format to wide format and back (like a pivot table in Excel),
 * and more.
 */
public abstract class DataFrameOperation 
{
	public enum JoinType { LEFT, RIGHT, FULL, INNER }
	
	public static DataFrame merge(DataFrame a, Map<?, ?> b, String onColumn, String valueColumnName) 
	{
		DataFrame dfNew = DefaultDataFrame.createWithHeader(new String[] { onColumn, valueColumnName });
		
		for (Map.Entry<?, ?> e : b.entrySet())
		{
			dfNew.rbind(e.getKey(), e.getValue());
		}
		
		return a.merge(dfNew, onColumn);
	}
	
	public static DataFrame merge (DataFrame a, DataFrame b, int onThisColumn, int onThatColumn)
	{
		return merge (a, b, onThisColumn, onThatColumn, JoinType.FULL);
	}

	private static int addLinear(Set<Integer> values, List<Integer> select, List<Integer> nullSelect)
	{
		for (int i : values)
		{
			select.add(i);
			nullSelect.add(null);
		}
		return values.size();
	}
	
	private static int addCartesian(Set<Integer> leftValues, Set<Integer> rightValues, List<Integer> leftSelect, List<Integer> rightSelect)
	{
		for (int left : leftValues)
		{
			for (int right : rightValues)
			{
				leftSelect.add(left);
				rightSelect.add(right);
			}
		}
		
		return leftValues.size() * rightValues.size();
	}
	
	private static int addCombination(Set<Integer> leftValues, Set<Integer> rightValues, JoinType joinType, List<Integer> leftSelect, List<Integer> rightSelect)
	{
		switch (joinType)
		{
		case FULL:
			if (leftValues.isEmpty() && rightValues.isEmpty()) return 0;
			else if (leftValues.isEmpty()) return addLinear (rightValues, rightSelect, leftSelect);
			else if (rightValues.isEmpty()) return addLinear (leftValues, leftSelect, rightSelect);
			else return addCartesian (leftValues, rightValues, leftSelect, rightSelect);		
		case LEFT:
			if (leftValues.isEmpty()) return 0;
			else if (rightValues.isEmpty()) return addLinear (leftValues, leftSelect, rightSelect);
			else return addCartesian (leftValues, rightValues, leftSelect, rightSelect);
		case RIGHT:
			if (rightValues.isEmpty()) return 0;
			else if (leftValues.isEmpty()) return addLinear (rightValues, rightSelect, leftSelect);
			else return addCartesian (leftValues, rightValues, leftSelect, rightSelect);			
		case INNER:
			if (leftValues.isEmpty() || rightValues .isEmpty()) return 0;
			return addCartesian (leftValues, rightValues, leftSelect, rightSelect);
		default:
			throw new IllegalArgumentException ("Invalid value for JoinType");
		}
	}
	
	public static DataFrame merge (DataFrame left, DataFrame right, int onLeftColumn, int onRightColumn, JoinType joinType)
	{
		HashMultimap<Object, Integer> leftIndex = HashMultimap.create();
		Set<Object> allKeys = new HashSet<Object>();
		for (int i = 0; i < left.getRowCount(); ++i)
		{
			Object key = left.getValueAt(i, onLeftColumn);
			leftIndex.put (key, i);
			allKeys.add(key);
		}

		HashMultimap<Object, Integer> rightIndex = HashMultimap.create();
		for (int i = 0; i < right.getRowCount(); ++i)
		{
			Object key = right.getValueAt(i, onRightColumn);
			rightIndex.put (key, i);
			allKeys.add(key);
		}
		
		List<Integer> leftSelect = new ArrayList<Integer>();
		List<Integer> rightSelect = new ArrayList<Integer>();
		
		List<Object> keyColumn = new ArrayList<Object>();
		
		for (Object key : allKeys)
		{
			Set<Integer> leftValues = leftIndex.get(key);
			Set<Integer> rightValues = rightIndex.get(key);
			
			int numrows = addCombination (leftValues, rightValues, joinType, leftSelect, rightSelect);
			for (int i = 0; i < numrows; ++i)
				keyColumn.add(key);
		}
		
		int[] leftColumns = new int[left.getColumnCount()-1];
		int pos = 0;
		for (int col = 0; col < left.getColumnCount(); ++col)
			if (col != onLeftColumn) leftColumns[pos++] = col;
		
		pos = 0;
		int[] rightColumns = new int[right.getColumnCount()-1];
		for (int col = 0; col < right.getColumnCount(); ++col)
			if (col != onRightColumn) rightColumns[pos++] = col;
		
		
		return DataFrameOperation.cbind (				
				new ListColumn(keyColumn, left.getColumnHeader(onLeftColumn).toString()),
				left.cut(leftColumns).select(leftSelect),
				right.cut(rightColumns).select(rightSelect)				
			);
	}

	public static DataFrame cbind(Object... columnLike)
	{
		List<Column<?>> resultColumns = new ArrayList<Column<?>>();
		
		for (Object o : columnLike)
		{
			if (o instanceof DataFrame)
			{
				DataFrame df = (DataFrame)o;
				for (int i = 0; i < df.getColumnCount(); ++i)
				{
					Column<?> col = new DefaultColumnView(df, i);

					resultColumns.add (col);
				}
			}
			else if (o instanceof Matrix)
			{
				//TODO
			}
			else if (o instanceof Object[])
			{
				//TODO
			}
			else if (o instanceof Column)
			{
				resultColumns.add ((Column<?>)o);
			}
			else if (o instanceof List)
			{
				resultColumns.add (new ListColumn((List)o, null));
			}
			else
			{
				throw new IllegalArgumentException("Not a suitable column-like class");
			}
			
		}
		
		Integer uniformColumnLength = null;
		List<String> headers = new ArrayList<String>();
		for (Column<?> col : resultColumns)
		{
			if (uniformColumnLength == null) uniformColumnLength = col.getSize(); 
			else 
				if (uniformColumnLength != col.getSize()) 
					throw new IllegalArgumentException ("Not all input columns have equal size");
		
			headers.add((String)col.getHeader());
		}
		
		DataFrame result = DataFrameOperation.createWithHeader(headers.toArray(new String[headers.size()]));
		for (int row = 0; row < uniformColumnLength; ++row)
		{
			Object[] rowObj = new Object[resultColumns.size()];
			for (int col = 0; col < resultColumns.size(); ++col)
			{
				rowObj[col] = resultColumns.get(col).get(row);
			}
			result.rbind(rowObj);
		}
		
		return result;
	}
	
	public static DataFrame merge (DataFrame a, DataFrame b, String onThisColumn, String onThatColumn, JoinType joinType)
	{		
		return merge (a, b, a.getColumnIndex(onThisColumn), b.getColumnIndex(onThatColumn), joinType);
	}

	public static DataFrame merge (DataFrame a, DataFrame b, String onColumn)
	{
		return merge (a, b, onColumn, onColumn, JoinType.FULL);
	}
	
	public static DataFrame merge (DataFrame a, DataFrame b, String onColumn, JoinType joinType) 
	{
		return merge(a, b, onColumn, onColumn, joinType);
	}

	public static WideFormatBuilder wideFormat(DataFrame a) 
	{
		return new WideFormatBuilder(a);
	}
	
	//TODO: groupBy and wideFormat are somewhat similar - both have grouping functions and grouping columns...
	public static GroupByBuilder groupBy (DataFrame a, String groupColumn)
	{
		return new GroupByBuilder (a, groupColumn);
	}
	
	public static class GroupByBuilder
	{
		private static class Aggregate
		{
			String col;
			BiFunction func;
		}
		
		private DataFrame parent;
		private String groupColumn;
		private List<Aggregate> aggs = new ArrayList<Aggregate>();
		List<String> headers = new ArrayList<String>();
		
		GroupByBuilder (DataFrame parent, String groupColumn)
		{
			this.parent = parent;
			this.groupColumn = groupColumn;
			headers.add(groupColumn);
		}
		
		public GroupByBuilder agg(String col, BiFunction<? extends Object, ? extends Object, ? extends Object> func)
		{
			Aggregate agg = new Aggregate();
			agg.col = col;
			agg.func = func;
			aggs.add(agg);
			headers.add(col);
			return this;
		}
		
		public DataFrame get()
		{
			DataFrame sorted = parent.sort(groupColumn);
			int colNum = headers.size();
			String[] header = headers.toArray(new String[colNum]); 
			DataFrame result = DataFrameOperation.createWithHeader(header);
			Object prev = null;
			
			Object[] row = null;			
			for (Record r : sorted.asRecordIterable())
			{
				Object current = r.get(groupColumn);
				if (row == null)
				{
					row = new Object[colNum];
				}
				else if (!ObjectUtils.safeEquals(current, prev))
				{					
					result.rbind (row);
					row = new Object[colNum];
				}
				
				row[0] = current;
				for (int i = 0; i < aggs.size(); ++i)
				{
					Aggregate agg = aggs.get(i); 
					Object more = r.get(agg.col);
					Object chain = row[i+1];
					row[i+1] = agg.func.apply(chain, more);					
				}
				prev = current;
			}
			
			if (row != null) result.rbind (row);
			
			return result;
		}
		
	}
	
	public static class WideFormatBuilder
	{
		private final DataFrame frame;
		private String[] columns;
		private String[] rows;
		private String rowNameField;
		private String colNameField;
		private String value; //TODO: option for aggregate functions...
		private BiFunction<Object, Object, Object> reduce;
		
		public WideFormatBuilder(DataFrame a) 
		{
			frame = a;
		}

		public WideFormatBuilder withRowFactor(String... string) 
		{
			rows = string;
			return this;
		}

		public WideFormatBuilder withColumnFactor(String... string) 
		{
			columns = string;
			return this;
		}

		public WideFormatBuilder withRowNames(String string) 
		{
			rowNameField = string;
			return this;
		}

		public WideFormatBuilder withColNames(String string) 
		{
			colNameField = string;
			return this;
		}

		public WideFormatBuilder withValue(String string) 
		{
			value = string;
			reduce = ReduceFunctions.FIRST;
			return this;
		}

		public WideFormatBuilder reduce(String string, BiFunction<Object, Object, Object> func) 
		{
			value = string;
			reduce = func;
			return this;
		}

		public static class CompoundKey implements Comparable<CompoundKey>
		{
			final String[] data;
						
			public CompoundKey(int length)
			{
				data = new String[length];
			}

			@Override
			public int compareTo(CompoundKey other) 
			{
				for (int i = 0; i < data.length; ++i)
				{
					Comparable a = data[i];
					Comparable b = other.data[i];
					if (a == null && b == null) continue;					 
					if (a == null) return -1;
					if (b == null) return 1;
					
					int result = a.compareTo(b); 
					if (result == 0) continue;
					return result;
				}
				return 0;
			}
			
			@Override
			public boolean equals(Object o)
			{
				if (o == null || o.getClass() != CompoundKey.class) return false;
				CompoundKey other = (CompoundKey)o;
				
				for (int i = 0; i < data.length; ++i)
				{
					String a = data[i];
					String b = other.data[i];
					if (a == null && b == null) continue;
					if (a == null || b == null) return false;
					
					if (!a.equals(b)) return false;
				}
				return true;
			}
			
			@Override
			public int hashCode()
			{
				int result = 0;
				
				for (int i = 0; i < data.length; ++i)
				{
					Object o = data[i];
					if (o == null) 
					{
						result = result * 5;
					}
					else
					{
						result = result ^ o.hashCode();
					}
				}
				return result;
			}
			
			public void put(int ix, String o)
			{
				data [ix] = o;
			}
			
			@Override 
			public String toString()
			{
				StringBuilder result = new StringBuilder();
				String sep = "[";
				for (Object o : data)
				{
					result.append(sep);
					result.append("" + o);
					sep = ", ";
				}
				result.append ("]");
				
				return result.toString();
//				return data[0];
			}
			
			public String get(int ix)
			{
				return data [ix];
			}
		}
		
		public DataFrame get() 
		{
			// first pass: build factors
			SortedMap<CompoundKey, Integer> rowFactors = new TreeMap<CompoundKey, Integer>();
			SortedMap<CompoundKey, Integer> columnFactors = new TreeMap<CompoundKey, Integer>();
			
			for (Record r : frame.asRecordIterable())
			{
				CompoundKey rowKey = selectFields(r, rows);
				rowFactors.put(rowKey, 0);
				
				CompoundKey colKey = selectFields(r, columns);
				columnFactors.put(colKey, 0);
			}
			
			int i = 0;
			List<String> rowNames = new ArrayList<String>();
			for (Map.Entry<CompoundKey, Integer> e : rowFactors.entrySet())
			{
				e.setValue(i++);
				rowNames.add("" + e.getKey());
			}
			
			i = 0;
			List<Object> colNames = new ArrayList<Object>();
			for (Map.Entry<CompoundKey, Integer> e : columnFactors.entrySet())
			{
				e.setValue(i++);
				colNames.add(e.getKey());
			}
				
			Matrix<Double> m = new Matrix<Double>(columnFactors.size(), rowFactors.size());
						
			for (Record r : frame.asRecordIterable())
			{
				CompoundKey rowKey = selectFields(r, rows);
				CompoundKey colKey = selectFields(r, columns);
				
				Object v = r.get(value);
				int row = rowFactors.get(rowKey);
				int col = columnFactors.get(colKey);
				
				Object value = m.get(row, col);
				Object reduced = reduce.apply(value, v);
				m.set(row, col, reduced);
				
				if (rowNameField != null) rowNames.set(row, "" + r.get(rowNameField));
				if (colNameField != null) colNames.set(col, "" + r.get(colNameField));
			}
			
			return MatrixDataFrame.fromMatrix(m, new DefaultHeader(colNames, columns.length), rowNames);
		}

		private CompoundKey selectFields(Record r, String[] selectedFields) 
		{
			CompoundKey rowKey = new CompoundKey(selectedFields.length);
			for (int i = 0; i < selectedFields.length; ++i)
			{
				rowKey.put(i, "" + r.get(selectedFields[i]));
			}
			return rowKey;
		}

	
	}

	public static DataFrame fromArray(String[] header, Object[][] objects) 
	{
		DataFrame df = DataFrameOperation.createWithHeader(header);
		for (Object[] row : objects)
		{
			df.rbind (row);
		}			
		return df;
	}

	public static DataFrame rbind(DataFrame df1, DataFrame df2) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	public static DataFrame columnSort(DataFrame in)
	{
		return columnSort(in, new Comparator<Column<?>>() {

			@Override
			public int compare(Column<?> o1, Column<?> o2) 
			{
				Comparable<Comparable> a1 = (Comparable<Comparable>)o1.getHeader();
				Comparable a2 = (Comparable)o2.getHeader();
				
				if (a1 == null && a2 == null) return 0;
				if (a1 == null) return 1;
				if (a2 == null) return -1;
				return a1.compareTo(a2);
			}
		});
	}
	
	public static DataFrame columnSort(DataFrame in, Comparator<Column<?>> comparator)
	{
		List<Column<?>> views = new ArrayList<Column<?>>();
		for (int i = 0; i < in.getColumnCount(); ++i)
		{
			views.add (new DefaultColumnView(in, i));
		}
		
		Collections.sort (views, comparator);
		
		return new ColumnBoundDataFrame (views, in);
	}

	public static void toTsv(PrintStream out, DataFrame df) 
	{
		for (int col = 0; col < df.getColumnCount(); ++col)
		{
			out.print ("\t");
			out.print (df.getColumnHeader(col).toString());
		}
		out.println();
		for (int row = 0; row < df.getRowCount(); ++row)
		{
			out.print (df.getRowName(row));
			for (int col = 0; col < df.getColumnCount(); ++col)
			{
				out.print ("\t");
				out.print (df.getValueAt(row, col));
			}
			out.println();
		}	
	}
	
	/**
	 * Render DataFrame as html.
	 * Excludes the <table> tag.
	 */
	public static void toHtml(PrintStream out, DataFrame df)
	{
		out.println ("<thead>");
		for (int h = 0; h < df.getColumnHeader().getSubHeaderCount(); ++h)
		{
			out.println ("<tr><th></th>");
			for (int c = 0; c < df.getColumnCount(); ++c)
			{
				Object o = df.getColumnHeader().get(c);
				String hstr = o.toString(); 
				out.print("<th>" + hstr + "</th>");
			}
			out.println ("</tr>");
		}
		out.println ("</thead>");
		
		
		for (int r = 0; r < df.getRowCount(); ++r)
		{
			out.println ("<tr><th>" + df.getRowName(r) + "</th>");
			for (int c = 0; c < df.getColumnCount(); ++c)
			{
				out.print("<td>" + df.getValueAt(r, c) + "</td>");		
			}
			out.println ("</tr>");
		}
	}

	public static TableModel asTableModel(DataFrame df) 
	{
		return new DataFrameTableModel (df, false);
	}
	
	public static DataFrame createWithHeader (String... header)
	{
		return DefaultDataFrame.createWithHeader(header);
	}
	
	public static DataFrame createFromRecordStream (RecordStream input)
	{
		return DefaultDataFrame.createFromRecordStream(input);
	}

}
