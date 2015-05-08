package nl.helixsoft.stats;

import java.util.List;

import nl.helixsoft.recordstream.DefaultRecordMetaData;
import nl.helixsoft.recordstream.Record;
import nl.helixsoft.recordstream.RecordMetaData;
import nl.helixsoft.recordstream.RecordStream;

public class MatrixDataFrame extends AbstractDataFrame 
{
	private Matrix<?> matrix;
	private List<String> columnNames;
	private RecordMetaData rmd;
	private List<String> rowNames;
	
	public static MatrixDataFrame fromMatrix(Matrix<?> in, List<String> columnNames, List<String> rowNames)
	{
		MatrixDataFrame result = new MatrixDataFrame();
		result.matrix = in;
		assert (columnNames.size() == in.getWidth());
		assert (rowNames.size() == in.getHeight());
		result.columnNames = columnNames; 
		result.rmd = new DefaultRecordMetaData(columnNames);
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
	public Iterable<Record> asRecordIterable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?> getColumnClass(int arg0) 
	{
		// TODO Auto-generated method stub
		return null;
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
	public boolean isCellEditable(int arg0, int arg1) 
	{
		return true;
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

}
