package nl.helixsoft.stats;

import nl.helixsoft.recordstream.BiFunction;
import nl.helixsoft.stats.impl.AbstractColumn;

public class DefaultColumnView<T> extends AbstractColumn<T>
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
	public T get(int pos) 
	{
		return (T)delegate.getValueAt(pos, col);
	}

	@Override
	public void set(int pos, T value) 
	{
		delegate.setValueAt(value, pos, col);
	}

	@Override
	public Object getHeader() 
	{
		return delegate.getColumnHeader(col);
	}

	@Override
	public void setHeader(String value) {
		delegate.setColumnHeader(col, value);		
	}

}
