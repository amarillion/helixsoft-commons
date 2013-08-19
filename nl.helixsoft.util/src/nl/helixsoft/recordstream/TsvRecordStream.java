package nl.helixsoft.recordstream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class TsvRecordStream extends AbstractRecordStream
{
	public static int FILTER_COMMENTS = 0x100;
	public static int NO_HEADER = 0x200;
	
	/** If this flag is on, check for each header and row field if it is enclosed in double quotes, and remove them */
	public static int REMOVING_OPTIONAL_QUOTES = 0x400;
	
	private int flags = 0;
	private final BufferedReader reader;
	private BiMap<String, Integer> header = HashBiMap.create();

	public TsvRecordStream (Reader _reader, String[] _header) throws RecordStreamException
	{
		this.reader = new BufferedReader(_reader);
		int i = 0;
		for (String h : _header)
		{
			header.put (h, i);
			i++;
		}
	}
	
	public TsvRecordStream (Reader _reader) throws RecordStreamException
	{
		this (_reader, 0);
	}

	private String removeOptionalQuotes(String in)
	{
		if (in.startsWith("\"") && in.endsWith("\""))
		{
			return in.substring (1, in.length() - 1);
		}
		else
			return in;
	}
	
	public TsvRecordStream (Reader _reader, int flags) throws RecordStreamException
	{
		this.flags = flags;
		try 
		{
			this.reader = new BufferedReader(_reader);
			int i = 0;
			
			String headerLine = getNextNonCommentLine();
			if (headerLine != null) // empty file has no header
			{
				for (String h : headerLine.split("\t"))
				{
					if ((flags & REMOVING_OPTIONAL_QUOTES) > 0)
					{
						header.put (removeOptionalQuotes(h), i);
					}
					else
						header.put (h, i);
					i++;
				}
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
			String line;
			// fetch next line that doesn't start with "#"
			line = getNextNonCommentLine();
			if (line == null) return null;
			
			String[] split = line.split("\t", -1);
			
			String[] fields;
			if (split.length == header.size())
			{
				fields = split;
				if ((flags & REMOVING_OPTIONAL_QUOTES) > 0)
				{
					for (int col = 0; col < header.size(); ++col)
					{
						fields[col] = removeOptionalQuotes(fields[col]);
					}
				}
			}
			else
			{
				// ensure that array of fields is the expected length
				fields = new String[header.size()];
				int col = 0;
				for (String field : split)
				{
					if ((flags & REMOVING_OPTIONAL_QUOTES) > 0)
						fields[col] = removeOptionalQuotes(field);
					else
						fields[col] = field;
					
					col++;
					if (col == header.size()) 
					{
						System.err.println ("Warning: found extra column in TSV file");
						break; // ignoring extra column
					}
				}
			}
			return new DefaultRecord(this, fields);
		} 
		catch (IOException e) 
		{
			throw new RecordStreamException(e);
		}
	}

	private String getNextNonCommentLine() throws IOException 
	{
		if  ((flags | FILTER_COMMENTS) == 0) return reader.readLine();
		
		String line;
		do {
			line = reader.readLine();
			if (line == null) return null;
		} 
		while (line.startsWith("#"));
		return line;
	}

	@Override
	public int getColumnIndex(String name) 
	{
		if (!header.containsKey(name)) throw new IllegalArgumentException("Column '" + name + "' doesn't exist, options are " + header.keySet());
		return header.get(name);
	}

}
