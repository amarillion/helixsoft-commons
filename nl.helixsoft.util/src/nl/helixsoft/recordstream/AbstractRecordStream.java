package nl.helixsoft.recordstream;

import java.util.Iterator;
import java.util.Map;

import nl.helixsoft.recordstream.Adjuster.AdjustFunc;

public abstract class AbstractRecordStream extends AbstractStream<Record> implements RecordStream, NextUntilNull<Record>
{

	@Override
	public RecordStream adjust (Map<String, AdjustFunc> adjustMap)
	{
		return new Adjuster (this, adjustMap);
	}

	@Override
	public Iterator<Record> iterator()
	{
		return new IteratorHelper<Record>(this);
	}
	
}
