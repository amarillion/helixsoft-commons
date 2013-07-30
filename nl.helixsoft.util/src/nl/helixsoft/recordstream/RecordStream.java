package nl.helixsoft.recordstream;

import java.util.Map;

import nl.helixsoft.recordstream.Adjuster.AdjustFunc;


public interface RecordStream extends Stream<Record>
{
	/** @deprecated use Record.getMetaData().getNumCols() */
	public int getNumCols();
	
	/** @deprecated use Record.getMetaData().getColumnName() */
	public String getColumnName(int i);
	
	/** @deprecated use Record.getMetaData().getColumnIndex() */
	public int getColumnIndex(String name);
	
	/** 
	 * Currently, returns null to indicate end of stream
		TODO: switch to a hasNext / getNext model to make implementation of Iterator easier.
	 */
	public Record getNext() throws RecordStreamException;
	
	// transformation methods ...
	public RecordStream filter (Predicate<Record> predicate);
	
	// Specific to RecordStreams - Not found in java 1.8 streams
	public RecordStream adjust (Map<String, AdjustFunc> adjustMap);

}