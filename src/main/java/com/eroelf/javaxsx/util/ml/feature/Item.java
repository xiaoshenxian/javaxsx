package com.eroelf.javaxsx.util.ml.feature;

import java.util.Iterator;

import com.eroelf.javaxsx.util.ml.feature.model.Modelable;
import com.eroelf.javaxsx.util.ml.feature.score.Scoreable;

/**
 * Base class of all items (from any source) for modeling and ranking.
 * 
 * @author weikun.zhong
 */
public abstract class Item extends Scoreable implements Modelable, UpdatableByItem<Item>
{
	public abstract double getFeature(int idx);
	public abstract void setFeature(int idx, double value);
	public abstract void deleteFeature(int idx);

	public abstract Object getFeatures();
	public abstract void setFeatures(Object features);

	public int featureSize()
	{
		int size=0;
		Iterator<IndexedFeature> iter=validFeatureIterator();
		while(iter.hasNext())
		{
			iter.next();
			++size;
		}
		return size;
	}

	public abstract String getFeatureString();

	public static interface IndexedFeature
	{
		public int getIdx();
		public double getFeature();
	}

	@Override
	public void update(Item item)
	{
		for(IndexedFeature indexedFeature : item.getValidFeatures())
		{
			setFeature(indexedFeature.getIdx(), indexedFeature.getFeature());
		}
	}

	public abstract Iterator<IndexedFeature> validFeatureIterator();
	public abstract Iterable<IndexedFeature> getValidFeatures();
}
