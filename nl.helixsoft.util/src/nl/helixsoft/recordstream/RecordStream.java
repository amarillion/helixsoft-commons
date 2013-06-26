package nl.helixsoft.recordstream;


public interface RecordStream
{
	public int getNumCols();
	public String getColumnName(int i);
	public Record getNext() throws RecordStreamException;
	public int getColumnIndex(String name);
	
	// transformation methods ...
	public RecordStream filter (Predicate<Record> predicate);
}