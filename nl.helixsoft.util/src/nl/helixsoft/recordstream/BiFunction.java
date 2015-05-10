package nl.helixsoft.recordstream;

/**
 * Function with two arguments, could be used for e.g. reduce.
 * 
 * designed to be similar to java 8 for future compatibility.
 */
public interface BiFunction<T, U, R> 
{
	R apply (T t, U u);
}
