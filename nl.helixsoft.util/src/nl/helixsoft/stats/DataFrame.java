package nl.helixsoft.stats;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import nl.helixsoft.recordstream.Record;
import nl.helixsoft.recordstream.RecordMetaData;
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
 * 
 * NOTE: implementing both TableModel and Iterable<Record> turned out not to be so hot because groovy inserts its own iterator() method in TableModels. 
 * Currently implementing neither. 
 * If you want a Iterable<Record>, see @link{DataFrame.asRecordIterable}
 * If you want a TableModel, use DataFrameOperation.asTableModel (df, editable)
 */
public interface DataFrame
{	
	public Record getRow(int rowIdx);
	
	/* get a name for each row, may return null */ 
	public List<String> getRowNames();
	public String getRowName (int rowIx);
	
	@Deprecated // use ColumnHeader instead
	public RecordMetaData getMetaData();
	
	/**
	 * Extract specified colums by index
	 * returns a new DataFrame object.
	 */
	public DataFrame cut (int... columnIdx);
	
	/**
	 * Extract specified rows by index
	 * returns a new DataFrame object.
	 */
	public DataFrame select (int... rowIdx);
	
	public DataFrame select (List<Integer> rowIdx);

	/**
	 * Performs a merge (a.k.a. JOIN in SQL terms) with another table.
	 * returns a new DataFrame object.
	 * This is a FULL JOIN - Rows where the primary key doesn't exists in either this or the other, are filled with null values.
	 * 
	 * @deprecated : use DataFrameOperation instead
	 * 
	 */
	public DataFrame merge (DataFrame that, int onThisColumn, int onThatColumn);
	
	/** shortCut in cases where the column name is the same
	 * 	 
	 * @deprecated : use DataFrameOperation instead
	 */
	@Deprecated public DataFrame merge (DataFrame that, String onColumn);
	
	/**
	 * return column names as list
	 * ... use getColumnHeader instead...
	 */
	@Deprecated
	List<String> getColumnNames();
	
	public Object getColumnHeader(int colIx);
	public Header getColumnHeader();
	
	/**
	 * Turn an array of column names into an array of column indices 
	 */
	public int[] getColumnIndexes(String... columnNames);
	
	public int getColumnIndex(String columnName);
	
	public void toOutputStream (OutputStream os) throws IOException;

	/** 
	 * Add a column
	 * @return a new dataframe // TODO - or modify in place?
	 */
	public <T> DataFrame cbind(List<T> column);
	
	//TODO: current implementation modifies in place and returns copy of this, unlike cbind which creates a copy.
	public DataFrame rbind(Object... row);
	
	//TODO: these are very similar... do we need both???
	//asRecordStream returns a copy of the data in the current implementation, but that is very inefficient.
	public RecordStream asRecordStream();
	public Iterable<Record> asRecordIterable();

	public int getColumnCount();
	public int getRowCount();

	public Object getValueAt(int rowIndex, int columnIndex);
	public void setValueAt(Object aValue, int rowIndex, int columnIndex);

	@Deprecated /** use getColumnHeader.toString() instead */
	public String getColumnName(int columnIndex); 

	public <T> Column<T> getColumn(Class<T> clazz, int columnIndex);
	public <T> Factor<T> getColumnAsFactor(Class<T> clazz, int columnIndex);
	
	public DataFrame sort (int columnIndex);
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
