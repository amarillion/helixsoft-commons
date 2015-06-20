package nl.helixsoft.recordstream;

import java.util.List;

//TODO: examine possible merging with 2DArray, TableModel, ...
public class MemoryRecordStream extends AbstractRecordStream
{
	private final List<Record> data;
	private final RecordMetaData rmd;
	int pos = 0;
	
	public MemoryRecordStream (List<Record> data)
	{
		this.data = data;
		if (data.size() > 0)
		{
			rmd = data.get(0).getMetaData();
		}
		else
		{
			rmd = new DefaultRecordMetaData(new String[] {});
		}
	}
	
	@Override
	public Record getNext() throws StreamException 
	{
		if (pos >= data.size()) return null;
		return data.get(pos++);
	}

	@Override
	public RecordMetaData getMetaData() 
	{
		return rmd;
	}

	@Override
	public void close() { }
}