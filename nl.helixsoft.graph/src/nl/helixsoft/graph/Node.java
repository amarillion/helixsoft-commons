package nl.helixsoft.graph;

import java.util.List;
import java.util.Set;

public interface Node
{
	public abstract List<EdgeImpl> getOutgoing();

	public abstract List<EdgeImpl> getIncoming();

	public abstract void setPos(double x, double y);

	public abstract Iterable<? extends Node> getOutgoingNodes();

	public abstract Iterable<? extends Node> getIncomingNodes();

	public abstract Object getId();

	public abstract double getX();

	public abstract double getY();

	public abstract void setAttribute(String key, Object value); //OK

	public abstract Object getAttribute(String key);

	public abstract Set<String> getAttributeSet();

	public abstract boolean hasAttribute(String key);

}