package nl.helixsoft.util;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

public class TestFileUtils extends TestCase
{
	
	public void testGlob()
	{
		File homeDir = new File (System.getProperty("user.home"));
		File rootDir = new File ("/");
		File currentDir = new File (".");
		
		assertTrue (FileUtils.expandGlob("~").contains(homeDir));
		assertTrue (FileUtils.expandGlob("/").contains(rootDir));
		assertTrue (FileUtils.expandGlob("~/..").contains (homeDir.getParentFile()));
		assertTrue (FileUtils.expandGlob(".").contains(currentDir));

		assertEquals (0, FileUtils.expandGlob("./nonexistingfile").size());
		assertEquals (0, FileUtils.expandGlob("nonexistingfile").size());
		assertEquals (0, FileUtils.expandGlob("/nonexistingfile").size());
		assertEquals (0, FileUtils.expandGlob("~/nonexistingfile").size());

		List<File> files;
		files = FileUtils.expandGlob("src/nl/helixsoft/*/Join.java");
		assertTrue (files.contains(new File ("./src/nl/helixsoft/recordstream/Join.java")));

		files = FileUtils.expandGlob("src/??/helixsoft/util/*Utils.*");
		assertTrue (files.contains(new File ("./src/nl/helixsoft/util/ObjectUtils.java")));
		assertTrue (files.contains(new File ("./src/nl/helixsoft/util/FileUtils.java")));
		assertTrue (files.contains(new File ("./src/nl/helixsoft/util/StringUtils.java")));
		assertTrue (files.contains(new File ("./src/nl/helixsoft/util/DebugUtils.java")));
		assertTrue (files.contains(new File ("./src/nl/helixsoft/util/CollectionUtils.java")));

		files = FileUtils.expandGlob("src/nl/helixsoft/recordstream/????.java");
		assertTrue (files.contains(new File ("./src/nl/helixsoft/recordstream/Join.java")));
		assertTrue (files.contains(new File ("./src/nl/helixsoft/recordstream/Melt.java")));
		assertTrue (files.contains(new File ("./src/nl/helixsoft/recordstream/Cast.java")));
	}
	
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