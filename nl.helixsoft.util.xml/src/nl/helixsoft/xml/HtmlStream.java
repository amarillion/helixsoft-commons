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
	
	public void add (Html html)
	{
		parent.print (html.toString());
	}
}
