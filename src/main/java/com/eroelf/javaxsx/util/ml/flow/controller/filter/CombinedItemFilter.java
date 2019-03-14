package com.eroelf.javaxsx.util.ml.flow.controller.filter;

import java.util.function.Predicate;

import com.eroelf.javaxsx.util.ml.feature.Item;

/**
 * An instance of this class is able to combine multiple {@link ItemFilter}s to filter a specified {@link Item} instance.
 * 
 * @author weikun.zhong
 */
public class CombinedItemFilter<T extends Item> implements ItemFilter<T>
{
	private Predicate<T> predicate;

	public CombinedItemFilter(@SuppressWarnings("unchecked") ItemFilter<T>... itemFilters)
	{
		for(ItemFilter<T> itemFilter : itemFilters)
		{
			addFilter(itemFilter);
		}
	}

	@Override
	public boolean test(T item)
	{
		return predicate.test(item);
	}

	public CombinedItemFilter<T> addFilter(ItemFilter<T> itemFilter)
	{
		if(itemFilter!=null)
			predicate=predicate!=null ? predicate.and(itemFilter) : itemFilter;
		return this;
	}
}
