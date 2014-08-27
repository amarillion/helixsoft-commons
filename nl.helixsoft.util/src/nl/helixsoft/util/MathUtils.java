package nl.helixsoft.util;

public class MathUtils 
{

	/**
	 * Make sure x is within the low and high bounds.
	 * If x is inside the bounds, return x.
	 * If x is too high or low, return the upper or lower bound respectively
	 */
	public static int bound (int low, int x, int high)
	{
		return (Math.min (high, Math.max (x, low)));
	}

	/**
	 * Make sure x is within the low and high bounds.
	 * If x is inside the bounds, return x.
	 * If x is too high or low, return the upper or lower bound respectively
	 */
	public static double bound (double low, double x, double high)
	{
		return (Math.min (high, Math.max (x, low)));
	}

	/**
	 * Make sure x is within the low and high bounds.
	 * If x is inside the bounds, return x.
	 * If x is too high or low, return the upper or lower bound respectively
	 */
	public static float bound (float low, float x, float high)
	{
		return (Math.min (high, Math.max (x, low)));
	}

	/**
	 * Make sure x is within the low and high bounds.
	 * If x is inside the bounds, return x.
	 * If x is too high or low, return the upper or lower bound respectively
	 */
	public static long bound (long low, long x, long high)
	{
		return (Math.min (high, Math.max (x, low)));
	}
	
	public static double safeDivide (double above, double below, double ifDivideByZero)
	{
		return below == 0 ? ifDivideByZero : (above / below);
	}	

}
