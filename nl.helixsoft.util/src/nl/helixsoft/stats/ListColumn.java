package nl.helixsoft.stats;

import java.util.List;

import nl.helixsoft.recordstream.BiFunction;
import nl.helixsoft.stats.impl.AbstractColumn;

public class ListColumn<T> extends AbstractColumn<T> {

	private final List<T> delegate;
	private String header;

	public ListColumn (List<T> _delegate, String _header)
	{
		header = _header;
		delegate = _delegate;
	}
				
	@Override
	public int getSize() {
		return delegate.size();
	}

	@Override
	public T get(int pos) {
		return delegate.get(pos);
	}

	@Override
	public void set(int pos, T value) 
	{
		delegate.set(pos, value);		
	}

	@Override
	public Object getHeader()
	{
		return header;
	}

	@Override
	public void setHeader(String value) {
		header = value;
	}

}
