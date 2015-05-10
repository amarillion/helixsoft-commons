package nl.helixsoft.stats;

/**
 * View to access just one column of a dataframe.
 */
public interface ColumnView<T>
{	
	// something about column meta-data...	
	
	int getSize();	
	
	T get(int pos);
	
	void set(int pos, T value);
	
	Object getHeader();
	
}
