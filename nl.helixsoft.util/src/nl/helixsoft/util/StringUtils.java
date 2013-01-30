package nl.helixsoft.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;

public class StringUtils 
{
	public static boolean emptyOrNull (String s)
	{
		return (s == null || s.equals (""));
	}

	public static String urlEncode(String name) {
		String encoded;
		try {
			encoded = URLEncoder.encode(name, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Programming error: wrong encoding");
		}
		return encoded;
	}

	/**
	 * Join collection into a single string, with a separator between.
	 */
	public static String join (String sep, Collection<?> values)
	{
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (Object o : values)
		{
			if (first)
				first = false;
			else
				builder.append (sep);
			builder.append ("" + o);
		}
		return builder.toString();
	}
}
