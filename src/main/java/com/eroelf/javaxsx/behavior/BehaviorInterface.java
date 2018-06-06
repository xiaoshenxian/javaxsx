package com.eroelf.javaxsx.behavior;

/**
 * This is an identifier interface indicates that any class implemented this interface is a some-kind-of-behavior class.
 * 
 * @author weikun.zhong
 */
public interface BehaviorInterface
{
	default public String getTabEscape()
	{
		return "tab!#\u0003#!Escape";
	}
}
