package nl.helixsoft.recordstream;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.helixsoft.util.StringUtils;

public class DefaultRecordMetaData implements RecordMetaData
{
	private final String[] columns;
	private final Map<String, Integer> index = new HashMap<String, Integer>();
	
	public DefaultRecordMetaData(String[] columns)
	{
		this.columns = columns;
		for (int i = 0; i < columns.length; ++i)
		{
			index.put (columns[i], i);
		}
	}

	public DefaultRecordMetaData(List<String> list)
	{
		this.columns = list.toArray(new String[0]);
		for (int i = 0; i < columns.length; ++i)
		{
			index.put (columns[i], i);
		}
	}
	
	public int getNumCols()
	{
		return columns.length;
	}
	
	public String getColumnName(int i)
	{
		return columns[i];
	}
	
	public int getColumnIndex(String name)
	{
		if (!index.containsKey(name))
			throw new IllegalArgumentException("Column '" + name + "' doesn't exist, there are " + columns.length + " options [" + StringUtils.join (",", columns) + "]");
		return index.get(name);
	}

	@Override
	public boolean hasColumnName(String name) 
	{
		return index.containsKey(name);
	}

}
