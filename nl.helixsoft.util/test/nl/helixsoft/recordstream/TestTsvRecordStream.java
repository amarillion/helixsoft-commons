package nl.helixsoft.recordstream;

import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;

public class TestTsvRecordStream extends TestCase 
{
	public void testNormal() throws RecordStreamException
	{
		Reader reader = new StringReader ("a\tb\tc\n1\t2\t3\n");
		TsvRecordStream rs = new TsvRecordStream(reader);
		
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
		TsvRecordStream rs = new TsvRecordStream(reader);
		
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
		TsvRecordStream rs = new TsvRecordStream(reader);
		
		Record r;
		
		r = rs.getNext();
		assertEquals("1", r.getValue("a"));
		assertEquals("2", r.getValue("b"));
		assertEquals("3", r.getValue("c"));
		// the fourth value will be ignored. A warning message will be logged here.
		r = rs.getNext();
		assertNull(r);
	}

}
