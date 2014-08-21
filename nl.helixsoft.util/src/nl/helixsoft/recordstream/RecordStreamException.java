package nl.helixsoft.recordstream;

import java.io.IOException;

/** @Deprecated use more generic StreamException instead */ 
public class RecordStreamException extends IOException
{
	public RecordStreamException(Throwable cause) { super (cause); }
}