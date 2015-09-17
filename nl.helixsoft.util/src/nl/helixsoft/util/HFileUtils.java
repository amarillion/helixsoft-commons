package nl.helixsoft.util;

import java.io.File;

public class HFileUtils {

	public static boolean isBelowDirectory(File ancestorDir, File child)
	{
		File current = child.getAbsoluteFile();
		while (current.getParentFile() != null)
		{
			if (current.equals (ancestorDir.getAbsoluteFile()))
				return true;
			current = current.getParentFile();
		}
		return false;
	}

}
