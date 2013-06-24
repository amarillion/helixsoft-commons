package nl.helixsoft.gui.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class ListTableModel<U> extends AbstractTableModel
{
	private final TableRowAdapter<U> rowMapper;
	private final List<U> rows = new ArrayList<U>();

	public String getColumnName(int col) 
	{
		return rowMapper.getColumnName(col);
	}

	public void setList(Collection<U> rows)
	{
		this.rows.clear();
		this.rows.addAll(rows);
		this.fireTableStructureChanged();
	}
	
	public ListTableModel(Collection<U> rows, TableRowAdapter<U> rowMapper)
	{
		this.rowMapper = rowMapper;
		this.rows.addAll(rows);
	}
	
	@Override
	public int getColumnCount()
	{
		return rowMapper.getColumnCount();
	}

	@Override
	public int getRowCount()
	{
		return rows.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		return rowMapper.getCell(columnIndex, rows.get(rowIndex));
	}
	
	public U getRow(int row)
	{
		return rows.get(row);
	}

}
