package nl.helixsoft.gui.table;

/**
 * Adapter to plug a list of S into a table
 * TODO: include in nl.helixsoft.util 
 */
public interface TableRowAdapter<S>
{
	public int getColumnCount();
	public Object getCell(int col, S val);
	public String getColumnName(int col);
}
