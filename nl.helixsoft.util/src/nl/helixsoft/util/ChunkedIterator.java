package nl.helixsoft.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//TODO: check against modification during iteration?
public class ChunkedIterator<T> implements Iterable<List<T>> 
{
	private final Iterable<T> delegate;
	private final int chunkSize;
	
	public ChunkedIterator(Iterable<T> delegate, int chunkSize)
	{
		this.delegate = delegate;
		this.chunkSize = chunkSize;
	}
	
	@Override
	public Iterator<List<T>> iterator() 
	{
		return new Iterator <List<T>>() 
		{
			private Iterator<T> it = delegate.iterator();			
			int pos = 0;
			
			@Override
			public boolean hasNext() 
			{
				return it.hasNext();
			}

			@Override
			public List<T> next()
			{
				if (!it.hasNext()) throw new IllegalStateException("Access beyond end of iterator"); // TODO, what exception to throw here?
				
				//TODO: more memory efficient solution is to use a sub-iterator here.
				List<T> result = new ArrayList<T>();
				for (int i = 0; i < chunkSize; ++i)
				{
					result.add (it.next());
					if (!it.hasNext()) break;
				}
				
				return result;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
				
			}
		};
		
	}

}
