package nl.helixsoft.stats;

import java.util.List;

import nl.helixsoft.recordstream.Record;
import nl.helixsoft.recordstream.RecordMetaData;
import nl.helixsoft.recordstream.RecordStream;

public class ColumnBoundDataFrame extends AbstractDataFrame
{
	private List<Column<?>> views;
	private List<String> rowNames;
	private int rowNum;
	private int subHeaderCount;
	
	public ColumnBoundDataFrame(List<Column<?>> views, DataFrame parent)
	{
		this.views = views;
		this.rowNames = parent.getRowNames();
		this.subHeaderCount = parent.getColumnHeader().getSubHeaderCount();
		this.rowNum = rowNames.size();
		
		for (Column<?> view : views)
		{
			if (rowNum != view.getSize()) throw new IllegalArgumentException("All columns must have the same length, expected " + rowNum + " found " + view.getSize());
		}
	}

	@Override
	public RecordMetaData getMetaData() 
	{
		// TODO Auto-generated method stub
		return null;
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
	public List<String> getColumnNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getColumnIndex(String columnName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T> DataFrame cbind(List<T> column) {
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
	public int getRowCount() 
	{
		return rowNum;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) 
	{
		return views.get(columnIndex).get(rowIndex);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		
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
	public int getColumnCount() 
	{
		return views.size();
	}

	@Override
	public Header getColumnHeader() 
	{
		return new HeaderView();
	}

	class HeaderView implements Header
	{
		@Override
		public String getColumnName(int colIdx) 
		{
			return views.get(colIdx).getHeader().toString();
		}

		@Override
		public Object get(int colIdx) 
		{
			return views.get(colIdx).getHeader();
		}

		@Override
		public int getSubHeaderCount() 
		{
			return subHeaderCount;
		}

		@Override
		public int size() 
		{
			return views.size();
		}
	}

	@Override
	public <T> Column<T> getColumn(Class<T> clazz, int columnIndex)  
	{
		return (Column<T>)views.get(columnIndex);
	}

	@Override
	public DataFrame select(List<Integer> rowIdx) {
		// TODO Auto-generated method stub
		return null;
	}

}
