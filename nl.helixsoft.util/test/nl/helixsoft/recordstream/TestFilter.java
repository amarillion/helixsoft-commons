package nl.helixsoft.recordstream;

import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;
import nl.helixsoft.recordstream.Filter.FieldEquals;

public class TestFilter extends TestCase
{
	
	public void testFieldEquals() throws StreamException
	{
		Reader reader = new StringReader (
				"a\tb\n" +
				"1\t2\n" +
				"3\t4\n");
		
		RecordStream rs = new TsvRecordStream(reader).filter (new FieldEquals ("a", "3"));
		
		Record r;
		
		r = rs.getNext();
		assertEquals("4", r.get("b"));
		r = rs.getNext();
		assertNull(r);
	}

}
