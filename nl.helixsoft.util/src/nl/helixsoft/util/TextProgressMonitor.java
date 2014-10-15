package nl.helixsoft.util;

import java.io.OutputStream;


public class TextProgressMonitor 
{
	private final OutputStream out;
	private long i = 0;
	final long period;
	private boolean adaptive = false;
	private String taskText = "";
	
	//TODO: param OutputStream not actually used!
	public TextProgressMonitor(OutputStream out, long period)
	{
		this.out = out;
		this.period = period;
		i = 0;
	}
	
	public void setTaskText(String val)
	{
		taskText = val;
	}
	
	public long getCount()
	{
		return i;
	}
	
	// TODO: make adaptive
	public void setAdaptive (boolean value)
	{
		adaptive = value;
	}
	
	public void increment()
	{
		if ((i % period) == 0)
		{
			printLine();
		}
		i++;
	}
	
	public void printLine()
	{
		if (max > 0)
		{
			float pct = (float)(i) / (float)(max);
			int filled = Math.min (20, Math.max (0, (int)(pct * 20)));
			String progressFormatted = StringUtils.rep("=", filled) + StringUtils.rep("-", 20-filled);
			
			String spinner;			
			switch ((int)(i % 4))
			{
			case 0: spinner = "-"; break;
			case 1: spinner = "/"; break;
			case 2: spinner = "|"; break;
			default: spinner = "\\"; break;
			}
			
			System.out.printf("\r%40s [%20s] %3.0f%% (%d / %d) %s", taskText, progressFormatted, pct, i, max, spinner);
		}
		else
		{
			System.out.printf("\r%40s: %s", taskText, NumberFormatter.thousandsSeparatedFormat(i, ',', 12));
		}
	}
				
	private long max = -1;
	
	public void setMax(long value)
	{
		max = value;
	}
	
	public void reset()
	{
		i = 0;
	}
}
