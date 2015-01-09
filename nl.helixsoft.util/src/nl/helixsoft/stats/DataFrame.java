package nl.helixsoft.stats;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.swing.table.TableModel;

import nl.helixsoft.recordstream.RecordStream;

/**
 * A table of data, ready for statistical operations.
 * <p>
 * All data is kept in memory
 * <p>
 * Each column can have headers
 * <p>
 * Each column is of a single type.
 * <p>
 * Implementations may use native type arrays (double[] or int[]) for efficiency.
 * <p>
 * There are facilities for statistical functions and plotting.
 * <p>
 * Some operations modify the data frame in-place. These methods typically return "this" to allow chaining operations.
 * <p>
 * Operations like cut() ... return a copy of the DataFrame object.
 */
public interface DataFrame extends TableModel //TODO: also implement List<Record>
{
	/**
	 * Extract specified colums by index
	 * returns a new DataFrame object.
	 */
	public DataFrame cut (int... columnIdx);
	
	/**
	 * Performs a merge (a.k.a. JOIN in SQL terms) with another table.
	 * returns a new DataFrame object.
	 * This is a FULL JOIN - Rows where the primary key doesn't exists in either this or the other, are filled with null values.
	 */
	public DataFrame merge (DataFrame that, int onThisColumn, int onThatColumn);
	
	/** shortCut in cases where the column name is the same */
	public DataFrame merge (DataFrame that, String onColumn);
	
	/**
	 * return column names as list
	 */
	List<String> getColumnNames();
	
	/**
	 * Turn an array of column names into an array of column indices 
	 */
	public int[] getColumnIndexes(String... columnNames);
	
	public int getColumnIndex(String columnName);
	
	public void toOutputStream (OutputStream os) throws IOException;

	/** 
	 * Add a column
	 * @return 
	 */
	public <T> DataFrame cbind(List<T> column);
	
	public RecordStream asRecordStream();

	/**
	 * Ideas:
	 * 
	 * statistical
	 * 
	 * sum
	 * stddev
	 * sqsum
	 * avg
	 * ...
	 * any aggregate function
	 * 
	 * filter (Predicate)
	 * 
	 * toLongFormat
	 * toWideFormat
	 * 
	 * Grouping: factors
	 * apply an aggregate function by group
	 * 
	 * sorting
	 * 
	 * -- efficiency
	 * colToIntArray - get column as int array
	 * colToDoubleArray
	 * colToStringArray
	 * colToObjectArray
	 * 
	 * Implement iteration, Collection interface
	 * Implement TableModel
	 * 
	 * Plotting
	 * 
	 * Change Events...
	 */
}
