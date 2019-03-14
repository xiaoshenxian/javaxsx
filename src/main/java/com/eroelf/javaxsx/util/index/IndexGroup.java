package com.eroelf.javaxsx.util.index;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.eroelf.javaxsx.util.index.Index.KeyIter;
import com.eroelf.javaxsx.util.index.Index.KeyMapping;

/**
 * Maintains a group of indices for a number of objects.
 * 
 * @author weikun.zhong
 *
 * @param <V> the object type of the index value.
 */
public class IndexGroup<V>
{
	private static final Index<?, ?> IDLE_INDEX=new Index<>(false);

	protected Map<String, Index<?, V>> groupMap=new HashMap<>();
	protected boolean concurrent;

	public IndexGroup(boolean concurrent)
	{
		this.concurrent=concurrent;
	}

	@SuppressWarnings("unchecked")
	public <K> void addItem(String groupName, K key, V item)
	{
		if(!groupMap.containsKey(groupName))
			groupMap.put(groupName, new Index<K, V>(concurrent));
		((Index<K, V>)groupMap.get(groupName)).addItem(key, item);
	}

	@SuppressWarnings("unchecked")
	public <K> void addItem(String groupName, V item, KeyMapping<K, V> keyMapping)
	{
		if(!groupMap.containsKey(groupName))
			groupMap.put(groupName, new Index<K, V>(concurrent));
		((Index<K, V>)groupMap.get(groupName)).addItem(item, keyMapping);
	}

	@SuppressWarnings("unchecked")
	public <K> void addItemToMultiKeys(String groupName, V item, KeyIter<K, V> keyIter)
	{
		if(!groupMap.containsKey(groupName))
			groupMap.put(groupName, new Index<K, V>(concurrent));
		((Index<K, V>)groupMap.get(groupName)).addItemToMultiKeys(item, keyIter);
	}

	@SuppressWarnings("unchecked")
	public <K> void addItems(String groupName, Iterable<V> items, KeyMapping<K, V> keyMapping)
	{
		if(!groupMap.containsKey(groupName))
			groupMap.put(groupName, new Index<K, V>(concurrent));
		((Index<K, V>)groupMap.get(groupName)).addItems(items, keyMapping);
	}

	@SuppressWarnings("unchecked")
	public <K> void addItemsToMultiKeys(String groupName, Iterable<V> items, KeyIter<K, V> keyIter)
	{
		if(!groupMap.containsKey(groupName))
			groupMap.put(groupName, new Index<K, V>(concurrent));
		((Index<K, V>)groupMap.get(groupName)).addItemsToMultiKeys(items, keyIter);
	}

	public void addIdx(String name, Index<?, V> index)
	{
		groupMap.put(name, index);
	}

	public void removeIdx(String name)
	{
		groupMap.remove(name);
	}

	public void removeItem(V item)
	{
		Iterator<Entry<String, Index<?, V>>> entryIter=groupMap.entrySet().iterator();
		while(entryIter.hasNext())
		{
			Entry<String, Index<?, V>> entry=entryIter.next();
			entry.getValue().removeItem(item);
			if(entry.getValue().isEmpty())
				entryIter.remove();
		}
	}

	public Index<?, V> getIdx(String name)
	{
		return groupMap.get(name);
	}

	public <K> Set<V> get(String name, Iterable<K> keyIter, @SuppressWarnings("unchecked") K... keys)
	{
		Index<K, V> idx=getNotNullIdx(name);
		return idx.get(keyIter, keys);
	}

	public <K> Set<V> intersect(Set<V> res, String name, Iterable<K> keyIter, @SuppressWarnings("unchecked") K... keys)
	{
		Index<K, V> idx=getNotNullIdx(name);
		return idx.intersect(res, keyIter, keys);
	}

	public <K> Set<V> union(Set<V> res, String name, Iterable<K> keyIter, @SuppressWarnings("unchecked") K... keys)
	{
		Index<K, V> idx=getNotNullIdx(name);
		return idx.union(res, keyIter, keys);
	}

	public <K> Set<V> intersectUnion(Set<V> res, String name, Iterable<K> keyIter, @SuppressWarnings("unchecked") K... keys)
	{
		Index<K, V> idx=getNotNullIdx(name);
		return idx.intersectUnion(res, keyIter, keys);
	}

	public <K> Set<V> remove(Set<V> res, String name, Iterable<K> keyIter, @SuppressWarnings("unchecked") K... keys)
	{
		Index<K, V> idx=getNotNullIdx(name);
		return idx.remove(res, keyIter, keys);
	}

	@SuppressWarnings("unchecked")
	public <K> Index<K, V> getNotNullIdx(String name)
	{
		Index<K, V> index=(Index<K, V>)groupMap.get(name);
		if(index!=null)
			return index;
		else
			return (Index<K, V>)IDLE_INDEX;
	}
}
