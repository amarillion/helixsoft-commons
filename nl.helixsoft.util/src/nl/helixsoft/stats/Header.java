package nl.helixsoft.stats;

public interface Header {

	String getColumnName(int colIdx);
	public Object get(int colIdx);
	public int getSubHeaderCount();
	int size();
}
