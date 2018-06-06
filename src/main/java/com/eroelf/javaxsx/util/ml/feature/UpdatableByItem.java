package com.eroelf.javaxsx.util.ml.feature;

/**
 * This interface provided an updating method which should be used to update the features of {@code this} object by the given {@code item}.
 * 
 * @author weikun.zhong
 *
 * @param <T> the type of the {@link Item} objects used to updating features of {@code this} object.
 */
public interface UpdatableByItem<T extends Item>
{
	/**
	 * Updates features of {@code this} object by the given {@code item} object.
	 * Generally, features with the same feature indices in {@code this} object should be overwritten.
	 * 
	 * @param item
	 */
	public void update(T item);
}
