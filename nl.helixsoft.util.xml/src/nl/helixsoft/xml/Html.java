package nl.helixsoft.xml;

import java.util.ArrayList;
import java.util.List;

public class Html
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
	private List<String> attrVal = new ArrayList<String>();
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

	public Html attr(String key, String value)
	{
		attrKey.add(key);
		attrVal.add(value);
		return this;
	}

	public Html href(String value)
	{
		return attr ("href", value);
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
		StringBuilder builder = new StringBuilder();
		builder.append ("<");
		builder.append (tag);

		if(attrKey.size() > 0) 
		{
			for(int i = 0; i < attrKey.size(); i++) 
			{
				builder.append(" ");
				builder.append(attrKey.get(i));
				builder.append("=\"");
				builder.append(attrVal.get(i));
				builder.append('"');
			}
		}

		builder.append (">\n");

		for (Object o : contents)
		{
			builder.append ("" + o);
		}

		builder.append ("</");
		builder.append (tag);
		builder.append (">\n");

		return builder.toString();
	}

	public Html addChild (Object... os)
	{
		for (Object o : os)
		{
			contents.add(o);
		}
		return this;
	}

}
