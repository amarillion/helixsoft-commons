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

public class Cast extends AbstractRecordStream
{
	private final RecordStream parent;
	private List<String> groupVar;
	private final int[] groupIdx;
	private final int columnIdx;
	private final int valueIdx;
	
	private List<String> outCols = new ArrayList<String>();
	private Map<String, Integer> outColIdx = new HashMap<String, Integer>();
	private final RecordMetaData rmd;
	
	/**
	 * If the input stream contains more than three columns, the remaining ones are quietly ignored.
	 * @param parent incoming recordStream. Note it must be sorted by groupVar for this to work properly.
	 * @param _groupVar
	 * @throws RecordStreamException 
	 */	
	public Cast (RecordStream parent, String[] _groupVar, String columnVar, String valueVar) throws RecordStreamException
	{
		Map<String, Integer> idx = new HashMap<String, Integer>();
		for (int i = 0; i < parent.getNumCols(); ++i)
		{
			idx.put (parent.getColumnName(i), i);
		}
		
		this.parent = parent;
		groupVar = new ArrayList<String>(_groupVar.length);
		groupIdx = new int[_groupVar.length];
		int i = 0;
		for (String g : _groupVar) 
		{
			groupVar.add(g);
			groupIdx[i++] = idx.get(g);
		}
		
		columnIdx = idx.get(columnVar);
		valueIdx = idx.get(valueVar);
		
		next = parent.getNext();
		loadNextRecord();
		
		List<String> colNames = new ArrayList<String>();
		colNames.addAll (groupVar);
		colNames.addAll (outCols);
		rmd = new DefaultRecordMetaData(colNames);
	}

	public Cast (RecordStream parent, String groupVar, String columnVar, String valueVar) throws RecordStreamException
	{
		this (parent, new String[] { groupVar }, columnVar, valueVar);
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
		public Object getValue(String s) 
		{
			return values.get(Cast.this.outColIdx.get(s));
		}

		@Override
		public RecordMetaData getMetaData() 
		{
			return Cast.this.rmd;
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
		String[] currentGroup = new String[groupIdx.length];
		for (int i = 0; i < groupIdx.length; ++i)
		{
			currentGroup[i] = "" + next.getValue(groupIdx[i]);
			nextResult.putValue (i, currentGroup[i]);
		}

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
			nextResult.putValue(idx + groupIdx.length, val);
			
			next = parent.getNext();
			if (next == null) break;
			
			if (!sameGroup(currentGroup)) break;
		}
	}

	private boolean sameGroup(String[] currentGroup)
	{
		for (int i = 0; i < groupIdx.length; ++i)
		{
			if (!currentGroup[i].equals ("" + next.getValue(groupIdx[i])))
			{
				return false;
			}
		}
		return true;
	}
	
	
	@Override
	public int getNumCols() 
	{
		return rmd.getNumCols();
	}

	@Override
	public String getColumnName(int i) 
	{
		return rmd.getColumnName(i);
	}

	@Override
	public Record getNext() throws RecordStreamException {
		Record result = nextResult;
		loadNextRecord();
		return result;
	}


	@Override
	public int getColumnIndex(String name)
	{
		return rmd.getColumnIndex(name);
	}
	
}
