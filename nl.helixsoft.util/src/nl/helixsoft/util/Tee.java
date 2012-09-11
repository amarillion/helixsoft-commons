package nl.helixsoft.util;

import java.io.IOException;
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
public class Tee extends Writer
{
	private final Writer a;
	private final Writer b;
	
	public Tee (Writer a, Writer b)
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
	public void write(char[] arg0, int arg1, int arg2) throws IOException
	{
		try
		{
			a.write(arg0, arg1, arg2);
		}
		finally
		{
			b.write(arg0, arg1, arg2);
		}
	}
	
	
}
