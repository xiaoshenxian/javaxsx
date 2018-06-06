package com.eroelf.javaxsx.util.index;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Maintains objects which are under a given key (the index).
 * 
 * @author weikun.zhong
 *
 * @param <K> the object type of the index key.
 * @param <V> the object type of the index value.
 */
public class Index<K, V>
{
	@FunctionalInterface
	public static interface KeyMapping<K, V>
	{
		public K getKey(V v);
	}

	@FunctionalInterface
	public static interface KeyIter<K, V>
	{
		public Iterable<K> getKeys(V v);
	}

	private Map<K, Set<V>> indexMap=new HashMap<>();

	public void addItem(K key, V item)
	{
		if(!indexMap.containsKey(key))
			indexMap.put(key, new HashSet<>());
		indexMap.get(key).add(item);
	}

	public void addItem(V item, KeyMapping<K, V> keyMapping)
	{
		addItem(keyMapping.getKey(item), item);
	}

	public void addItemToMultiKeys(V item, KeyIter<K, V> keyIter)
	{
		for(K key : keyIter.getKeys(item))
		{
			addItem(key, item);
		}
	}

	public void addItems(Iterable<V> items, KeyMapping<K, V> keyMapping)
	{
		for(V item : items)
		{
			addItem(item, keyMapping);
		}
	}

	public void addItemsToMultiKeys(Iterable<V> items, KeyIter<K, V> keyIter)
	{
		for(V item : items)
		{
			addItemToMultiKeys(item, keyIter);
		}
	}

	public void addIdx(K key, Set<V> values)
	{
		indexMap.put(key, values);
	}

	public void removeIdx(K key)
	{
		indexMap.remove(key);
	}

	public Set<V> get(Iterable<K> keyIter, @SuppressWarnings("unchecked") K... keys)
	{
		Set<V> res=new HashSet<>();
		if(!indexMap.isEmpty())
		{
			if(keyIter!=null)
				_union(res, keyIter);
			if(keys.length>0)
				_union(res, Arrays.asList(keys));
		}
		return res;
	}

	public Set<V> intersect(Set<V> res, Iterable<K> keyIter, @SuppressWarnings("unchecked") K... keys)
	{
		if(!indexMap.isEmpty())
		{
			if(keyIter!=null)
				_intersect(res, keyIter);
			if(keys.length>0)
				_intersect(res, Arrays.asList(keys));
		}
		return res;
	}

	protected Set<V> _intersect(Set<V> res, Iterable<K> keyIter)
	{
		for(K key : keyIter)
		{
			if(res.isEmpty())
				break;
			Set<V> temp=indexMap.get(key);
			if(temp!=null)
				res.retainAll(temp);
		}
		return res;
	}

	public Set<V> union(Set<V> res, Iterable<K> keyIter, @SuppressWarnings("unchecked") K... keys)
	{
		if(!indexMap.isEmpty())
		{
			if(keyIter!=null)
				_union(res, keyIter);
			if(keys.length>0)
				_union(res, Arrays.asList(keys));
		}
		return res;
	}

	protected Set<V> _union(Set<V> res, Iterable<K> keyIter)
	{
		for(K key : keyIter)
		{
			Set<V> temp=indexMap.get(key);
			if(temp!=null)
				res.addAll(temp);
		}
		return res;
	}

	public Set<V> intersectUnion(Set<V> res, Iterable<K> keyIter, @SuppressWarnings("unchecked") K... keys)
	{
		if(!res.isEmpty() && !indexMap.isEmpty())
			res.retainAll(union(new HashSet<>(), keyIter, keys));
		return res;
	}

	public Set<V> remove(Set<V> res, Iterable<K> keyIter, @SuppressWarnings("unchecked") K... keys)
	{
		if(keyIter!=null)
			_remove(res, keyIter);
		if(keys.length>0)
			_remove(res, Arrays.asList(keys));
		return res;
	}

	protected Set<V> _remove(Set<V> res, Iterable<K> keyIter)
	{
		if(!indexMap.isEmpty())
		{
			for(K key : keyIter)
			{
				Set<V> temp=indexMap.get(key);
				if(temp!=null)
					res.removeAll(temp);
			}
		}
		return res;
	}
}
