package nl.helixsoft.recordstream;


public interface RecordStream
{
	public int getNumCols();
	public String getColumnName(int i);
	public Record getNext() throws RecordStreamException;
}