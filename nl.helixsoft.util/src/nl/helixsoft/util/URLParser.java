package nl.helixsoft.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Does the opposite of URLBuilder. This takes a URL as input, and splits it in parts.
 * <p>
 * The part before '?' goes into base.
 * The remaining string is split by '&amp;' Every part is treated as a key-value pair with a '=' in between.
 * <p>
 * For example:</br>
 * <pre>example.com/search?query=needle&case-sensitive=true</pre></br>
 * Will give a base of <pre>example.com/search</pre> And key value pairs of 
 * <pre>query -> needle</pre> 
 * <pre>case-sensitive -> true</pre>
 * The URL will be decoded.
 */
public class URLParser 
{
	private Map<String, String> params = new HashMap<String, String>();
	private String base;
	
	/**
	 * @param input URL to be parsed.
	 */
	public URLParser (String input)
	{
		int pos = input.indexOf("?");
		if (pos < 0)
		{
			base = input;
			// DONE.
		}
		else
		{
			base = input.substring(0, pos);
			String remain = input.substring(pos+1);
			for (String param : remain.split ("&"))
			{
				if (param.isEmpty()) continue;
				if (param.indexOf("=") < 0) continue; // Empty param, not sure how to deal with this. Right now it's discarded...
				
				String[] fields = param.split ("=", 2);
				try {
					params.put (fields[0], URLDecoder.decode(fields[1], "UTF-8"));
				} 
				catch (UnsupportedEncodingException e) 
				{
					throw new IllegalStateException("Unsupported encoding, programming error");
				}
			}
		}
	}

	/**
	 * @return the base part before &amp;
	 */
	public String getBase() 
	{
		return base;
	}

	/**
	 * @param key the name of the parameter, for example "query"
	 * @return the value of the given parameter, or null if it wasn't present.
	 * Any URL-encoded characters such as '%20' will be decoded, in this case to a space character.
	 */
	public String getParam(String key) 
	{
		return params.get(key);
	}
	
}
