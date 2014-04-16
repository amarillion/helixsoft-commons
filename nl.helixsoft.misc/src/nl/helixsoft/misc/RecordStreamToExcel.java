package nl.helixsoft.misc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import nl.helixsoft.recordstream.Record;
import nl.helixsoft.recordstream.RecordMetaData;
import nl.helixsoft.recordstream.RecordStream;
import nl.helixsoft.util.StringUtils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class RecordStreamToExcel 
{
	private HSSFWorkbook wb = new HSSFWorkbook();
	
	public void addSheet(RecordStream rs, String title) throws IOException
	{
		HSSFSheet xsheet = wb.createSheet(title);
		
		// first header
		int irow = 0;
		HSSFRow xrow = xsheet.createRow(irow++);
		
		RecordMetaData rmd = rs.getMetaData();
		for (int col = 0; col < rmd.getNumCols(); ++col)
		{
			xrow.createCell(col).setCellValue(rmd.getColumnName(col));
		}
		
		for (Record r : rs)
		{
			xrow = xsheet.createRow(irow++);
			for (int col = 0; col < rmd.getNumCols(); ++col)
			{
				HSSFCell cell = xrow.createCell(col);
				String val = StringUtils.safeToString(r.get(col));
				cell.setCellValue(val);
			}
		}
	}
	
	public void save(File f) throws IOException
	{
		FileOutputStream fileOut = new FileOutputStream(f);
		wb.write(fileOut);
		fileOut.close();
	}
	
	
	public static void toExcel(RecordStream rs, File f) throws IOException
	{
		RecordStreamToExcel x = new RecordStreamToExcel();
		x.addSheet (rs, "RecordStream");
		x.save(f);
	}
	
}
