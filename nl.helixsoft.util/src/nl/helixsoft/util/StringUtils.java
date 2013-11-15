package nl.helixsoft.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils 
{
	/** default ascii-betical null-safe string comparator implementation */
	public static class StringComparator implements Comparator<String> 
	{
		public int compare(String s1, String s2) 
		{
			if (s1 == null)
			{
				if (s2 == null) return 0;
				return -1;
			}
			if (s2 == null) return 1;
			return s1.compareTo(s2);
		}
	}
	
	public static boolean emptyOrNull (String s)
	{
		return (s == null || s.equals (""));
	}

	public static String urlEncode(String name) {
		//TODO: replace with apache codec?
		String encoded;
		try {
			encoded = URLEncoder.encode(name, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Programming error: wrong encoding");
		}
		return encoded;
	}

	/** 
	 * Strip html tags from a String.
	 * For example, abc&lt;b&gt;def will be turned into abcdef.
	 * Http entities such as &amp;amp; are left unchanged, for doing that as well, see stripHtml.
	 */
	public static String stripTags(String s)
	{
		return s.replaceAll("<[^>]+>", "");
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
	 * Join collection into and appent to a StringBuilder, with a separator between.
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

	public static String rep (String base, int count)
	{
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < count; ++i)
		{
			builder.append (base);
		}
		return builder.toString();
		
	}

	private static final Map<String, String> httpEntities;
	
	//TODO: add more. see http://en.wikipedia.org/wiki/List_of_XML_and_HTML_character_entity_references
	static {
		httpEntities = new HashMap<String, String>();
		httpEntities.put ("larr", "\u2190");
		httpEntities.put ("harr", "\u2194");
		httpEntities.put ("rarr", "\u2192");			
		httpEntities.put ("amp", "&");			
		httpEntities.put ("lt", "<");
		httpEntities.put ("gt", ">");
		
		httpEntities.put ("alpha",		"\u03B1");
		httpEntities.put ("Alpha",		"\u0391");			

		httpEntities.put ("beta",		"\u03B2");			
		httpEntities.put ("Beta",		"\u0392");			
		
		httpEntities.put ("gamma",		"\u03B3");			
		httpEntities.put ("Gamma",		"\u0393");			
		
		httpEntities.put ("delta",		"\u03B4");			
		httpEntities.put ("Delta",		"\u0394");
		
		httpEntities.put ("epsilon",	"\u03B5");			
		httpEntities.put ("Epsilon",	"\u0395");
		
		httpEntities.put ("zeta",		"\u03B6");			
		httpEntities.put ("eta",		"\u03B7");			
		httpEntities.put ("theta",		"\u03B8");			
		httpEntities.put ("iota",		"\u03B9");			
		httpEntities.put ("kappa",		"\u03BA");			
		httpEntities.put ("lambda",		"\u03BB");			
		httpEntities.put ("mu", 		"\u03BC");			
		httpEntities.put ("nu", 		"\u03BD");			
		httpEntities.put ("xi", 		"\u03BE");			
		httpEntities.put ("omicron",	"\u03BF");			
		httpEntities.put ("pi",			"\u03C0");			
		httpEntities.put ("rho",		"\u03C1");
		// gap
		httpEntities.put ("sigma",		"\u03C3");			
		httpEntities.put ("tau",		"\u03C4");			
		httpEntities.put ("upsilon",	"\u03C5");			
		httpEntities.put ("phi",		"\u03C6");			
		httpEntities.put ("chi",		"\u03C7");			
		httpEntities.put ("psi",		"\u03C8");			
		
		httpEntities.put ("Omega", 		"\u03A9");
		httpEntities.put ("omega", 		"\u03C9");
	}

	private static final Map<Character, String> greek;
	
	//TODO: add more. see http://en.wikipedia.org/wiki/List_of_XML_and_HTML_character_entity_references
	static {
		greek = new HashMap<Character, String>();
		greek.put ('\u03B1',	"alpha");
		greek.put ('\u0391',	"Alpha");			

		greek.put ('\u03B2',	"beta");			
		greek.put ('\u0392',	"Beta");			
		
		greek.put ('\u03B3',	"gamma");			
		greek.put ('\u0393',	"Gamma");			
		
		greek.put ('\u03B4',	"delta");			
		greek.put ('\u0394',	"Delta");
		
		greek.put ('\u03B5',	"epsilon");
		greek.put ('\u0395',	"Epsilon");	
		
		greek.put ('\u03B6',	"zeta");			
		greek.put ('\u03B7',	"eta");			
		greek.put ('\u03B8',	"theta");			
		greek.put ('\u03B9',	"iota");			
		greek.put ('\u03BA',	"kappa");			
		greek.put ('\u03BB',	"lambda");			
		greek.put ('\u03BC', 	"mu");			
		greek.put ('\u03BD', 	"nu");			
		greek.put ('\u03BE', 	"xi");			
		greek.put ('\u03BF',	"omicron");			
		greek.put ('\u03C0',	"pi");			

		greek.put ('\u03C1',	"rho");
		// gap
		greek.put ('\u03C3',	"sigma");	
		greek.put ('\u03C4',	"tau");
		greek.put ('\u03C5',	"upsilon");			
		greek.put ('\u03C6',	"phi");			
		greek.put ('\u03C7',	"chi");			
		greek.put ('\u03C8',	"psi");	
		greek.put ('\u03A9', 	"Omega");
		greek.put ('\u03C9', 	"omega");
	}
	
	/**
	 * Replace greek letters such as \u03B2 with english text such as "Beta";
	 */
	public static String greekToEnglish(String input)
	{
		Pattern patGreek = Pattern.compile("[\u0391-\u03A9\u03B1-\u03C9]");

		if (!patGreek.matcher(input).find())
			return input; // no replacement needed.
		
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < input.length(); ++i)
		{
			 char c = input.charAt(i);
			 String rep = greek.get(c); 
			 if (rep == null)
				 result.append (c);
			 else
				 result.append (rep);
		}
		return result.toString();
	}
	
	/**
	 * Strips html tags and http entities.
	 * Http entities such as '&amp;amp;' are replaced with '&amp;'
	 */
	public static String stripHtml(String string) 
	{
		//TODO: replace with apache codec?
		String s = string;
		
		// strip tags
		s = s.replaceAll("<[^>]+>", "");
		// reduce double whitespace that may have been left as a result from previous step.
		s = s.replaceAll(" +", " ");			
		// replace some common entities

		// now replace http entities
		StringBuffer buf = new StringBuffer();
		Matcher m = Pattern.compile("&(\\w+);").matcher(s);
		while (m.find()) 
		{
			String r = httpEntities.get(m.group(1));
			if (r != null) {
				m.appendReplacement(buf, r);
			}
			else
			{
				// put original back
				m.appendReplacement(buf, m.group(0));
				System.err.println ("Failed to look up " + m.group(1));
			}
		}
		m.appendTail(buf);

		return buf.toString();
	}
	
	public static String escapeHtml(String s)
	{
		//TODO: replace with apache codec?
		s = s.replaceAll("<", "&lt;");
		s = s.replaceAll(">", "&gt;");
		return s;
	}
	
	public static final String[] EMPTY_STRING_ARRAY = new String[] {};
	
	/** safely split a string, 
	 * Unlike String.split(), this works on null strings.
	 * Returns a zero-length array if the input is null */
	public static String[] safeSplit(String regex, Object o)
	{
		if (o == null) return EMPTY_STRING_ARRAY;
		String s = o.toString();
		if ("".equals(s)) return EMPTY_STRING_ARRAY;
		return s.split(regex);
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
	 * Safely convert an object to null;
	 * If the input is null, the result is null as well.
	 */
	public static String safeToString (Object o)
	{
		if (o == null) return null;
		return o.toString();
	}

	/**
	 * Make a string suitable for use as filename, 
	 * by replacing unsafe characters with a dash.
	 */
	public static String makeFileName (String s)
	{
		return s.replaceAll("[^a-zA-Z0-9\\-_()\\]\\[}{. ]+", "-");
	}

	/**
	 * Make sure a string stays within a certain length, by cutting a bit from the middle.
	 */
	public static String abbrev(String result, int maxLength, String separator)
	{
		int mid = maxLength / 2;
		if (result.length() > maxLength) result = result.substring(0, mid - 1) + separator + result.substring(result.length() - mid);
		return result;
	}
	
	/**
	 * Compare two strings, without throwing nullpointerexception, ignoring case
	 */
	public static boolean safeEqualsIgnoreCase(String a, String b)
	{
		return (a == null) ? a == b : b.equalsIgnoreCase(a); 
	}
	
}
