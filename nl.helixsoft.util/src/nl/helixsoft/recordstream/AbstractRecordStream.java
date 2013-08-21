package nl.helixsoft.recordstream;

import java.util.Iterator;
import java.util.Map;

import nl.helixsoft.recordstream.Adjuster.AdjustFunc;

public abstract class AbstractRecordStream extends AbstractStream<Record> implements RecordStream
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

	private class RecordStreamIterator implements Iterator<Record>
	{
		private Record next;
		private final RecordStream parent;
		
		RecordStreamIterator (RecordStream parent)
		{
			this.parent = parent;
			try {
				next = parent.getNext();
			} 
			catch (RecordStreamException e) 
			{
				throw new RuntimeException (e);
			}
		}

		@Override
		public boolean hasNext() 
		{
			return (next != null);
		}

		@Override
		public Record next() 
		{
			Record result = next;
			try {
				next = parent.getNext();
			} 
			catch (RecordStreamException e) 
			{
				throw new RuntimeException(e);
			}
			return result;
		}

		@Override
		public void remove() 
		{
			throw new UnsupportedOperationException();
		}
	}
		
	@Override
	public Iterator<Record> iterator()
	{
		return new RecordStreamIterator(this);
	}
}
