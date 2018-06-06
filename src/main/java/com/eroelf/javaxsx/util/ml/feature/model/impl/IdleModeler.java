package com.eroelf.javaxsx.util.ml.feature.model.impl;

import com.eroelf.javaxsx.util.ml.feature.model.Modelable;
import com.eroelf.javaxsx.util.ml.feature.model.Modeler;

/**
 * A singleton {@link Modeler} instance which does nothing.
 * 
 * @author weikun.zhong
 */
public class IdleModeler implements Modeler
{
	private static final IdleModeler INSTANCE=new IdleModeler();

	public static Modeler get()
	{
		return INSTANCE;
	}

	@Override
	public Modelable model(Modelable modelable)
	{
		return modelable;
	}

	private IdleModeler()
	{}

	@Override
	public IdleModeler clone()
	{
		return INSTANCE;
	}
}
