package nl.helixsoft.recordstream;

import java.util.Collection;
import java.util.Iterator;

public abstract class AbstractStream<T> implements Stream<T>
{
	@Override
	public Stream<T> filter (Predicate<T> predicate)
	{
		return new FilterStream (this, predicate);
	}
	
	@Override
	public <R> Stream<R> map(final Function<? super T,? extends R> mapper)
	{
		final Iterator<T> p = this.iterator();
		
		return new AbstractStream<R>() 
		{
			@Override
			public Iterator<R> iterator() {
				
				return new MappingIterator<R, T> (p, mapper);
			}

		};
	}

	@Override
	public Collection<T> into(Collection<T> x) 
	{
		for (T t : this)
		{
			x.add(t);
		}
		return x;
	}

	@Override
	public void close()	{  }

}
