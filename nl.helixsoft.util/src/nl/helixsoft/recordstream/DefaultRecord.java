package nl.helixsoft.recordstream;

public class DefaultRecord implements Record
{
	private final RecordMetaData metaData;
	private final Object[] fields;
	
	@Deprecated
	public DefaultRecord (RecordStream parent, Object fields[]) { this.metaData = new DefaultRecordMetaData(parent); this.fields = fields; }
	
	public DefaultRecord (RecordMetaData _metaData, Object fields[]) { this.metaData = _metaData; this.fields = fields; }
	
	public Object getValue(int i) { return fields[i]; }
	public Object getValue(String s) { return fields[metaData.getColumnIndex(s)]; }
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append ("DefaultRecord{");
		for (int i = 0; i < getMetaData().getNumCols(); ++i)
		{
			if (i != 0) builder.append (", ");
			builder.append ("'");
			builder.append (getMetaData().getColumnName(i));
			builder.append ("':'");
			builder.append (fields[i]);
			builder.append ("'");
			
		}
		builder.append ("}");
		return builder.toString();
	}
	@Override
	public RecordMetaData getMetaData() 
	{
		return metaData;
	}
}