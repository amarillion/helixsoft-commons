package nl.helixsoft.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EdgeImpl implements Edge
{
	private Map<String, Object> attributes = new HashMap<String, Object>();
	private String predicate; // non-unique;

	public EdgeImpl (String predicate) { this (null, null, predicate); }
	public Set<String> getAttributeSet() { return attributes.keySet(); }
	public void setAttribute (String key, Object val) { attributes.put (key, val); };
	public Object getAttribute (String key) { return attributes.get (key); }

	// TODO: get rid of src and dest, this is not compatible with Jung.
	private Node src;
	private Node dest;

	@Override
	public Node getDest()
	{
		return dest;
	}

	@Override
	public Node getSrc()
	{
		return src;
	}

	public EdgeImpl (Node src, Node dest, String predicate)
	{
		this.src = src;
		this.dest = dest;
		this.predicate = predicate;
	}

	@Override
	public String toString()
	{
		if (src == null || dest == null) return predicate;
		return  "Edge: " + src.getId() + " " + predicate + " " + dest.getId();
	}

	@Override
	public String getPredicate()
	{
		return predicate;
	}
}