package nl.helixsoft.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

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

	
}
