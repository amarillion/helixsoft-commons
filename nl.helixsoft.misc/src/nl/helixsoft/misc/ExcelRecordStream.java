package nl.helixsoft.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import nl.helixsoft.recordstream.AbstractRecordStream;
import nl.helixsoft.recordstream.DefaultRecord;
import nl.helixsoft.recordstream.DefaultRecordMetaData;
import nl.helixsoft.recordstream.Record;
import nl.helixsoft.recordstream.RecordMetaData;
import nl.helixsoft.recordstream.RecordStream;
import nl.helixsoft.recordstream.RecordStreamException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelRecordStream extends AbstractRecordStream 
{
	private final HSSFSheet sheet;
	private final RecordMetaData rmd;
	
	int rowIndex = 1;
	
	public ExcelRecordStream (File xlsFile, String sheetTitle) throws IOException
	{
		HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream (new File ("/home/martijn/prg/doenz/scripts/TODO.xls")));
		sheet = wb.getSheet(sheetTitle);

		HSSFRow headerRow = sheet.getRow(0);
		
		String[] header = new String[headerRow.getLastCellNum()];
		
		for (int col = 0; col < headerRow.getLastCellNum(); ++col)
		{
			HSSFCell cell = headerRow.getCell(col);
			header[col] = cell.getStringCellValue();
		}
		
		rmd = new DefaultRecordMetaData(header);
	}

	@Override
	public RecordMetaData getMetaData() 
	{
		return rmd;
	}

	@Override
	public Record getNext() throws RecordStreamException 
	{
		if (rowIndex >= sheet.getLastRowNum()) return null;
		
		HSSFRow row = sheet.getRow(rowIndex++);
		Object[] fields = new Object[rmd.getNumCols()];
		
		int fieldNum = Math.min (rmd.getNumCols(), row.getLastCellNum());
		
		if (rmd.getNumCols() != row.getLastCellNum())
		{
			System.out.println ("Warning: number of cells " + row.getLastCellNum() + 
					" is different from number of headers " + rmd.getNumCols() + " in row " + rowIndex);
		}
		
		for (int col = 0; col < fieldNum; ++col)
		{
			HSSFCell cell = row.getCell(col);
			if (cell == null) continue;
			int cellType = cell.getCellType();
			if (cellType == HSSFCell.CELL_TYPE_FORMULA) cellType = cell.getCachedFormulaResultType();

			switch (cellType)
			{
			case HSSFCell.CELL_TYPE_NUMERIC:
				fields[col] = cell.getNumericCellValue();
				break;
			case HSSFCell.CELL_TYPE_BOOLEAN:
				fields[col] = cell.getBooleanCellValue();
				break;
			case HSSFCell.CELL_TYPE_BLANK:
				fields[col] = null;
				break;
			case HSSFCell.CELL_TYPE_STRING:
				fields[col] = cell.getStringCellValue();
				break;
			default:
				 throw new IllegalStateException ("Unknown type " + cell.getCellType());
			}
		}
		
		return new DefaultRecord (rmd, fields);
	}
	
	
	
}
