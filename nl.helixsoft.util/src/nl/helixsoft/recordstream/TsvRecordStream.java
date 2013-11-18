package nl.helixsoft.recordstream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Turn a stream of delimited values into a record stream.
 */
public class TsvRecordStream extends AbstractRecordStream
{
	public static int FILTER_COMMENTS = 0x100;
	public static int NO_HEADER = 0x200;
	
	/** If this flag is on, check for each header and row field if it is enclosed in double quotes, and remove them */
	public static int REMOVING_OPTIONAL_QUOTES = 0x400;

	public static int TAB_DELIMITED = 0x000; // default, so zero.
	public static int COMMA_DELIMITED = 0x800;

	private int flags = 0;
	private final BufferedReader reader;
	private final RecordMetaData rmd;
	private String delimiter = "\t";
	
	public TsvRecordStream (Reader _reader, String[] _header) throws RecordStreamException
	{
		this.reader = new BufferedReader(_reader);
		rmd = new DefaultRecordMetaData (_header);
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
		if ((flags & COMMA_DELIMITED) > 0)
		{
			delimiter = ",";
		}
		
		try 
		{
			this.reader = new BufferedReader(_reader);
			String headerLine = getNextNonCommentLine();
			List<String> header = new ArrayList<String>();
			if (headerLine != null) // empty file has no header
			{
				for (String h : headerLine.split(delimiter))
				{
					if ((flags & REMOVING_OPTIONAL_QUOTES) > 0)
					{
						header.add (removeOptionalQuotes(h));
					}
					else
						header.add (h);
				}
			}
			
			rmd = new DefaultRecordMetaData(header);
		} 
		catch (IOException e) 
		{
			throw new RecordStreamException(e);
		}
	}

	@Override
	public int getNumCols() 
	{
		return rmd.getNumCols();
	}

	@Override
	public String getColumnName(int i) 
	{
		return rmd.getColumnName(i);
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
			
			String[] split = line.split(delimiter, -1);
			
			String[] fields;
			if (split.length == rmd.getNumCols())
			{
				fields = split;
				if ((flags & REMOVING_OPTIONAL_QUOTES) > 0)
				{
					for (int col = 0; col < rmd.getNumCols(); ++col)
					{
						fields[col] = removeOptionalQuotes(fields[col]);
					}
				}
			}
			else
			{
				// ensure that array of fields is the expected length
				fields = new String[rmd.getNumCols()];
				int col = 0;
				for (String field : split)
				{
					if ((flags & REMOVING_OPTIONAL_QUOTES) > 0)
						fields[col] = removeOptionalQuotes(field);
					else
						fields[col] = field;
					
					col++;
					if (col == rmd.getNumCols()) 
					{
						System.err.println ("Warning: found extra column in TSV file");
						break; // ignoring extra column
					}
				}
			}
			return new DefaultRecord(rmd, fields);
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
		return rmd.getColumnIndex(name);
	}

}
