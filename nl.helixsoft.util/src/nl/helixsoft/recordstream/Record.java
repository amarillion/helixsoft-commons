package nl.helixsoft.recordstream;

public interface Record
{
	public Object getValue(int i);
	public RecordStream getParent();
}