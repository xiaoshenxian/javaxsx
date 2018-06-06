package com.eroelf.javaxsx.util.geo;

/**
 * Preserve constants related to geography.
 * 
 * @author weikun.zhong
 */
public class GeoConst
{
	public static final double a=6378245.0;
	public static final double ee=0.00669342162296594323;

	private GeoConst()
	{}

	@Override
	public GeoConst clone()
	{
		throw new UnsupportedOperationException("This method is not allowed!");
	}
}
