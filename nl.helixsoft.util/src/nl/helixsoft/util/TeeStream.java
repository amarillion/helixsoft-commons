package nl.helixsoft.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * A writer that writes to two streams at the same time.
 * 
 * Usage example:
 * 
 * <code>
 * fout = new FileWriter (new File ("outfile.txt"));
 * writer = new PrintWriter (new Tee (fout, System.out));
 * writer.println ("Hello world");
 * </code>
 */
public class TeeStream extends OutputStream
{
	private final OutputStream a;
	private final OutputStream b;
	
	public TeeStream (OutputStream a, OutputStream b)
	{
		this.a = a;
		this.b = b;
	}

	@Override
	public void close() throws IOException
	{
		try
		{
			a.close();
		}
		finally
		{
			b.close();
		}
	}

	@Override
	public void flush() throws IOException
	{
		try
		{
			a.flush();
		}
		finally
		{
			b.flush();
		}
	}

	@Override
	public void write(int arg0) throws IOException 
	{
		try
		{
			a.write(arg0);
		}
		finally
		{
			b.write(arg0);
		}
	}
	
}
