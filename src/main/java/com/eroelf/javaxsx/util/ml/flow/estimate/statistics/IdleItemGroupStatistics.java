package com.eroelf.javaxsx.util.ml.flow.estimate.statistics;

import com.eroelf.javaxsx.util.ml.feature.Item;

/**
 * A singleton {@link ItemGroupStatistics} instance which does nothing.
 * 
 * @author weikun.zhong
 *
 * @param <T> the type of those {@link Item} objects whose data need to be calculated into the statistics.
 */
public class IdleItemGroupStatistics<T extends Item> extends ItemGroupStatistics<T>
{
	private static final IdleItemGroupStatistics<?> INSTANCE=new IdleItemGroupStatistics<Item>();

	@SuppressWarnings("unchecked")
	public static <U extends Item> ItemGroupStatistics<U> get()
	{
		return (IdleItemGroupStatistics<U>)INSTANCE;
	}

	@Override
	public void increaseStatistics(T item)
	{
		return;
	}

	@Override
	public void computeStatistics(Iterable<T> itemIter)
	{
		return;
	}

	@Override
	public void increaseStatistics(Iterable<T> itemIter)
	{
		return;
	}

	private IdleItemGroupStatistics()
	{}

	@SuppressWarnings("unchecked")
	@Override
	public IdleItemGroupStatistics<T> clone()
	{
		return (IdleItemGroupStatistics<T>)INSTANCE;
	}
}
