package nl.helixsoft.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DebugUtils 
{
	/**
	 * Check arguments for null values...
	 */
	public static void testNull (Object... args)
	{
		for (int i = 0; i < args.length; i += 2)
		{
			StackTraceElement[] trace = Thread.currentThread().getStackTrace();
			String caller = trace[2].getMethodName();
			String msg = "Method " + caller + " called with null argument '" + args[i] + "'";
			if (args[i+1] == null) carp (new NullPointerException (msg));
		}
	}
	
	/**
	 * throw e while removing the highest level from the stack frame.
	 * See: http://stackoverflow.com/questions/727628/how-do-i-throw-an-exception-from-the-callers-scope
	 */
	public static <T extends Exception> void carp(T e) throws T
	{
		List<StackTraceElement> stack = new ArrayList<StackTraceElement>(Arrays.asList(e.getStackTrace()));
		stack.remove(0); // remove caller from call stack
		e.setStackTrace(stack.toArray(new StackTraceElement[stack.size()]));
		throw e;
	}
	
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
