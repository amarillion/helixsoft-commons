package nl.helixsoft.recordstream;

public class DefaultRecord implements Record
{
	private final RecordMetaData metaData;
	private final Object[] fields;
	
	public DefaultRecord (RecordMetaData _metaData, Object fields[]) 
	{ 
		this.metaData = _metaData; this.fields = fields;
		assert (metaData.getNumCols() == fields.length);
	}

	public Object get(int i) { return fields[i]; }
	public Object get(String s) { return fields[metaData.getColumnIndex(s)]; }

	@Override @Deprecated public Object getValue(int i) { return get(i); }
	@Override @Deprecated public Object getValue(String s) { return get(s); }
	
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