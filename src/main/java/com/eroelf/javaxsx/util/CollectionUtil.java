package com.eroelf.javaxsx.util;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

/**
 * Some APIs for containers.
 * 
 * @author weikun.zhong
 */
public class CollectionUtil
{
	/**
	 * Put all elements that both collection c1 and collection c2 has into the retainedCollection, maintaining both c1 and c2 unchanged.
	 * This method will first adds all elements of the smaller collection of c1 and c2 into retainedCollection, and then calls the {@linkplain Collection retainAll} methods of the retainedCollection with c2 as the parameter.
	 * 
	 * @param <T> the element type.
	 * 
	 * @param c1 One Collection object to be processing.
	 * @param c2 The other Collection object to be processing.
	 * @param retainedCollection Receive the retaining result.
	 */
	public static <T> void getRetainedAll(Collection<T> c1, Collection<T> c2, Collection<T> retainedCollection)
	{
		if(c1.size()<=c2.size())
		{
			retainedCollection.addAll(c1);
			retainedCollection.retainAll(c2);
		}
		else
		{
			retainedCollection.addAll(c2);
			retainedCollection.retainAll(c1);
		}
	}

	/**
	 * Retains only the elements in sorted collection c1 that are contained in sorted collection c2.
	 * In other words, removes from sorted collection c1 all of its elements that are not contained in sorted collection c2.
	 * <p><i>Both collections must be sorted by the <b>same comparator</b> before calling this method, and their iterator() method must be able to return an iterator which can <b>iterate the collection according to the sorted order</b>.</i></p>
	 * 
	 * @param <T> the element type.
	 * 
	 * @param c1 Collection containing elements to be checking for retaining according to elements in c2.
	 * @param c2 Collection containing elements to be retaining in c1.
	 */
	public static <T extends Comparable<T>> void retainAllForSortedCollections(Collection<T> c1, Collection<T> c2)
	{
		Iterator<T> iter1=c1.iterator();
		Iterator<T> iter2=c2.iterator();
		if(iter1.hasNext())
		{
			T t1=iter1.next();
			int v=0;
			while(iter2.hasNext())
			{
				T t2=iter2.next();
				while(true)
				{
					v=t1.compareTo(t2);
					if(v<0)
					{
						iter1.remove();
					}
					else if(v>0)
						break;
					if(iter1.hasNext())
					{
						t1=iter1.next();
					}
					else
						return;
				}
			}
			iter1.remove();
			while(iter1.hasNext())
			{
				iter1.next();
				iter1.remove();
			}
		}
	}

	/**
	 * Put all the elements in sorted collection c1 that are contained in sorted collection c2 into collection retainedCollection, maintaining the insertion order the same as the sorted order (which must be the same order the input collections' iterator() method gives), and leaving both the input collections unchanged.
	 * <p><i>Both input collections must be sorted by the <b>same comparator</b> before calling this method, and their iterator() method must be able to return an iterator which can <b>iterate the collection according to the sorted order</b>.</i></p>
	 * 
	 * @param <T> the element type.
	 * 
	 * @param c1 Collection containing elements to be checking for retaining according to elements in c2.
	 * @param c2 Collection containing elements to be retaining in c1.
	 * @param retainedCollection Receives the retaining result.
	 */
	public static <T extends Comparable<T>> void getRetainedAllForSortedCollections(Collection<T> c1, Collection<T> c2, Collection<T> retainedCollection)
	{
		Iterator<T> iter1=c1.iterator();
		Iterator<T> iter2=c2.iterator();
		if(iter1.hasNext())
		{
			T t1=iter1.next();
			int v=0;
			while(iter2.hasNext())
			{
				T t2=iter2.next();
				while(true)
				{
					v=t1.compareTo(t2);
					if(v==0)
					{
						retainedCollection.add(t1);
					}
					else if(v>0)
						break;
					if(iter1.hasNext())
					{
						t1=iter1.next();
					}
					else
						return;
				}
			}
		}
	}

	/**
	 * Randomly select selectSize elements from values, maintaining the original order of the data provided. The size of values may be unknown.
	 * 
	 * @param <T> the element type.
	 * 
	 * @param values The {@link Iterable} object contains the data to be selecting.
	 * @param selectSize Indicates how many elements should be selected. If the selectSize is larger than the size of the data contained in the values, all data will be retained.
	 * @param des The receiver list of the selected elements.
	 */
	public static <T> void randomlySelect(Iterable<T> values, int selectSize, List<T> des)
	{
		randomlySelect(values.iterator(), selectSize, des);
	}

	/**
	 * Randomly select selectSize elements from valuesIter, maintaining the original order of the data provided. The size of valuesIter may be unknown.
	 * 
	 * @param <T> the element type.
	 * 
	 * @param valuesIter The iterator of the data to be selecting.
	 * @param selectSize Indicates how many elements should be selected. If the selectSize is larger than the size of the data provided by the valuesIter, all data will be retained.
	 * @param des The receiver list of the selected elements.
	 */
	public static <T> void randomlySelect(Iterator<T> valuesIter, int selectSize, List<T> des)
	{
		Random random=new Random(System.currentTimeMillis());
		int count=0;
		List<Entry<Integer, T>> retains=new ArrayList<>(selectSize);
		while(valuesIter.hasNext())
		{
			T value=valuesIter.next();
			if(count<selectSize)
				retains.add(new AbstractMap.SimpleEntry<Integer, T>(count, value));
			else
			{
				int idx=random.nextInt(count+1);
				if(idx<selectSize)
					retains.set(idx, new AbstractMap.SimpleEntry<Integer, T>(count, value));
			}
			++count;
		}
		Collections.sort(retains, new Comparator<Entry<Integer, T>>() {
			@Override
			public int compare(Entry<Integer, T> o1, Entry<Integer, T> o2)
			{
				return o1.getKey().compareTo(o2.getKey());
			}
		});
		for(Entry<Integer, T> entry : retains)
		{
			des.add(entry.getValue());
		}
	}
}
