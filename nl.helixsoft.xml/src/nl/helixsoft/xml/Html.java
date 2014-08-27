package nl.helixsoft.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Html implements HtmlRenderable
{
	/** Color constants */
	public static String RED = "#FF0000";
	public static String WHITE = "#FFFFFF";
	public static String BLUE = "#0000FF";
	public static String YELLOW = "#FFFF00";
	public static String BLACK = "#000000";
	public static String GREY = "#AAAAAA";
	public static String GREEN = "#00FF00";
	public static String CYAN = "#FF00FF";
	
	private final String tag;
	private List<String> attrKey = new ArrayList<String>();
	private List<Object> attrVal = new ArrayList<Object>();
	private List<Object> contents = new ArrayList<Object>();

	private Html(String tag)
	{
		this.tag = tag;
	}

	public static Html tag(String tag, Object... contents)
	{
		Html result = new Html(tag);
		if (contents != null) 
			for (Object o : contents)
			{
				result.contents.add(o);
			}
		return result;
	}

	public static Html h1 (Object... os)
	{
		return tag ("h1", os);
	}

	public static Html html (Object... os)
	{
		return tag ("html", os);
	}

	public static Html body (Object... os)
	{
		return tag ("body", os);
	}

	public static Html h2 (Object... os)
	{
		return tag ("h2", os);
	}

	public static Html h3 (Object... os)
	{
		return tag ("h3", os);
	}

	public static Html p (Object... os)
	{
		return tag ("p", os);
	}

	public static Html div (Object... os)
	{
		return tag ("div", os);
	}

	public static Html a (Object... os)
	{
		return tag ("a", os);
	}

	public static Html table (Object... os)
	{
		return tag ("table", os);
	}

	public static Html ul(Object... os)
	{
		return tag ("ul", os);
	}

	public static Html ol(Object... os)
	{
		return tag ("ol", os);
	}

	public static Html img(Object... os)
	{
		return tag ("img", os);
	}

	public static Html li(Object... os)
	{
		return tag ("li", os);
	}

	public static Html b(Object... os)
	{
		return tag ("b", os);
	}

	public static Html i(Object... os)
	{
		return tag ("i", os);
	}

	public static String br()
	{
		return "</br>";
	}

	public static Html tr(Object[] data)
	{
		Html result = new Html ("tr");
		for (Object cell : data)
		{
			result.addChild (Html.tag("td", cell));
		}
		return result;
	}

	public Html attr(String key, Object value)
	{
		attrKey.add(key);
		attrVal.add(value);
		return this;
	}

	public Html href(Object value)
	{
		return attr ("href", value);
	}

	public Html href(Page p)
	{
		return attr ("href", p.getLink());
	}

	public Html id(String value)
	{
		return attr ("id", value);
	}

	public Html style(String value)
	{
		return attr ("style", value);
	}
	
	public Html border(String value)
	{
		return attr ("border", value);
	}

	@Override
	public String toString()
	{
		Context c = new Context();
		flush (c);
		return c.builder.toString();
	}

	public Html addChild (Object... os)
	{
		for (Object o : os)
		{
			contents.add(o);
		}
		return this;
	}

	public static Html title(Object... os) 
	{
		return tag ("title", os);
	}

	public static Html head(Object... os) 
	{
		return tag ("head", os);
	}

	public static Html td(Object... os) 
	{
		return tag ("td", os);
	}

	public void flush (Context c)
	{
		c.builder.append ("<");
		c.builder.append (tag);

		if(attrKey.size() > 0) 
		{
			for(int i = 0; i < attrKey.size(); i++) 
			{
				c.builder.append(" ");
				c.builder.append(attrKey.get(i));
				c.builder.append("=\"");
				c.render (attrVal.get(i));
				c.builder.append('"');
			}
		}

		if (contents.isEmpty())
		{
			c.builder.append ("/>\n");
		}
		else
		{
			c.builder.append (">\n");
	
			for (Object o : contents)
			{
				c.render(o);
			}
	
			c.builder.append ("</");
			c.builder.append (tag);
			c.builder.append (">\n");
		}
	}

	public static Html th(Object[] data) 
	{
		Html result = new Html ("tr");
		for (Object cell : data)
		{
			result.addChild (Html.tag("th", cell));
		}
		return result;
	}

	private static Random random = new Random();
	
	/**
	 * Create a div that invokes the switchit function.
	 * If you make use of this, make sure you include collapseScript in head.
	 */
	public static Html collapseDiv (Object title, Object contents)
	{
		String id = "item" + random.nextInt(Integer.MAX_VALUE);

		return Html.div (
				Html.div().attr("class", "switchit_toggle").addChild(Html.a(title).href("javascript:switchit('" + id + "')")),
				Html.div().id(id).style("display: none").attr ("class", "switchit_detail").addChild(contents)
			);
	}

	public static Html collapseScript()
	{
		return Html.tag("script",			
				"function switchit(list) " +
				"{ " +
				"  var listElementStyle = document.getElementById(list).style;" +
				"  if (listElementStyle.display == 'none') " +
				"  {" +
				"    listElementStyle.display = 'block';" +
				"  } else {" +
				"    listElementStyle.display = 'none';" +
				"  }" +
				"}");
	}

}
