package com.eroelf.javaxsx.util.group.updater;

import com.eroelf.javaxsx.util.group.HashGetter;

/**
 * The Factory to create {@link HashGetter} instances.
 * 
 * @author weikun.zhong
 */
public interface HashGetterFactory
{
	public HashGetter create(String facetName);
}
