package nl.helixsoft.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

	public static <T> List<T> filter(Collection<T> haystack, Predicate<T> p)
	{
		List<T> result = new ArrayList<T>();
		
		for (T t : haystack)
		{
			if (p.accept(t))
			{
				result.add(t);
			}
		}
		
		return result;		
	}
				
	public static boolean emptyOrNull (Collection<?> c)
	{
		return c == null || c.isEmpty();
	}

}
