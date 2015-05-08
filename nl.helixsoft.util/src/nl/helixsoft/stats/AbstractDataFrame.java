package nl.helixsoft.stats;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.TableModelListener;

import com.google.common.collect.Multimap;

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
	public void addTableModelListener(TableModelListener arg0)
	{
		throw new UnsupportedOperationException("Unimplemented");
	}
	
	@Override
	public void removeTableModelListener(TableModelListener arg0) 
	{
		throw new UnsupportedOperationException("Unimplemented");		
	}

	Map<String, Multimap<Object, Integer>> factors = new HashMap<String, Multimap<Object, Integer>>();
	
	public void putFactor(String factorName, Multimap<Object, Integer> factor)
	{
		factors.put(factorName, factor);
	}
	
	public Multimap<Object, Integer> getFactor(String factorName)
	{
		return factors.get(factorName);
	}
}
