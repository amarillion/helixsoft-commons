package nl.helixsoft.stats;

import nl.helixsoft.recordstream.BiFunction;

/**
 * View to access just one column of a dataframe.
 */
public interface Column<T>
{	
	// something about column meta-data...	
	
	/**
	 * Return the size (= number of rows) of this column
	 */
	int getSize();	
	
	/**
	 * return the element at a certain position (=row)
	 */
	T get(int pos);
	
	/**
	 * Set the element at a certain position (=row). Not supported by some Read-only column types.
	 */
	void set(int pos, T value);
	
	Object getHeader();

	/**
	 * Apply a function to all elements in a column
	 */
	public <R> R apply (R start, BiFunction<R, T, R> applyFunc);

	void setHeader(String value);
}
