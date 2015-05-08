package nl.helixsoft.stats;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import nl.helixsoft.recordstream.Record;

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
			return this;
		}

		static class CompoundKey implements Comparable<CompoundKey>
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
			List<String> colNames = new ArrayList<String>();
			for (Map.Entry<CompoundKey, Integer> e : columnFactors.entrySet())
			{
				e.setValue(i++);
				colNames.add("" + e.getKey());
			}
				
			Matrix<Double> m = new Matrix<Double>(columnFactors.size(), rowFactors.size());
						
			for (Record r : frame.asRecordIterable())
			{
				CompoundKey rowKey = selectFields(r, rows);
				CompoundKey colKey = selectFields(r, columns);
				
				Double v = (Double)r.get(value);
				int row = rowFactors.get(rowKey);
				int col = columnFactors.get(colKey);
				m.set(row, col, v);
				rowNames.set(row, "" + r.get(rowNameField));
				colNames.set(col, "" + r.get(colNameField));
			}		
			
			return MatrixDataFrame.fromMatrix(m, colNames, rowNames);
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

}
