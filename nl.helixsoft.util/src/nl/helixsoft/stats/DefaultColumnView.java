package nl.helixsoft.stats;

public class DefaultColumnView implements ColumnView
{
	private final DataFrame delegate;
	private final int col;
	
	public DefaultColumnView(DataFrame in, int i) 
	{
		delegate = in;
		col = i;
	}

	@Override
	public int getSize() 
	{
		return delegate.getRowCount();
	}

	@Override
	public Object get(int pos) 
	{
		return delegate.getValueAt(pos, col);
	}

	@Override
	public void set(int pos, Object value) 
	{
		delegate.setValueAt(value, pos, col);
	}

	@Override
	public Object getHeader() 
	{
		return delegate.getColumnHeader(col);
	}

}
