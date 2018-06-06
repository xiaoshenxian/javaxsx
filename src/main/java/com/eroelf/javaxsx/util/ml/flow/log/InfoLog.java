package com.eroelf.javaxsx.util.ml.flow.log;

import com.eroelf.javaxsx.util.ml.feature.Item;

/**
 * This is the interface of all data structures that logging information of objects given by a certain modeling and scoring flow.
 * 
 * @author weikun.zhong
 */
public interface InfoLog
{
	default public void logFrom(Item item, boolean verbose)
	{}
}
