package nl.helixsoft.recordstream;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.helixsoft.recordstream.Reducer.Count;
import nl.helixsoft.recordstream.Reducer.GroupFunc;

import junit.framework.TestCase;

public class TestReduceAndCast extends TestCase {

	private static class MockRecordStream extends AbstractRecordStream
	{
		private final List<String> cols;
		private final Object[][] data;
		private final RecordMetaData rmd;
		
		MockRecordStream (String[] _cols, Object[][] data)
		{
			this.cols = Arrays.asList(_cols);
			this.data = data;
			rmd = new DefaultRecordMetaData(_cols);
		}
		
		int row = 0;

		@Override
		public int getNumCols() 
		{
			return cols.size();
		}

		@Override
		public String getColumnName(int i) {
			return cols.get(i);
		}

		@Override
		public Record getNext() throws RecordStreamException {
			if (row >= data.length) return null;
			return new DefaultRecord(rmd, data[row++]);
		}

		@Override
		public int getColumnIndex(String name) 
		{
			return cols.indexOf(name);
		}
	}

	/*
	 * Test Reduce, and in particular the AsSet and AsList functions
	 */
	@SuppressWarnings("unchecked")
	public void testReduceSet() throws RecordStreamException
	{
		Map<String, GroupFunc> map = new HashMap<String, GroupFunc>();
		map.put ("str1_set", new Reducer.AsSet("str1"));
		map.put ("str2_list", new Reducer.AsList("str2"));

		RecordStream rs = new MockRecordStream (
				new String[] { "groupid", "str1", "str2" },
				new Object[][] 	{
							{ 1, "a", "x" },
							{ 1, "a", "y" },
							{ 2, "b", "z" }, 
							{ 2, "c", "z" },
							{ 3, "d", "p" },
							{ 3, "e", "q" },
				});

		Reducer reducer = new Reducer (rs, "groupid", map);
		
		assertEquals (3, reducer.getNumCols());
		
		@SuppressWarnings("unused")
		int dummy = reducer.getColumnIndex("groupid"); // throws exception if not found
		dummy = reducer.getColumnIndex("str1_set"); // throws exception if not found
		dummy = reducer.getColumnIndex("str2_list"); // throws exception if not found
		try
		{
			dummy = reducer.getColumnIndex("strx_set");
			fail ("Expected an exception after requesting unknown column");
		}
		catch (IllegalArgumentException e) { /* OK, as expected */ }
		
		Set<Object> set1;
		List<Object> list2;
		
		Record r = reducer.getNext();
		set1 = (Set<Object>)r.getValue("str1_set");
		assertTrue (set1.size() == 1 && set1.contains ("a"));
		list2 = (List<Object>)r.getValue("str2_list");
		assertTrue (list2.size() == 2 && list2.get(0).equals("x") && list2.get(1).equals ("y"));
		
		r = reducer.getNext();
		set1 = (Set<Object>)r.getValue("str1_set");
		assertTrue (set1.size() == 2 && set1.contains ("b") && set1.contains ("c"));
		list2 = (List<Object>)r.getValue("str2_list");
		assertTrue (list2.size() == 2 && list2.get(0).equals("z") && list2.get(1).equals ("z"));

		r = reducer.getNext();
		set1 = (Set<Object>)r.getValue("str1_set");
		assertTrue (set1.size() == 2 && set1.contains ("d") && set1.contains ("e"));
		list2 = (List<Object>)r.getValue("str2_list");
		assertTrue (list2.size() == 2 && list2.get(0).equals("p") && list2.get(1).equals ("q"));

		r = reducer.getNext();
		assertNull (r);
	}
	
	public void testReduce() throws RecordStreamException
	{
		Map<String, GroupFunc> map = new HashMap<String, GroupFunc>();
		map.put ("count", new Count());
		map.put ("avg_float", new Reducer.AverageFloat("float"));
		map.put ("concat_str", new Reducer.Concatenate("str", "-"));

		RecordStream rs = new MockRecordStream (
				new String[] { "groupid", "float", "str" },
				new Object[][] 	{
							{ 1, 1.0f, "abc" },
							{ 1, 2.0f, "def" },
							{ 2, 3.0f, "ghi" }, 
							{ 3, 4.0f, "jkl" }
				});

		Reducer reducer = new Reducer (rs, "groupid", map);
		
		assertEquals (4, reducer.getNumCols());
		
		Map<String, Integer> idx = new HashMap<String, Integer>();
		for (int i = 0; i < reducer.getNumCols(); ++i) idx.put (reducer.getColumnName(i), i);
		assertTrue (idx.containsKey("groupid"));
		assertTrue (idx.containsKey("count"));
		assertTrue (idx.containsKey("avg_float"));
		assertTrue (idx.containsKey("concat_str"));
		
		Record r;
		r = reducer.getNext();
		assertEquals (1, r.getValue(idx.get("groupid")));
		assertEquals (2, r.getValue(idx.get("count")));
		assertEquals (1.5f, (Float)r.getValue(idx.get("avg_float")), 0.01f);
		assertEquals ("abc-def", r.getValue(idx.get("concat_str")));
		
		r = reducer.getNext();
		assertEquals (2, r.getValue(idx.get("groupid")));
		assertEquals (1, r.getValue(idx.get("count")));
		assertEquals (3.0f, (Float)r.getValue(idx.get("avg_float")), 0.01f);
		assertEquals ("ghi", r.getValue(idx.get("concat_str")));

		r = reducer.getNext();
		assertEquals (3, r.getValue(idx.get("groupid")));
		assertEquals (1, r.getValue(idx.get("count")));
		assertEquals (4.0f, (Float)r.getValue(idx.get("avg_float")), 0.01f);
		assertEquals ("jkl", r.getValue(idx.get("concat_str")));

		r = reducer.getNext();
		assertNull (r);
	}

	public void testCast() throws RecordStreamException
	{
		RecordStream rs = new MockRecordStream (
				new String[] { "group", "col", "var" },
				new Object[][] 	{
							{ "group1", "col1", "abc" },
							{ "group1", "col2", "def" },
							{ "group2", "col1", "ghi" }, 
							{ "group2", "col2", "jkl" }
				});

		Cast cast = new Cast (rs, "group", "col", "var");
		
		assertEquals (3, cast.getNumCols());
		
		Map<String, Integer> idx = new HashMap<String, Integer>();
		for (int i = 0; i < cast.getNumCols(); ++i) idx.put (cast.getColumnName(i), i);
		assertTrue (idx.containsKey("group"));
		assertTrue (idx.containsKey("col1"));
		assertTrue (idx.containsKey("col2"));
		
		Record r;
		r = cast.getNext();
		assertEquals ("group1", r.getValue(idx.get("group")));
		assertEquals ("abc", r.getValue(idx.get("col1")));
		assertEquals ("def", r.getValue(idx.get("col2")));
		
		r = cast.getNext();
		assertEquals ("group2", r.getValue(idx.get("group")));
		assertEquals ("ghi", r.getValue(idx.get("col1")));
		assertEquals ("jkl", r.getValue(idx.get("col2")));

		r = cast.getNext();
		assertNull (r);
	}

	public void testCastMultiKey() throws RecordStreamException
	{
		RecordStream rs = new MockRecordStream (
				new String[] { "group", "subgroup", "col", "var" },
				new Object[][] 	{
							{ "group0", "subgroup1", "col1", "abc" },
							{ "group0", "subgroup1", "col2", "def" },
							{ "group0", "subgroup2", "col1", "ghi" }, 
							{ "group0", "subgroup2", "col2", "jkl" }
				});

		Cast cast = new Cast (rs, new String[] { "group", "subgroup" }, "col", "var");
		
		assertEquals (4, cast.getNumCols());
		
		Map<String, Integer> idx = new HashMap<String, Integer>();
		for (int i = 0; i < cast.getNumCols(); ++i) idx.put (cast.getColumnName(i), i);
		assertTrue (idx.containsKey("group"));
		assertTrue (idx.containsKey("subgroup"));
		assertTrue (idx.containsKey("col1"));
		assertTrue (idx.containsKey("col2"));
		
		Record r;
		r = cast.getNext();
		assertEquals ("group0", r.getValue(idx.get("group")));
		assertEquals ("subgroup1", r.getValue(idx.get("subgroup")));
		assertEquals ("abc", r.getValue(idx.get("col1")));
		assertEquals ("def", r.getValue(idx.get("col2")));
		
		r = cast.getNext();
		assertEquals ("group0", r.getValue(idx.get("group")));
		assertEquals ("subgroup2", r.getValue(idx.get("subgroup")));
		assertEquals ("ghi", r.getValue(idx.get("col1")));
		assertEquals ("jkl", r.getValue(idx.get("col2")));

		r = cast.getNext();
		assertNull (r);
	}

}
