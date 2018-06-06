package com.eroelf.javaxsx.util.ml.flow.estimate.statistics;

import com.eroelf.javaxsx.util.ml.feature.Item;

/**
 * Calculates and maintains necessary statistics for a group of {@link Item} objects.
 * 
 * @author weikun.zhong
 *
 * @param <T> the type of those {@link Item} objects whose data need to be calculated into the statistics.
 */
public abstract class ItemGroupStatistics<T extends Item>
{
	/**
	 * Calculates statistics which cannot be calculated increasingly by seeing new {@link Item} objects one by one.
	 * 
	 * @param itemIter all {@link Item} objects.
	 */
	public abstract void computeStatistics(Iterable<T> itemIter);

	/**
	 * Increasingly calculates statistics which can be calculated online by seeing new {@link Item} objects one by one.
	 * 
	 * @param item
	 */
	public abstract void increaseStatistics(T item);

	public void increaseStatistics(Iterable<T> itemIter)
	{
		for(T item : itemIter)
		{
			increaseStatistics(item);
		}
	}
}
