package nl.helixsoft.util;

import java.util.Collection;

public class CollectionUtils 
{
	public static boolean emptyOrNull (Collection<?> c)
	{
		return c == null || c.isEmpty();
	}

}
