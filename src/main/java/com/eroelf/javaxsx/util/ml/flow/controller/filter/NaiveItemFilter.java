package com.eroelf.javaxsx.util.ml.flow.controller.filter;

import com.eroelf.javaxsx.util.ml.feature.Item;

/**
 * A singleton {@link ItemFilter} instance which accepts anything.
 * 
 * @author weikun.zhong
 *
 * @param <T> the type of those {@link Item} instances which are about to be checked.
 */
public class NaiveItemFilter<T extends Item> implements ItemFilter<T>
{
	private static final NaiveItemFilter<?> INSTANCE=new NaiveItemFilter<Item>();

	@SuppressWarnings("unchecked")
	public static <U extends Item> NaiveItemFilter<U> get()
	{
		return (NaiveItemFilter<U>)INSTANCE;
	}

	@Override
	public boolean test(T item)
	{
		return true;
	}

	private NaiveItemFilter()
	{}

	@SuppressWarnings("unchecked")
	@Override
	public NaiveItemFilter<T> clone()
	{
		return (NaiveItemFilter<T>)INSTANCE;
	}
}
