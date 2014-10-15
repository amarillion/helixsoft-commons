package nl.helixsoft.debug;

/**
 * Copied from org.pathvisio.desktop/test/org.pathvisio.desktop.debug.TestAndMeasure
 */
public class MemWatch
{
	private Runtime runtime = Runtime.getRuntime();

	private void runGC()
	{
		for (int i = 0; i < 20; ++i)
		{
			System.gc();
			try { Thread.sleep(100); } catch (InterruptedException ex) {}
		}
	}
	
	private long memStart;
	
	public void start()
	{
		runGC();
		memStart = (runtime.totalMemory() - runtime.freeMemory());
	}
	
	public long stop()
	{
		runGC();
		long memEnd = (runtime.totalMemory() - runtime.freeMemory());
		return (memEnd - memStart);
	}		
}
