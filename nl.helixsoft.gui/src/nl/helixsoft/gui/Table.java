package nl.helixsoft.gui;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Source:
 * Java developers almanac 1.4
 * http://www.exampledepot.com/egs/javax.swing.table/RowHeight.html
 */
public class Table 
{
	  // Returns the preferred height of a row.
    // The result is equal to the tallest cell in the row.
    public static int getPreferredRowHeight(JTable table, int rowIndex, int margin) {
        // Get the current default height for all rows
        int height = table.getRowHeight();
    
        // Determine highest cell in the row
        for (int c=0; c<table.getColumnCount(); c++) {
            TableCellRenderer renderer = table.getCellRenderer(rowIndex, c);
            Component comp = table.prepareRenderer(renderer, rowIndex, c);
            int h = comp.getPreferredSize().height + 2*margin;
            height = Math.max(height, h);
        }
        return height;
    }
    
    // The height of each row is set to the preferred height of the
    // tallest cell in that row.
    public static void packRows(JTable table, int margin) {
        packRows(table, 0, table.getRowCount(), margin);
    }
    
    // For each row >= start and < end, the height of a
    // row is set to the preferred height of the tallest cell
    // in that row.
    public static void packRows(JTable table, int start, int end, int margin) {
        for (int r=0; r<table.getRowCount(); r++) {
            // Get the preferred height
            int h = getPreferredRowHeight(table, r, margin);
    
            // Now set the row height using the preferred height
            if (table.getRowHeight(r) != h) {
                table.setRowHeight(r, h);
            }
        }
    }
}
