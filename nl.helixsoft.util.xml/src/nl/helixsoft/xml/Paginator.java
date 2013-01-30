package nl.helixsoft.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class Paginator 
{
	private List<?> data;
	private int itemsPerPage;
	private String basename;
	
	public class PaginatorPage
	{
		private final int pageno;
		
		public PaginatorPage (int i)
		{
			pageno = i;
		}
		
		private String getName(int i)
		{
			if (i == 0)
			{
				return basename + ".html";
			}
			else
				return basename + i + ".html";
			
		}
		
		public String getPage()
		{
			return getName(pageno);
		}
		
		public String getNextPage()
		{
			return getName (pageno + 1);
		}
		
		public String getPrevPage()
		{
			if (pageno == 0)
				return null;
			else return getName(pageno - 1);
		}
		
		public int getPageNo()
		{
			return pageno + 1;
		}
		
		public List<?> getPageItems()
		{
			int end = Math.min (((pageno + 1) * itemsPerPage) - 1, data.size());
			return data.subList(pageno * itemsPerPage, end);
		}
	}
	
	private File baseDir;
	
	public Paginator(File basedir, String name, List<?> data, int itemsPerPage)
	{
		this.data = data;
		this.itemsPerPage = itemsPerPage;
		this.basename = name;
		this.baseDir = basedir;
	}
	
	public void flush (Html template) throws FileNotFoundException
	{
		int page = 0;
		for (int i = 0; i < data.size(); i += itemsPerPage)
		{
			PaginatorPage x = new PaginatorPage(page);
			page++;
			
			Page p = new Page (new File (baseDir, x.getPage()), x.getPage());
			p.render(template, x);
		}
	}
	
}
