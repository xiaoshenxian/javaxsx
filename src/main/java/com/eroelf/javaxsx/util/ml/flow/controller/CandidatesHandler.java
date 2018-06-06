package com.eroelf.javaxsx.util.ml.flow.controller;

import com.eroelf.javaxsx.util.ml.feature.Item;

/**
 * Handles {@link Item} candidates.
 * 
 * @author weikun.zhong
 *
 * @param <T> the type of those {@link Item} candidates.
 */
public interface CandidatesHandler<T extends Item>
{
	public Iterable<T> getCandidates();
}
