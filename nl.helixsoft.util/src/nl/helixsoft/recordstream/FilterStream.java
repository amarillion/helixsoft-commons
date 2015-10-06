package nl.helixsoft.recordstream;

import java.util.Iterator;

/**
 * Wrap a record stream, while filtering out records that do not meet a certain criterion
 */
public class FilterStream<T> extends AbstractStream<T>
{
	private final Stream<T> parent;
	private final Predicate<T> func;
	
	public FilterStream (Stream<T> parent, Predicate<T> func)
	{
		this.parent = parent;
		this.func = func;
	}
	
	public class IteratorHelper implements Iterator<T>
	{
		private final Iterator<T> parent;
		T next;
		
		public IteratorHelper (Iterator<T> parent)
		{
			this.parent = parent;
			
			try {
				scanNext();
			} 
			catch (Exception e) 
			{
				throw new RuntimeException (e);
			}
		}

		private void scanNext()
		{
			next = null;
			while (parent.hasNext())
			{
				T value = parent.next();
				if (func.accept(value))
				{
					next = value; 
					break;
				}
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
			scanNext();
			return result;
		}

		@Override
		public void remove() 
		{
			throw new UnsupportedOperationException();
		}
	}
	
	@Override
	public Iterator<T> iterator()
	{
		return new IteratorHelper(parent.iterator());
	}	

	@Override
	public void close()	{ parent.close(); }

}
