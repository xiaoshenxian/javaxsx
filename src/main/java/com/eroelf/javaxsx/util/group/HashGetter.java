package com.eroelf.javaxsx.util.group;

import java.util.function.ToDoubleFunction;

/**
 * Class to help to calculate hashes for {@link GroupingUtil}.
 * 
 * @author weikun.zhong
 */
@FunctionalInterface
public interface HashGetter extends ToDoubleFunction<String>
{
	public double hash(String identifier);

	default double applyAsDouble(String identifier)
	{
		return hash(identifier);
	}
}
