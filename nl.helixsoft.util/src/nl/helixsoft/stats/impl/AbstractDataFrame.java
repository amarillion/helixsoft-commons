package nl.helixsoft.stats.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.helixsoft.recordstream.AbstractRecordStream;
import nl.helixsoft.recordstream.Predicate;
import nl.helixsoft.recordstream.Record;
import nl.helixsoft.recordstream.RecordMetaData;
import nl.helixsoft.recordstream.RecordStream;
import nl.helixsoft.recordstream.RecordStreamFormatter;
import nl.helixsoft.recordstream.StreamException;
import nl.helixsoft.stats.Column;
import nl.helixsoft.stats.DataFrame;
import nl.helixsoft.stats.DefaultColumnView;
import nl.helixsoft.stats.Factor;
import nl.helixsoft.stats.RecordView;

/**
 * Default implementations of some methods that can be implemented
 * without knowing details of the DataFrame implementation.
 */
public abstract class AbstractDataFrame implements DataFrame 
{
	@Override
	/**
	 * Basic default implementation, implementing classes can override if there is a more efficient way to provide this.
	 */
	public Record getRow(int rowIx) 
	{
		return new RecordView(this, rowIx);
	}
	
	@Override
	/**
	 * Basic default implementation, implementing classes can override if there is a more efficient way to provide this.
	 */
	public RecordStream asRecordStream() 
	{
		return new AbstractRecordStream() {
			
			int pos = 0;

			@Override
			public RecordMetaData getMetaData() {
				return AbstractDataFrame.this.getMetaData();
			}

			@Override
			public Record getNext() throws StreamException {
				return pos < getRowCount() ? getRow(pos++) : null;
			}
		};
	}


	@Override
	public void toOutputStream(OutputStream os) throws IOException 
	{
		RecordStreamFormatter.asTsv(
				new PrintStream(os), asRecordStream(), null, true);
	}

	@Override
	public int[] getColumnIndexes(String... columnNames) 
	{
		int[] result = new int[columnNames.length];
		for (int i = 0; i < columnNames.length; ++i)
		{
			result[i] = getColumnIndex(columnNames[i]);
		}
		return result;
	}

	@Override
	public DataFrame merge(DataFrame that, String onColumn) 
	{
		return merge(that, this.getColumnIndex(onColumn), that.getColumnIndex(onColumn));
	}

	@Override
	public String getColumnName(int columnIndex) 
	{
		return getColumnHeader(columnIndex).toString();
	}

	@Override
	public Object getColumnHeader(int colIx) 
	{
		return getColumnHeader().get(colIx);
	}

	@Override
	public <T> Column<T> getColumn(Class<T> clazz, int columnIndex)
	{
		return new DefaultColumnView<T>(this, columnIndex);
	}

	@Override
	public <T> Factor<T> getColumnAsFactor(Class<T> clazz, int columnIndex) 
	{
		return new Factor<T>(this, columnIndex);
	}

	/** @inheritDocs */
	@Override
	public DataFrame sort(final int columnIndex)
	{
		List<Integer> rowIndexes = new ArrayList<Integer>();
		
		for (int i = 0; i < getRowCount(); ++i)
		{
			rowIndexes.add (i);
		}
		
		Collections.sort (rowIndexes, new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				
				Comparable c1 = (Comparable)getValueAt(o1, columnIndex);
				Comparable c2 = (Comparable)getValueAt(o2, columnIndex);
				if (c1 == c2) return 0; // same or both null
				if (c1 == null) return -1;
				if (c2 == null) return 1;
				int result = c1.compareTo(c2);
				if (result == 0) result = o1.compareTo(o2); // stable sorting...
				return result;
			} 
		});
		
		return select (rowIndexes);
	}

	/** @inheritDocs */
	@Override
	public DataFrame sort(String columnName)
	{
		return sort(getColumnIndex(columnName));
	}
	
	public List<Record> filter(Predicate<Record> predicate)
	{
		List<Record> result = new ArrayList<Record>();
		for (Record r : asRecordIterable())
		{
			if (predicate.accept(r))
			{
				result.add(r);
			}
		}
		return result;
	}
	
}
