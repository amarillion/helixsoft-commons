package nl.helixsoft.recordstream;

import java.util.Collection;

public interface Stream<T> extends Iterable<T>
{
	/**
	 * Add all the elements of this stream into a collection (List or Set) 
	 */
	public Collection<T> into(Collection<T> x);
	
	/**
	 * Apply a function to each element of the stream, and wrap the result into another stream.
	 */
	public <R> Stream<R> map(Function<? super T,? extends R> mapper);
	
	
	public Stream<T> filter (Predicate<T> predicate);
	
	
	public void close();

}
