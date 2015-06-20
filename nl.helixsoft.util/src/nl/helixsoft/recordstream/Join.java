package nl.helixsoft.recordstream;

//TODO: unfinished
public class Join extends AbstractRecordStream
{
	private final String onField;
	private final RecordStream left;
	private final RecordStream right;
	private int idxLeft = -1;
	private int idxRight = -1;	
	
	/**
	 * Join left and right columns on joining key.
	 * Input streams must be sorted.
	 * 
	 * Result will be the cartesian product of the two streams.
	 */
	public Join (String onField, RecordStream left, RecordStream right)
	{
		this.onField = onField;
		this.left = left;
		this.right = right;
	}
	
	@Override
	public Record getNext() throws StreamException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public RecordMetaData getMetaData() 
	{
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	
	
}
