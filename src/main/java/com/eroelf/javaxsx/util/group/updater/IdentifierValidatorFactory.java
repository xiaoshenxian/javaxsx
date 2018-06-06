package com.eroelf.javaxsx.util.group.updater;

import com.eroelf.javaxsx.util.group.IdentifierValidator;

/**
 * The Factory to create {@link IdentifierValidator} instances.
 * 
 * @author weikun.zhong
 */
public interface IdentifierValidatorFactory
{
	public IdentifierValidator create(String facetName);
}
