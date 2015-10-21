package nl.helixsoft.recordstream;

import java.io.File;
import java.io.FileNotFoundException;
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

	
	//TODO... also builder?
	public static void write (DataFrame df, OutputStream os) throws StreamException
	{
		RecordStreamFormatter.asTsv(new PrintStream (os), df.asRecordStream(), null, true);
	}
}
