package com.eroelf.javaxsx.util.ml.flow.info;

import com.eroelf.javaxsx.util.ml.feature.Item;

/**
 * This is the interface of all data structures that represents objects given by a certain modeling and scoring flow and used out of the flow.
 * 
 * @author weikun.zhong
 */
public interface Info
{
	default public boolean isValid()
	{
		return true;
	}

	default public void convertFrom(Item item, boolean verbose)
	{}
}
