package nl.helixsoft.util;

import java.util.Collection;
import java.util.List;

public abstract class HStringUtils 
{
	private HStringUtils() {} // instantiation forbidden

	public static List<String> quotedCommaSplit(String input)
	{
		return StringUtils.quotedSplit(input, '"', ',');
	}
	
	/**
	 * Permissive version of quotedCommaSplit that prints warnings instead of throwing exceptions
	 * in certain cases that don't adhere to the spec but are recoverable.
	 */
	public static List<String> permissiveQuotedCommaSplit(String input)
	{
		return StringUtils.quotedSplit(input, '"', ',', false);
	}

	/**
	 * Concat a prefix and suffix to each element in a list, and join with a separator.
	 * For example, turn the list a b c into {a};{b};{c}
	 * 
	 * Equivalent to the following groovy code:
	 * 
	 * <pre>data.collect{ prefix + it + suffix }.join(sep)</pre>
	 * 
	 * @param sep separator between list elements
	 * @param data list of strings
	 * @param prefix prefix concatenated before each element in the list
	 * @param suffix suffix concatenated after each element in the list 
	 */
	public static String concatAndJoin (String sep, List<String> data, String prefix, String suffix)
	{
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		
		for (String str : data)
		{
			if (first) 
			{
				first = false;
			}
			else
			{
				builder.append(sep);
			}
			
			builder.append (prefix);
			builder.append (str);
			builder.append (suffix);
		}
		
		return builder.toString();
	}
	
	/**
	 * May return null, but will not throw an exception
	 */
	public static Double safeParseDouble(String val)
	{
		if (val == null) return null;
		try
		{
			Double result = Double.parseDouble(val);
			return result;
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}
	
	/**
	 * May return null, but will not throw an exception
	 */
	public static Integer safeParseInt(String val) 
	{
		if (val == null) return null;
		try
		{
			Integer result = Integer.parseInt(val);
			return result;
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}

	/**
	 * May return null, but will not throw an exception
	 */
	public static Long safeParseLong(String val) 
	{
		if (val == null) return null;
		try
		{
			Long result = Long.parseLong(val);
			return result;
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}

	public static String escapeHtml(String s)
	{
		//TODO: replace with apache codec?
		s = s.replaceAll("<", "&lt;");
		s = s.replaceAll(">", "&gt;");
		return s;
	}

	/**
	 * Join collection into a single string, with a separator between.
	 */
	public static String join (String sep, Collection<?> values)
	{
		StringBuilder builder = new StringBuilder();
		join (builder, sep, values);
		return builder.toString();
	}

	/**
	 * Join collection into and append to a StringBuilder, with a separator between.
	 * Useful if you want to join strings and append to an existing StringBuilder.
	 * @param builder StringBuilder you want to append to. This variable will be modified.
	 * @param sep Separator between strings
	 * @param values collection of strings to join.
	 */
	public static void join (StringBuilder builder, String sep, Collection<?> values)
	{
		boolean first = true;
		for (Object o : values)
		{
			if (first)
				first = false;
			else
				builder.append (sep);
			builder.append ("" + o);
		}
	}

	/**
	 * Join an multi value list into a single string, with a separator between.
	 */
	public static <T> void join (StringBuilder builder, String sep, T... values)
	{
		boolean first = true;
		for (Object o : values)
		{
			if (first)
				first = false;
			else
				builder.append (sep);
			builder.append ("" + o);
		}
	}
	
	/**
	 * Join an multi value list into a single string, with a separator between.
	 */
	public static <T> String join (String sep, T... values)
	{
		StringBuilder builder = new StringBuilder();
		join (builder, sep, values);
		return builder.toString();
	}

	/**
	 * If input string starts with a quote character (") AND ends with the same quote character, both a removed.
	 * Otherwise the input is returned unchanged.
	 */
	public static String removeOptionalQuotes(String in)
	{
		if (in == null) return null;
		if (in.startsWith("\"") && in.endsWith("\""))
		{
			return in.substring (1, in.length() - 1);
		}
		else
			return in;
	}

	public static boolean emptyOrNull (String s)
	{
		return (s == null || s.equals (""));
	}

}
