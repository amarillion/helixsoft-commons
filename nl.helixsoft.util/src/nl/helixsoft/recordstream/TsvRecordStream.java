package nl.helixsoft.recordstream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class TsvRecordStream implements RecordStream
{
	private final BufferedReader reader;
	private BiMap<String, Integer> header = HashBiMap.create();
	
	public TsvRecordStream (Reader _reader) throws RecordStreamException
	{
		try 
		{
			this.reader = new BufferedReader(_reader);
			int i = 0;
			for (String h : reader.readLine().split("\t"))
			{
				header.put (h, i);
				i++;
			}
		} 
		catch (IOException e) 
		{
			throw new RecordStreamException(e);
		}
	}

	@Override
	public int getNumCols() 
	{
		return header.size();
	}

	@Override
	public String getColumnName(int i) 
	{
		return header.inverse().get(i);
	}

	@Override
	public Record getNext() throws RecordStreamException 
	{
		try 
		{
			String line = reader.readLine();
			if (line == null) return null;
			return new DefaultRecord(this, line.split("\t"));
		} 
		catch (IOException e) 
		{
			throw new RecordStreamException(e);
		}
	}

	@Override
	public int getColumnIndex(String name) 
	{
		if (!header.containsKey(name)) throw new IllegalArgumentException("Column doesn't exist");
		return header.get(name);
	}

}
