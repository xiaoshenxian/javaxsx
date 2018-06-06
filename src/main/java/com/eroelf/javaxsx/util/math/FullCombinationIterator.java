package com.eroelf.javaxsx.util.math;

import java.util.Iterator;

public class FullCombinationIterator implements Iterator<int[]>
{
	private int totalGroups;
	private int totalCount;
	private int currCount;

	public FullCombinationIterator(int totalGroups)
	{
		this.totalGroups=totalGroups;
		totalCount=(int)Math.pow(2, totalGroups)-1;
		currCount=0;
	}

	public int getTotalCount()
	{
		return totalCount;
	}

	public void reset()
	{
		currCount=0;
	}

	@Override
	public boolean hasNext()
	{
		return currCount<totalCount;
	}

	@Override
	public int[] next()
	{
		int[] indices=new int[totalGroups];
		int remain=++currCount;
		for(int i=0; remain>0; i++)
		{
			indices[i]=remain%2;
			remain/=2;
		}
		return indices;
	}
}
