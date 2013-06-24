package nl.helixsoft.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Build a url of the following form
 * <code>
 * BASE_URL/optional/additions?param=val&param=val&param=val
 * </code>
 * 
 * class can be subclassed to provide extra utility methods.
 * <p>
 * Most methods return <code>this</code> so calls can be chained.
 */
public class URLBuilder
{
	private StringBuilder result;
	private Map<String, String> params = new HashMap<String, String>();

	public URLBuilder(String base)
	{
		result = new StringBuilder (base);
	}
	
	public String build()
	{
		String sep = "?";
		for (Map.Entry<String, String> param : params.entrySet())
		{
			result.append (sep);
			result.append (param.getKey());
			result.append ("=");
			result.append (param.getValue());
			sep = "&";
		}
		return result.toString();
	}

	public URLBuilder param(String key, Object val)
	{
		params.put (key, val.toString());
		return this;
	}
	
	public URLBuilder append(String val)
	{
		result.append(val);
		return this;
	}

	public URLBuilder appendEncoded(String val)
	{
		try
		{
			result.append (URLEncoder.encode(val, "UTF-8"));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			throw new IllegalStateException("Programming bug: unknown encoding");
		}
		return this;
	}

}