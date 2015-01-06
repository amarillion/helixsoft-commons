package nl.helixsoft.util;

import java.util.Arrays;

import junit.framework.TestCase;

public class TestStringUtils extends TestCase 
{
	public void testFilenameSafe()
	{
		assertFalse (StringUtils.isFileNameSafe("hello?.txt"));
		assertTrue (StringUtils.isFileNameSafe("hello.txt"));	
	}
	
	public void testStripTags()
	{
		assertEquals ("abc", StringUtils.stripTags("abc"));
		assertEquals ("abc", StringUtils.stripTags("<b>abc</b>"));
		assertEquals ("abc", StringUtils.stripTags("<a href=\"xyz\">abc</a>"));
		assertEquals ("abc", StringUtils.stripTags("abc"));
	}
	
	public void testDecodeEntities()
	{
		assertEquals ("ab\u00cfc", StringUtils.decodeEntities("ab&#x00cf;c"));
		assertEquals ("ab\u00ebc", StringUtils.decodeEntities("ab&#0235;c"));
	}
	
	public void testCamelCase()
	{
		assertEquals ("SmallMolecule", StringUtils.toCamelCase("Small molecule"));		
		assertEquals ("ShowMeYourID!", StringUtils.toCamelCase("Show me your ID!"));
		assertEquals ("TwoSpaces", StringUtils.toCamelCase("Two  spaces"));
		assertEquals ("Surrounded", StringUtils.toCamelCase(" surrounded "));
	}
	
	public void testQuotedSplit()
	{
		assertEquals (
				Arrays.asList(new String[] { "Run", "Sample Characteristics[Organism]" }),
				StringUtils.quotedSplit ("\"Run\"\t\"Sample Characteristics[Organism]\"", '"', '\t')
			);
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

		assertEquals (
				Arrays.asList(new String[] { "quote-at-end\"", "\"quote-at-start", "quote\"in\"middle"}),
				StringUtils.quotedCommaSplit("\"quote-at-end\"\"\", \"\"\"quote-at-start\", \"quote\"\"in\"\"middle\"")
			);

		assertEquals (
				Arrays.asList(new String[] { "new\nline", "within", "quotes"}),
				StringUtils.quotedCommaSplit("\"new\nline\", within, quotes")
			);				

		try {
			StringUtils.quotedCommaSplit("unbalanced\"quote");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// expected exception
		}
		
		try {
			StringUtils.quotedCommaSplit("\"quote not closed");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// expected exception
		}

	}

}
