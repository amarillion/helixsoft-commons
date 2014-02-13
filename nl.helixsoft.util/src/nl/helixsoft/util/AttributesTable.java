package nl.helixsoft.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** 
 * 2D map of attributes
 * An attribute is a (String, Object) pair.
 * Each pair is associated with a Key of any type. 
 * <p>
 * Could be used to store attributes for Nodes or Edges in a graph.
 */
public class AttributesTable<K> 
{	
	private Map<K, Map<String, Object> > data = new HashMap<K, Map <String, Object>>();
	
	public void put (K t, String key, Object val)
	{
		Map<String, Object> sub = data.get(t);
		if (sub == null)
		{
			sub = new HashMap<String, Object>();
			data.put(t, sub);
		}
		sub.put (key, val);
	}
	
	public Object get(K t, String key)
	{
		if (data.containsKey(t))
		{
			return data.get(t).get(key);
		}
		else
		{
			return null;
		}
	}
	
	public Collection<K> getElements()
	{
		return data.keySet();
	}
	
	/** More efficient than getAttributeSet if you want to loop over key, value pairs */
	public Set<Map.Entry<String, Object>> getAttributes(K t)
	{
		Map<String, Object> e = data.get(t);
		if (e == null) return Collections.emptySet();
		return e.entrySet();
	}
	
	public Map<String, Object> getRow(K t)
	{
		return data.get(t);
	}
	
	public Set<String> getAttributeSet(K t)
	{
		if (data.containsKey(t))
		{
			return data.get(t).keySet();
		}
		else
		{
			return Collections.emptySet();
		}
	}
}
