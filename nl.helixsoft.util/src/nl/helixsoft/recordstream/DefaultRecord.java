package nl.helixsoft.recordstream;

public class DefaultRecord implements Record
{
	private final RecordStream parent;
	public RecordStream getParent() { return parent; }
	private final Object[] fields;
	public DefaultRecord (RecordStream parent, Object fields[]) { this.parent = parent; this.fields = fields; }
	public Object getValue(int i) { return fields[i]; }
}