package nl.helixsoft.util;

public class HStringUtils 
{

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

}
