package nl.helixsoft.recordstream;

public class DefaultRecordMetaData implements RecordMetaData
{
	private final RecordStream parent;
	
	public DefaultRecordMetaData(RecordStream parent)
	{
		this.parent = parent;
	}
	
	public int getNumCols()
	{
		return parent.getNumCols();
	}
	
	public String getColumnName(int i)
	{
		return parent.getColumnName(i);
	}
	
	public int getColumnIndex(String name)
	{
		return parent.getColumnIndex(name);
	}

}
