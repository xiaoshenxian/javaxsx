package com.eroelf.javaxsx.util.index;

import java.io.Serializable;
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
 * Maintains a group of indices for a number of objects.
 * 
 * @author weikun.zhong
 *
 * @param <V> the object type of the index value.
 */
public class IndexGroup<V> implements Serializable
{
	private static final long serialVersionUID=-1767062789829143153L;

	private static final Index<?, ?> IDLE_INDEX=new Index<>(false);

	protected Map<String, Index<?, V>> groupMap=new HashMap<>();
	protected transient Supplier<Index<?, V>> indexFactory;

	public IndexGroup(Map<String, Index<?, V>> groupMap, Supplier<Index<?, V>> indexFactory)
	{
		this.groupMap=groupMap;
		this.indexFactory=indexFactory;
	}

	public IndexGroup(boolean concurrent)
	{
		this.indexFactory=() -> new Index<>(concurrent);
		if(concurrent)
			groupMap=new ConcurrentHashMap<>();
		else
			groupMap=new HashMap<>();
	}

	public void setIndexFactory(Supplier<Index<?, V>> indexFactory)
	{
		this.indexFactory=indexFactory;
	}

	@SuppressWarnings("unchecked")
	public <K> void addItem(String groupName, K key, V item)
	{
		Index<K, V> index=(Index<K, V>)groupMap.get(groupName);
		if(index==null)
		{
			index=(Index<K, V>)indexFactory.get();
			groupMap.put(groupName, index);
		}
		index.addItem(key, item);
	}

	@SuppressWarnings("unchecked")
	public <K> void addItem(String groupName, V item, Function<V, K> keyMapping)
	{
		Index<K, V> index=(Index<K, V>)groupMap.get(groupName);
		if(index==null)
		{
			index=(Index<K, V>)indexFactory.get();
			groupMap.put(groupName, index);
		}
		index.addItem(item, keyMapping);
	}

	@SuppressWarnings("unchecked")
	public <K> void addItemToMultiKeys(String groupName, V item, Function<V, Iterable<K>> keyIter)
	{
		Index<K, V> index=(Index<K, V>)groupMap.get(groupName);
		if(index==null)
		{
			index=(Index<K, V>)indexFactory.get();
			groupMap.put(groupName, index);
		}
		index.addItemToMultiKeys(item, keyIter);
	}

	@SuppressWarnings("unchecked")
	public <K> void addItems(String groupName, K key, Iterable<V> items)
	{
		Index<K, V> index=(Index<K, V>)groupMap.get(groupName);
		if(index==null)
		{
			index=(Index<K, V>)indexFactory.get();
			groupMap.put(groupName, index);
		}
		index.addItems(key, items);
	}

	@SuppressWarnings("unchecked")
	public <K> void addItems(String groupName, Iterable<V> items, Function<V, K> keyMapping)
	{
		Index<K, V> index=(Index<K, V>)groupMap.get(groupName);
		if(index==null)
		{
			index=(Index<K, V>)indexFactory.get();
			groupMap.put(groupName, index);
		}
		index.addItems(items, keyMapping);
	}

	@SuppressWarnings("unchecked")
	public <K> void addItemsToMultiKeys(String groupName, Iterable<V> items, Function<V, Iterable<K>> keyIter)
	{
		Index<K, V> index=(Index<K, V>)groupMap.get(groupName);
		if(index==null)
		{
			index=(Index<K, V>)indexFactory.get();
			groupMap.put(groupName, index);
		}
		index.addItemsToMultiKeys(items, keyIter);
	}

	@SuppressWarnings("unchecked")
	public <K> void addIndex(String groupName, Index<K, V> index)
	{
		Index<K, V> idx=(Index<K, V>)groupMap.get(groupName);
		if(idx==null)
		{
			idx=(Index<K, V>)indexFactory.get();
			groupMap.put(groupName, idx);
		}
		idx.addAll(index);
	}

	@SuppressWarnings("unchecked")
	public <KO, KN> Index<KO, V> putIndex(String groupName, Index<KN, V> index)
	{
		Index<KN, V> idx=(Index<KN, V>)indexFactory.get();
		if(index!=null)
			idx.addAll(index);
		return (Index<KO, V>)groupMap.put(groupName, idx);
	}

	@SuppressWarnings("unchecked")
	public <K> Index<K, V> removeIndex(String groupName)
	{
		return (Index<K, V>)groupMap.remove(groupName);
	}

	@SuppressWarnings("unchecked")
	public <K> void removeIndexIdx(String groupName, K key)
	{
		Index<K, V> index=(Index<K, V>)groupMap.get(groupName);
		if(index!=null)
		{
			index.removeIdx(key);
			if(index.isEmpty())
				groupMap.remove(groupName);
		}
	}

	@SuppressWarnings("unchecked")
	public <K> void removeFromIndexIdx(String groupName, K key, V item)
	{
		Index<K, V> index=(Index<K, V>)groupMap.get(groupName);
		if(index!=null)
		{
			index.removeFromIdx(key, item);
			if(index.isEmpty())
				groupMap.remove(groupName);
		}
	}

	public void removeIndexItem(String groupName, V item)
	{
		Index<?, V> index=groupMap.get(groupName);
		if(index!=null)
		{
			index.removeItem(item);
			if(index.isEmpty())
				groupMap.remove(groupName);
		}
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

	public Set<String> getGroupNames()
	{
		return new HashSet<>(groupMap.keySet());
	}

	public <K> Set<K> getIndexKeys(String groupName)
	{
		Index<K, V> index=getNotNullIdx(groupName);
		return index.getKeys();
	}

	@SuppressWarnings("unchecked")
	public <K> Index<K, V> getIndex(String groupName)
	{
		return (Index<K, V>)groupMap.get(groupName);
	}

	public <K> Set<V> get(String groupName, Iterable<K> keyIter, @SuppressWarnings("unchecked") K... keys)
	{
		Index<K, V> index=getNotNullIdx(groupName);
		return index.get(keyIter, keys);
	}

	public <K> Set<V> intersect(Set<V> res, String groupName, Iterable<K> keyIter, @SuppressWarnings("unchecked") K... keys)
	{
		Index<K, V> index=getNotNullIdx(groupName);
		return index.intersect(res, keyIter, keys);
	}

	public <K> Set<V> union(Set<V> res, String groupName, Iterable<K> keyIter, @SuppressWarnings("unchecked") K... keys)
	{
		Index<K, V> index=getNotNullIdx(groupName);
		return index.union(res, keyIter, keys);
	}

	public <K> Set<V> intersectUnion(Set<V> res, String groupName, Iterable<K> keyIter, @SuppressWarnings("unchecked") K... keys)
	{
		Index<K, V> index=getNotNullIdx(groupName);
		return index.intersectUnion(res, keyIter, keys);
	}

	public <K> Set<V> subtract(Set<V> res, String groupName, Iterable<K> keyIter, @SuppressWarnings("unchecked") K... keys)
	{
		Index<K, V> index=getNotNullIdx(groupName);
		return index.subtract(res, keyIter, keys);
	}

	@SuppressWarnings("unchecked")
	protected <K> Index<K, V> getNotNullIdx(String groupName)
	{
		Index<K, V> index=(Index<K, V>)groupMap.get(groupName);
		if(index!=null)
			return index;
		else
			return (Index<K, V>)IDLE_INDEX;
	}
}
