package nl.helixsoft.recordstream;

import java.util.HashSet;
import java.util.Set;

/**
 * Wrap a record stream, while filtering out records that do not meet a certain criterion
 */
public class Filter extends AbstractRecordStream
{
	public static class FieldInSet implements Predicate<Record>
	{
		private Set<String> allowedVals = new HashSet<String>();
		private final String field;
		private int idx = -1;
		
		public FieldInSet (String field, String[] set)
		{
			this.field = field;
			for (String s : set) allowedVals.add(s);
		}

		public FieldInSet (String field, Set<String> set)
		{
			this.field = field;
			allowedVals.addAll(set);
		}

		public boolean accept (Record r)
		{
			// find column index if needed
			if (idx == -1)
			{
				for (idx = 0; idx < r.getMetaData().getNumCols(); ++idx)
				{
					if (field.equals (r.getMetaData().getColumnName(idx)))
							break;
				}
			}
			
			
			String val = "" + r.getValue(idx);
			return allowedVals.contains(val);
		}
	}

	public static class FieldEquals implements Predicate<Record>
	{ 
		private final String eq;
		private final String field;
		
		public FieldEquals (String field, String eq)
		{
			this.eq = eq.toString();
			this.field = field;
		}
		
		public boolean accept (Record r) 
		{ 
			return r.getValue(field).toString().equals(eq); 
		} 
	}

	public static class FieldNotEquals implements Predicate<Record>
	{ 
		private final String neq;
		private final String field;
		
		public FieldNotEquals (String field, String neq)
		{
			this.neq = neq.toString();
			this.field = field;
		}
		
		public boolean accept (Record r) 
		{ 
			return !r.getValue(field).toString().equals(neq); 
		} 
	}

	private final RecordStream parent;
	private final Predicate<Record> func;
	
	public Filter (RecordStream parent, Predicate<Record> func)
	{
		this.parent = parent;
		this.func = func;
	}
	
	@Override
	public Record getNext() throws StreamException
	{
		while (true)
		{
			Record result = parent.getNext();
			if (result == null) return null; // end of stream
			if (func.accept(result)) 
				return result; // return accepted record
			// not accepted -> try next
		}
	}

	@Override
	public RecordMetaData getMetaData() 
	{
		return parent.getMetaData();
	}

	@Override
	public void close()	{ parent.close(); }
}
