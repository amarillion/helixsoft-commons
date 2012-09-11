package nl.helixsoft.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class HtmlSite
{
	File siteDir;
	
	public HtmlSite (File f)
	{
		if (!f.exists())
		{
			f.mkdirs();
		}
		else if (!f.isDirectory())
		{
			throw new IllegalArgumentException("Must pass a directory to HtmlSite");
		}
	}
	
	public HtmlStream addPage(String name) throws FileNotFoundException
	{
		HtmlStream result;
		File f = new File (siteDir, name + ".html");
		PrintStream str = new PrintStream (new FileOutputStream (f));
		result = new HtmlStream (str);
		return result;
	}
	
}
