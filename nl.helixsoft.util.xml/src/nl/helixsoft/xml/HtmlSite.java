package nl.helixsoft.xml;

import java.io.File;

public class HtmlSite
{
	private final File siteDir;
	
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
		siteDir = f;
	}
	
	
	public Page addPage(String name)
	{
		String link = name + ".html";
		File f = new File (siteDir, link);
		return new Page (f, link);
	}
	
}
