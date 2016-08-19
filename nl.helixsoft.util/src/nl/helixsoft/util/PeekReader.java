package nl.helixsoft.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * A {@link Reader} with buffer and a peek method to just see the next character. Useful in many parser designs.
 * Unlike {@link ParseBuffer} this is a Reader so it doesn't ignore text encodings.
 */
public class PeekReader extends Reader
{
	static final int BUFFER_SIZE = 0x10000;
			
	private final Reader parent;
	private final char[] buf;
	final int size;
	int pos = 0;
	int fill = 0;
	boolean eof = false;
	
	public PeekReader (InputStream parent) throws UnsupportedEncodingException
	{
		this (new InputStreamReader (parent, "UTF-8"), BUFFER_SIZE);
	}

	public PeekReader (InputStream parent, String charsetName) throws UnsupportedEncodingException
	{
		this (new InputStreamReader(parent, charsetName), BUFFER_SIZE);
	}

	public PeekReader (InputStream parent, int size) throws UnsupportedEncodingException
	{
		this (new InputStreamReader(parent, "UTF-8"), size);
	}

	public PeekReader(Reader parent, int size) 
	{
		this.parent = parent;
		buf = new char[size];
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
		int result = buf[pos];
		// check for characters above 127
		if (result < 0) throw new IllegalStateException ("ParseBuffer.peek() does not yet support anything non-ascii!");
		return result;
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

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException 
	{
		if (pos == fill && eof) return -1;
		
		for (int i = 0; i < len; ++i)
		{
			int r = read();
			if (r == -1) return i;
			cbuf[off + i] = (char)r;	
		}
		return len;
	}

	@Override
	public void close() throws IOException {
	}
	
}
