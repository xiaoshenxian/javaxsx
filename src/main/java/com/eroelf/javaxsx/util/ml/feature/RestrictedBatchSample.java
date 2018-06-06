package com.eroelf.javaxsx.util.ml.feature;

import com.eroelf.javaxsx.util.ml.feature.score.Scoreable;
import com.eroelf.javaxsx.util.ml.feature.score.Scorer;

/**
 * Collects several {@link Scoreable} instances together as a batch.
 * Every {@link Scoreable} object must be an instance of type T.
 * Can be scored by any {@link Scorer}, but only be scored one by one of its batched {@link Scoreable} objects.
 * 
 * @author weikun.zhong
 * 
 * @param <T> the type of the {@link Scoreable} instances.
 */
public class RestrictedBatchSample<T extends Scoreable> extends BatchScoreableRestrictedBatchSample<T>
{
	public RestrictedBatchSample(int batchSize)
	{
		super(batchSize);
	}

	@Override
	public double scoreBy(Scorer scorer)
	{
		score=0;
		for(T sample : samples)
		{
			score+=sample.scoreBy(scorer);
		}
		return score=score/Math.max(samples.size(), 1);
	}
}
