package nl.helixsoft.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Page 
{
	private final File f;
	private final String link;
	
	public Page (File f, String link)
	{
		this.f = f;
		this.link = link;
	}
	
	public HtmlStream asStream() throws FileNotFoundException
	{
		HtmlStream result;
		PrintStream str = new PrintStream (new FileOutputStream (f));

		result = new HtmlStream (str);
		return result; 
	}

	public String getLink() 
	{
		// hack for a name that starts with %2F....
		return link.replaceAll("%2F", "%252F").replaceAll("%3A", "%253A");
//		try {
//			return URLEncoder.encode (link, "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//			throw new RuntimeException("Wrong encoding, programming error");
//		}
	}
	
	public void render(Html template, Object data) throws FileNotFoundException
	{
//		System.out.println ("Rendering " + f.getAbsolutePath());
		if (!f.getParentFile().exists()) 
		{
			f.getParentFile().mkdirs();
		}
		
		Context c = new Context(data);
		template.flush(c);
		
		HtmlStream hs = asStream();
		hs.println(c.builder.toString());
		hs.close();
	}
}
