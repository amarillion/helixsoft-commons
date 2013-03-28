package nl.helixsoft.util;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ForwardingMultiset;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.primitives.Ints;

/**
 * This is like a SortedMultiset, but it sorts by frequency (i.e. Value) instead of by Key.
 * From: http://stackoverflow.com/a/4375211/3306, with modifications
 */
public class FreqSortMultiset<E> extends ForwardingMultiset<E> 
{
	/**
	 * Belongs with FreqSortMultiset.
	 */
	// From: http://stackoverflow.com/a/4375211/3306
	enum EntryComp implements Comparator<Multiset.Entry<?>> {
	    
		DESCENDING() {
	        @Override
	        public int compare(final Entry<?> a, final Entry<?> b) {
	            int result = Ints.compare(b.getCount(), a.getCount());
	            // If we return 0 if the objects are different, the objects will disappear from a sorted treeset.
	            // So as a tiebreaker, we compare the stringified forms of a & b, which works even if a & b don't implement comparable.
	            if (result == 0) result = a.toString().compareTo(b.toString());
	            return result;
	        }
	    },
	    ASCENDING() {
	        @Override
	        public int compare(final Entry<?> a, final Entry<?> b) {
	            return -DESCENDING.compare(a, b);
	        }
	    }
	    ;
	}
	
	private Multiset<E> delegate;
	private EntryComp comp;

	private FreqSortMultiset(Multiset<E> delegate, boolean ascending) 
	{
		this.delegate = delegate;
		if (ascending)
			this.comp = EntryComp.ASCENDING;
		else
			this.comp = EntryComp.DESCENDING;
		assert (EntryComp.ASCENDING != null); //For some reason, EntryComp is occasionally unitialized...
		assert (EntryComp.DESCENDING != null);
		assert (this.comp != null);
	}

	@Override
	protected Multiset<E> delegate() 
	{
		return delegate;
	}

	@Override
	public Set<Entry<E>> entrySet() 
	{
		assert (comp != null);
		TreeSet<Entry<E>> sortedEntrySet = new TreeSet<Entry<E>>(comp);
		sortedEntrySet.addAll(delegate.entrySet());
		return sortedEntrySet;
	}

	@Override
	public Set<E> elementSet() 
	{
		Set<E> sortedEntrySet = new LinkedHashSet<E>();
		for (Entry<E> en : entrySet())
			sortedEntrySet.add(en.getElement());
		return sortedEntrySet;
	}

    public static <E> FreqSortMultiset<E> create(boolean ascending) 
    {
    	return new FreqSortMultiset<E>(HashMultiset.<E> create(), ascending);
    }

    public static <E> FreqSortMultiset<E> create() 
    {
    	return new FreqSortMultiset<E>(HashMultiset.<E> create(), false);
    }

	/*
	 * For Testing
	 * public static void main(String[] args) {
	        Multiset<String> s = FreqSortMultiSet.create(false);
	        s.add("Hello");
	        s.add("Hello");
	        s.setCount("World", 3);
	        s.setCount("Bye", 5);
	        System.out.println(s.entrySet());
	    }*/

}