package nl.helixsoft.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Wraps an input stream. Removes characters that are not valid XML
 * <p>
 * Note that this assumes UTF-7 input, it doesn't handle UTF-8 characters. 
 * <p>
 * NB: this is a regular InputStream, not a RecordStream.
 */
public class ValidCharacterStream extends InputStream
{
	InputStream parent;
	
	/**
	 * Filter out invalid XML characters from given parent stream.
	 */
	public ValidCharacterStream (InputStream parent)
	{
		this.parent = parent;
	}
	
	@Override
	public void close() throws IOException 
	{
		parent.close();	
	}

	@Override
	public int read() throws IOException 
	{
		do
		{
			int next = parent.read();
			if (isValidChar (next) || (next == -1))
			{
				return next;
			}
			System.err.println ("Warning: skipping invalid character: " + next);
		}
		while (true);
	}

	private boolean isValidChar (int next) 
	{
		return (
				next == '\t' ||
				next == '\n' ||
				next == '\r' ||
				(next >= 0x20 && next <= 0x7F)
			);
	}
}
