package com.eroelf.javaxsx.util.ml.feature.score;

import java.util.Iterator;
import java.util.List;

import com.eroelf.javaxsx.util.ml.feature.BatchScoreableRestrictedBatchSample;

/**
 * A simple implementation of {@link Scorer}s which can be used to score a batched sample
 * 
 * @author weikun.zhong
 *
 * @see {@link BatchScoreableRestrictedBatchSample}
 */
public abstract class BatchScorer implements Scorer
{
	public abstract List<Double> getAllScores(BatchScoreableRestrictedBatchSample<?> batchSample);

	@Override
	public double score(Scoreable scoreable)
	{
		if(scoreable instanceof BatchScoreableRestrictedBatchSample)
		{
			BatchScoreableRestrictedBatchSample<?> batchSample=(BatchScoreableRestrictedBatchSample<?>)scoreable;
			Iterator<? extends Scoreable> iter=batchSample.iterator();
			batchSample.score=0;
			int count=0;
			for(double score : getAllScores(batchSample))
			{
				iter.next().score=score;
				batchSample.score+=score;
				++count;
			}
			return batchSample.score/=Math.max(count, 1);
		}
		else
			return scoreable.score;
	}
}
