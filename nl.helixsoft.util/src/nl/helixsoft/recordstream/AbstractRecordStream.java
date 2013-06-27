package nl.helixsoft.recordstream;

import java.util.Map;

import nl.helixsoft.recordstream.Adjuster.AdjustFunc;

public abstract class AbstractRecordStream implements RecordStream
{
	@Override
	public RecordStream filter (Predicate<Record> predicate)
	{
		return new Filter (this, predicate);
	}

	@Override
	public RecordStream adjust (Map<String, AdjustFunc> adjustMap)
	{
		return new Adjuster (this, adjustMap);
	}

}
