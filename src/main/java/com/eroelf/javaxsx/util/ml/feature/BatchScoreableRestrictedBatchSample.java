package com.eroelf.javaxsx.util.ml.feature;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.eroelf.javaxsx.util.ml.feature.score.Scoreable;
import com.eroelf.javaxsx.util.ml.feature.score.Scorer;

/**
 * Collects several {@link Scoreable} instances together as a batch.
 * Every {@link Scoreable} object must be an instance of type T.
 * Can be regarded as a simple {@link Scoreable} and scored by a group of {@link Scorer}s which support batch scoring.
 * 
 * @author weikun.zhong
 * 
 * @param <T> the type of the {@link Scoreable} instances.
 */
public class BatchScoreableRestrictedBatchSample<T extends Scoreable> extends Scoreable implements Iterable<T>
{
	protected List<T> samples;
	protected int batchSize;

	public BatchScoreableRestrictedBatchSample(int batchSize)
	{
		assert batchSize>0 : "batchSize must be greater than 0!";
		samples=new ArrayList<>(batchSize);
		this.batchSize=batchSize;
	}

	public int add(T scoreable)
	{
		if(samples.size()>=batchSize)
			samples.clear();
		samples.add(scoreable);
		return getValidSize();
	}

	public int getValidSize()
	{
		return samples.size();
	}

	@Override
	public Iterator<T> iterator()
	{
		return samples.iterator();
	}
}
