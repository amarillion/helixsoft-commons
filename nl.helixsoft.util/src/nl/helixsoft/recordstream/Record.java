package nl.helixsoft.recordstream;

/**
 * Represent a record or row of data from a table or stream.
 * Values can be retreived either by position (column number) or name (field or attribute)
 * In essence, a Record is a sorted map.
 * <p>
 * Values can be any Object.
 */
public interface Record
{
	/** use get(String) instead */
	@Deprecated public Object getValue(String s);
	
	/** use get(int) instead */
	@Deprecated public Object getValue(int i);
	
	/**
	 * Get value by column name. Note that column names are not guaranteed to be unique
	 * (depending on the underlying Record implementation)
	 * In case of duplicate column names, use get(int) instead.
	 */
	public Object get(String s);
	
	/**
	 * Get value by column index.
	 */
	public Object get(int i);
	
	public RecordMetaData getMetaData();
}