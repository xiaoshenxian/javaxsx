package com.eroelf.javaxsx.util.index;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Maintains objects which are under a given key (the index).
 * 
 * @author weikun.zhong
 *
 * @param <K> the object type of the index key.
 * @param <V> the object type of the index value.
 */
public class Index<K, V> implements Serializable
{
	private static final long serialVersionUID=5287011192325259476L;

	protected Map<K, Set<V>> indexMap;
	protected transient Supplier<Set<V>> setFactory;

	public Index(Supplier<Set<V>> setFactory)
	{
		this.setFactory=setFactory;
	}

	public Index(boolean concurrent)
	{
		if(concurrent)
		{
			indexMap=new ConcurrentHashMap<>();
			setFactory=() -> Collections.newSetFromMap(new ConcurrentHashMap<V, Boolean>());
		}
		else
		{
			indexMap=new HashMap<>();
			setFactory=HashSet::new;
		}
	}

	public void setSetFactory(Supplier<Set<V>> setFactory)
	{
		this.setFactory=setFactory;
	}

	public void addItem(K key, V item)
	{
		Set<V> set=indexMap.get(key);
		if(set==null)
		{
			set=setFactory.get();
			indexMap.put(key, set);
		}
		set.add(item);
	}

	public void addItem(V item, Function<V, K> keyMapping)
	{
		addItem(keyMapping.apply(item), item);
	}

	public void addItemToMultiKeys(V item, Function<V, Iterable<K>> keyIter)
	{
		for(K key : keyIter.apply(item))
		{
			addItem(key, item);
		}
	}

	public void addItems(K key, Iterable<V> items)
	{
		for(V item : items)
		{
			addItem(key, item);
		}
	}

	public void addItems(Iterable<V> items, Function<V, K> keyMapping)
	{
		for(V item : items)
		{
			addItem(item, keyMapping);
		}
	}

	public void addItemsToMultiKeys(Iterable<V> items, Function<V, Iterable<K>> keyIter)
	{
		for(V item : items)
		{
			addItemToMultiKeys(item, keyIter);
		}
	}

	public void addAll(Index<K, V> index)
	{
		for(Entry<K, Set<V>> entry : index.indexMap.entrySet())
		{
			addItems(entry.getKey(), entry.getValue());
		}
	}

	public Set<V> putIdx(K key, Iterable<V> items)
	{
		Set<V> set=setFactory.get();
		if(items!=null)
		{
			for(V item : items)
			{
				set.add(item);
			}
		}
		return indexMap.put(key, set);
	}

	public Set<V> removeIdx(K key)
	{
		return indexMap.remove(key);
	}

	public void removeFromIdx(K key, V item)
	{
		Set<V> set=indexMap.get(key);
		if(set!=null)
		{
			set.remove(item);
			if(set.isEmpty())
				indexMap.remove(key);
		}
	}

	public void removeItem(V item)
	{
		Iterator<Entry<K, Set<V>>> entryIter=indexMap.entrySet().iterator();
		while(entryIter.hasNext())
		{
			Entry<K, Set<V>> entry=entryIter.next();
			entry.getValue().remove(item);
			if(entry.getValue().isEmpty())
				entryIter.remove();
		}
	}

	public boolean isEmpty()
	{
		return indexMap.isEmpty();
	}

	public Set<K> getKeys()
	{
		return new HashSet<>(indexMap.keySet());
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

	public Set<V> subtract(Set<V> res, Iterable<K> keyIter, @SuppressWarnings("unchecked") K... keys)
	{
		if(keyIter!=null)
			_subtract(res, keyIter);
		if(keys.length>0)
			_subtract(res, Arrays.asList(keys));
		return res;
	}

	protected Set<V> _subtract(Set<V> res, Iterable<K> keyIter)
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
