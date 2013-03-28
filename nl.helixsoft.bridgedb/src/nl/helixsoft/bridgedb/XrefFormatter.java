package nl.helixsoft.bridgedb;

import org.bridgedb.Xref;

/**
 * Marker interface for any class that can convert an Xref into a String.
 */
public interface XrefFormatter 
{
	public String format (Xref ref);
}
