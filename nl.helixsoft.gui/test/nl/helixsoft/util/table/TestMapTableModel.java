package nl.helixsoft.util.table;

import nl.helixsoft.gui.table.MapTableModel;
import junit.framework.TestCase;

public class TestMapTableModel extends TestCase
{
	public void test1()
	{
		MapTableModel<Integer, Integer> map = new MapTableModel<Integer, Integer>();
		map.put (4, 16);
		map.put (5, 25);
		map.put (6, 36);
		
		assertEquals (3, map.getRowCount());
		
		assertEquals (5, map.getValueAt(1, 0));
		assertEquals (25, map.getValueAt(1, 1));
		
		assertEquals (6, map.getValueAt(2, 0));
		assertEquals (36, map.getValueAt(2, 1));

	}
}
