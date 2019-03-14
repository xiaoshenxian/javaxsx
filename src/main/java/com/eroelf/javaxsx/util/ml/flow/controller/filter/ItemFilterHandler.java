package com.eroelf.javaxsx.util.ml.flow.controller.filter;

import java.util.function.Predicate;

import com.eroelf.javaxsx.util.ml.feature.Item;
import com.eroelf.javaxsx.util.ml.flow.estimate.statistics.ItemGroupStatistics;

/**
 * Maintains three {@link Predicate} instances which will perform as filters and impact on different stages of a modeling and scoring flow. 
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
	 * @return an {@link Predicate} object.
	 */
	public Predicate<T> getPreFilter();

	/**
	 * Impacts when an {@link Item} instance has been modeled and scored.
	 * 
	 * @return an {@link Predicate} object.
	 */
	public Predicate<T> getInnerFilter();

	/**
	 * Impacts when all candidates are modeled and scored.
	 * 
	 * @param itemGroupStatistics statistics of all candidates.
	 * @return an {@link Predicate} object.
	 */
	public Predicate<T> getAfterFilter(ItemGroupStatistics<T> itemGroupStatistics);
}
