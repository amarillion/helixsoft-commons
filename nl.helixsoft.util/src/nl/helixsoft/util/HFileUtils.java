package nl.helixsoft.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public abstract class HFileUtils 
{
	private HFileUtils() { /* no instantiation of util class */ }

	//TODO: copied from pathvisio
	/**
	 * Get all files in a directory
	 * @param directory	The directory to get the files from
	 * @param recursive	Whether to include subdirectories or not
	 * @return A list of files in the given directory
	 */
	public static List<File> getFiles(File directory, boolean recursive) {
		List<File> fileList = new ArrayList<File>();

		if(!directory.isDirectory()) { //File is not a directory, return file itself (if has correct extension)
			fileList.add(directory);
			return fileList;
		}

		//Get all files in this directory
		File[] files = directory.listFiles();

		//Recursively add the files
		for(File f : files)
		{
			if(f.isDirectory())
			{
				if (recursive) fileList.addAll(getFiles(f, true));
			}
			else fileList.add(f);
		}

		return fileList;
	}

	//TODO: copied from pathvisio
	/**
	 * Get all files in a directory
	 * @param directory	The directory to get the files from
	 * @param extension	The extension of the files to get, without the dot so e.g. "gpml"
	 * @param recursive	Whether to include subdirectories or not
	 * @return A list of files with given extension present in the given directory
	 */
	public static List<File> getFiles(File directory, final String extension, boolean recursive) {
		List<File> fileList = new ArrayList<File>();

		if(!directory.isDirectory()) { //File is not a directory, return file itself (if has correct extension)
			if(directory.getName().endsWith("." + extension)) fileList.add(directory);
			return fileList;
		}

		//Get all files in this directory
		File[] files = directory.listFiles(new FileFilter() {
			public boolean accept(File f) {
				return (f.isDirectory() || f.getName().endsWith("." + extension)) ? true : false;
			}
		});

		//Recursively add the files
		for(File f : files)
		{
			if(f.isDirectory())
			{
				if (recursive) fileList.addAll(getFiles(f, extension, true));
			}
			else fileList.add(f);
		}

		return fileList;
	}

	/**
	 * Open a file as a regular file input stream or a gzip input stream, depending on the extension.
	 * @throws IOException 
	 */
	public static InputStream openZipStream(File g) throws IOException 
	{
		InputStream is;
		if (g.getName().endsWith(".gz"))
		{
			is = new GZIPInputStream(new FileInputStream(g));
		}
		else if (g.getName().endsWith(".xz"))
		{
			// optional dependency on xz 
			try {
				Constructor<?> cl =  
					Class.forName("org.tukaani.xz.XZInputStream").getConstructor(InputStream.class);
				is = (InputStream)cl.newInstance(new FileInputStream(g));
			} catch (NoSuchMethodException | SecurityException | ClassNotFoundException | 
					InstantiationException | IllegalAccessException | IllegalArgumentException | 
					InvocationTargetException e) 
			{	
				throw new IOException ("Could not create an XZInputStream", e);
			}
			
		}
		else
		{
			is = new FileInputStream(g);
		}
		return is;
	}

	public static String addBeforeExtension(String fname, String string) 
	{
		int last = fname.lastIndexOf('.');
		if (last >= 0)
		{
			return fname.substring (0, last) + string + fname.substring (last);
		}
		else
		{
			return fname + string;
		}
	}

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
	 */
	public static List<File> expandGlob (String glob, File baseDir)
	{
		List<File> result = new ArrayList<File>();
		
		File base;
		if (glob.startsWith ("/"))
		{
			base = new File ("/");
			glob = glob.substring(1);
		}
		else if (glob.equals ("~") || glob.startsWith ("~/"))
		{
			base = new File (System.getProperty("user.home"));
			glob = glob.substring(1);			
		}
		else
		{
			// relative - use supplied base Directory (if it isn't null)
			base = baseDir == null ? new File(".") : baseDir;
		}
		
		result.addAll (expandGlobHelper (glob, base));
		return result;
	}

	/** expand glob relative to current directory */
	public static List<File> expandGlob (String glob)
	{
		return expandGlob (glob, new File("."));
	}

	/**
	 * Helper for expandGlob.
	 * <br>
	 * Tests if a given file name matches a given glob pattern with * or ?.
	 * Does not handle directories.
	 */
	private static boolean matches(String text, String glob) 
	{
	    String rest = null;
	    int pos = glob.indexOf('*');
	    if (pos != -1) {
	        rest = glob.substring(pos + 1);
	        glob = glob.substring(0, pos);
	    }

	    if (glob.length() > text.length())
	        return false;

	    // handle the part up to the first *
	    for (int i = 0; i < glob.length(); i++)
	        if (glob.charAt(i) != '?' 
	                && !glob.substring(i, i + 1).equalsIgnoreCase(text.substring(i, i + 1)))
	            return false;

	    // recurse for the part after the first *, if any
	    if (rest == null) {
	        return glob.length() == text.length();
	    } else {
	        for (int i = glob.length(); i <= text.length(); i++) {
	            if (matches(text.substring(i), rest))
	                return true;
	        }
	        return false;
	    }
	}
	
	/**
	 * Helper for expandGlob.
	 * <br>
	 * get a list of files matching a glob in a directory. Does not traverse directories. 
	 */
	private static List<File> getLocalMatches (String remain, File base)
	{
		List<File> result = new ArrayList<File>();
		if (remain.equals ("."))
		{
			result.add (base);
		}
		else if (remain.equals (".."))
		{
			result.add (base.getAbsoluteFile().getParentFile());
		}
		else if (remain.contains("*") || remain.contains ("?"))
		{			
			// look for directories in base matching
			for (File child : base.listFiles())
			{
				if (matches (child.getName(), remain))
				{
					result.add (child);
				}
			}
		}
		else
		{
			File f = new File (base, remain);
			if (f.exists()) result.add (f);
		}
		return result;
	}
	
	private static List<File> expandGlobHelper (String glob, File base)
	{
		// deal with sequences of slashes...
		while (glob.startsWith ("/"))
		{
			glob = glob.substring (1);
		}
		
		// if nothing remains, then we only select the current directory.
		if (glob.equals (""))
		{
			List<File> result = new ArrayList<File>();
			result.add (base);
		}
		
		int pos = glob.indexOf ('/');
		
		if (pos >= 0)
		{
			List<File> result = new ArrayList<File>();
			
			String dir = glob.substring (0, pos);
			glob = glob.substring (pos);
			for (File f: getLocalMatches (dir, base))
			{
				if (f.isDirectory())
				{
					result.addAll (expandGlobHelper(glob, f));
				}				
			}
			return result;
		}
		else
		{
			return getLocalMatches(glob, base);
		}
	}

	/**
	 * Get suitable directory to store application data in a cross-platform way.
	 * On *NIX: $HOME
	 * On Windows: %APPDATA%
	 * Then create a subdirectiory in that based on name.
	 * Create it if it doesn't yet exist.
	 * Use a staring period on *NIX, not on windows.
	 */
	public static File makeApplicationDir(String name) 
	{
		File dirApplication;
		if (!StringUtils.isFileNameSafe(name))
		{
			throw new IllegalArgumentException (name + " contains illegal characters for a filename");
		}
		
		//Windows specific directory configuration
		String os = System.getProperty("os.name");
		if	(os.startsWith("Win"))
		{
			dirApplication = new File(System.getenv("APPDATA"), name);
		} 
		else 
		{ 
			//All other OS
			dirApplication = new File(System.getProperty("user.home"), "." + name);
		}
		if (!dirApplication.exists()) dirApplication.mkdir();
		return dirApplication;
	}

	/**
	 * Get suitable directory to store application data in a cross-platform way.
	 * On *NIX: $HOME
	 * On Windows: %APPDATA%
	 */
	public static File getApplicationDir() 
	{
		File dirApplication = null;
		
		String os = System.getProperty("os.name");
		if	(os.startsWith("Win"))
		{
			dirApplication = new File(System.getenv("APPDATA"));
		} 
		else 
		{ //All other OS
			dirApplication = new File(System.getProperty("user.home"));
		}
		return dirApplication;
	}

	/**
	 * 
	 * The standard java way of getting the machine name, using InetAddress.getLocalHost().getHostName(), 
	 * could lead to a reverse dns lookup with all kinds of failure modes.
	 * <p>
	 * This method simply wraps that call in a try/catch handler, and returns a reasonable default otherwise   
	 */
	public static String safeMachineName()
	{	
		return safeMachineName("could not determine machine name");
	}
	
	/**
	 * 
	 * Same, but specify the default if no machine name was specified.   
	 */
	public static String safeMachineName(String defaultMachineName)
	{	
		String machineName;
	    try
		{
			machineName = InetAddress.getLocalHost().getHostName();
		}
		catch (java.net.UnknownHostException e)
		{
			machineName = defaultMachineName;
		}
	    return machineName;
	}

}
