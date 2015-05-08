package nl.helixsoft.recordstream;

public interface RecordMetaData 
{
	public int getNumCols();
	
	public String getColumnName(int i);
	
	/** throws IllegalArgumentException if not found */
	public int getColumnIndex(String name);
	
	public boolean hasColumnName (String name);
}
