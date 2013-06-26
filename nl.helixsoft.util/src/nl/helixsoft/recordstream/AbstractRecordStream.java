package nl.helixsoft.recordstream;

public abstract class AbstractRecordStream implements RecordStream
{
	public RecordStream filter (Predicate<Record> predicate)
	{
		throw new UnsupportedOperationException();
	}
	
}
