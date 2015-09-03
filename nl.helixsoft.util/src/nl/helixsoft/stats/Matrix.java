package nl.helixsoft.stats;

import java.util.ArrayList;
import java.util.List;

// TODO: replace with Google.collect Table.
public class Matrix<T> 
{
	private final int width;
	private final int height;
	final List<T> data;
	
	public Matrix(int aWidth, int aHeight)
	{
		width = aWidth;
		height = aHeight;
		data = new ArrayList<T>(aWidth * aHeight);
		
		// initialize all nulls
		for (int i = 0; i < (aWidth * aHeight); ++i) {
			data.add(null);
		}		
	}
	
	public void set(int y, int x, Object value)
	{
		if (x > width || x < 0) throw new IndexOutOfBoundsException("x: " + x + ", width: " + width);
		if (y > height || y < 0) throw new IndexOutOfBoundsException("y: " + y + ", height: " + height);
		data.set (x + (width * y), (T)value);
	}
	
	public T get(int y, int x)
	{
		return data.get(x + (width * y));
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
