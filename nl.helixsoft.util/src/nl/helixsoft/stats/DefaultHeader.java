package nl.helixsoft.stats;

import java.util.List;

public class DefaultHeader implements Header 
{
	private List<?> headerObjects;
	private int subHeaderCount;
	
	public DefaultHeader (List<? extends Object> headerObjects)
	{
		this(headerObjects, 1);
	}

	public DefaultHeader (List<? extends Object> headerObjects, int subHeaderCount)
	{
		this.headerObjects = headerObjects;
		this.subHeaderCount = subHeaderCount;
	}

	@Override
	public String getColumnName(int colIdx)
	{
		return headerObjects.get(colIdx).toString();
	}

	@Override
	public Object get(int colIdx) 
	{
		return headerObjects.get(colIdx);
	}

	@Override
	public int getSubHeaderCount() 
	{
		return subHeaderCount;
	}

	@Override
	public int size() 
	{
		return headerObjects.size();
	}
}
