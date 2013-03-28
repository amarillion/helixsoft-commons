package nl.helixsoft.util;

public class DebugUtils 
{

	/** 
	 * Annoying thing about java: assertions are disabled by default, and thus easy to forget  
	 * when you create a new Run configuration in eclipse.
	 * <p>
	 * Call this method at startup and you can never forget.
	 * @throws IllegalStateException when you don't have assertions enabled.
	 */
	public static void mustEnableAssertions()
	{
		try
		{
			assert(false);
			throw new IllegalStateException("Assertions are not enabled. To enable assertions, pass the -ea option to the JVM.");
		}
		catch (AssertionError ex)
		{
			/* Ok, assertion was enabled */
		}
	}
	
}
