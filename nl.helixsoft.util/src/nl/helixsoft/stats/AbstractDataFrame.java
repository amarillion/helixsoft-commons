package nl.helixsoft.stats;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import nl.helixsoft.recordstream.RecordStreamFormatter;

/**
 * Default implementations of some methods that can be implemented
 * without knowing details of the DataFrame implementation.
 */
public abstract class AbstractDataFrame implements DataFrame 
{

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

}
