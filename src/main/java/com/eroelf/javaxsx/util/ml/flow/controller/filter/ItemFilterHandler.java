package com.eroelf.javaxsx.util.ml.flow.controller.filter;

import com.eroelf.javaxsx.util.ml.feature.Item;
import com.eroelf.javaxsx.util.ml.flow.estimate.statistics.ItemGroupStatistics;

/**
 * Maintains three {@link ItemFilter} instances which will impact on different stages of a modeling and scoring flow. 
 * 
 * @author weikun.zhong
 *
 * @param <T> the type of those {@link Item} instances which are about to be checked.
 */
public interface ItemFilterHandler<T extends Item>
{
	/**
	 * Impacts when gets an {@link Item} instance from candidates.
	 * 
	 * @return an {@link ItemFilter} object.
	 */
	public ItemFilter<T> getPreFilter();

	/**
	 * Impacts when an {@link Item} instance has been modeled and scored.
	 * 
	 * @return an {@link ItemFilter} object.
	 */
	public ItemFilter<T> getInnerFilter();

	/**
	 * Impacts when all candidates are modeled and scored.
	 * 
	 * @param itemGroupStatistics statistics of all candidates.
	 * @return an {@link ItemFilter} object.
	 */
	public ItemFilter<T> getAfterFilter(ItemGroupStatistics<T> itemGroupStatistics);
}
