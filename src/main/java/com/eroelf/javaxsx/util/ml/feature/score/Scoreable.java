package com.eroelf.javaxsx.util.ml.feature.score;

/**
 * Any instance of this class should be able to be scored by one or more {@link Scorer} instance.
 * 
 * @author weikun.zhong
 */
public abstract class Scoreable implements Comparable<Scoreable>
{
	public double score=Double.NaN;

	public double scoreBy(Scorer scorer)
	{
		return score=scorer.score(this);
	}

	@Override
	public int compareTo(Scoreable obj)
	{
		if(!Double.isNaN(score) && !Double.isNaN(obj.score))
			return -Double.compare(score, obj.score);
		else if(!Double.isNaN(score))
			return -1;
		else if(!Double.isNaN(obj.score))
			return 1;
		else
			return 0;
	}
}
