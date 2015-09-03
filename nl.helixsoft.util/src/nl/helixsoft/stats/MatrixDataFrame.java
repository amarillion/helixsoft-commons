package nl.helixsoft.stats;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.helixsoft.recordstream.DefaultRecordMetaData;
import nl.helixsoft.recordstream.Record;
import nl.helixsoft.recordstream.RecordMetaData;
import nl.helixsoft.recordstream.RecordStream;

public class MatrixDataFrame extends AbstractDataFrame 
{
	private Matrix<?> matrix;
	private Header columnHeader;
	private RecordMetaData rmd;
	private List<String> rowNames;
	private List<String> columnNames;
	
	public static MatrixDataFrame fromMatrix(Matrix<?> in, Header columnHeader, List<String> rowNames)
	{
		MatrixDataFrame result = new MatrixDataFrame();
		result.matrix = in;
		assert (columnHeader.size() == in.getWidth());
		assert (rowNames.size() == in.getHeight());
		result.columnHeader = columnHeader;
		
		result.columnNames = new ArrayList<String>();
		for (int i = 0; i < columnHeader.size(); ++i)
		{
			result.columnNames.add(columnHeader.getColumnName(i));
		}
		
		result.rmd = new DefaultRecordMetaData(result.columnNames);
		result.rowNames = rowNames;
		return result;
	}
	
	@Override
	public RecordMetaData getMetaData() 
	{
		return rmd;
	}

	@Override
	public DataFrame cut(int... columnIdx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataFrame select(int... rowIdx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataFrame select(List<Integer> rowIdx) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public DataFrame merge(DataFrame that, int onThisColumn, int onThatColumn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getColumnNames() 
	{
		return columnNames;
	}

	@Override
	public int getColumnIndex(String columnName) 
	{
		return rmd.getColumnIndex(columnName);
	}

	@Override
	public <T> DataFrame cbind(List<T> column) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataFrame rbind(Object... row) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecordStream asRecordStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Record> asRecordIterable() 
	{
		return new Iterable<Record>() 
		{
			
			@Override
			public Iterator<Record> iterator() 
			{
				
				return new Iterator<Record>() 
						{
							int pos = 0;

							@Override
							public boolean hasNext() 
							{
								return pos < matrix.getHeight();
							}

							@Override
							public Record next() {
								return getRow(pos++);
							}

							@Override
							public void remove() 
							{
								throw new UnsupportedOperationException("Can't remove from a MatrixDataFrame");
							}
				};
			}
		};
	}

	@Override
	public int getColumnCount() 
	{
		return matrix.getWidth();
	}

	@Override
	public String getColumnName(int col) 
	{
		return columnNames.get(col);
	}

	@Override
	public int getRowCount() 
	{
		return matrix.getHeight();
	}

	@Override
	public Object getValueAt(int row, int col) 
	{
		return matrix.get(row, col);
	}

	@Override
	public void setValueAt(Object value, int row, int col) 
	{
		matrix.set(row, col, value);
	}

	@Override
	public List<String> getRowNames() 
	{
		return rowNames;
	}

	@Override
	public String getRowName(int rowIx) 
	{
		return rowNames.get(rowIx);
	}

	@Override
	public Object getColumnHeader(int colIx) 
	{
		return columnHeader.get(colIx);
	}

	@Override
	public Header getColumnHeader() 
	{
		return columnHeader;
	}

}
