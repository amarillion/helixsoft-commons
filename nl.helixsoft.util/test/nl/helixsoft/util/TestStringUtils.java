package nl.helixsoft.util;

import java.util.Arrays;
import java.util.List;

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
	
	public void testQuotedCommaSplit()
	{
		assertEquals (
			Arrays.asList(new String[] { "a", "b", "c" }),
			StringUtils.quotedCommaSplit("a, b, c")
		);
		assertEquals (
				Arrays.asList(new String[] { "a", "b", "c"}),
				StringUtils.quotedCommaSplit("a,b,c")
			);
		assertEquals (
				Arrays.asList(new String[] { "a", "", "b", "", "c", "", "d"}),
				StringUtils.quotedCommaSplit("a,,b, , c, ,d")
			);
		assertEquals (
				Arrays.asList(new String[] { "a", "b", "", "c"}),
				StringUtils.quotedCommaSplit("a, b,,c")
			);
		
		assertEquals (
				Arrays.asList(new String[] { "OsAMT1;1", "OsAMT1:1", "OsAMT1,1" }),
				StringUtils.quotedCommaSplit("OsAMT1;1, OsAMT1:1, \"OsAMT1,1\"")
			);
		
		assertEquals (
				Arrays.asList(new String[] { "OsAMT1;1", "OsAMT1,1", "OsAMT1:1" }),
				StringUtils.quotedCommaSplit("OsAMT1;1, \"OsAMT1,1\", OsAMT1:1")
			);

		
		//NOTE: unquoted trailing whitespace is not removed. Not sure if that should be considered a feature or a bug.
		assertEquals (
				Arrays.asList(new String[] { "a ", "b ", "c" }),
				StringUtils.quotedCommaSplit("a ,b ,c")
			);

	}

}
