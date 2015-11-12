package nl.helixsoft.recordstream;

/**
 * Function with two arguments, could be used for e.g. reduce.
 * <p>
 * designed to be similar to java 8 for future compatibility.
 * will be replaced by java.util.function.BiFunction<T, U, R> from Java 8
 */
public interface BiFunction<T, U, R> 
{
	R apply (T t, U u);
}
