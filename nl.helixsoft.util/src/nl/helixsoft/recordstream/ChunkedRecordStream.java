package nl.helixsoft.recordstream;

import java.util.ArrayList;
import java.util.List;

import nl.helixsoft.util.ObjectUtils;
import nl.helixsoft.util.StringUtils;

/**
 * A chunked record stream groups subsequent records that share the value for one
 * field, the key.
 * All consecutive records that have the same value in the key column
 * are accumulated in a List, one list for each value.
 */
public class ChunkedRecordStream 
{
	private final RecordStream parent;
	private final String key;
	private Record next;
	
	public ChunkedRecordStream (String key, RecordStream parent) throws RecordStreamException
	{
		this.parent = parent;
		next = parent.getNext();
		this.key = key;
	}
	
	public List<Record> getNext() throws RecordStreamException
	{
		if (next == null) return null; // eof.
		String currentKey = StringUtils.safeToString(next.getValue(key));
		List<Record> result = new ArrayList<Record>();
		
		do
		{
			result.add(next);
			next = parent.getNext();
			if (next == null) break;
		} 
		while (next != null && ObjectUtils.safeEquals(currentKey, next.getValue(key)));
		return result;
	}
}
