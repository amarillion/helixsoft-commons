package nl.helixsoft.recordstream;

import java.util.Map;

import nl.helixsoft.util.StringUtils;

/**
 * Adjuster can apply operations to individual fields of a RecordStream on the fly.
 * <p>
 * Pass one or more AdjustFunc implementations to the constructor. Unadjusted fields are
 * left unharmed.
 */
public class Adjuster implements RecordStream
{
	private final Map<String, AdjustFunc> adjust;
	private final RecordStream parent;
	
	/**
	 * Wraps a recordstream, modifies it on the fly by applying one or more adjustment functions.
	 * <p>
	 * @param parent: the RecordStream to adjust
	 * @param adjust: a map of fieldname / AdjustFunc pairs. The given field will be adjusted by the AdjustFunc. 
	 * Any field not in this map will be passed through unharmed.
	 */
	public Adjuster (RecordStream parent, Map<String, AdjustFunc> adjust)
	{
		this.parent = parent;
		this.adjust = adjust; //TODO: really should make a defensive copy here.
	}

	/**
	 * The number of columns in this record stream is always the 
	 * same as the number of columns in the parent record stream.
	 */
	@Override
	public int getNumCols() 
	{
		return parent.getNumCols();
	}

	@Override
	/**
	 * Column names are always the same as the parent record stream.
	 */
	public String getColumnName(int i)
	{
		return parent.getColumnName(i);
	}

	@Override
	/**
	 * Pull the next record from this record stream. 
	 * Adjustment functions are applied just before returning.
	 */
	public Record getNext() throws RecordStreamException 
	{
		int colNum = parent.getNumCols();
		Object[] fields = new Object[colNum];
		Record r = parent.getNext();
		if (r == null) return null;
		
		for (int col = 0; col < colNum; ++col)
		{
			String colName = parent.getColumnName(col);
			if (adjust.containsKey(colName))
				fields[col] = adjust.get(colName).adjust(r.getValue(col));
			else
				fields[col] = r.getValue(col);
		}
		return new DefaultRecord(this, fields);
	}
	
	public interface AdjustFunc
	{
		public Object adjust (Object val);
	}
	
	/** 
	 * Adjuster func to remove html tags, and replace html entities with the corresponding characters.
	 **/
	public static class HtmlStrip implements AdjustFunc
	{
		public Object adjust (Object val) { 
			return StringUtils.stripHtml("" + val);
		}
	}

	@Override
	public int getColumnIndex(String name)
	{
		return parent.getColumnIndex(name);
	}

}