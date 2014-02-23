package nl.helixsoft.recordstream;

/**
 * Map, or convert, from one type to another.
 * 
 * <p>
 * Note the first type is the input, the second type is the output.
 */
public interface Function <T, R>
{
	public R apply (T t);
}
