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

	/** use {@link #getZStream} or {@link #getNStream}. Added z clarifies that gzip stream will be used if possible.*/
	@Deprecated
	public OutputStream getStream() throws IOException
	{
		return getZStream();
	}

	public OutputStream getStream(boolean _isGzip) throws IOException
	{
		isGzip = _isGzip;
				
		File dir = out.getAbsoluteFile().getParentFile();
		if (!dir.exists()) dir.mkdirs();
		
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
	
	/** 
	 * Create an output stream to write to this temporary file.
	 * If the filename ends with .gz, will automatically create a GzipOutputStream. 
	 */
	public OutputStream getZStream() throws IOException
	{
		return getStream(out.getName().endsWith(".gz"));
	}

	/**
	 * Create an output stream that is not a GzipOutputStream, no matter what the file extension
	 */
	public OutputStream getNStream() throws IOException
	{
		return getStream(false);
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

		if (!temp.renameTo(out))
		{
			String reason = "Unknown failure";
			if (!temp.exists()) reason = "File doesn't exist anymore";
			if (out.exists()) reason = "Target already exists";
			throw new IOException ("Failure when attempting to rename " + temp + " to " + out + ". " + reason);
		}
	}
	
}
