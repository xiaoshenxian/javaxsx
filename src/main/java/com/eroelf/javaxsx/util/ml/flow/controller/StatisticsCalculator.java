package com.eroelf.javaxsx.util.ml.flow.controller;

/**
 * Calculates necessary statistics just before a modeling and scoring flow start.
 * 
 * @author weikun.zhong
 */
public interface StatisticsCalculator
{
	public StatisticsInfo calcStatisticsInfo();
}
