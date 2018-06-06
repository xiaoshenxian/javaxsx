package com.eroelf.javaxsx.util.ml.feature.model;

/**
 * Any instance of this class should be able to be modeled by one or more {@link Modeler} instance.
 * 
 * @author weikun.zhong
 */
public interface Modelable
{
	default public Modelable modelBy(Modeler modeler)
	{
		return modeler.model(this);
	}
}
