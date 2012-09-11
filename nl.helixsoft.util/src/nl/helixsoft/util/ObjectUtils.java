package nl.helixsoft.util;

public class ObjectUtils 
{
	public static boolean safeEquals(Object o1, Object o2)
	{
		return (o1 == null ? o2 == null : o1.equals(o2));
	}

}
