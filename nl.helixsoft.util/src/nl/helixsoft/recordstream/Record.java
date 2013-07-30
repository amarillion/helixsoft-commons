package nl.helixsoft.recordstream;

public interface Record
{
	public Object getValue(String s);
	public Object getValue(int i);
	public RecordMetaData getMetaData();
}