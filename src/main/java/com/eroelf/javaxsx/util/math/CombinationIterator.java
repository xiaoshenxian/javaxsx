package com.eroelf.javaxsx.util.math;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An {@link Iterator} for combination.
 * 
 * @author weikun.zhong
 */
public class CombinationIterator implements Iterator<int[]>
{
	private int totalNum;
	private int chosenNum;
	private int totalCount;
	private int currCount;

	private int[] stack;
	private int size;

	public CombinationIterator(int totalNum, int chosenNum)
	{
		if(totalNum<=0)
			throw new IllegalArgumentException("totalNum must be greater than 0!");
		if(chosenNum<0)
			throw new IllegalArgumentException("chosenNum must not be less than 0!");
		if(chosenNum>totalNum)
			throw new IllegalArgumentException("chosenNum must not be greater than totalNum!");

		this.totalNum=totalNum;
		this.chosenNum=chosenNum;
		totalCount=1;
		stack=new int[chosenNum];
		if(chosenNum>totalNum/2)
			chosenNum=totalNum-chosenNum;
		for(int i=0; i<chosenNum; )
		{
			totalCount=totalCount*totalNum--/++i;
		}
		reset();
	}

	public int getTotalCount()
	{
		return totalCount;
	}

	public void reset()
	{
		currCount=0;
		size=0;
	}

	@Override
	public boolean hasNext()
	{
		return currCount<totalCount;
	}

	@Override
	public int[] next()
	{
		if(chosenNum==0 && currCount==0)
		{
			++currCount;
			return Arrays.copyOf(stack, size);
		}
		while(currCount<totalCount)
		{
			if(size==0)
				stack[size++]=0;
			else if(stack[size-1]<totalNum-chosenNum+size)
			{
				if(size==chosenNum)
					++stack[size-1];
				else
				{
					stack[size]=stack[size-1]+1;
					++size;
				}
			}
			else
			{
				if(size>1)
					++stack[--size-1];
				else
					throw new NoSuchElementException();
			}
			if(size==chosenNum && stack[size-1]<totalNum)
			{
				++currCount;
				return Arrays.copyOf(stack, size);
			}
		}
		throw new NoSuchElementException();
	}
}
