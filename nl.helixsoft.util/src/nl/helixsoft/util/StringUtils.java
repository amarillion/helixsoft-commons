package nl.helixsoft.util;

import java.util.Collection;
import java.util.List;

public class StringUtils 
{
	@Deprecated
	public static List<String> quotedCommaSplit(String input)
	{
		return HStringUtils.quotedCommaSplit(input);
	}

	@Deprecated
	public static List<String> permissiveQuotedCommaSplit(String input)
	{
		return HStringUtils.permissiveQuotedCommaSplit(input);
	}

	
	@Deprecated
	public static boolean quotedSplit (String input, char quoteChar, char separatorChar, boolean continued, List<String> previouslist)
	{
		return HStringUtils.quotedSplit(input, quoteChar, separatorChar, continued, previouslist);
	}
	
	
	@Deprecated
	public static List<String> quotedSplit(String input, char quoteChar, char separatorChar)
	{
		return HStringUtils.quotedSplit(input, quoteChar, separatorChar);
	}
	
	
	@Deprecated
	public static List<String> quotedSplit(String input, char quoteChar, char separatorChar, boolean strictValidation)
	{
		return HStringUtils.quotedSplit(input, quoteChar, separatorChar, strictValidation);
	}
	
	
	@Deprecated
	public static String urlEncode(String name) 
	{
		return HStringUtils.urlEncode(name);
	}
	
	
	@Deprecated
	public static String stripTags(String s)
	{
		return HStringUtils.stripTags(s);
	}
	
	
	@Deprecated
	public static String safeToString (Object o)
	{
		return HStringUtils.safeToString(o);
	}
	
	
	@Deprecated
	public static String[] safeSplit(String regex, Object o)
	{
		return HStringUtils.safeSplit(regex, o);
	}
	
	
	@Deprecated
	public static String escapeHtml(String s)
	{
		return HStringUtils.escapeHtml(s);
	}
	
	
	@Deprecated
	public static String makeFileName (String s)
	{
		return HStringUtils.makeFileName(s);
	}
	
	
	@Deprecated
	public static boolean isFileNameSafe (String s)
	{
		return HStringUtils.isFileNameSafe (s);
	}
	
	
	@Deprecated
	public static String checkForIllegalCharacter (String haystack, String allowedCharacters)
	{
		return HStringUtils.checkForIllegalCharacter(haystack, allowedCharacters);
	}
	
	
	@Deprecated
	public static String abbrev(String result, int maxLength, String separator)
	{
		return HStringUtils.abbrev(result, maxLength, separator);
	}
	
	
	@Deprecated
	public static boolean safeEqualsIgnoreCase(String a, String b)
	{
		return HStringUtils.safeEqualsIgnoreCase(a, b);
	}

	@Deprecated
	public static class StringComparator extends HStringUtils.StringComparator { }
	
	@Deprecated
	public static boolean emptyOrNull (String s)
	{
		return HStringUtils.emptyOrNull(s);
	}

	
	@Deprecated
	public static String join (String sep, Collection<?> values)
	{
		return HStringUtils.join (sep, values);
	}
	
	@Deprecated
	public static void join (StringBuilder builder, String sep, Collection<?> values)
	{
		HStringUtils.join (builder, sep, values);
	}
	
	@Deprecated
	public static <T> void join (StringBuilder builder, String sep, T... values)
	{
		HStringUtils.join (builder, sep, values);
	}
	
	@Deprecated
	public static <T> String join (String sep, T... values)
	{
		return HStringUtils.join (sep, values);
	}
	
	@Deprecated
	public static String rep (String base, int count)
	{
		return HStringUtils.rep (base, count);
	}
	
	@Deprecated
	public static String greekToEnglish(String input)
	{
		return HStringUtils.greekToEnglish(input);
	}
	
	@Deprecated
	public static String decodeEntities (String s)
	{
		return HStringUtils.decodeEntities(s);
	}
	
	@Deprecated
	public static String stripHtml(String string)
	{
		return HStringUtils.stripHtml(string);
	}

	@Deprecated
	public static final String[] EMPTY_STRING_ARRAY = HStringUtils.EMPTY_STRING_ARRAY;
	
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

		
	@Deprecated
	public static String initialUpper (String input)
	{
		return HStringUtils.initialUpper (input);
	}

	@Deprecated
	public static String toLowerCase (String input)
	{
		return HStringUtils.toLowerCase(input);
	}

	@Deprecated
	public static String toUpperCase (String input)
	{
		return HStringUtils.toUpperCase(input);
	}
	
	@Deprecated
	public static String toCamelCase(String value)
	{
		return HStringUtils.toCamelCase (value);
	}	
	
	@Deprecated
	public static String scientificShort(String species)
	{
		return HStringUtils.scientificShort(species);
	}
		
	@Deprecated
	public static String escapeMysqlLiteral(String literal)
	{
		return HStringUtils.escapeMysqlLiteral (literal);
	}	

}
