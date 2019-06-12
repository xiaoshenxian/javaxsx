package com.eroelf.javaxsx.util.ml.feature.score.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.eroelf.javaxsx.util.io.FileIterator;
import com.eroelf.javaxsx.util.math.mapping.LinearMapping;
import com.eroelf.javaxsx.util.ml.feature.Item;
import com.eroelf.javaxsx.util.ml.feature.Item.IndexedFeature;
import com.eroelf.javaxsx.util.ml.feature.score.Scoreable;
import com.eroelf.javaxsx.util.ml.feature.score.Scorer;
import com.google.common.collect.Iterators;

/**
 * A logistic regression model to score {@link Item} objects.
 * Feature mapping are included in this implementation.
 * 
 * @author weikun.zhong
 */
public class LogisticRegressionScorer implements Scorer
{
	private Map<Integer, LinearMapping> w;
	private double b=0;

	private static class MappingMap
	{
		private static class Mapping extends LinearMapping
		{
			private int originalIdx;
			private int mappedIdx;

			public Mapping(double k, double b, int originalIdx, int mappedIdx)
			{
				super(k, b);
				this.originalIdx=originalIdx;
				this.mappedIdx=mappedIdx;
			}
		}
		private int counter=0;

		private Map<Integer, Mapping> forword=new HashMap<>();
		private Map<Integer, Mapping> backword=new HashMap<>();

		private void add(int originalIdx, double k, double b)
		{
			Mapping mapping=new Mapping(k, b, originalIdx, counter++);
			forword.put(mapping.originalIdx, mapping);
			backword.put(mapping.mappedIdx, mapping);
		}
	}

	private static MappingMap getMapping(Iterator<String> mapping)
	{
		MappingMap mappingMap=new MappingMap();
		while(mapping.hasNext())
		{
			String str=mapping.next();
			String[] items=str.split("\t");
			mappingMap.add(Integer.parseInt(items[0]), Double.parseDouble(items[1]), Double.parseDouble(items[2]));
		}
		return mappingMap;
	}

	private void init(Iterator<Double> weightIter, Iterator<String> mappingIter)
	{
		MappingMap mappingMap=getMapping(mappingIter);
		int count=0;
		w=new HashMap<>();
		while(weightIter.hasNext())
		{
			double weight=weightIter.next();
			if(count==0)
				b=weight;
			else
			{
				com.eroelf.javaxsx.util.ml.feature.score.impl.LogisticRegressionScorer.MappingMap.Mapping m=mappingMap.backword.get(count-1);
				w.put(m.originalIdx, new LinearMapping(m.k*weight, m.b*weight));
			}
			++count;
		}
	}

	private void init(Iterable<Double> weights, Iterable<String> mapping)
	{
		init(weights.iterator(), mapping.iterator());
	}

	public LogisticRegressionScorer(Iterator<Double> weightIter, Iterator<String> mappingIter)
	{
		init(weightIter, mappingIter);
	}

	public LogisticRegressionScorer(Iterable<Double> weights, Iterable<String> mapping)
	{
		init(weights, mapping);
	}

	public LogisticRegressionScorer(final String weightFile, final String mappingFile)
	{
		Iterator<Double> weights=Iterators.transform(new FileIterator(weightFile), Double::parseDouble);
		Iterator<String> mapping=new FileIterator(mappingFile);
		init(weights, mapping);
	}

	public <T> LogisticRegressionScorer(Class<T> desClass, final String weightFile, final String mappingFile)
	{
		Iterator<Double> weights=Iterators.transform(new FileIterator(desClass, weightFile), Double::parseDouble);
		Iterator<String> mapping=new FileIterator(desClass, mappingFile);
		init(weights, mapping);
	}

	@Override
	public double score(Scoreable scoreable)
	{
		if(scoreable instanceof Item)
		{
			Item item=(Item)scoreable;
			item.score=b;
			for(IndexedFeature indexedFeature : item.getValidFeatures())
			{
				if(w.containsKey(indexedFeature.getIdx()))
					item.score+=w.get(indexedFeature.getIdx()).map(indexedFeature.getFeature());
			}
			return item.score=1/(1+Math.exp(-item.score));
		}
		return scoreable.score=0;
	}
}
