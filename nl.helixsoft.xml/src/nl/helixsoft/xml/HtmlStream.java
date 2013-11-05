package nl.helixsoft.xml;

import java.io.PrintStream;

//TODO: Could be shared with HtmlExporter
public class HtmlStream
{	
	final PrintStream parent;
	
	public HtmlStream (PrintStream parent)
	{
		this.parent = parent;
	}
	
	public void begin(String tag)
	{
		parent.println("<" + tag + ">");
	}

	public void end(String tag)
	{
		parent.println("</" + tag + ">");
	}

	public void println(String string)
	{
		parent.println(string);
	}
	
	public HtmlStream add (Html html)
	{
		parent.print (html.toString());
		return this;
	}

	public void render(Html template, Object[] data)
	{
		Context c = new Context(data);
		template.flush(c);
		
		this.println(c.builder.toString());
	}
	
	public void close() 
	{
		parent.close();
	}
}
