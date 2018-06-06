package com.eroelf.javaxsx.util.ml.flow.controller.filter;

import java.util.function.Predicate;

import com.eroelf.javaxsx.util.ml.feature.Item;

/**
 * Check if an {@link Item} instance satisfies some specified criteria.
 * 
 * @author weikun.zhong
 *
 * @param <T> the type of those {@link Item} instances which are about to be checked.
 */
@FunctionalInterface
public interface ItemFilter<T extends Item> extends Predicate<T>
{
	/**
	 * The check method.
	 * 
	 * @param item the {@link Item} instance which is about to be checked.
	 * @return {@code true} if the given {@code item} satisfied the specified criteria, otherwise {@code false}.
	 */
	public boolean test(T item);
}
