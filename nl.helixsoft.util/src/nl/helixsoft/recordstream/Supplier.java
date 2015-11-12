package nl.helixsoft.recordstream;

/**
 * 
 * Note: designed to be similar to java 8 for future compatibility.
 * will be replaced by java.util.function.Supplier<T> from Java 8.
 */
public interface Supplier<T> 
{
	T get();
}
