package com.eroelf.javaxsx.util.ml.flow.controller.filter;

import com.eroelf.javaxsx.util.ml.feature.Item;
import com.eroelf.javaxsx.util.ml.flow.estimate.statistics.ItemGroupStatistics;

/**
 * A singleton {@link ItemFilterHandler} instance which provides {@link NaiveItemFilter} preFilter and innerFilter while {@code null} afterFilter.
 * 
 * @author weikun.zhong
 *
 * @param <T> the type of those {@link Item} instances which are about to be checked.
 */
public class NaiveItemFilterHandler<T extends Item> implements ItemFilterHandler<T>
{
	private static final NaiveItemFilterHandler<?> INSTANCE=new NaiveItemFilterHandler<Item>();

	@SuppressWarnings("unchecked")
	public static <U extends Item> NaiveItemFilterHandler<U> get()
	{
		return (NaiveItemFilterHandler<U>)INSTANCE;
	}

	@Override
	public ItemFilter<T> getPreFilter()
	{
		return NaiveItemFilter.get();
	}

	@Override
	public ItemFilter<T> getInnerFilter()
	{
		return NaiveItemFilter.get();
	}

	@Override
	public ItemFilter<T> getAfterFilter(ItemGroupStatistics<T> itemGroupStatistics)
	{
		return null;
	}

	private NaiveItemFilterHandler()
	{}

	@SuppressWarnings("unchecked")
	@Override
	public NaiveItemFilterHandler<T> clone()
	{
		return (NaiveItemFilterHandler<T>)INSTANCE;
	}
}
