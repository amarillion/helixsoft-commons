package nl.helixsoft.recordstream;

/**
 * Used by filters. Test an object if it is acceptable or not.
 * Note: will be replaced by java.util.function.Predicate<T> from Java 8.
 */
public interface Predicate<T>
{
	public boolean accept (T r);
}
