package nl.helixsoft.recordstream;

import java.util.Map;

import nl.helixsoft.util.StringUtils;

/**
 * Adjuster can apply operations to individual fields of a RecordStream on the fly.
 * <p>
 * Pass one or more AdjustFunc implementations to the constructor. Unadjusted fields are
 * left unharmed.
 */
public class Adjuster extends AbstractRecordStream
{
	private final Map<String, AdjustFunc> adjust;
	private final RecordStream parent;
	private final RecordMetaData rmd;

	/**
	 * Wraps a recordstream, modifies it on the fly by applying one or more adjustment functions.
	 * <p>
	 * @param parent the RecordStream to adjust
	 * @param adjust a map of fieldname / AdjustFunc pairs. The given field will be adjusted by the AdjustFunc. 
	 * Any field not in this map will be passed through unharmed.
	 */
	public Adjuster (RecordStream parent, Map<String, AdjustFunc> adjust)
	{
		this.parent = parent;
		this.adjust = adjust; //TODO: really should make a defensive copy here.
		
		String[] colNames = new String[parent.getMetaData().getNumCols()];
		for (int i = 0; i < parent.getMetaData().getNumCols(); ++i)
		{
			colNames[i] = parent.getMetaData().getColumnName(i);
		}
		rmd = new DefaultRecordMetaData(colNames);	
	}

	@Override
	/**
	 * Pull the next record from this record stream. 
	 * Adjustment functions are applied just before returning.
	 */
	public Record getNext() throws StreamException 
	{
		int colNum = rmd.getNumCols();
		Object[] fields = new Object[colNum];
		Record r = parent.getNext();
		if (r == null) return null;
		
		for (int col = 0; col < colNum; ++col)
		{
			String colName = rmd.getColumnName(col);
			if (adjust.containsKey(colName))
				fields[col] = adjust.get(colName).apply(r.get(col));
			else
				fields[col] = r.get(col);
		}
		return new DefaultRecord(rmd, fields);
	}
	
	public interface AdjustFunc extends Function<Object, Object> {}
	
	/** 
	 * Adjuster func to remove html tags, and replace html entities with the corresponding characters.
	 **/
	public static class HtmlStrip implements AdjustFunc
	{
		public Object apply (Object val) { 
			return StringUtils.stripHtml("" + val);
		}
	}

	/**
	 * The number of columns in this record stream is always the 
	 * same as the number of columns in the parent record stream.
	 * Column names are always the same as the parent record stream.
	 */
	@Override
	public RecordMetaData getMetaData() 
	{
		return rmd;
	}

	@Override
	public void close()	{ parent.close(); }

}