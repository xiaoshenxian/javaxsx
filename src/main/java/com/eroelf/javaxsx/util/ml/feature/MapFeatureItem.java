package com.eroelf.javaxsx.util.ml.feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

/**
 * Base class of all items (from any source) for modeling and ranking with features sparsely stored in a HashMap.
 * 
 * @author weikun.zhong
 */
public class MapFeatureItem extends Item
{
	protected Map<Integer, Double> featuresMap;

	public MapFeatureItem()
	{
		featuresMap=new HashMap<>();
	}

	public MapFeatureItem(int validSize)
	{
		featuresMap=new HashMap<>(validSize);
	}

	@Override
	public double getFeature(int idx)
	{
		if(featuresMap.containsKey(idx))
			return featuresMap.get(idx);
		else
			return 0;
	}

	@Override
	public void setFeature(int idx, double value)
	{
		if(value!=0)
			featuresMap.put(idx, value);
	}

	@Override
	public void deleteFeature(int idx)
	{
		featuresMap.remove(idx);
	}

	@Override
	public Map<Integer, Double> getFeatures()
	{
		return featuresMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setFeatures(Object features)
	{
		this.featuresMap=(Map<Integer, Double>)features;
	}

	@Override
	public String getFeatureString()
	{
		StringBuilder stringBuilder=new StringBuilder();
		List<Entry<Integer, Double>> list=new ArrayList<>(featuresMap.entrySet());
		Collections.sort(list, COMPARATOR);
		for(Entry<Integer, Double> entry : list)
		{
			double value=entry.getValue();
			if(value!=0)
			{
				stringBuilder.append(" ").append(entry.getKey()).append(":").append(value);
			}
		}
		return stringBuilder.toString().trim();
	}

	public void trimFeatures()
	{
		Iterator<Entry<Integer, Double>> iter=featuresMap.entrySet().iterator();
		while(iter.hasNext())
		{
			Entry<Integer, Double> entry=(Entry<Integer, Double>)iter.next();
			if(entry.getValue()==0)
				iter.remove();
		}
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
		private Iterator<Entry<Integer, Double>> iter=featuresMap.entrySet().iterator();
		private Entry<Integer, Double> currentEntry;
		private Entry<Integer, Double> workingEntry;
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
			while(iter.hasNext())
			{
				currentEntry=iter.next();
				if(currentEntry.getValue()!=0)
				{
					workingEntry=currentEntry;
					return true;
				}
			}
			currentEntry=null;
			workingEntry=null;
			return false;
		}

		@Override
		public IndexedFeature next()
		{
			if(workingEntry!=null || hasNext())
			{
				entry=workingEntry;
				workingEntry=null;
				return indexedFeature;
			}
			else
				throw new NoSuchElementException();
		}

		@Override
		public void remove()
		{
			iter.remove();
		}
	}
}
