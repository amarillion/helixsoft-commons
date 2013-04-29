package nl.helixsoft.graph;

import java.util.Set;

public interface Edge
{
	@Deprecated
	public Node getDest();

	@Deprecated
	public Node getSrc();

	public String toString();

	public String getPredicate();

	public Set<String> getAttributeSet();

	public Object getAttribute(String key);

	public void setAttribute(String key, Object val);

}