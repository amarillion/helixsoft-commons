package nl.helixsoft.recordstream;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ResultSetRecordStream implements RecordStream
{
	private final ResultSet rs;
	private int colNum;
	private List<String> colnames;
	
	public ResultSetRecordStream(ResultSet wrapped) throws RecordStreamException 
	{ 
		this.rs = wrapped;
		try {
		colNum = rs.getMetaData().getColumnCount();
		colnames = new ArrayList<String>();
		for (int col = 1; col <= colNum; ++col)
		{
			colnames.add(rs.getMetaData().getColumnName(col));
		}
		}
		catch (SQLException ex)
		{
			throw new RecordStreamException(ex);
		}
	}

	@Override
	public int getNumCols() {
		return colNum;
	}

	@Override
	public String getColumnName(int i) 
	{
		return colnames.get(i);
	}

	@Override
	public Record getNext() throws RecordStreamException 
	{
		try {
			if (!rs.next()) return null;
			
			Object[] data = new Object[colNum];
			for (int col = 1; col <= colNum; ++col)
			{
				data[col-1] = rs.getObject(col);
			}
			return new DefaultRecord(this, data);
		} catch (SQLException ex) {
			throw new RecordStreamException(ex);
		}
	}
}