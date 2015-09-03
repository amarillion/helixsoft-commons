package nl.helixsoft.stats;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class TestDataFrame extends TestCase 
{

	public void testCbind()
	{
		List<Integer> ints = new ArrayList<Integer>();
		for (int x : new int[] { 1, 2, 3, 4 }) { ints.add(x); }		
		String[] data = new String[] {"one", "two", "three", "four"};

		Matrix m1 = new Matrix(4, 4);

//		DataFrame df = DataFrameOperation.cbind(ints, data, m1);
	}
	
	public void testRowBind()
	{
		DataFrame df1 = DataFrameOperation.fromArray(
				new Object[][] {
						{ 1.0, "Hello" },
						{ 2.0, "World" }
				});
		DataFrame df2 = DataFrameOperation.fromArray(
				new Object[][] {
						{ 3.0, "x" },
						{ 4.0, "y" }
				});
		DataFrame df = DataFrameOperation.rbind(df1, df2);
	}
	
	public void testWide()
	{
		DataFrame dfLong = DataFrameOperation.fromArray(
				new Object[][] {
						{ "2014", "Spring", "Project1", 5 },
						{ "2014", "Spring", "Project2", 3 },
						{ "2014", "Fall", "Project1", 6 },
						{ "2014", "Fall", "Project2", 4 },
						{ "2015", "Spring", "Project1", 1 },
						{ "2015", "Spring", "Project2", 2 },
						{ "2015", "Fall", "Project1", 8 },
						{ "2015", "Fall", "Project2", 9 },
				});
		
	}
	
	public void testMerge()
	{
	
//		DataFrameOperation.merge (df1, df2);
	}
	
	public void testFactors()
	{
		
//		DataFrameOperation.merge (df1, df2);		
	}
	
}
