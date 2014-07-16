package nl.helixsoft.util;

import junit.framework.TestCase;

public class TestDebugUtils extends TestCase 
{
	public void testTestNull()
	{
		try
		{
			DebugUtils.testNull("arg1", null);
			fail ("Expected NullPointerException");
		}
		catch (NullPointerException e)
		{
			assertEquals ("testTestNull", e.getStackTrace()[0].getMethodName());
			// pass
		}
	}
	

}
