package nl.helixsoft.misc;

import java.io.File;

import nl.helixsoft.recordstream.AbstractRecordStream;
import nl.helixsoft.recordstream.DefaultRecord;
import nl.helixsoft.recordstream.DefaultRecordMetaData;
import nl.helixsoft.recordstream.Record;
import nl.helixsoft.recordstream.RecordMetaData;
import nl.helixsoft.recordstream.RecordStream;
import nl.helixsoft.recordstream.StreamException;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;

public class ODSRecordStream extends AbstractRecordStream 
{
	private final RecordMetaData rmd; 
	private int rowIndex = 1;
	private Table t1;
	
	public ODSRecordStream(File f, String title) throws Exception
	{
		SpreadsheetDocument doc1 = SpreadsheetDocument.loadDocument(f);
		t1 = doc1.getSheetByName(title);
		
		Row headerRow = t1.getRowByIndex(0);
		String[] header = new String[headerRow.getCellCount()];
		
		for (int i = 0; i < headerRow.getCellCount(); ++i)
		{
			header[i] = headerRow.getCellByIndex(i).getStringValue();
		}
		
		rmd = new DefaultRecordMetaData(header);
	}
	
	//tester
	public static void main(String[] args) throws Exception
	{
		RecordStream rs = new ODSRecordStream(new File ("/home/martijn/Documents/personal-git/TODO.ods"), "TODO Home");
		for (Record r : rs)
		{
			System.out.println (r);
		}
	}

	@Override
	public RecordMetaData getMetaData() 
	{
		return rmd;
	}

	@Override
	public Record getNext() throws StreamException 
	{
		if (rowIndex >= t1.getRowCount()) return  null;
		
		Row row = t1.getRowByIndex(rowIndex++);
		
		Object[] fields = new String[row.getCellCount()];
		for (int i = 0; i < row.getCellCount(); ++i)
		{
			fields[i] = row.getCellByIndex(i).getStringValue();
		}
		
		return new DefaultRecord(rmd, fields);
	}
	
}
