package com.eroelf.javaxsx.util.ml.flow.controller;

import com.eroelf.javaxsx.util.ml.feature.Item;

/**
 * Handles the context of a modeling and scoring flow in which candidates can be enumerated.
 * 
 * @author weikun.zhong
 *
 * @param <T> the type of those {@link Item} instances to be processed.
 */
public interface EnumerableFlowHandler<T extends Item> extends StatisticsCalculator, CandidatesHandler<T>, ModelerHandler, ScorerHandler, BatchHandler
{}
