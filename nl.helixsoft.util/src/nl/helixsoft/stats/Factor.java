package nl.helixsoft.stats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// a category or oridinal Column
public class Factor<T> 
{
	private Map<T, List<Integer>> index;
	private DataFrame parent;
	int columnIndex;
	
	public Factor(DataFrame parent, int columnIndex) 
	{
		this.parent = parent;
		this.columnIndex = columnIndex;
		
		// create a Map
		updateIndex();
	}

	private void updateIndex() 
	{
		index = new HashMap<T, List<Integer>>();
		for (int i = 0; i < parent.getRowCount(); ++i)
		{
			T key = (T)parent.getValueAt(i, columnIndex);
			if (!index.containsKey(key))
			{
				index.put (key, new ArrayList<Integer>());
			}
			index.get(key).add (i);
		}		
	}

	public Collection<T> getFactors() 
	{
		return index.keySet();
	}

	public DataFrame getRows(T category) 
	{
		return parent.select(index.get(category));
	}
}
