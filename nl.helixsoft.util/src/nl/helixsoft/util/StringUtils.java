package nl.helixsoft.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils 
{
	public static List<String> quotedCommaSplit(String input)
	{
		return quotedSplit(input, '"', ',');
	}

	/**
	 * Permissive version of quotedCommaSplit that prints warnings instead of throwing exceptions
	 * in certain cases that don't adhere to the spec but are recoverable.
	 */
	public static List<String> permissiveQuotedCommaSplit(String input)
	{
		return quotedSplit(input, '"', ',', false);
	}
	
	/**
	 * To better deal with newline characters in CSV files,
	 * 
	 * The idea is that a line is parsed in multiple invocations like this:
	 * 
	 * List<String> row = new ArrayList<String>();
	 * boolean continued = false;  
	 * do
	 * {
	 *     String line = readLine();
	 *     if (line == null) break; // reached end-of-file.
	 *     continued = quotedSplit (line, '"', ',', continued, row);	
	 * } while (continued)
	 */
	public static boolean quotedSplit (String input, char quoteChar, char separatorChar, boolean continued, List<String> previouslist)
	{
		//TODO: implement
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public static List<String> quotedSplit(String input, char quoteChar, char separatorChar)
	{
		return quotedSplit(input, quoteChar, separatorChar, true);
	}
	
	/**
	 * Efficiently parses strings like:
	 * 
	 * a, b, c <br>
	 * 
	 * a,"b", c <br>
	 * 
	 * a,"b,b", c <br>
	 * 
	 * a,"b""b", c <br>
	 * 
	 * Also handles newline characters between quotes, assuming a multi-line string is passed as argument.
	 * 
	 * @param strictValidation if you pass true, may throw an exception if the line doesn't adhere to CSV spec. If false, merely print a warning to STDERR.
	 * @see https://en.wikipedia.org/wiki/Comma-separated_values
	 */
	public static List<String> quotedSplit(String input, char quoteChar, char separatorChar, boolean strictValidation)
	{
		assert quoteChar != separatorChar;
		
		List<String> result = new ArrayList<String>();
		final int BOUNDARY = 0;
		final int CONTENT = 1;
		final int QUOTED = 2;
		final int QUOTE_AFTER_QUOTE = 3;
		
		int state = 0;
		int pos = 0;
		int start = 0;
		StringBuilder current = null;
		
		while (pos < input.length())
		{
			char c = input.charAt(pos);
			
			switch (state)
			{
			case BOUNDARY:
				current = new StringBuilder();
				if (c == quoteChar)
				{
					state = QUOTED;
					start = pos + 1;
				}
				else if (c == ' ')
				{
					// ignore opening whitespace
				}
				else if (c == separatorChar)
				{
					result.add ("");
				}
				else
				{
					start = pos;
					state = CONTENT;
				}
				break;
			case CONTENT:
				if (c == separatorChar)
				{
					state = BOUNDARY;
					result.add (input.substring (start, pos));
				}
				else if (c == quoteChar)
				{
					if (strictValidation)
					{
						throw new IllegalArgumentException("Found quote in middle of field: " + input);
					}
					else
					{
						System.err.println ("WARNING: Found quote in middle of field: " + input + " which is against CSV spec");
					}
				}
				break;
			case QUOTED:
				if (c == quoteChar)
				{	
					state = QUOTE_AFTER_QUOTE;
				}
				break;
			case QUOTE_AFTER_QUOTE:
				if (c == quoteChar)
				{
					// double quote, go back to quoted state.
					current.append (input.substring (start, pos-1));
					start = pos;
					state = QUOTED;
				}
				else if (c == separatorChar)
				{
					current.append (input.substring (start, pos-1));
					result.add (current.toString());
					state = BOUNDARY;
				}
				else if (c == ' ')
				{
					// skip whitespace
				}
				else
				{
					throw new IllegalArgumentException("Illegal character after closing quote: " + input);
				}
				break;
			}
			
			pos++;
			
		}
		
		// close up
		if (state == QUOTED)
		{
			throw new IllegalArgumentException("Missing closing quote: " + input);
		}
		else if (state == CONTENT)
		{
			result.add (input.substring (start, pos));
		}
		else if (state == QUOTE_AFTER_QUOTE)
		{
			current.append (input.substring (start, pos-1));
			result.add (current.toString());
		}
		
		return result;
	}

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
	 * Create a string by repeating a given base a number of times. 
	 */
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
	
	public static String decodeEntities (String s)
	{
		// now replace http entities
		StringBuffer buf = new StringBuffer();
		Matcher m = Pattern.compile("&(\\w+|#x?[0-9a-fA-F]+);").matcher(s);
		while (m.find()) 
		{
			String entity = m.group(1);
			Matcher m2 = Pattern.compile ("#x([0-9a-fA-F]+)").matcher(entity); 
			if (m2.matches())
			{
				m.appendReplacement(buf, "" + (char)Integer.parseInt(m2.group(1), 16));
			}
			else if (Pattern.compile ("#\\d+").matcher(entity).matches())
			{
				m.appendReplacement(buf, "" + (char)Integer.parseInt(entity.substring(1)));
			}
			else
			{
				String r = httpEntities.get(entity);
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
		}
		m.appendTail(buf);

		return buf.toString();
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
		
		// replace http entities like &amp;
		return decodeEntities (s);
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
	 * @deprecated use HStringUtils.safeParseDouble
	 */
	public static Double safeParseDouble(String val)
	{
		return HStringUtils.safeParseDouble(val);
	}
	
	/**
	 * May return null, but will not throw an exception
	 * @deprecated use HStringUtils.safeParseInt
	 */
	public static Integer safeParseInt(String val) 
	{
		return HStringUtils.safeParseInt(val);
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
	
	public static boolean isFileNameSafe (String s)
	{
		return !s.matches(".*[^a-zA-Z0-9\\-_()\\]\\[}{. ].*");
	}

	/**
	 * Check if the string contains only allowed characters .
	 * @returns null if ok, or an error message otherwise.
	 */
	public static String checkForIllegalCharacter (String haystack, String allowedCharacters)
	{	
		Set<Character> allowedSet = new HashSet<Character>();
		for (char c : allowedCharacters.toCharArray())
		{
			allowedSet.add(c);
		}
		
		int pos = 0;
		for (char hay : haystack.toCharArray())
		{
			if (!allowedSet.contains(hay)) 
			{
				return "contains illegal character '" + hay + "' at position " + pos;
			}
			
			pos++;
		}
		
		return null; // no problem found
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
	
	/**
	 * Make the first character in the String uppercase.
	 * Leave the remaining characters unchanged.
	 * Null-Safe: If the input value is null, this returns null;
	 */
	public static String initialUpper (String input)
	{
		if (input == null) return null;
		String result = input.substring(0, 1).toUpperCase() + input.substring (1);
		return result;
	}
	
	/**
	 * Null-Safe version of String.toLowerCase;
	 */
	public static String toLowerCase (String input)
	{
		if (input == null) return null;
		return input.toLowerCase();
	}

	/**
	 * Null-Safe version of String.toLowerCase;
	 */
	public static String toUpperCase (String input)
	{
		if (input == null) return null;
		return input.toUpperCase();
	}

	/**
	 * Remove spaces, make each word start with uppercase.
	 * CamelCase is a method for removing spaces from a phrase while maintaining leglibility.
	 *  
	 * For example "Small molecule" -> "SmallMolecule"
	 * "Show me your ID!" -> "ShowMeYourID!"
	 * "Two  spaces" -> "TwoSpaces"
	 * " surrounded " -> "Surrounded"
	 * Null-Safe: returns null if input is null;
	 */
	public static String toCamelCase(String value)
	{
		if (value == null) return null;
		
		StringBuilder result = new StringBuilder();
		for (String word : value.trim().split (" +"))
		{
			result.append (word.substring (0, 1).toUpperCase());
			result.append (word.substring (1));
		}
		return result.toString();
	}
	
	/**
	 * //TODO: this is bioinformatics, may be moved to a different class
	 * 
	 *  Shorten a scientific species name.
	 *  Takes the first capital letter of the first word, plus the second word.
	 *  Homo sapiens -> Hsapiens
	 */
	public static String scientificShort(String species)
	{
		Pattern pat = Pattern.compile ("^([A-Z])[a-z]+ ([a-z]+)");
		Matcher mat = pat.matcher(species);
		if (!mat.matches()) throw new IllegalArgumentException (species + " is not a valid scientific name."); 
		return mat.group(1) + mat.group(2);
	}
	
	/**
	 * Applies escaping as described in MySQL documentation here: https://dev.mysql.com/doc/refman/5.0/en/string-literals.html
	 * Note that this applies escapes for a literal enclosed by single quotes, not double quotes!
	 */
	public static String escapeMysqlLiteral(String literal)
	{
		StringBuilder result = new StringBuilder();
		for (char c : literal.toCharArray())
		{
			switch (c)
			{
			case '\'': case '\\': result.append ('\\'); result.append(c); break;
			case 0: result.append ("\\0"); break;
			case '\b': result.append ("\\b"); break;
			case '\n': result.append ("\\n"); break;
			case '\r': result.append ("\\r"); break;
			case '\t': result.append ("\\t"); break;
			case 26: result.append ("\\Z"); break;
			default: result.append (c); break;
			}
		}
		return result.toString();
	}

}
