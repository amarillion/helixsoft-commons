/**
 * 
 */
package nl.helixsoft.graph;

public interface Emitter
{
	public void startList(String key);
	public void closeList();
	public void stringLiteral (String key, String value);
	public void intLiteral(String key, int value);
	public void doubleLiteral(String key, double value);
}