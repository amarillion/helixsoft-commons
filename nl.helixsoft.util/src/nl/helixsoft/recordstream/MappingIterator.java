package nl.helixsoft.recordstream;

import java.util.Iterator;

class MappingIterator<R, T> implements Iterator<R>
{
	private final Iterator<T> p;
	private final Function<? super T, ? extends R> mapper;
	
	public MappingIterator(Iterator<T> iterator, Function<? super T, ? extends R> mapper2)
	{
		this.p = iterator;
		this.mapper = mapper2;
	}

	@Override
	public boolean hasNext() 
	{
		return p.hasNext();
	}

	@Override
	public R next() 
	{	
		return mapper.apply(p.next());
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Remove not supported");			
	}
}