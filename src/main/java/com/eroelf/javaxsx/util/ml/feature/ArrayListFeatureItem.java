package com.eroelf.javaxsx.util.ml.feature;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.eroelf.javaxsx.util.ml.feature.Item;

/**
 * Base class of all items (from any source) for modeling and ranking with features sparsely stored in an ArrayList.
 * 
 * @author weikun.zhong
 */
public class ArrayListFeatureItem extends Item
{
	protected List<Entry<Integer, Double>> featureList;

	public ArrayListFeatureItem()
	{
		featureList=new ArrayList<>();
	}

	public ArrayListFeatureItem(int validSize)
	{
		featureList=new ArrayList<>(validSize);
	}

	@Override
	public double getFeature(int idx)
	{
		int pos=Collections.binarySearch(featureList, new AbstractMap.SimpleEntry<>(idx, 0.0), COMPARATOR);
		if(pos>=0)
			return featureList.get(pos).getValue();
		else
			return 0;
	}

	@Override
	public void setFeature(int idx, double value)
	{
		int pos=Collections.binarySearch(featureList, new AbstractMap.SimpleEntry<>(idx, 0.0), COMPARATOR);
		if(pos>=0)
			featureList.get(pos).setValue(value);
		else
			featureList.add(-pos-1, new AbstractMap.SimpleEntry<Integer, Double>(idx, value));
	}

	@Override
	public void deleteFeature(int idx)
	{
		int pos=Collections.binarySearch(featureList, new AbstractMap.SimpleEntry<>(idx, 0.0), COMPARATOR);
		if(pos>=0)
			featureList.remove(pos);
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
		featureList=(List<Entry<Integer, Double>>)features;
	}

	@Override
	public String getFeatureString()
	{
		StringBuilder stringBuilder=new StringBuilder();
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
