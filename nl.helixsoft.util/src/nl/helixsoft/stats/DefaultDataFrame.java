package nl.helixsoft.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.helixsoft.recordstream.DefaultRecord;
import nl.helixsoft.recordstream.DefaultRecordMetaData;
import nl.helixsoft.recordstream.MemoryRecordStream;
import nl.helixsoft.recordstream.Record;
import nl.helixsoft.recordstream.RecordMetaData;
import nl.helixsoft.recordstream.RecordStream;

/**
 * Simple implementation of DataFrame, not very optimized.
 * 
 * //TODO cut and merge implementation would be much faster with col-based rather than row-based storage...
 * //TODO: rename to something more explicit like e.g. rowBoundDataFrame  
 */ 
public class DefaultDataFrame extends AbstractDataFrame 
{
	private List<Record> records;
	private RecordMetaData rmd;
	private Header header;
	
	/**
	 * static Constructor-like to create a DataFrame by sucking all records from a RecordStream.
	 */
	public static DataFrame createFromRecordStream (RecordStream input)
	{
		DefaultDataFrame df = new DefaultDataFrame();
		df.rmd = input.getMetaData();
		df.records = new ArrayList<Record>();
		
		List<String> header = new ArrayList<String>();
		for (int i = 0; i < df.rmd.getNumCols(); ++i)
		{
			header.add (df.rmd.getColumnName(i));
		}

		df.header = new DefaultHeader(header);
		input.into(df.records);
		return df;
	}

	/**
	 * Creates an empty DataFrame with given header.
	 */
	public static DataFrame createWithHeader (String... header)
	{
		DefaultDataFrame df = new DefaultDataFrame();
		df.rmd = new DefaultRecordMetaData(header);
		df.records = new ArrayList<Record>();
		return df;
	}
	
	/** @inheritDocs */
	@Override
	public DataFrame select(int... rowIdx)
	{
		DefaultDataFrame result = new DefaultDataFrame();
		result.records = new ArrayList<Record>();
		result.header = header;
		result.rmd = rmd;
		
		for (int i = 0; i < rowIdx.length; ++i)
		{
			result.records.add(records.get(i));
		}
		
		result.rmd = rmd;		
		return result;
	}

	/** @inheritDocs */
	@Override
	public DataFrame select(List<Integer> rowIndexes)
	{
		DefaultDataFrame result = new DefaultDataFrame();
		result.records = new ArrayList<Record>();
		result.header = header;
		result.rmd = rmd;
				
		for (Integer i : rowIndexes)
		{
			result.records.add(records.get(i));
		}
		
		result.rmd = rmd;
		return result;
	}
	
	/** @inheritDocs */
	@Override
	public DataFrame cut(int... columnIdx) 
	{
		String[] colNames = new String[columnIdx.length];
		for (int i = 0; i < columnIdx.length; ++i)
		{
			colNames[i] = rmd.getColumnName(columnIdx[i]);
		}
		
		RecordMetaData newRmd = new DefaultRecordMetaData(colNames);
		
		List<Record> newRecords = new ArrayList<Record>();
		
		for (Record r : records)
		{
			Object[] fields = new Object[columnIdx.length];
			for (int i = 0; i < columnIdx.length; ++i)
			{
				fields[i] = r.get(columnIdx[i]);
			}
			
			Record newR = new DefaultRecord(newRmd, fields);
			newRecords.add (newR);
		}
		
		DefaultDataFrame newDataFrame = new DefaultDataFrame();
		newDataFrame.rmd = newRmd;
		newDataFrame.records = newRecords;
		
		return newDataFrame;
	}

	/** @inheritDocs */
	@Override
	public DataFrame merge(DataFrame _other, int onColumn, int onOtherColumn) 
	{
		DefaultDataFrame other;
		if (_other instanceof DefaultDataFrame)
		{
			other = (DefaultDataFrame)_other;
		}
		else
		{
			//TODO - this really should be implemented for all possible values, but currently not enough methods exposed in DataFrame interface.
			throw new UnsupportedOperationException("Not implemented yet. Only supported if other is a DefaultDataFrame.");
		}
		
		int newColNum = rmd.getNumCols() + other.rmd.getNumCols() - 1;
		String[] colNames = new String[newColNum];
		
		// first column will be join column.
		colNames[0] = rmd.getColumnName(onColumn);
		
		int pos = 1;
		for (int i = 0; i < rmd.getNumCols(); ++i)
		{
			if (i == onColumn) continue;
			colNames[pos++] = rmd.getColumnName(i);
		}

		for (int i = 0; i < other.rmd.getNumCols(); ++i)
		{
			if (i == onOtherColumn) continue;
			colNames[pos++] = other.rmd.getColumnName(i);
		}

		RecordMetaData newRmd = new DefaultRecordMetaData(colNames);
		List<Record> newRecords = new ArrayList<Record>();
		
		Map<Object, Record> otherIndex = new HashMap<Object, Record>();
		Map<Object, Record> index = new HashMap<Object, Record>();
		Set<Object> allKeys = new HashSet<Object>();

		for (Record r : records)
		{
			String key = r.get(onColumn).toString();
			allKeys.add (key);
			index.put (key, r);
		}

		for (Record r : other.records)
		{
			Object key = r.get(onOtherColumn);
			allKeys.add (key);
			otherIndex.put (key, r);
		}
		
		for (Object key : allKeys)
		{
			Object[] fields = new Object[newColNum];
			
			fields[0] = key;
			
			int fpos = 1;
			
			Record r = index.get(key);			
			for (int i = 0; i < rmd.getNumCols(); ++i)
			{
				if (i == onColumn) continue;
				fields[fpos++] = r == null ? null : r.get(i);
			}

			Record otherR = otherIndex.get(key);
			for (int i = 0; i < other.rmd.getNumCols(); ++i)
			{
				if (i == onOtherColumn) continue;
				fields[fpos++] = otherR == null ? null : otherR.get(i);
			}
			newRecords.add (new DefaultRecord(newRmd, fields));
		}
		
		DefaultDataFrame results = new DefaultDataFrame();
		results.records = newRecords;
		results.rmd = newRmd;
		
		return results;
	}

	/** @inheritDocs */
	@Override
	public List<String> getColumnNames() 
	{
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < rmd.getNumCols(); ++i)
		{
			result.add (rmd.getColumnName(i));
		}
		return result;
	}

	@Override
	public int getColumnIndex(String columnName)
	{
		return rmd.getColumnIndex(columnName);
	}
	
	@Override
	public RecordStream asRecordStream()
	{
		return new MemoryRecordStream (records);
	}

	@Override
	public int getRowCount() {
		return records.size();
	}

	@Override
	public int getColumnCount() 
	{
		return rmd.getNumCols();
	}

	@Override
	public String getColumnName(int columnIndex) 
	{
		return rmd.getColumnName(columnIndex);
	}
	
	@Override
	public Object getColumnHeader(int colIx) 
	{
		return header.getColumnName(colIx);
	}

	@Override
	public Header getColumnHeader() 
	{
		return header;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) 
	{
		return records.get(rowIndex).get(columnIndex);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		throw new UnsupportedOperationException("Writing data not supported"); // TODO
	}

	@Override
	public <T> DataFrame cbind(List<T> column) 
	{
		if (column.size() != records.size()) throw new IllegalArgumentException ("DataFrame has " + records.size() + " rows but trying to add column of size " + column.size());
		
		int newColNum = rmd.getNumCols() + 1;
		String[] colNames = new String[newColNum];

		int pos = 0;
		for (int i = 0; i < rmd.getNumCols(); ++i)
		{
			colNames[pos++] = rmd.getColumnName(i);
		}
		
		colNames[pos++] = "cbind_" + pos;
		
		RecordMetaData newRmd = new DefaultRecordMetaData(colNames); 
		List<Record> newRecords = new ArrayList<Record>();
		
		for (int row = 0; row < records.size(); ++ row)
		{
			Record r = records.get(row);			
			
			Object[] fields = new Object[newColNum];
			
			int fpos = 0;			
			for (int i = 0; i < rmd.getNumCols(); ++i)
			{
				fields[fpos++] = r == null ? null : r.get(i);
			}
			fields[fpos++] = column.get(row);

			Record newRecord = new DefaultRecord (newRmd, fields);
			newRecords.add (newRecord);
		}
		
		DefaultDataFrame result = new DefaultDataFrame();
		result.records = newRecords;
		result.rmd = newRmd;
		return result;
	}

	
	@Override
	public Iterable<Record> asRecordIterable() 
	{
		return records;
	}

	@Override
	public Record getRow(int rowIdx) 
	{
		return records.get(rowIdx);
	}

	@Override
	public RecordMetaData getMetaData() 
	{
		return rmd;
	}

	@Override
	public DataFrame rbind(Object... row)
	{
		if (row.length != rmd.getNumCols()) throw new IllegalArgumentException ("DataFrame has " + rmd.getNumCols() + " columns but trying to add row of size " + row.length);

		Record r = new DefaultRecord(rmd, row);
		records.add(r);
		
		return this;
	}

	@Override
	public List<String> getRowNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRowName(int rowIx) {
		// TODO Auto-generated method stub
		return null;
	}	

}
