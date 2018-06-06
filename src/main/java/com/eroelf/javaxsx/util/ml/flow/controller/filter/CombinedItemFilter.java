package com.eroelf.javaxsx.util.ml.flow.controller.filter;

import java.util.ArrayList;
import java.util.List;

import com.eroelf.javaxsx.util.ml.feature.Item;

/**
 * An instance of this class is able to combine multiple {@link ItemFilter}s to filter a specified {@link Item} instance.
 * 
 * @author weikun.zhong
 */
public class CombinedItemFilter<T extends Item> implements ItemFilter<T>
{
	private List<ItemFilter<T>> filterList=new ArrayList<>();

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
		for(ItemFilter<T> itemFilter : filterList)
		{
			if(!itemFilter.test(item))
				return false;
		}
		return true;
	}

	public CombinedItemFilter<T> addFilter(ItemFilter<T> itemFilter)
	{
		if(itemFilter!=null)
			filterList.add(itemFilter);
		return this;
	}
}
