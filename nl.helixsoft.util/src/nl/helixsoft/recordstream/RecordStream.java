package nl.helixsoft.recordstream;

import java.util.Map;

import nl.helixsoft.recordstream.Adjuster.AdjustFunc;


public interface RecordStream extends Stream<Record>
{
	public RecordMetaData getMetaData();
		
	/** 
	 * Currently, returns null to indicate end of stream
		TODO: switch to a hasNext / getNext model to make implementation of Iterator easier.
	 */
	public Record getNext() throws StreamException;
		
	// Specific to RecordStreams - Not found in java 1.8 streams
	public RecordStream adjust (Map<String, AdjustFunc> adjustMap);
	
	/** close underlying file or stream */
	public void close();
	

}