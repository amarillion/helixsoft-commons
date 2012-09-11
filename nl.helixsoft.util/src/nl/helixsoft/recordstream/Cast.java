package nl.helixsoft.recordstream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Convert a record stream in long form into short form.
 * The opposite operation is performed by Melt
 * Similar to the cast function of the R reshape package.
 * It's also similar to the pivot operation in MS Excel
 * 
 * Transform a record stream which is in long form:
 * 
 * <pre>
 * groupvar colvar value 
 * group1   col1   1     
 * group1   col2   2     
 * group2   col1   3     
 * group2   col2   4     
 * </pre>
 * 
 * into wide form
 * 
 * <pre>
 * groupvar col1 col2
 * group1   1    2
 * group2   3    4 
 * </pre>
 * 
 * 
 * Note that the incoming recordstream must be sorted by groupVar for this to work properly.
 */

public class Cast implements RecordStream
{
	private final RecordStream parent;
	private final String groupVar;
	private final int groupIdx;
	private final int columnIdx;
	private final int valueIdx;
	
	private List<String> outCols = new ArrayList<String>();
	private Map<String, Integer> outColIdx = new HashMap<String, Integer>();
	
	/**
	 * If the input stream contains more than three columns, the remaining ones are quietly ignored.
	 * @param parent incoming recordStream. Note it must be sorted by groupVar for this to work properly.
	 * @param groupVar
	 * @throws RecordStreamException 
	 */	
	public Cast (RecordStream parent, String groupVar, String columnVar, String valueVar) throws RecordStreamException
	{
		Map<String, Integer> idx = new HashMap<String, Integer>();
		for (int i = 0; i < parent.getNumCols(); ++i)
		{
			idx.put (parent.getColumnName(i), i);
		}
		
		this.parent = parent;
		this.groupVar = groupVar;
		groupIdx = idx.get(groupVar);
		columnIdx = idx.get(columnVar);
		valueIdx = idx.get(valueVar);
		
		next = parent.getNext();
		loadNextRecord();
	}
	
	private class IndexedRecord implements Record
	{
		Map<Integer, Object> values = new HashMap<Integer, Object>();
		
		@Override
		public Object getValue(int i) {
			return values.get(i);
		}
		
		public void putValue (int i, Object val)
		{
			values.put (i, val);
		}

		@Override
		public RecordStream getParent() {
			return Cast.this;
		}
	}

	private IndexedRecord nextResult;
	private Record next;

	private void loadNextRecord() throws RecordStreamException
	{	
		if (next == null)
		{
			nextResult = null;
			return;
		}

		nextResult = new IndexedRecord();
		String currentGroup = "" + next.getValue(groupIdx);
		nextResult.putValue (0, currentGroup);

		while (true)
		{
			String col = "" + next.getValue(columnIdx);
			Object val = next.getValue(valueIdx);
			
			if (!outColIdx.containsKey(col))
			{
				outColIdx.put(col, outCols.size());
				outCols.add(col);
			}
			int idx = outColIdx.get(col);
			nextResult.putValue(idx+1, val);
			
			next = parent.getNext();
			if (next == null) break;
			if (!currentGroup.equals ("" + next.getValue(groupIdx))) break;
		}
	}
	
	
	@Override
	public int getNumCols() 
	{
		return outCols.size() + 1;
	}

	@Override
	public String getColumnName(int i) 
	{
		if (i == 0)
			return groupVar;
		else
			return outCols.get(i - 1);
	}

	@Override
	public Record getNext() throws RecordStreamException {
		Record result = nextResult;
		loadNextRecord();
		return result;
	}
	
}
