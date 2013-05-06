package nl.helixsoft.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class FileUtils 
{
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
	 */
	public static InputStream openZipStream(File g) throws IOException 
	{
		InputStream is;
		if (g.getName().endsWith(".gz"))
		{
			is = new GZIPInputStream(new FileInputStream(g));
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

}
