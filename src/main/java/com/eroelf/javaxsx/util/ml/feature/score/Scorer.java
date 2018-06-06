package com.eroelf.javaxsx.util.ml.feature.score;

import java.util.function.ToDoubleFunction;

/**
 * Any class implements this interface should be able to score one or more specified types of {@link Scoreable} instance.
 * 
 * @author weikun.zhong
 */
public interface Scorer extends ToDoubleFunction<Scoreable>
{
	public double score(Scoreable scoreable);

	@Override
	default public double applyAsDouble(Scoreable scoreable)
	{
		return score(scoreable);
	}
}
