package com.eroelf.javaxsx.util.ml.flow.controller;

import java.util.List;

import com.eroelf.javaxsx.util.ml.feature.Item;
import com.eroelf.javaxsx.util.ml.feature.strategy.Strategy;

/**
 * Handles {@link Strategy} instances used to generate {@link Item} candidates.
 * 
 * @author weikun.zhong
 *
 * @param <T> the type of those {@link Item} candidates which the {@link Strategy} instances will generate.
 */
public interface StrategyHandler<T extends Item>
{
	public List<Strategy<T>> getStrategies();
}
