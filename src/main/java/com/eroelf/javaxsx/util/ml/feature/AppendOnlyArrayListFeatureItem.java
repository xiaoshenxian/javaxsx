package com.eroelf.javaxsx.util.ml.feature;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * Base class of all items (from any source) for modeling and ranking with features sparsely stored in an append-only ArrayList.
 * There is no conflict detection for features has same index.
 * The getFeature method is not optimized.
 * 
 * @author weikun.zhong
 */
public class AppendOnlyArrayListFeatureItem extends Item
{
	protected List<Entry<Integer, Double>> featureList;

	public AppendOnlyArrayListFeatureItem()
	{
		featureList=new ArrayList<>();
	}

	public AppendOnlyArrayListFeatureItem(int validSize)
	{
		featureList=new ArrayList<>(validSize);
	}

	@Override
	public double getFeature(int idx)
	{
		for(Entry<Integer, Double> entry : featureList)
		{
			if(entry.getKey()==idx)
				return entry.getValue();
		}
		return 0;
	}

	/**
	 * Attach the new feature and its index to the end of the list, with no conflict detection for features has same index.
	 */
	@Override
	public void setFeature(int idx, double value)
	{
		if(value!=0)
			featureList.add(new AbstractMap.SimpleEntry<Integer, Double>(idx, value));
	}

	/**
	 * Delete feature with the given index, if it can be found.
	 * Only the first feature found will be delete, since the method assumes there is no repetition of indexes.
	 */
	@Override
	public void deleteFeature(int idx)
	{
		Iterator<Entry<Integer, Double>> iter=featureList.iterator();
		while(iter.hasNext())
		{
			Entry<Integer, Double> entry=iter.next();
			if(entry.getKey()==idx)
			{
				iter.remove();
				break;
			}
		}
	}

	@Override
	public List<Entry<Integer, Double>> getFeatures()
	{
		return featureList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setFeatures(Object features)
	{
		this.featureList=(List<Entry<Integer, Double>>)features;
	}

	@Override
	public String getFeatureString()
	{
		StringBuilder stringBuilder=new StringBuilder();
		Collections.sort(featureList, COMPARATOR);
		for(Entry<Integer, Double> entry : featureList)
		{
			double value=entry.getValue();
			if(value!=0)
			{
				stringBuilder.append(" ").append(entry.getKey()).append(":").append(value);
			}
		}
		return stringBuilder.toString().trim();
	}

	@Override
	public Iterator<IndexedFeature> validFeatureIterator()
	{
		return new ValidFeatureIterator();
	}

	@Override
	public Iterable<IndexedFeature> getValidFeatures()
	{
		return new Iterable<IndexedFeature>() {
			@Override
			public Iterator<IndexedFeature> iterator()
			{
				return validFeatureIterator();
			}
		};
	}

	private static final Comparator<Entry<Integer, Double>> COMPARATOR=new Comparator<Entry<Integer,Double>>() {
		@Override
		public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2)
		{
			return Integer.compare(o1.getKey(), o2.getKey());
		}
	};

	private class ValidFeatureIterator implements Iterator<IndexedFeature>
	{
		private Iterator<Entry<Integer, Double>> iter=featureList.iterator();
		private Entry<Integer, Double> entry;
		private IndexedFeature indexedFeature=new IndexedFeature() {
			@Override
			public int getIdx()
			{
				return entry.getKey();
			}
			
			@Override
			public double getFeature()
			{
				return entry.getValue();
			}
		};

		@Override
		public boolean hasNext()
		{
			return iter.hasNext();
		}

		@Override
		public IndexedFeature next()
		{
			entry=iter.next();
			return indexedFeature;
		}

		@Override
		public void remove()
		{
			iter.remove();
		}
	}
}
