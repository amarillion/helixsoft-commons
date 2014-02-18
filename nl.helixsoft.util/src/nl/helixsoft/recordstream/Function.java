package nl.helixsoft.recordstream;

/**
 * Map, or convert, from one type to another.
 */
public interface Function <T, R>
{
	public R apply (T t);
}
