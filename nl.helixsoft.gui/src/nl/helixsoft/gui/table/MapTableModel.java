package nl.helixsoft.gui.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

/**
 * TableModel that is also a Map.
 */
// TODO: reusable class, move to nl.helixsoft.gui.
// TODO: make sort order configurable
// TODO: make rendering of key / value configurable
// TODO: automatic adjusting
// TODO: make table model editable
// TODO: make it possible to pass an existing map (e.g. TreeMap).
public class MapTableModel<K extends Comparable<K>, V> extends AbstractTableModel implements Map<K, V>
{
	private final Map<K, V> delegate = new HashMap<K, V>();
	
	@Override
	public int getColumnCount() 
	{
		return 2;
	}

	@Override
	public int getRowCount() 
	{
		return delegate.size();
	}

	@Override
	public String getColumnName(int col) 
	{
		if (col == 0) return "Key"; else return "Value";
	}
	
	List<K> sortedKeys = Collections.emptyList();
	
	private void refresh()
	{
		sortedKeys =  new ArrayList<K>(delegate.keySet());
		Collections.sort(sortedKeys);
		fireTableDataChanged();
	}
	
	@Override
	public Object getValueAt(int row, int col) 
	{
		switch (col)
		{
		case 0:
			return sortedKeys.get(row);
		case 1:
			return delegate.get(sortedKeys.get(row));
		default:
			throw new IllegalArgumentException("Wrong column number: " + col);
		}
	}

	public K getRowKey(int row)
	{
		return sortedKeys.get(row);
	}
	
	@Override
	public void clear() 
	{
		delegate.clear();
		refresh();
	}

	@Override
	public boolean containsKey(Object arg0) 
	{
		return delegate.containsKey(arg0);
	}

	@Override
	public boolean containsValue(Object arg0) 
	{
		return delegate.containsValue(arg0);
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() 
	{
		return delegate.entrySet();
	}

	@Override
	public V get(Object arg0) 
	{
		return delegate.get(arg0);
	}

	@Override
	public boolean isEmpty() 
	{
		return delegate.isEmpty();
	}

	@Override
	public Set<K> keySet() 
	{
		return delegate.keySet();
	}

	@Override
	public V remove(Object arg0) 
	{
		return delegate.remove(arg0);
	}

	@Override
	public int size() 
	{
		return delegate.size();
	}

	@Override
	public Collection<V> values() 
	{
		return delegate.values();
	}

	@Override
	public V put(K key, V value) 
	{
		V result = delegate.put(key, value);
		refresh();
		return result;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) 
	{
		delegate.putAll(m);
		refresh();
	}

}
