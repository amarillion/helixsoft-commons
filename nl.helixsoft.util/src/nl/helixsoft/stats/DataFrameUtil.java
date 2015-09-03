package nl.helixsoft.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.helixsoft.recordstream.Record;
import nl.helixsoft.stats.DataFrame;

public class DataFrameUtil 
{
	public static <T> Map<String, T> asSingleValueMap(String key, T value)
	{
		Map<String, T> result = new HashMap<String, T>();
		result.put (key, value);
		return result;
	}
	
	public static List<Map<String, Object>> asListOfMap(DataFrame df) 
	{
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < df.getRowCount(); ++i)
		{
			 Map<String, Object> item = asMap(df.getRow(i));
			 item.put("name", df.getRowName(i)); //TODO: assumes valid row names...
			 result.add(item);
		}
		return result;
	}
	
	public static Map<String, Object> asMap(Record r)
	{
		Map<String, Object> result = new HashMap<String, Object>();
		for (int i = 0; i < r.getMetaData().getNumCols(); ++i)
		{
			result.put (r.getMetaData().getColumnName(i), r.get(i));
		}
		return result;	
	}
}
