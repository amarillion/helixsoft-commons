package nl.helixsoft.recordstream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;

import nl.helixsoft.recordstream.TsvRecordStream.Builder;
import nl.helixsoft.stats.DataFrame;
import nl.helixsoft.util.HFileUtils;

/**
 * Various functions for reading / writing tabular data formats.
 * 
 * 
 * Merges TsvRecordStream and RecordStreamFormatter in a more sensible naming structure
 *
 */
public class TabularIO 
{

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
	 * Open a {@link File} and create a {@link RecordStream} from it.
	 * If the file happens to end with .gz or .xz, then the file is automatically decompressed upon reading.
	 *  
	 * @return a builder object, on which configuration settings can be chained.
	 */
	public static Builder openz (File _file) throws IOException
	{
		return new Builder(HFileUtils.openZipStream(_file));	
	}

	
	/** @deprecated use write(df).to(os).go() */
	public static void write (DataFrame df, OutputStream os) throws StreamException
	{
		RecordStreamFormatter.asTsv(new PrintStream (os), df.asRecordStream(), null, true);
	}
	
	/** @deprecated use write(rs).to(os).go() */
	public static void write (RecordStream rs, OutputStream os) throws StreamException
	{
		RecordStreamFormatter.asTsv(new PrintStream (os), rs, null, true);
	}

	/** 
	 * start building a write operation, starting from a DataFrame as the source data.
	 * Follow this with additional WriteBuilder methods such as to() to specify the file or destination to write to,
	 * and go () to perform the actual write.
	 */
	public static WriteBuilder write(DataFrame df) throws StreamException
	{
		return new WriteBuilder(df);
	}

	/** 
	 * start building a write operation, starting from a RecordStream as the source data.
	 * Follow this with additional WriteBuilder methods such as to() to specify the file or destination to write to,
	 * and go () to perform the actual write.
	 */
	public static WriteBuilder write(RecordStream rs) throws StreamException
	{
		return new WriteBuilder(rs);
	}

	/** Builder pattern to compose a tabular write operation */
	public static class WriteBuilder
	{
		private final RecordStream source;
		private OutputStream dest;
		
		WriteBuilder(DataFrame df) 
		{
			this.source = df.asRecordStream();
		}

		WriteBuilder(RecordStream rs) 
		{
			this.source = rs;
		}

		/** Specify file to write to */
		public WriteBuilder to (File dest) throws FileNotFoundException
		{
			this.dest = new FileOutputStream(dest); 
			return this;
		}

		/** Specify an outputstream to write to */
		public WriteBuilder to (OutputStream os) throws FileNotFoundException
		{
			this.dest = os; 
			return this;
		}

		/** Final step in chain, perform the actual write. */
		public void go() throws StreamException
		{
			assert (source != null) : "When doing a TabularIO.write(), must specify a destination with to() before calling go()";
			RecordStreamFormatter.asTsv(new PrintStream (dest), source, null, true);
		}

	}

}
