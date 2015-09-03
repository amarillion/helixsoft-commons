package nl.helixsoft.stats;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.helixsoft.recordstream.Record;
import nl.helixsoft.recordstream.RecordStreamFormatter;

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
				int result = c1.compareTo(c2);
				if (result == 0) result = o1.compareTo(o2); // stable sorting...
				return result;
			} 
		});
		
		return select (rowIndexes);
	}

}
