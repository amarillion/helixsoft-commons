package nl.helixsoft.recordstream;

/**
 * Map, or convert, from one type to another.
 * 
 * <p>
 * Note the first type is the input, the second type is the output.
 * <p>
 * Note: will be replaced by java.util.function.Function<T, R> from Java 8
 */
public interface Function <T, R>
{
	public R apply (T t);
}
