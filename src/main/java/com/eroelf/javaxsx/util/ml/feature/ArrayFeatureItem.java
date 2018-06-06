package com.eroelf.javaxsx.util.ml.feature;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Base class of all items (from any source) for modeling and ranking with features densely stored in an array.
 * 
 * @author weikun.zhong
 */
public class ArrayFeatureItem extends Item
{
	protected double[] features;

	public ArrayFeatureItem(double[] features)
	{
		setFeatures(features);
	}

	public ArrayFeatureItem(int featureSize)
	{
		features=new double[featureSize];
	}

	@Override
	public double getFeature(int idx)
	{
		return features[idx];
	}

	@Override
	public void setFeature(int idx, double value)
	{
		features[idx]=value;
	}

	@Override
	public void deleteFeature(int idx)
	{
		features[idx]=0;
	}

	@Override
	public double[] getFeatures()
	{
		return features;
	}

	@Override
	public void setFeatures(Object features)
	{
		this.features=(double[])features;
	}

	@Override
	public String getFeatureString()
	{
		StringBuilder stringBuilder=new StringBuilder();
		for(int i=0; i<features.length; i++)
		{
			if(features[i]!=0)
			{
				stringBuilder.append(" ").append(i).append(":").append(features[i]);
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

	private class ValidFeatureIterator implements Iterator<IndexedFeature>
	{
		private int currentIdx=-1;
		private int workingIdx=-1;
		private int idx;
		private IndexedFeature indexedFeature=new IndexedFeature() {
			@Override
			public int getIdx()
			{
				return idx;
			}
			
			@Override
			public double getFeature()
			{
				return features[idx];
			}
		};

		@Override
		public boolean hasNext()
		{
			while(++currentIdx<features.length)
			{
				if(features[currentIdx]!=0)
				{
					workingIdx=currentIdx;
					return true;
				}
			}
			currentIdx=-1;
			workingIdx=-1;
			return false;
		}

		@Override
		public IndexedFeature next()
		{
			if(workingIdx>=0 || hasNext())
			{
				idx=workingIdx;
				workingIdx=-1;
				return indexedFeature;
			}
			else
				throw new NoSuchElementException();
		}

		@Override
		public void remove()
		{
			features[currentIdx]=0;
		}
	}
}
