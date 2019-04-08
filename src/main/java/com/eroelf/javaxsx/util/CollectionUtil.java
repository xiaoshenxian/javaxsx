package com.eroelf.javaxsx.util;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Some APIs for containers.
 * 
 * @author weikun.zhong
 */
public final class CollectionUtil
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

	/**
	 * Check if the given map contains the given keys in a nested form, e.g., like {@code map[key1][key2][...][keyN]} in python.
	 * 
	 * @param map the object to check.
	 * @param keys the keys which are supposed to be contained by the {@code map} in a nested form according to the {@code keys}' order.
	 * @return {@code true} if all {@code keys} are contained by the {@code map} in the nested form defined by the {@code keys}' order, otherwise {@code false}.
	 */
	public static boolean nestedContainsKeys(Object map, Object... keys)
	{
		for(Object key : keys)
		{
			if(map instanceof Map && ((Map<?, ?>)map).containsKey(key))
				map=((Map<?, ?>)map).get(key);
			else
				return false;
		}
		return true;
	}

	/**
	 * Find the first N "minimal" elements of the given {@code iterable} by using a {@link PriorityQueue}.
	 * The "minimal" strategy is defined by the elements' natural order.
	 * 
	 * @param <T> the element type.
	 * 
	 * @param iterable an {@link Iterable} object to retrieve elements.
	 * @param num the number of "minimal" elements to be retrieving. The returned {@link PriorityQueue} object will contain all the elements if the {@code iterable} has elements less than the given {@code num}.
	 * @return a {@link PriorityQueue} instance containing the first N "minimal" elements.
	 */
	public static <T extends Comparable<T>> PriorityQueue<T> minimalN(Iterable<T> iterable, int num)
	{
		return minimalN(iterable.iterator(), num);
	}

	/**
	 * Find the first N "minimal" elements of the given {@code iterator} by using a {@link PriorityQueue}.
	 * The "minimal" strategy is defined by the elements' natural order.
	 * 
	 * @param <T> the element type.
	 * 
	 * @param iterator an {@link Iterator} object to retrieve elements.
	 * @param num the number of "minimal" elements to be retrieving. The returned {@link PriorityQueue} object will contain all the elements if the {@code iterator} has elements less than the given {@code num}.
	 * @return a {@link PriorityQueue} instance containing the first N "minimal" elements.
	 */
	public static <T extends Comparable<T>> PriorityQueue<T> minimalN(Iterator<T> iterator, int num)
	{
		return minimalN(iterator, num, Comparator.naturalOrder());
	}

	/**
	 * Find the first N "minimal" elements of the given {@code iterable} by using a {@link PriorityQueue}.
	 * The "minimal" strategy is defined by the given {@code comparator}.
	 * 
	 * @param <T> the element type.
	 * 
	 * @param iterable an {@link Iterable} object to retrieve elements.
	 * @param num the number of "minimal" elements to be retrieving. The returned {@link PriorityQueue} object will contain all the elements if the {@code iterable} has elements less than the given {@code num}.
	 * @param comparator the {@link Comparator} object defining the "minimal" strategy.
	 * @return a {@link PriorityQueue} instance containing the first N "minimal" elements.
	 */
	public static <T> PriorityQueue<T> minimalN(Iterable<T> iterable, int num, Comparator<T> comparator)
	{
		return minimalN(iterable.iterator(), num, comparator);
	}

	/**
	 * Find the first N "minimal" elements of the given {@code iterator} by using a {@link PriorityQueue}.
	 * The "minimal" strategy is defined by the given {@code comparator}.
	 * 
	 * @param <T> the element type.
	 * 
	 * @param iterator an {@link Iterator} object to retrieve elements.
	 * @param num the number of "minimal" elements to be retrieving. The returned {@link PriorityQueue} object will contain all the elements if the {@code iterator} has elements less than the given {@code num}.
	 * @param comparator the {@link Comparator} object defining the "minimal" strategy.
	 * @return a {@link PriorityQueue} instance containing the first N "minimal" elements.
	 */
	public static <T> PriorityQueue<T> minimalN(Iterator<T> iterator, int num, Comparator<T> comparator)
	{
		PriorityQueue<T> heap=new PriorityQueue<>(num+1, Collections.reverseOrder(comparator));
		while(iterator.hasNext())
		{
			heap.add(iterator.next());
			if(heap.size()>num)
				heap.remove();
		}
		return heap;
	}

	/**
	 * Get an {@link Iterator} instance that iterates the {@code heap} elements in the order defined by the {@code heap}'s {@link Comparator} object.
	 * 
	 * @param <T> the element type of the given {@code heap}.
	 * 
	 * @param heap the heap to be retrieve elements.
	 * @return the {@link Iterator} instance.
	 */
	public static <T> Iterator<T> deheap(PriorityQueue<T> heap)
	{
		return new Iterator<T>() {
			@Override
			public boolean hasNext()
			{
				return !heap.isEmpty();
			}

			@Override
			public T next()
			{
				return heap.remove();
			}
		};
	}

	public static <T extends Collection<E>, E> T toCollection(Supplier<T> creator, Iterable<E> iterable)
	{
		return toCollection(creator, iterable.iterator());
	}

	public static <T extends Collection<E>, E> T toCollection(Supplier<T> creator, Iterator<E> iter)
	{
		T res=creator.get();
		while(iter.hasNext())
			res.add(iter.next());
		return res;
	}

	public static <T extends Map<K, V>, K, V> T toMap(Supplier<T> creator, Iterable<K> keyIterable, Iterable<V> valueIterable)
	{
		return toMap(creator, keyIterable.iterator(), valueIterable.iterator());
	}

	public static <T extends Map<K, V>, K, V> T toMap(Supplier<T> creator, Iterator<K> keyIter, Iterator<V> valueIter)
	{
		T map=creator.get();
		while(keyIter.hasNext() && valueIter.hasNext())
			map.put(keyIter.next(), valueIter.next());
		return map;
	}

	public static <T extends Map<K, V>, K, V> T toMap(Supplier<T> creator, Iterable<Entry<K, V>> iterable)
	{
		return toMap(creator, iterable.iterator());
	}

	public static <T extends Map<K, V>, K, V> T toMap(Supplier<T> creator, Iterator<Entry<K, V>> iter)
	{
		T map=creator.get();
		while(iter.hasNext())
		{
			Entry<K, V> entry=iter.next();
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}

	private CollectionUtil()
	{}

	@Override
	public CollectionUtil clone()
	{
		throw new UnsupportedOperationException("This method is not allowed!");
	}
}
