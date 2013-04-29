package nl.helixsoft.util;

import junit.framework.TestCase;

public class TestFileUtils extends TestCase
{
	public void testAddBeforeExtension()
	{
		assertEquals (
				"base-debug.txt",
				FileUtils.addBeforeExtension("base.txt", "-debug")
				);
		assertEquals (
				"base-debug",
				FileUtils.addBeforeExtension("base", "-debug")
				);
		assertEquals (
				"base.name-debug.txt",
				FileUtils.addBeforeExtension("base.name.txt", "-debug")
				);
		assertEquals (
				"-debug.txt",
				FileUtils.addBeforeExtension(".txt", "-debug")
				);
		assertEquals (
				"/path/to/base-debug.txt",
				FileUtils.addBeforeExtension("/path/to/base.txt", "-debug")
				);
		assertEquals (
				"C:\\path\\to\\base-debug.txt",
				FileUtils.addBeforeExtension("C:\\path\\to\\base.txt", "-debug")
				);
	}
	
}