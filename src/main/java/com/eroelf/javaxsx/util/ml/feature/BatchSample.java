package com.eroelf.javaxsx.util.ml.feature;

import com.eroelf.javaxsx.util.ml.feature.score.Scoreable;

/**
 * Collects several {@link Scoreable} instances together as a batch.
 * 
 * @author weikun.zhong
 */
public class BatchSample extends RestrictedBatchSample<Scoreable>
{
	public BatchSample(int batchSize)
	{
		super(batchSize);
	}
}
