package nl.helixsoft.recordstream;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Turn a {@link java.sql.ResultSet} into a {@link RecordStream}.
 * Underlying resources are closed automatically if you read the stream until the end.
 * You can also close manually with {@link #close}
 */
public class ResultSetRecordStream extends AbstractRecordStream
{
	Logger log = LoggerFactory.getLogger("com.generalbioinformatics.rdf.ResultSetRecordStream");

	private final ResultSet rs;
	private boolean closed = false;
	
	private final RecordMetaData rmd;
	
	private final Statement wrappedStatement; // may be null
	private final Connection wrappedConnection; // may be null

	@Deprecated	
	/** 
	 * Better use the other constructor, to make sure Statement (and optionally Connection) are closed as well.
	 */
	public ResultSetRecordStream(ResultSet wrapped) throws StreamException 
	{ 
		this(wrapped, null, null);
	}

	/** 
	 * @param wrapped ResultSet to read records from
	 * @param st Underlying statement, will be closed when this recordstream is closed. 
	 * @param con Connection. Will be closed when this recordstream is closed. May be null if you want to keep the connection alive. 
	 */ 
	public ResultSetRecordStream(ResultSet wrapped, Statement st, Connection con) throws StreamException 
	{ 
		this.rs = wrapped;
		this.wrappedStatement = st;
		this.wrappedConnection = con;
		
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
			close();
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
				close(); // automatically close at end of stream.
				return null;
			}
			
			Record result = processRow(rs); 
			return result;
			
		} catch (SQLException ex) {
			throw new StreamException(ex);
		}
	}

	/** designed to be overridden by subclasses */
	protected Record processRow(ResultSet rs) throws SQLException 
	{
		Object[] data = new Object[rmd.getNumCols()];
		for (int col = 1; col <= rmd.getNumCols(); ++col)
		{
			data[col-1] = rs.getObject(col);
		}
		return new DefaultRecord(rmd, data);
	}

	public final void close() 
	{
		if (closed) { log.debug ("Ignoring second close"); return; }
		closed = true;
		try {
			rs.close();			
		} catch (SQLException e) { log.debug ("Exception during close", e); }
		finally {
			try
			{
				if (wrappedStatement != null)
				{
					wrappedStatement.close();
				}
			} catch (SQLException e) { log.debug ("Exception during close", e); }
			finally
			{
				if (wrappedConnection != null)
					try {
						log.info ("Returning connection to the pool");
						wrappedConnection.close();
					} catch (SQLException e) { log.debug ("Exception during close", e); }
			}
		}
	}

	@Override
	public final RecordMetaData getMetaData() 
	{
		return rmd;
	}
	
	@Override
	public void finalize()
	{
		// if we close at garbage collection, it's already too late... We missed a close() somewhere.
		if (!closed) 
		{
			log.warn ("ResultSet was not closed properly");
			close();
		}
	}

}