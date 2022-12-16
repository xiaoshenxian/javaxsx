package com.eroelf.javaxsx.util.ml.feature.score.impl;

import java.util.Map;
import java.util.stream.Collectors;

import com.eroelf.javaxsx.util.io.FileIterator;
import com.eroelf.javaxsx.util.ml.feature.Item;
import com.eroelf.javaxsx.util.ml.feature.Item.IndexedFeature;
import com.eroelf.javaxsx.util.ml.feature.score.Scoreable;
import com.eroelf.javaxsx.util.ml.feature.score.Scorer;

/**
 * A simple logistic regression model to score {@link Item} objects.
 * 
 * @author weikun.zhong
 */
public class LRScorer implements Scorer
{
	private Map<Integer, Double> weight;

	public LRScorer(Map<Integer, Double> weight)
	{
		this.weight=weight;
	}

	public LRScorer(Class<?> cls, String fileName)
	{
		this(new FileIterator(cls, fileName).lines().map(line -> line.trim().split("[\\s=,;:#/]+")).filter(it -> it.length>=2).collect(Collectors.toMap(it -> Integer.parseInt(it[0]), it -> Double.parseDouble(it[1]))));
	}

	public LRScorer(String fileName)
	{
		this(new FileIterator(fileName).lines().map(line -> line.trim().split("[\\s=,;:#/]+")).filter(it -> it.length>=2).collect(Collectors.toMap(it -> Integer.parseInt(it[0]), it -> Double.parseDouble(it[1]))));
	}

	@Override
	public double score(Scoreable scoreable)
	{
		if(scoreable instanceof Item)
		{
			Item item=(Item)scoreable;
			item.score=0;
			for(IndexedFeature indexedFeature : item.getValidFeatures())
			{
				if(weight.containsKey(indexedFeature.getIdx()))
					item.score+=weight.get(indexedFeature.getIdx())*indexedFeature.getFeature();
			}
			return item.score=1/(1+Math.exp(-item.score));
		}
		return scoreable.score=0;
	}
}
