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
		
		assertTrue (HFileUtils.expandGlob("~").contains(homeDir));
		assertTrue (HFileUtils.expandGlob("/").contains(rootDir));
		assertTrue (HFileUtils.expandGlob("~/..").contains (new File(homeDir, "..")));
		assertTrue (HFileUtils.expandGlob(".").contains(currentDir));

		assertEquals (0, HFileUtils.expandGlob("./nonexistingfile").size());
		assertEquals (0, HFileUtils.expandGlob("nonexistingfile").size());
		assertEquals (0, HFileUtils.expandGlob("/nonexistingfile").size());
		assertEquals (0, HFileUtils.expandGlob("~/nonexistingfile").size());

		List<File> files;
		files = HFileUtils.expandGlob("src/nl/helixsoft/*/Join.java");
		assertTrue (files.contains(new File ("./src/nl/helixsoft/recordstream/Join.java")));

		files = HFileUtils.expandGlob("src/??/helixsoft/util/*Utils.*");
		assertTrue (files.contains(new File ("./src/nl/helixsoft/util/ObjectUtils.java")));
		assertTrue (files.contains(new File ("./src/nl/helixsoft/util/FileUtils.java")));
		assertTrue (files.contains(new File ("./src/nl/helixsoft/util/StringUtils.java")));
		assertTrue (files.contains(new File ("./src/nl/helixsoft/util/DebugUtils.java")));
		assertTrue (files.contains(new File ("./src/nl/helixsoft/util/CollectionUtils.java")));

		files = HFileUtils.expandGlob("src/nl/helixsoft/recordstream/????.java");
		assertTrue (files.contains(new File ("./src/nl/helixsoft/recordstream/Join.java")));
		assertTrue (files.contains(new File ("./src/nl/helixsoft/recordstream/Melt.java")));
		assertTrue (files.contains(new File ("./src/nl/helixsoft/recordstream/Cast.java")));

		files = HFileUtils.expandGlob("../nl.helixsoft.util/src/nl/helixsoft/recordstream/????.java");
		assertTrue (files.contains(new File ("./../nl.helixsoft.util/src/nl/helixsoft/recordstream/Join.java")));

	}
	
	public void testAddBeforeExtension()
	{
		assertEquals (
				"base-debug.txt",
				HFileUtils.addBeforeExtension("base.txt", "-debug")
				);
		assertEquals (
				"base-debug",
				HFileUtils.addBeforeExtension("base", "-debug")
				);
		assertEquals (
				"base.name-debug.txt",
				HFileUtils.addBeforeExtension("base.name.txt", "-debug")
				);
		assertEquals (
				"-debug.txt",
				HFileUtils.addBeforeExtension(".txt", "-debug")
				);
		assertEquals (
				"/path/to/base-debug.txt",
				HFileUtils.addBeforeExtension("/path/to/base.txt", "-debug")
				);
		assertEquals (
				"C:\\path\\to\\base-debug.txt",
				HFileUtils.addBeforeExtension("C:\\path\\to\\base.txt", "-debug")
				);
	}
	
}