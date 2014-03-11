package nl.helixsoft.recordstream;

public interface Record
{
	/** use get(String) instead */
	@Deprecated public Object getValue(String s);
	
	/** use get(int) instead */
	@Deprecated public Object getValue(int i);
	
	public Object get(String s);
	public Object get(int i);
	
	public RecordMetaData getMetaData();
}