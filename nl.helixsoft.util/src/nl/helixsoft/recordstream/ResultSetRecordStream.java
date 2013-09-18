package nl.helixsoft.recordstream;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class ResultSetRecordStream extends AbstractRecordStream
{
	private final ResultSet rs;
	private boolean closed = false;
	private final RecordMetaData rmd;
	
	public ResultSetRecordStream(ResultSet wrapped) throws RecordStreamException 
	{ 
		this.rs = wrapped;
		try {
			int colNum = rs.getMetaData().getColumnCount();
			List<String> colnames = new ArrayList<String>();
			for (int col = 1; col <= colNum; ++col)
			{
				colnames.add(rs.getMetaData().getColumnName(col));
			}
			rmd = new DefaultRecordMetaData(colnames);
		}
		catch (SQLException ex)
		{
			throw new RecordStreamException(ex);
		}
	}

	@Override
	public int getNumCols() 
	{
		return rmd.getNumCols();
	}

	@Override
	public String getColumnName(int i) 
	{
		return rmd.getColumnName(i);
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
			
			Object[] data = new Object[rmd.getNumCols()];
			for (int col = 1; col <= rmd.getNumCols(); ++col)
			{
				data[col-1] = rs.getObject(col);
			}
			return new DefaultRecord(rmd, data);
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
		return rmd.getColumnIndex(name);
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