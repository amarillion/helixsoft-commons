package nl.helixsoft.recordstream;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class ResultSetRecordStream implements RecordStream
{
	private final ResultSet rs;
	private boolean closed = false;
	private int colNum;
	private BiMap<String, Integer> colnames;
	
	public ResultSetRecordStream(ResultSet wrapped) throws RecordStreamException 
	{ 
		this.rs = wrapped;
		try {
			colNum = rs.getMetaData().getColumnCount();
			colnames = HashBiMap.create();
			for (int col = 1; col <= colNum; ++col)
			{
				colnames.put(rs.getMetaData().getColumnName(col), col-1);
			}
		}
		catch (SQLException ex)
		{
			throw new RecordStreamException(ex);
		}
	}

	@Override
	public int getNumCols() 
	{
		return colNum;
	}

	@Override
	public String getColumnName(int i) 
	{
		return colnames.inverse().get(i);
	}

	@Override
	public Record getNext() throws RecordStreamException 
	{
		try {
			if (closed)
				return null;
			
			if (!rs.next()) 
			{
				close();
				return null;
			}
			
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

	private void close() throws SQLException 
	{
		closed = true;
		rs.close();
	}

	@Override
	public int getColumnIndex(String name) 
	{
		return colnames.get(name);
	}
	
	@Override
	public void finalize()
	{
		try {
			if (!closed) rs.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
}