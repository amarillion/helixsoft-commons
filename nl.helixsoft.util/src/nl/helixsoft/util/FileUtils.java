package nl.helixsoft.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public abstract class FileUtils 
{
	private FileUtils() { /* no instantiation of util class */ }
	
	//TODO: copied from pathvisio
	/**
	 * Get all files in a directory
	 * @param directory	The directory to get the files from
	 * @param recursive	Whether to include subdirectories or not
	 * @return A list of files in the given directory
	 * @deprecated use HFileUtils.getFiles
	 */
	public static List<File> getFiles(File directory, boolean recursive) 
	{
		return HFileUtils.getFiles(directory, recursive);
	}

	//TODO: copied from pathvisio
	/**
	 * Get all files in a directory
	 * @param directory	The directory to get the files from
	 * @param extension	The extension of the files to get, without the dot so e.g. "gpml"
	 * @param recursive	Whether to include subdirectories or not
	 * @return A list of files with given extension present in the given directory
	 * @deprecated use HFileUtils.getFiles
	 */
	public static List<File> getFiles(File directory, final String extension, boolean recursive) 
	{
		return HFileUtils.getFiles(directory, extension, recursive);
	}

	/**
	 * Open a file as a regular file input stream or a gzip input stream, depending on the extension.
	 * @deprecated use HFileUtils.openZipStream
	 */
	public static InputStream openZipStream(File g) throws IOException 
	{
		return HFileUtils.openZipStream(g);
	}

	/**
	 * @deprecated use HFileUtils.
	 */
	public static String addBeforeExtension(String fname, String string) 
	{
		return HFileUtils.addBeforeExtension(fname, string);
	}
	
	/**
	 * Expand a unix GLOB, such as '~' or '*.java' into a list of files.
	 * only files or directories that actually exist are returned. If there are no matches,
	 * returns an empty list.
	 * 
	 * <p>
	 * TODO only tested on unix, not likely to work well on Windows.
	 * 
	 * @param glob the glob pattern
	 * @param baseDir if the glob is relative, it will be taken relative to this Directory. If it is absolute, this parameter has no effect. 
	 *        If baseDir is null, the current directory will be used.
	 * @return list of files or directories, or an empty list if there are no matches.
	 * @deprecated use HFileUtils.
	 */
	public static List<File> expandGlob (String glob, File baseDir)
	{
		return HFileUtils.expandGlob (glob, baseDir);
	}

	/** 
	 * expand glob relative to current directory
	 * @deprecated use HFileUtils.
	 */
	public static List<File> expandGlob (String glob)
	{
		return HFileUtils.expandGlob (glob);
	}
		
	/**
	 * Get suitable directory to store application data in a cross-platform way.
	 * On *NIX: $HOME
	 * On Windows: %APPDATA%
	 * Then create a subdirectiory in that based on name.
	 * Create it if it doesn't yet exist.
	 * Use a staring period on *NIX, not on windows.
	 * @deprecated use HFileUtils.
	 */
	public static File makeApplicationDir(String name) 
	{
		return HFileUtils.makeApplicationDir(name);
	}

	/**
	 * Get suitable directory to store application data in a cross-platform way.
	 * On *NIX: $HOME
	 * On Windows: %APPDATA%
	 * @deprecated use HFileUtils.
	 */
	public static File getApplicationDir() 
	{
		return HFileUtils.getApplicationDir();
	}

	/**
	 * 
	 * The standard java way of getting the machine name, using InetAddress.getLocalHost().getHostName(), 
	 * could lead to a reverse dns lookup with all kinds of failure modes.
	 * <p>
	 * This method simply wraps that call in a try/catch handler, and returns a reasonable default otherwise   
	 * @deprecated use HFileUtils.
	 */
	public static String safeMachineName()
	{	
		return HFileUtils.safeMachineName();
	}
	
	/**
	 * 
	 * Same, but specify the default if no machine name was specified.   
	 * @deprecated use HFileUtils.
	 */
	public static String safeMachineName(String defaultMachineName)
	{	
		return HFileUtils.safeMachineName(defaultMachineName);
	}

}
