package nl.helixsoft.recordstream;

/**
 * Used by filters. Test an object if it is acceptable or not.
 */
public interface Predicate<T>
{
	public boolean accept (T r);
}
