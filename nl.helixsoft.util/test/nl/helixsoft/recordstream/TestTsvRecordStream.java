package nl.helixsoft.recordstream;

import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;

public class TestTsvRecordStream extends TestCase 
{
	public void testNormal() throws StreamException
	{
		Reader reader = new StringReader ("a\tb\tc\n1\t2\t3\n");
		TsvRecordStream rs = TsvRecordStream.open(reader).get();
		
		Record r;
		
		r = rs.getNext();
		assertEquals("1", r.get("a"));
		assertEquals("2", r.get("b"));
		assertEquals("3", r.get("c"));
		r = rs.getNext();
		assertNull(r);
	}

	public void testTooFewCols() throws StreamException
	{
		Reader reader = new StringReader ("a\tb\tc\n1\t2\n");
		TsvRecordStream rs = TsvRecordStream.open(reader).get();
		
		Record r;
		
		r = rs.getNext();
		assertEquals("1", r.get("a"));
		assertEquals("2", r.get("b"));
		assertNull(r.get("c"));
		r = rs.getNext();
		assertNull(r);
	}

	public void testTooManyCols() throws StreamException
	{
		Reader reader = new StringReader ("a\tb\tc\n1\t2\t3\t4\n");
		TsvRecordStream rs = TsvRecordStream.open(reader).get();
		
		Record r;
		
		r = rs.getNext();
		assertEquals("1", r.get("a"));
		assertEquals("2", r.get("b"));
		assertEquals("3", r.get("c"));
		// the fourth value will be ignored. A warning message will be logged here.
		r = rs.getNext();
		assertNull(r);
	}

	public void testComments() throws StreamException
	{
		Reader reader = new StringReader ("#commentline\nx\ty\tz\n1\t2\t3\n");
		TsvRecordStream rs = TsvRecordStream.open(reader).filterComments().get();
		
		Record r;
		
		r = rs.getNext();
		assertEquals("1", r.get("x"));
		assertEquals("2", r.get("y"));
		assertEquals("3", r.get("z"));
		r = rs.getNext();
		assertNull(r);
	}

	public void testComma() throws StreamException
	{
		Reader reader = new StringReader ("\"x\",\"y\",\"z\"\n1,2,3\n\"Hello, World\",\"\"\"Hello World\"\"\",Goodbye World");
		TsvRecordStream rs = TsvRecordStream.open(reader).removeOptionalQuotes().commaSeparated().get();
		
		Record r;
		
		r = rs.getNext();
		assertEquals("1", r.get("x"));
		assertEquals("2", r.get("y"));
		assertEquals("3", r.get("z"));
		r = rs.getNext();
		assertEquals("Hello, World", r.get("x"));
		assertEquals("\"Hello World\"", r.get("y"));
		assertEquals("Goodbye World", r.get("z"));
		r = rs.getNext();
		assertNull(r);
	}

	public void testCustom() throws StreamException
	{
		Reader reader = new StringReader ("x;y;z\n1;2;3\n");
		TsvRecordStream rs = TsvRecordStream.open(reader).customSeparator(";").get();
		
		Record r;
		
		r = rs.getNext();
		assertEquals("1", r.get("x"));
		assertEquals("2", r.get("y"));
		assertEquals("3", r.get("z"));
		r = rs.getNext();
		assertNull(r);
	}

}
