package nl.helixsoft.util;

import java.io.IOException;
import java.io.InputStream;

/** A ring input stream buffer */
public class ParseBuffer extends InputStream
{
	static final int BUFFER_SIZE = 0x10000;
			
	private final InputStream parent;
	private final byte[] buf;
	final int size;
	int pos = 0;
	int fill = 0;
	boolean eof = false;
	
	public ParseBuffer (InputStream parent)
	{
		this (parent, BUFFER_SIZE);
	}

	public ParseBuffer(InputStream parent, int size) 
	{
		this.parent = parent;
		buf = new byte[size];
		this.size = size;
	}

	@Override
	public int read() throws IOException 
	{
		int result = peek();
		if (result < 0) return result;
		pos++;
		pos %= size;
		return result;
	}

	public int peek() throws IOException 
	{
		if (pos == fill)
		{
			if (eof) return -1;
			fillNext();
			if (pos == fill && eof) return -1;
		}
		return buf[pos];
	}
	
	private void fillNext() throws IOException
	{
		int end = Math.min (size, pos + (size / 2));
		int delta = end - pos;
		int result = parent.read(buf, fill, delta);
		if (result < 0)
		{
			eof = true;
			return;
		}
		fill += result;
		fill %= size;
	}
	
	public int getPos()
	{
		return pos;
	}
	
	public String subString(int start, int end)
	{
		String result;
		if (start < end)
			result = new String(buf, start, end - start);
		else
		{
			StringBuilder builder = new StringBuilder(size + end - start);
			builder.append (new String(buf, start, (size - start)));
			builder.append (new String(buf, 0, end));
			result = builder.toString();
		}
		return result;
	}
	
	// for testing
	int getFill()
	{
		return fill;
	}
	
}
