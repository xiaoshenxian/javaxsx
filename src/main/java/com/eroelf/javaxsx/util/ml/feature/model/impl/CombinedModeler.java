package com.eroelf.javaxsx.util.ml.feature.model.impl;

import java.util.ArrayList;
import java.util.List;

import com.eroelf.javaxsx.util.ml.feature.model.Modelable;
import com.eroelf.javaxsx.util.ml.feature.model.Modeler;

/**
 * An instance of this class is able to combine multiple {@link Modeler}s to model a specified {@link Modelable} instance.
 * 
 * @author weikun.zhong
 */
public class CombinedModeler implements Modeler
{
	private List<Modeler> modelerList=new ArrayList<>();

	public CombinedModeler(Modeler... modelers)
	{
		for(Modeler modeler : modelers)
		{
			addModeler(modeler);
		}
	}

	@Override
	public Modelable model(Modelable modelable)
	{
		for(Modeler modeler : modelerList)
		{
			modeler.model(modelable);
		}
		return modelable;
	}

	public CombinedModeler addModeler(Modeler modeler)
	{
		if(modeler!=null)
			modelerList.add(modeler);
		return this;
	}
}
