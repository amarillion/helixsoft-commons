package nl.helixsoft.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Open temp file and create stream.
 * If dest file ends with .gz, a GZIP stream is opened.
 * Otherwise, a regular FileOutputStream is opened. 
 */
public class TempFile 
{
	private final File out;
	private OutputStream decoratedStream;
	private GZIPOutputStream gzip = null;
	private boolean isGzip;
	private File temp;
	
	public TempFile(File out)
	{
		this.out = out;
	}

	/** use getzStream. Added z clarifies that gzip stream will be used if possible.*/
	@Deprecated
	public OutputStream getStream() throws IOException
	{
		return getZStream();
	}
	
	public OutputStream getZStream() throws IOException
	{
		File dir = out.getAbsoluteFile().getParentFile();
		if (!dir.exists()) dir.mkdirs();

		isGzip = out.getName().endsWith(".gz");
		
		try
		{
			temp = File.createTempFile("task-", ".tmp", dir);
		}
		catch (IOException e)
		{
			throw new IOException ("Failed to create temp file in " + dir, e);
		}
		
		temp.deleteOnExit();
		
		FileOutputStream rawStream = new FileOutputStream (temp);
		
		if (isGzip)
		{
			gzip = new GZIPOutputStream(rawStream);
			decoratedStream = new BufferedOutputStream (gzip);
		}
		else
		{
			decoratedStream = new BufferedOutputStream (rawStream);
		}
		
		return decoratedStream;

	}
	
	public void close() throws IOException
	{
		decoratedStream.flush();

		// maybe this will do it...
		// http://wondersofcomputing.blogspot.co.uk/2010/02/gzipoutputstream-remember-to-finish.html
		if (gzip != null)
		{
			gzip.finish();
		}
		
		decoratedStream.close();

		if (!temp.renameTo(out)) throw new IOException ("Failure when attempting to rename " + temp + " to " + out);
	}
	
}
