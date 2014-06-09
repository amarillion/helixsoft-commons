package nl.helixsoft.util;

import java.util.Collection;

import nl.helixsoft.recordstream.Predicate;

public class CollectionUtils 
{
	/**
	 * Find the first occurence in the collection that matches the Predicate.
	 */
	public static <T> T findFirst(Collection<T> col, Predicate<T> p)
	{
		for (T t : col)
		{
			if (p.accept(t)) return t;
		}
		return null;
	}

	public static boolean emptyOrNull (Collection<?> c)
	{
		return c == null || c.isEmpty();
	}

}
