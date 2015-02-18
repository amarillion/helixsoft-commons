package nl.helixsoft.recordstream;

import java.util.Iterator;

/**
 * Helper class to generate an Iterator for a class that implements NextUntilNull.
 */
public class IteratorHelper <T> implements Iterator<T>
{
	private T next;
	private final NextUntilNull<T> parent;
	
	public IteratorHelper (NextUntilNull<T> parent)
	{
		this.parent = parent;
		try {
			next = parent.getNext();
		} 
		catch (Exception e) 
		{
			throw new RuntimeException (e);
		}
	}

	@Override
	public boolean hasNext() 
	{
		return (next != null);
	}

	@Override
	public T next() 
	{
		T result = next;
		try {
			next = parent.getNext();
		} 
		catch (StreamException e) 
		{
			throw new RuntimeException(e);
		}
		return result;
	}

	@Override
	public void remove() 
	{
		throw new UnsupportedOperationException();
	}
}