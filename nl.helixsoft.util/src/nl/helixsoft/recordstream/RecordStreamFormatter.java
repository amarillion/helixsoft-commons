package nl.helixsoft.recordstream;

import java.io.PrintStream;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class RecordStreamFormatter 
{

	/** Format as a TableModel */
	public static TableModel asTableModel(RecordStream rs)
			throws StreamException 
	{
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		Vector<String> colnames = new Vector<String>();

		int colNum = rs.getMetaData().getNumCols();

		for (int col = 0; col < colNum; ++col) {
			colnames.add(rs.getMetaData().getColumnName(col));
		}

		Record rec;
		while ((rec = rs.getNext()) != null) {
			Vector<String> row = new Vector<String>();
			for (int col = 0; col < colNum; ++col) {
				row.add("" + rec.get(col));
			}
			data.add(row);
		}

		DefaultTableModel model = new DefaultTableModel(data, colnames);
		return model;
	}

	/**
	 * 
	 * @param out
	 * @param rs
	 * @param progress may be null.
	 * @throws RecordStreamException 
	 */
	public static long asTsv(PrintStream out, RecordStream rs, PrintStream progress, boolean addHeader) throws StreamException
	{
		int colNum = rs.getMetaData().getNumCols();
		
		if (addHeader)
		{
			String sep = "";
			for (int col = 0; col < colNum; ++col)
			{
				out.print (sep);
				out.print (rs.getMetaData().getColumnName(col));
				sep = "\t";
			}
			out.println();
		}
		
		long crow = 0;
		Record row;
		while ((row = rs.getNext()) != null)
		{
			String sep = "";
			for (int col = 0; col < colNum; ++col)
			{
				out.print (sep);
				out.print (row.get(col));
				sep = "\t";
			}
			out.println();
			crow++;
			if (progress != null && (crow % 1000 == 0)) progress.println("Row " + crow);
		}
		return crow;
	}

}
