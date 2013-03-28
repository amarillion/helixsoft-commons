package nl.helixsoft.util;

import java.io.IOException;
import java.io.StringBufferInputStream;

import junit.framework.TestCase;

public class TestParseBuffer extends TestCase
{
	public void testRead() throws IOException
	{
		StringBufferInputStream is = new StringBufferInputStream("Almost, but not quite, entirely unlike tea.");
		
		ParseBuffer pb = new ParseBuffer(is, 4);
		
		assertEquals (0, pb.getPos());
		assertEquals (0, pb.getFill());
		
		assertEquals ('A', pb.peek());
		assertEquals ('A', pb.read());

		assertEquals (1, pb.getPos());
		assertEquals (2, pb.getFill());

		assertEquals ('l', pb.peek());
		assertEquals ('l', pb.read());
		
		assertEquals (2, pb.getPos());
		assertEquals (2, pb.getFill());

		assertEquals ('m', pb.peek());
		assertEquals ('m', pb.read());
		
		assertEquals (3, pb.getPos());
		assertEquals (0, pb.getFill());

		assertEquals ('o', pb.peek());
		assertEquals ('o', pb.read());
		
		assertEquals (0, pb.getPos());
		assertEquals (0, pb.getFill());

		assertEquals ('s', pb.peek());
		assertEquals ('s', pb.read());
		
		assertEquals (1, pb.getPos());
		assertEquals (2, pb.getFill());
	}

	public void testReadMulti() throws IOException
	{
		StringBufferInputStream is = new StringBufferInputStream("So long, and thanks for all the fish!");		
		ParseBuffer pb = new ParseBuffer(is, 4);

		byte[] buf = new byte[10];
		pb.read(buf);
		assertEquals ("So long, a", new String(buf));
	}

	public void testEofMid() throws IOException
	{
		StringBufferInputStream is = new StringBufferInputStream("42");		
		ParseBuffer pb = new ParseBuffer(is, 4);

		assertEquals ('4', pb.read());
		assertEquals ('2', pb.read());
		assertEquals (2, pb.getFill());
		assertEquals (-1, pb.read());
		assertEquals (-1, pb.read());
	}

	public void testEof() throws IOException
	{
		StringBufferInputStream is = new StringBufferInputStream("Pan");		
		ParseBuffer pb = new ParseBuffer(is, 4);

		assertEquals ('P', pb.read());
		assertEquals ('a', pb.read());
		assertEquals ('n', pb.read());
		assertEquals (3, pb.getFill());
		assertEquals (-1, pb.read());
		assertEquals (-1, pb.read());
	}

	public void testEofBoundary() throws IOException
	{
		StringBufferInputStream is = new StringBufferInputStream("Ford");		
		ParseBuffer pb = new ParseBuffer(is, 4);

		assertEquals ('F', pb.read());
		assertEquals ('o', pb.read());
		assertEquals ('r', pb.read());
		assertEquals ('d', pb.read());
		assertEquals (-1, pb.read());
		assertEquals (-1, pb.read());
	}

	public void testSubstring() throws IOException
	{		
		StringBufferInputStream is = new StringBufferInputStream("The question of life, the universe, and everything.");
		
		ParseBuffer pb = new ParseBuffer(is, 4);
		
		int start = pb.getPos();
		pb.read();
		pb.read();
		int end = pb.getPos();
		pb.read();
		
		assertEquals (0, start);
		assertEquals (2, end);
		assertEquals ("Th", pb.subString(start, end));
		
		start = pb.getPos();
		
		pb.read();
		pb.read();
		end = pb.getPos();
		
		assertEquals (3, start);
		assertEquals (1, end);
		assertEquals (" q", pb.subString(start, end));

	}
	
}
