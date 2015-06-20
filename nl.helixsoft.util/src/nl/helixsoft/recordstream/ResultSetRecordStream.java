package nl.helixsoft.recordstream;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultSetRecordStream extends AbstractRecordStream
{
	private final ResultSet rs;
	private boolean closed = false;
	private final RecordMetaData rmd;
	
	public ResultSetRecordStream(ResultSet wrapped) throws StreamException 
	{ 
		this.rs = wrapped;
		try {
			int colNum = rs.getMetaData().getColumnCount();
			List<String> colnames = new ArrayList<String>();
			for (int col = 1; col <= colNum; ++col)
			{
				// use getColumnLabel instead of getColumnName, so ALIASed columns work
				// see: http://bugs.mysql.com/bug.php?id=43684
				colnames.add(rs.getMetaData().getColumnLabel(col));
			}
			rmd = new DefaultRecordMetaData(colnames);
		}
		catch (SQLException ex)
		{
			throw new StreamException(ex);
		}
	}

	@Override
	public Record getNext() throws StreamException 
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
			throw new StreamException(ex);
		}
	}

	@Override public void close() 
	{
		closed = true;
		try {
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	@Override
	public RecordMetaData getMetaData() 
	{
		return rmd;
	}
}