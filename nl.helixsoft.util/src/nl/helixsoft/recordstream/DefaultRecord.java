package nl.helixsoft.recordstream;

public class DefaultRecord implements Record
{
	private final RecordStream parent;
	public RecordStream getParent() { return parent; }
	private final Object[] fields;
	public DefaultRecord (RecordStream parent, Object fields[]) { this.parent = parent; this.fields = fields; }
	public Object getValue(int i) { return fields[i]; }
	public Object getValue(String s) { return fields[parent.getColumnIndex(s)]; }
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append ("DefaultRecord{");
		for (int i = 0; i < getParent().getNumCols(); ++i)
		{
			if (i != 0) builder.append (", ");
			builder.append ('"');
			builder.append (getParent().getColumnName(i));
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
		return new DefaultRecordMetaData(parent);
	}
}