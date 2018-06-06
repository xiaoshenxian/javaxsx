package com.eroelf.javaxsx.util.math.mapping;

public class LinearMapping implements BiMapping
{
	public double k;
	public double b;

	public LinearMapping(double k, double b)
	{
		this.k=k;
		this.b=b;
	}

	@Override
	public double map(double v)
	{
		return k*v+b;
	}

	@Override
	public double reverseMap(double v)
	{
		return (v-b)/k;
	}
}
