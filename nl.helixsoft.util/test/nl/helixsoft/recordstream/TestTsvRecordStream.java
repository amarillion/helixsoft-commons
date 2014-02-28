package nl.helixsoft.recordstream;

import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;

public class TestTsvRecordStream extends TestCase 
{
	public void testNormal() throws RecordStreamException
	{
		Reader reader = new StringReader ("a\tb\tc\n1\t2\t3\n");
		TsvRecordStream rs = TsvRecordStream.open(reader).get();
		
		Record r;
		
		r = rs.getNext();
		assertEquals("1", r.getValue("a"));
		assertEquals("2", r.getValue("b"));
		assertEquals("3", r.getValue("c"));
		r = rs.getNext();
		assertNull(r);
	}

	public void testTooFewCols() throws RecordStreamException
	{
		Reader reader = new StringReader ("a\tb\tc\n1\t2\n");
		TsvRecordStream rs = TsvRecordStream.open(reader).get();
		
		Record r;
		
		r = rs.getNext();
		assertEquals("1", r.getValue("a"));
		assertEquals("2", r.getValue("b"));
		assertNull(r.getValue("c"));
		r = rs.getNext();
		assertNull(r);
	}

	public void testTooManyCols() throws RecordStreamException
	{
		Reader reader = new StringReader ("a\tb\tc\n1\t2\t3\t4\n");
		TsvRecordStream rs = TsvRecordStream.open(reader).get();
		
		Record r;
		
		r = rs.getNext();
		assertEquals("1", r.getValue("a"));
		assertEquals("2", r.getValue("b"));
		assertEquals("3", r.getValue("c"));
		// the fourth value will be ignored. A warning message will be logged here.
		r = rs.getNext();
		assertNull(r);
	}

	public void testComments() throws RecordStreamException
	{
		Reader reader = new StringReader ("#commentline\nx\ty\tz\n1\t2\t3\n");
		TsvRecordStream rs = TsvRecordStream.open(reader).filterComments().get();
		
		Record r;
		
		r = rs.getNext();
		assertEquals("1", r.getValue("x"));
		assertEquals("2", r.getValue("y"));
		assertEquals("3", r.getValue("z"));
		r = rs.getNext();
		assertNull(r);
	}

	public void testComma() throws RecordStreamException
	{
		Reader reader = new StringReader ("\"x\",\"y\",\"z\"\n1,2,3\n");
		TsvRecordStream rs = TsvRecordStream.open(reader).removeOptionalQuotes().commaSeparated().get();
		
		Record r;
		
		r = rs.getNext();
		assertEquals("1", r.getValue("x"));
		assertEquals("2", r.getValue("y"));
		assertEquals("3", r.getValue("z"));
		r = rs.getNext();
		assertNull(r);
	}

	public void testCustom() throws RecordStreamException
	{
		Reader reader = new StringReader ("x;y;z\n1;2;3\n");
		TsvRecordStream rs = TsvRecordStream.open(reader).customSeparator(";").get();
		
		Record r;
		
		r = rs.getNext();
		assertEquals("1", r.getValue("x"));
		assertEquals("2", r.getValue("y"));
		assertEquals("3", r.getValue("z"));
		r = rs.getNext();
		assertNull(r);
	}

}
