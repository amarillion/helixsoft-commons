package nl.helixsoft.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SplitGzipOutputStream extends OutputStream
{
	private Logger log = LoggerFactory.getLogger("nl.helixsoft.SplitGZip.SplitGzipOutputStream");
			
	private OutputStream os = null;
	private GZIPOutputStream gos = null;
	private final File destDir;
	
	private int next = -1;
	
	private long maxLines;
	private String prefix;
	private String suffix;

	private int partCount = 0;
	private long lineCount = 0;

	public SplitGzipOutputStream(String prefix, String suffix, long maxLines, File destDir) 
	{
		this.prefix = prefix;
		this.suffix = suffix;
		this.maxLines = maxLines;
		this.destDir = destDir;
	}

	@Override
	public void write(int curr) throws IOException 
	{
    	if (closed) throw new IOException ("Stream already closed");
    			
		if (os == null)
		{
			File outFile = new File (destDir, String.format("%s%06d%s", prefix, partCount, suffix));
			log.debug ("Output file: " + outFile); 
			gos = new GZIPOutputStream(new FileOutputStream (outFile));
			os = new BufferedOutputStream (gos);
		}

		os.write(curr);
		
		if (curr == '\n' || (curr == '\r' && next != '\n'))
		{
			lineCount++;
			if (lineCount >= maxLines)
			{
				gos.flush();
				os.flush(); 
				
				gos.finish();
				
				gos.close();
				os.close();
				
				partCount++;
				os = null; // trigger opening in next loop
				gos = null;
				lineCount = 0;
			}
		}
		
		curr = next;
	}

    public void flush() throws IOException 
    {
    	if (closed) throw new IOException ("Stream already closed");
    	
    	gos.flush();
    	os.flush();
    }

    boolean closed = false;
    
    @Override
    public void close() throws IOException 
    {
		if (os != null && gos != null)
		{
			gos.flush();
			os.flush();
			
			gos.finish();
			
			gos.close();
			os.close();
			
			os = null;
			gos = null;
		}
    	closed = true;
    }
    
    @Override
    protected void finalize() throws Throwable 
    {
    	super.finalize();
    	
    	if (!closed) log.error ("SplitGzipOutputStream not closed properly");
    	close();
    }

}