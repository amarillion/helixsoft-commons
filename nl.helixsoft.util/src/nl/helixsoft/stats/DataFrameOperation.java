package nl.helixsoft.stats;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import nl.helixsoft.recordstream.Record;
import nl.helixsoft.recordstream.BiFunction;
import nl.helixsoft.recordstream.ReduceFunctions;
import nl.helixsoft.stats.DataFrameOperation.WideFormatBuilder;

public abstract class DataFrameOperation 
{
	public static DataFrame merge(DataFrame a, Map<?, ?> b, String joinColumn) 
	{
		return null; // TODO
	}
	
	public static DataFrame merge (DataFrame a, DataFrame b, int onThisColumn, int onThatColumn)
	{
		// TODO, move implementation accross
		return a.merge(b, onThisColumn, onThatColumn);
	}

	public static DataFrame merge (DataFrame a, DataFrame b, String onColumn) 
	{
		// TODO, move implementation accross
		return a.merge(b, onColumn);
	}

	public static WideFormatBuilder wideFormat(DataFrame a) 
	{
		return new WideFormatBuilder(a);
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
				rowNames.set(row, "" + r.get(rowNameField));
//				colNames.set(col, "" + r.get(colNameField));
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

	public static DataFrame fromArray(Object[][] objects) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	public static DataFrame rbind(DataFrame df1, DataFrame df2) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	public static DataFrame columnSort(DataFrame in)
	{
		return columnSort(in, new Comparator<ColumnView<?>>() {

			@Override
			public int compare(ColumnView<?> o1, ColumnView<?> o2) 
			{
				Comparable a1 = (Comparable)o1.getHeader();
				Comparable a2 = (Comparable)o2.getHeader();
				
				if (a1 == null && a2 == null) return 0;
				if (a1 == null) return 1;
				if (a2 == null) return -1;
				return a1.compareTo(a2);
			}
		});
	}
	
	public static DataFrame columnSort(DataFrame in, Comparator<ColumnView<?>> comparator)
	{
		List<ColumnView<?>> views = new ArrayList<ColumnView<?>>();
		for (int i = 0; i < in.getColumnCount(); ++i)
		{
			views.add (new DefaultColumnView(in, i));
		}
		
		Collections.sort (views, comparator);
		
		return new ColumnBoundDataFrame (views, in);
	}

	public static void toTsv(PrintStream out, DataFrame wide) 
	{
		for (int col = 0; col < wide.getColumnCount(); ++col)
		{
			out.print ("\t");
			out.print (wide.getColumnHeader(col).toString());
		}
		out.println();
		for (int row = 0; row < wide.getRowCount(); ++row)
		{
			out.print (wide.getRowName(row));
			for (int col = 0; col < wide.getColumnCount(); ++col)
			{
				out.print ("\t");
				out.print (wide.getValueAt(row, col));
			}
			out.println();
		}
	
	}
	
}
