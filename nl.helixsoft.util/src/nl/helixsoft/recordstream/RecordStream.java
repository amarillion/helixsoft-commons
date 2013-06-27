package nl.helixsoft.recordstream;

import java.util.Map;

import nl.helixsoft.recordstream.Adjuster.AdjustFunc;


public interface RecordStream
{
	public int getNumCols(); //TODO: move to RecordMetaData
	public String getColumnName(int i); //TODO: move to RecordMetaData
	public int getColumnIndex(String name); //TODO: move to RecordMetaData
	
	public Record getNext() throws RecordStreamException;
	
	// transformation methods ...
	public RecordStream filter (Predicate<Record> predicate);
	
	// Specific to RecordStreams - Not found in java 1.8 streams
	public RecordStream adjust (Map<String, AdjustFunc> adjustMap);
	
}