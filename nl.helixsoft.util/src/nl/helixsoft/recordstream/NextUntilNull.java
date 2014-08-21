package nl.helixsoft.recordstream;

/**
 * Helper interface for stream-like objects.
 * Call getNext() repeatedly until null is returned. Null indicates the end of stream.
 * Especially usefull in combination with IteratorHelper
 */
public interface NextUntilNull <T>
{
	T getNext() throws StreamException;
}