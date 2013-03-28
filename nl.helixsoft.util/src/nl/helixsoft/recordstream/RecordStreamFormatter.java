package nl.helixsoft.recordstream;

import java.io.PrintStream;

public class RecordStreamFormatter {
	
	/**
	 * 
	 * @param out
	 * @param rs
	 * @param progress may be null.
	 * @throws RecordStreamException 
	 */
	public static void asTsv(PrintStream out, RecordStream rs, PrintStream progress, boolean addHeader) throws RecordStreamException
	{
		int colNum = rs.getNumCols();
		
		if (addHeader)
		{
			String sep = "";
			for (int col = 0; col < colNum; ++col)
			{
				out.print (sep);
				out.print (rs.getColumnName(col));
				sep = "\t";
			}
			out.println();
		}
		
		int crow = 0;
		Record row;
		while ((row = rs.getNext()) != null)
		{
			String sep = "";
			for (int col = 0; col < colNum; ++col)
			{
				out.print (sep);
				out.print (row.getValue(col));
				sep = "\t";
			}
			out.println();
			crow++;
			if (progress != null && (crow % 1000 == 0)) progress.println("Row " + crow);
		}
	}

}
