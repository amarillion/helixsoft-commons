package nl.helixsoft.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NodeImpl implements Node
{
	private Object id;
	private double x = 0;
	private double y = 0;
	
	private List<EdgeImpl> outgoing = new ArrayList<EdgeImpl>();
	private List<EdgeImpl> incoming = new ArrayList<EdgeImpl>();
	
	private Map<String, Object> attributes = new HashMap<String, Object>();
	
	@Override
	public List<EdgeImpl> getOutgoing() { return outgoing; }

	@Override
	public List<EdgeImpl> getIncoming() { return incoming; }

	@Override
	public void setPos (double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	@Override
	public Iterable<? extends Node> getOutgoingNodes() 
	{
		List<Node> nodes = new ArrayList<Node>();
		for (Edge e : getOutgoing())
		{
			nodes.add(e.getDest());
		}
		return nodes;
	}

	@Override
	public Iterable<? extends Node> getIncomingNodes()
	{
		List<Node> nodes = new ArrayList<Node>();
		for (Edge e : getIncoming())
		{
			nodes.add(e.getSrc());
		}
		return nodes;
	}

	public NodeImpl (Object id)
	{
		this.id = id;
	}

	@Override
	public Object getId()
	{
		return id;
	}

	@Override
	public double getX()
	{
		return x;
	}

	@Override
	public double getY()
	{
		return y;
	}
	
	@Override
	public void setAttribute (String key, Object value)
	{
		attributes.put (key, value);
	}
	
	@Override
	public Object getAttribute (String key)
	{
		return attributes.get(key);
	}
	
	@Override
	public Set<String> getAttributeSet()
	{
		return attributes.keySet();
	}
	
	@Override
	public boolean hasAttribute(String key)
	{
		return attributes.containsKey(key);
	}
}