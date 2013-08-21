package nl.helixsoft.recordstream;

import java.util.Collection;

public abstract class AbstractStream<T> implements Stream<T>
{

	@Override
	public Collection<T> into(Collection<T> x) 
	{
		for (T t : this)
		{
			x.add(t);
		}
		return x;
	}

}
