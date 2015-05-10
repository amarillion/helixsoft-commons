package nl.helixsoft.stats;

import nl.helixsoft.recordstream.Record;
import nl.helixsoft.recordstream.RecordMetaData;

/**
 * A view of a row in a DataFrame
 */
public class RecordView implements Record 
{
	private final DataFrame parent;
	private final int rowIx;
	
	public RecordView (DataFrame parent, int rowIx)
	{
		this.rowIx = rowIx;
		this.parent = parent;
	}
	
	/** use get(s) instead */
	@Override
	@Deprecated
	public Object getValue(String s) 
	{
		return get(s);
	}

	/** use get(colIx) instead */
	@Override
	@Deprecated
	public Object getValue(int colIx) 
	{
		return get(colIx);
	}

	@Override
	public Object get(String s) 
	{
		int colIx = parent.getColumnIndex(s);
		return parent.getValueAt(rowIx, colIx);
	}

	@Override
	public Object get(int colIx) 
	{
		return parent.getValueAt(rowIx, colIx);
	}

	@Override
	public RecordMetaData getMetaData() 
	{
		return parent.getMetaData();
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append ("RecordView{");
		for (int i = 0; i < getMetaData().getNumCols(); ++i)
		{
			if (i != 0) builder.append (", ");
			builder.append ("'");
			builder.append (getMetaData().getColumnName(i));
			builder.append ("':'");
			builder.append (get(i));
			builder.append ("'");
			
		}
		builder.append ("}");
		return builder.toString();
	}

}
