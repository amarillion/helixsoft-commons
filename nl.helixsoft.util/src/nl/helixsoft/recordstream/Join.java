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
	public int getNumCols() 
	{
		return left.getNumCols() + right.getNumCols() - 1;
	}

	@Override
	public String getColumnName(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Record getNext() throws RecordStreamException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getColumnIndex(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

}
