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
			if ((i % (period * 40)) == 0)
			{
				System.out.print("\n" + taskText + NumberFormatter.thousandsSeparatedFormat(i, ',', 12) + ": ");
			}
			System.out.print ('.');
		}
		i++;
	}
	
	public void reset()
	{
		i = 0;
	}
}
