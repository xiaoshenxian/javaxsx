package com.eroelf.javaxsx.util.ml.flow.info;

import java.util.function.BiFunction;

import com.eroelf.javaxsx.util.ml.feature.Item;

/**
 * Factory interface to create {@link Info} instances.
 * 
 * @author weikun.zhong
 *
 * @param <T> the type of the {@link Item} instances to be used to create {@link Info} instances.
 * @param <I> the type of the {@link Info} instances to be created.
 */
public interface InfoFactory<T extends Item, I extends Info> extends BiFunction<T, Boolean, I>
{
	public I create(T item, boolean verbose);

	@Override
	default I apply(T item, Boolean verbose)
	{
		return create(item, verbose);
	}
}
