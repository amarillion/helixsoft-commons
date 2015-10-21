package nl.helixsoft.recordstream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import nl.helixsoft.stats.DataFrame;
import nl.helixsoft.stats.DefaultDataFrame;
import nl.helixsoft.util.StringUtils;

//TODO: rename to FileRecordStream
/**
 * Turn a stream of delimited values into a record stream.
 * Uses the <a href="http://www.informit.com/articles/article.aspx?p=1216151&seqNum=2">Builder Pattern</a> for configuration.
 * <p>
 * Usage examples:<br>
 * <p>
 * Open a tab-delimited text file:<br>
 * <code>
 * TsvRecordStream.open(file).get();
 * </code>
 * <p>
 * Open a tab-delimited text file with #-comments, specifying the header:<br>
 * <code>
 * TsvRecordStream.open(file).filterComments().setHeader(new String["ice-cream consumption", "drowning deaths"]).get();
 * </code>
 * <p>
 * Open a comma-separated values file that has quotes around string values:<br>
 * <code>
 * TsvRecordStream.open(file).commaSeparated().removeOptionalQuotes().get();
 * </code>
 * <p>
 * NOTE: I am considering to rename this FileRecordStream, because this class can deal with various kinds of files, not just TSV and CSV but arbitrary separated files. 
 */
public class TsvRecordStream extends AbstractRecordStream
{
	private static int FILTER_COMMENTS = 0x100;
	private static int NO_HEADER = 0x200;
	
	/** If this flag is on, check for each header and row field if it is enclosed in double quotes, and remove them */
	private static int REMOVING_OPTIONAL_QUOTES = 0x400;
	private static int COMMA_DELIMITED = 0x800;

	private int flags = 0;
	private final BufferedReader reader;
	private final RecordMetaData rmd;
	private String delimiter = "\t";
	
	
	/**
	 * Turn a {@link Reader} object into a {@link RecordStream}. 
	 * @return a builder object, on which configuration settings can be chained.
	 */
	public static Builder open (Reader _reader)
	{
		return new Builder(_reader);	
	}

	/**
	 * Turn a {@link InputStream} object into a {@link RecordStream}. 
	 * @return a builder object, on which configuration settings can be chained.
	 */
	public static Builder open (InputStream _is)
	{
		return new Builder(_is);	
	}

	/**
	 * Open a {@link File} and create a {@link RecordStream} from it. 
	 * @return a builder object, on which configuration settings can be chained.
	 */
	public static Builder open (File _file) throws FileNotFoundException
	{
		return new Builder(_file);	
	}
	
	/**
	 * Builder for configuration options.
	 * <p>
	 * All the various configuration options are available through this builder. Once the configuration is complete,
	 * call the get() method to obtain the actual RecordStream.
	 * <p>
	 * Each configuration method returns <code>this</code>, so the configuration can be chained.
	 */
	public static class Builder
	{
		private final Reader reader;
		private String delimiter = "\t";
		private int flags;
		private String[] header = null;
		
		Builder(Reader _reader)
		{
			this.reader = _reader;
		}

		Builder(File f) throws FileNotFoundException
		{
			this.reader = new FileReader (f);
		}

		Builder(InputStream is)
		{
			this.reader = new InputStreamReader (is);
		}

		/**
		 * Configure a tab-separated stream
		 */
		public Builder tabSeparated()
		{
			delimiter = "\t";
			return this;
		}
		
		/**
		 * Configure a comma-separated stream instead of the default (tab-delimited)
		 */
		public Builder commaSeparated()
		{
			delimiter = ",";
			return this;
		}
		
		/**
		 * Use a regular expression as a custom separator.
		 * For example, the following threats any consecutive whitespace as a delimiter.
		 * <code>
		 * customSeparator("\s+")
		 * </code>
		 */
		public Builder customSeparator(String regex)
		{
			delimiter = regex;
			return this;
		}

		/**
		 * If a field is surrounded by '"' quotes, remove them. Particularly useful for CSV files.
		 * 
		 * for the combination of commaSeparated and removeOptionalQuotes, use the function StringUtils.quotedCommaSplit, to deal correctly with comma's inside quotes
		 * //TODO: currently doesn't handle newlines within quotes, as per the semi-official specification: https://en.wikipedia.org/wiki/Comma-separated_values
		 */
		public Builder removeOptionalQuotes()
		{
			flags |= REMOVING_OPTIONAL_QUOTES;
			return this;
		}

		/**
		 * The first line is a header line.
		 */
		public Builder firstLineIsHeader()
		{
			if ((flags & NO_HEADER) > 0) flags -= NO_HEADER;
			return this;
		}
		
		/**
		 * Instead of using the first line as a header line, set the values of the header you would like to see. 
		 * <p>
		 * Implies that there is no header line in the data.
		 */
		public Builder setHeader(String[] header)
		{
			this.header = header;
			flags |= NO_HEADER;
			return this;
		}

		/**
		 * Instead of using the first line as a header line, set the values of the header you would like to see. 
		 * <p>
		 * Implies that there is no header line in the data.
		 */
		public Builder setHeader(List<String> header)
		{
			this.header = header.toArray(new String[header.size()]);
			flags |= NO_HEADER;
			return this;
		}

		/**
		 * Filter out any lines that start with a '#' comment marker.
		 */
		public Builder filterComments()
		{
			flags |= FILTER_COMMENTS;
			return this;
		}
		
		public TsvRecordStream get() throws StreamException
		{
			return asRecordStream();
		}

		public TsvRecordStream asRecordStream() throws StreamException
		{
			if (header == null)
			{
				return new TsvRecordStream (reader, delimiter, flags);
			}
			else
			{
				return new TsvRecordStream (reader, delimiter, header, flags);
			}			
		}
		
		public DataFrame asDataFrame() throws StreamException
		{
			return DefaultDataFrame.createFromRecordStream(get());
		}

	}
	
	private String[] splitLine(String line)
	{
		String[] result;
		
		if ((flags & REMOVING_OPTIONAL_QUOTES) > 0 && (",".equals(delimiter)))
		{
			result = StringUtils.quotedCommaSplit(line).toArray(new String[] {});
		}
		else
		{
			result = line.split(delimiter, -1);
			if ((flags & (REMOVING_OPTIONAL_QUOTES)) > 0)
			{
				for (int i = 0; i < result.length; ++i)
				{
					result[i] = removeOptionalQuotes(result[i]);
				}
			}
			
		}
		
		return result;
	}
	
	/**
	 * Don't use, use open() instead.
	 * //TODO make private
	 */
	public TsvRecordStream (Reader _reader, String _delimiter, String[] _header, int flags) throws StreamException
	{
		this.flags = flags;
		if ((flags & COMMA_DELIMITED) > 0)
		{
			delimiter = ",";
		}
		else
		{
			delimiter = _delimiter;
		}
		
		this.reader = new BufferedReader(_reader);
		rmd = new DefaultRecordMetaData (_header);		
	}
	
	@Deprecated
	public TsvRecordStream (Reader _reader, String[] _header) throws StreamException
	{
		this (_reader, "\t", _header, 0);
	}

	// TODO: this constructor has some redundancy with TsvRecordStream(Reader, String, int)   
	/**
	 * Don't use, use open() instead.
	 * //TODO make private
	 */
	public TsvRecordStream (Reader _reader, String[] _header, int flags) throws StreamException
	{
		this (_reader, "\t", _header, flags);
	}

	@Deprecated
	public TsvRecordStream (Reader _reader) throws StreamException
	{
		this (_reader, "\t", 0);
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

	@Deprecated
	public TsvRecordStream (Reader _reader, int flags) throws StreamException
	{
		this (_reader, "\t", flags);
	}
	
	// TODO: this constructor has some redundancy with TsvRecordStream(Reader, String, String[], int)
	/**
	 * Don't use, use open() instead.
	 * //TODO make private
	 */
	public TsvRecordStream (Reader _reader, String _delimiter, int flags) throws StreamException
	{
		this.flags = flags;
		if ((flags & COMMA_DELIMITED) > 0)
		{
			delimiter = ",";
		}
		else
		{
			delimiter = _delimiter;
		}
		
		try 
		{
			this.reader = new BufferedReader(_reader);
			String headerLine = getNextNonCommentLine();
			List<String> header = new ArrayList<String>();
			if (headerLine != null) // empty file has no header
			{
				for (String h : splitLine(headerLine))
				{
					header.add (h);
				}
			}
			
			rmd = new DefaultRecordMetaData(header);
		} 
		catch (IOException e) 
		{
			throw new StreamException(e);
		}
	}

	/**
	 * Return the next Record, part of the RecordStream interface
	 * {@inheritDoc}
	 */
	@Override
	public Record getNext() throws StreamException 
	{
		try 
		{
			String line;
			// fetch next line that doesn't start with "#"
			line = getNextNonCommentLine();
			if (line == null) {	return null; }
			
			String[] split = splitLine(line);
			
			String[] fields;
			if (split.length == rmd.getNumCols())
			{
				fields = split;
			}
			else
			{
				// ensure that array of fields is the expected length
				fields = new String[rmd.getNumCols()];
				int col = 0;
				for (String field : split)
				{
					fields[col] = field;
					
					col++;
					if (col == rmd.getNumCols()) 
					{
						// there are extra columns at the end. Check if they are empty or if they contain data.
						for (int i = col; i < split.length; ++i)
						{
							if (!split[col].equals(""))
							{
								System.err.println ("Warning: found extra non-empty columns in TSV file");
								break; // ignoring extra column
							}
						}
						break;
					}
				}
			}
			return new DefaultRecord(rmd, fields);
		} 
		catch (IOException e) 
		{
			throw new StreamException(e);
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

	/**
	 * get the assodicated RecordMetaData, part of the RecordStream interface
	 * {@inheritDoc}
	 */
	@Override
	public RecordMetaData getMetaData() 
	{
		return rmd;
	}

	@Override
	public void close() 
	{
		try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
