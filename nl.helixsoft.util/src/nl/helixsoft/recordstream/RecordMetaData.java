package nl.helixsoft.recordstream;

public interface RecordMetaData 
{
	public int getNumCols();
	public String getColumnName(int i);
	public int getColumnIndex(String name);
}
