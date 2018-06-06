package com.eroelf.javaxsx.util.ml.feature.model;

import java.util.function.UnaryOperator;

/**
 * Any class implements this interface should be able to model one or more specified types of {@link Modelable} instance.
 * 
 * @author weikun.zhong
 */
public interface Modeler extends UnaryOperator<Modelable>
{
	public Modelable model(Modelable modelable);

	@Override
	default Modelable apply(Modelable modelable)
	{
		return model(modelable);
	}
}
