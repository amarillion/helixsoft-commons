package nl.helixsoft.util;

import java.io.OutputStream;

public class NumberFormatter 
{
	/** 
	 * format a long number using a thousands separator 
	 * 
	 * @param val number to format
	 * @param sep thousands separator to use, for example ',' in UK.
	 * @param min the minimum length of the resulting string, useful for alignment purposes
	 **/
	public static String thousandsSeparatedFormat (long val, char sep, int min)
	{
		long l = Math.abs(val);
		boolean neg = val < 0;
		
		//TODO: can be made faster using a fixed char array instead of a string builder, 
		// and filling it from the back.
		StringBuilder s = new StringBuilder();
		
		int pos = 0;
		do
		{			
			if ((++pos % 4) == 0)
			{
				s.append(sep);
			}
			else
			{
				long rem = l % 10;
				s.append ((char)(rem + 48));
				l /= 10;
			}
		}
		while (l > 0);
		
		if (neg) 
		{
			s.append("-");
		}
			
		while (s.length() < min)
		{
			s.append(' ');
		}
		return new String(s.reverse().toString());
	}
	
}
