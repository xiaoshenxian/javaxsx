package com.eroelf.javaxsx.util.group;

import java.util.function.Predicate;

/**
 * Class to help to validate identifiers for {@link GroupingUtil}.
 * 
 * @author weikun.zhong
 */
@FunctionalInterface
public interface IdentifierValidator extends Predicate<String>
{
	public boolean isValidIdentifier(String identifier);

	default public boolean test(String identifier)
	{
		return isValidIdentifier(identifier);
	}
}
