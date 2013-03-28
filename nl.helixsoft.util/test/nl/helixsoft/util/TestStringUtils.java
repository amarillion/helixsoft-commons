package nl.helixsoft.util;

import junit.framework.TestCase;

public class TestStringUtils extends TestCase 
{
	public void testStripTags()
	{
		assertEquals ("abc", StringUtils.stripTags("abc"));
		assertEquals ("abc", StringUtils.stripTags("<b>abc</b>"));
		assertEquals ("abc", StringUtils.stripTags("<a href=\"xyz\">abc</a>"));
		assertEquals ("abc", StringUtils.stripTags("abc"));
	}

}
