package nl.helixsoft.stats;

import nl.helixsoft.recordstream.BiFunction;

public class DefaultColumnView<T> implements Column<T>
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
	public <R> R apply (R start, BiFunction<R, T, R> applyFunc)
	{
		R accumulator = start;
		for (int i = 0; i < getSize(); ++i)
		{
			accumulator = applyFunc.apply(accumulator, get(i));
		}
		return accumulator;
	}
	

}
