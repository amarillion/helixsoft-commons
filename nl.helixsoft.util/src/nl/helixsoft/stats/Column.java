package nl.helixsoft.stats;

import nl.helixsoft.recordstream.BiFunction;

/**
 * View to access just one column of a dataframe.
 */
public interface Column<T>
{	
	// something about column meta-data...	
	
	int getSize();	
	
	T get(int pos);
	
	void set(int pos, T value);
	
	Object getHeader();
	
	public <R> R apply (R start, BiFunction<R, T, R> applyFunc);
}
