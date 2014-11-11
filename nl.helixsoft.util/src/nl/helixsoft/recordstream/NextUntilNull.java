package nl.helixsoft.recordstream;

/**
 * Helper interface for stream-like objects.
 * Call getNext() repeatedly until null is returned. Null indicates the end of stream.
 * Especially useful in combination with {@link IteratorHelper}
 */
public interface NextUntilNull <T>
{
	T getNext() throws StreamException;
}