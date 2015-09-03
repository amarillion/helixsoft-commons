package nl.helixsoft.stats;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class DataFrameTableModel implements TableModel 
{
	private final DataFrame delegate;
	private boolean editable;
	
	public DataFrameTableModel (DataFrame aDelegate, boolean aEditable)
	{
		this.delegate = aDelegate;
		this.editable = aEditable;
	}
	
	@Override
	public int getRowCount() 
	{
		return delegate.getRowCount();
	}

	@Override
	public int getColumnCount() 
	{
		return delegate.getColumnCount();
	}

	@Override
	public String getColumnName(int columnIndex) 
	{
		return delegate.getColumnHeader(columnIndex).toString();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) 
	{
		return String.class; //TODO
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) 
	{
		return editable;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) 
	{
		return delegate.getValueAt(rowIndex, columnIndex);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) 
	{
		delegate.setValueAt(aValue, rowIndex, columnIndex);
	}

	@Override
	public void addTableModelListener(TableModelListener l) 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void removeTableModelListener(TableModelListener l) 
	{
		// TODO Auto-generated method stub

	}

}
