package nl.helixsoft.stats;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import nl.helixsoft.recordstream.ReduceFunctions;
import nl.helixsoft.recordstream.StreamException;
import nl.helixsoft.recordstream.TabularIO;
import nl.helixsoft.stats.DataFrameOperation.JoinType;

/** Tests for all types of DataFrame and DataFrameOperation */
public class TestDataFrame extends TestCase 
{
	/** check if two dataframes are equal */
	private static void assertSame (DataFrame expected, DataFrame observed)
	{
		assertEquals ("Number of columns is not the same", expected.getColumnCount(), observed.getColumnCount());
		assertEquals ("Number of rows is not the same", expected.getRowCount(), observed.getRowCount());
		for (int col = 0; col < expected.getColumnCount(); ++col)
		{
			assertEquals ("Column name mismatch", expected.getColumnHeader(col), observed.getColumnHeader(col));
			for (int row = 0; row < expected.getRowCount(); ++row)
			{
				assertEquals("Value mismatch at (" + row + "," + col + ")", expected.getValueAt(row, col), observed.getValueAt(row, col));
			}
		}
	}
	
	DataFrame days = DataFrameOperation.fromArray(
			new String[] { "number", "day" },
			new Object[][] {
					{ 1.0, "Monday" },
					{ 2.0, "Tuesday" },
					{ 3.0, "Wednesday" },
					{ 4.0, "Thursday" },
					{ 5.0, "Friday" },
					{ 6.0, "Saturday" },
					{ 7.0, "Sunday" },
			});


	public void testHeader()
	{
		assertEquals ("number", days.getColumnHeader(0).toString());
		assertEquals ("day", days.getColumnHeader(1).toString());	
	}
	
	public void testColumnView()
	{
		Column<?> c = new DefaultColumnView(days, 0);
		assertEquals (c.getHeader(), "number");
		
		Column<?> c2 = new DefaultColumnView(days, 1);
		assertEquals (c2.getHeader(), "day");

	}
	
	public void testRowSort()
	{
		DataFrame result = days.sort("day");	
		DataFrame expect = DataFrameOperation.fromArray(
				new String[] { "number", "day" },
				new Object[][] {
						{ 5.0, "Friday" },
						{ 1.0, "Monday" },
						{ 6.0, "Saturday" },
						{ 7.0, "Sunday" },
						{ 4.0, "Thursday" },
						{ 2.0, "Tuesday" },
						{ 3.0, "Wednesday" },
				});
		assertSame (expect, result);
	}
	
	public void testCut()
	{
		DataFrame result = days.cut(0);
		DataFrame expect = DataFrameOperation.fromArray(
				new String[] { "number" },
				new Object[][] {
						{ 1.0 },
						{ 2.0 },
						{ 3.0 },
						{ 4.0 },
						{ 5.0 },
						{ 6.0 },
						{ 7.0 },
				});
		assertSame (expect, result);
	}
	
	public void testCbind()
	{
		List<Integer> ints = new ArrayList<Integer>();
		for (int x : new int[] { 1, 2, 3, 4 }) { ints.add(x); }
		
		String[] data = new String[] {"one", "two", "three", "four"};

		Matrix m1 = new Matrix(4, 4);

		DataFrame df = DataFrameOperation.cbind(ints, data, m1);
		
		//TODO
	}
	
	public void testRowBind()
	{
		DataFrame df1 = DataFrameOperation.fromArray(
				new String[] { "number", "string" },
				new Object[][] {
						{ 1.0, "Hello" }
				});
		DataFrame df2 = DataFrameOperation.fromArray(
				new String[] { "number", "string" },
				new Object[][] {
						{ 2.0, "World" }
				});
		DataFrame result = DataFrameOperation.rbind(df1, df2);
		
		DataFrame expect = DataFrameOperation.fromArray(
				new String[] { "number", "string" },
				new Object[][] {
						{ 1.0, "Hello" },
						{ 2.0, "World" },
				});
//		assertSame (expect, result); //TODO
	}
	
	DataFrame dfLong = DataFrameOperation.fromArray(
			new String[] { "year", "quarter", "project", "days" },
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
	
	public void testWide() throws StreamException, FileNotFoundException
	{
		//TODO
		
		DataFrame result = DataFrameOperation.wideFormat(dfLong).withColumnFactor("year", "quarter").withRowFactor("project").withValue("days").get();
		TabularIO.write(result).to(System.out).go();
	}

	public void testCollapse() throws StreamException
	{
		DataFrame result = DataFrameOperation.groupBy(dfLong, "year").agg("days", ReduceFunctions.INT_SUM).get();
		DataFrame expect = DataFrameOperation.fromArray(
				new String[] { "year", "days" },
				new Object[][] {
					{ "2014", 18 },
					{ "2015", 20 },
				});
		assertSame (expect, result);
	}
	
	/** Test the DataFrame.select method */
	public void testSelect() throws StreamException
	{
		DataFrame result = days.select(Arrays.asList (new Integer[] { 5, 5, null, 3 }));
		DataFrame expect = DataFrameOperation.fromArray(
				new String[] { "number", "day" },
				new Object[][] {
					{ 6.0, "Saturday" },
					{ 6.0, "Saturday" },
					{ null, null },
					{ 4.0, "Thursday" },
				});
		assertSame (expect, result);
	}

	DataFrame dfLeft = DataFrameOperation.fromArray(
			new String[] { "id", "letter" },
			new Object[][] {
				{  "1", "A" },
				{  "1", "a" },
				{  "2", "B" }
		});

	DataFrame dfRight = DataFrameOperation.fromArray(
			new String[] { "id", "roman" },
			new Object[][] {
				{  "1", "I" },
				{  "3", "III" }
		});

	/** Test the DataFrameOperation.join method */
	public void testJoin() throws StreamException
	{
		DataFrame result, expect;
		
		result = DataFrameOperation.merge(dfLeft, dfRight).onColumn("id").fullJoin().get().sort("id");
		expect = DataFrameOperation.fromArray(
				new String[] { "id", "letter", "roman" },
				new Object[][] {
					{  "1", "A", "I" },
					{  "1", "a", "I" },
					{  "2", "B", null },
					{  "3", null, "III" }
			});
		assertSame (expect, result);
		
		result = DataFrameOperation.merge(dfLeft, dfRight).onColumn("id").leftJoin().get().sort("id");
		expect = DataFrameOperation.fromArray(
				new String[] { "id", "letter", "roman" },
				new Object[][] {
					{  "1", "A", "I" },
					{  "1", "a", "I" },
					{  "2", "B", null },
			});
		assertSame (expect, result);

		
		result = DataFrameOperation.merge(dfLeft, dfRight).onColumn("id").rightJoin().get().sort("id");
		expect = DataFrameOperation.fromArray(
				new String[] { "id", "letter", "roman" },
				new Object[][] {
					{  "1", "A", "I" },
					{  "1", "a", "I" },
					{  "3", null, "III" }
			});
		assertSame (expect, result);

		
		result = DataFrameOperation.merge(dfLeft, dfRight).onColumn("id").innerJoin().get().sort("id");
		expect = DataFrameOperation.fromArray(
				new String[] { "id", "letter", "roman" },
				new Object[][] {
					{  "1", "A", "I" },
					{  "1", "a", "I" },
			});
		assertSame (expect, result);
	}

	public void testJoinNulls()
	{
	}
	
	public void testFactors()
	{
		
//		DataFrameOperation.merge (df1, df2);		
	}
	
}
