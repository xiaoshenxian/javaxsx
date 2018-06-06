package com.eroelf.javaxsx.util.ml.flow.log;

import java.util.function.BiFunction;

import com.eroelf.javaxsx.util.ml.feature.Item;

/**
 * Factory interface to create {@link InfoLog} instances.
 * 
 * @author weikun.zhong
 *
 * @param <T> the type of the {@link Item} instances to be used to create {@link InfoLog} instances.
 * @param <L> the type of the {@link InfoLog} instances to be created.
 */
public interface InfoLogFactory<T extends Item, L extends InfoLog> extends BiFunction<T, Boolean, L>
{
	public L create(T item, boolean verbose);
	
	default L apply(T item, Boolean verbose)
	{
		return create(item, verbose);
	}
}
