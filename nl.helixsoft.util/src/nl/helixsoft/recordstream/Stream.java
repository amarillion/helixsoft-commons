package nl.helixsoft.recordstream;

import java.util.Collection;

public interface Stream<T> extends Iterable<T>
{
	public Collection<T> into(Collection<T> x);

}
