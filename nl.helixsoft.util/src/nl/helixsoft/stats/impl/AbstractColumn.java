package nl.helixsoft.stats.impl;

import nl.helixsoft.recordstream.BiFunction;
import nl.helixsoft.stats.Column;

public abstract class AbstractColumn<T> implements Column<T> 
{
	
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
