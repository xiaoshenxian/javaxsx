package com.eroelf.javaxsx.util.ml.flow.estimate;

import java.util.Iterator;
import java.util.List;

import com.eroelf.javaxsx.util.ml.feature.BatchSample;
import com.eroelf.javaxsx.util.ml.feature.BatchScoreableRestrictedBatchSample;
import com.eroelf.javaxsx.util.ml.feature.Item;
import com.eroelf.javaxsx.util.ml.feature.RestrictedBatchSample;
import com.eroelf.javaxsx.util.ml.feature.model.Modeler;
import com.eroelf.javaxsx.util.ml.feature.score.Scorer;
import com.eroelf.javaxsx.util.ml.flow.controller.EnumerableFlowHandler;
import com.eroelf.javaxsx.util.ml.flow.controller.filter.ItemFilter;
import com.eroelf.javaxsx.util.ml.flow.controller.filter.ItemFilterHandler;
import com.eroelf.javaxsx.util.ml.flow.controller.filter.NaiveItemFilterHandler;
import com.eroelf.javaxsx.util.ml.flow.estimate.statistics.IdleItemGroupStatistics;
import com.eroelf.javaxsx.util.ml.flow.estimate.statistics.ItemGroupStatistics;

/**
 * Controls an modeling and scoring flow in which {@link Item} candidates can be enumerated, modeled, and scored.
 * 
 * @author weikun.zhong
 *
 * @param <T> the type of the generated {@link Item} objects.
 */
public abstract class EnumerableItemGenerator<T extends Item> implements ItemGenerator<T>
{
	/**
	 * Creates a specified flow handler for this generator.
	 * 
	 * @return a {@link EnumerableFlowHandler} object.
	 * @see {@link EnumerableFlowHandler}
	 */
	protected abstract EnumerableFlowHandler<T> getFlowHandler();

	/**
	 * Creates a specified filter handler for this generator to deal with {@link Item} objects with some filtering rules which has nothing to do with the flow handler returned by the {@link EnumerableItemGenerator#getFlowHandler() getFlowHandler} method.
	 * Returns the {@link NaiveItemFilterHandler} instance by default.
	 * 
	 * @return a {@link ItemFilterHandler} object.
	 * @see {@link ItemFilterHandler}
	 */
	protected ItemFilterHandler<T> getFilterHandler()
	{
		return NaiveItemFilterHandler.get();
	}

	/**
	 * Creates a specified {@link BatchScoreableRestrictedBatchSample} object for this generator.
	 * 
	 * @param batchSize the sample batch size.
	 * @return a {@link BatchScoreableRestrictedBatchSample} object.
	 * @see {@link BatchScoreableRestrictedBatchSample}
	 * @see {@link RestrictedBatchSample}
	 * @see {@link BatchSample}
	 */
	protected BatchScoreableRestrictedBatchSample<T> createBatchSample(int batchSize)
	{
		return new RestrictedBatchSample<T>(batchSize);
	}

	/**
	 * Retains some other information during the flow.
	 * Does nothing by default.
	 * This method will be called just after the specified {@link Item} object is modeled (before it be scored).
	 * 
	 * @param item the {@link Item} objects whose other information is about to be retained.
	 */
	protected void verbose(T item)
	{}

	/**
	 * Creates a specified {@link ItemGroupStatistics} object for this generator to handle the generated {@link Item} statistics.
	 * Returns the {@link IdleItemGroupStatistics} instance by default, which does nothing.
	 * 
	 * @return a {@link ItemGroupStatistics} object.
	 * @see {@link ItemGroupStatistics}
	 */
	protected ItemGroupStatistics<T> getItemGroupStatistics()
	{
		return IdleItemGroupStatistics.get();
	}

	@Override
	public List<T> generate(List<T> destination)
	{
		int start=destination.size();

		ItemFilterHandler<T> filterHandler=getFilterHandler();
		ItemFilter<T> preFilter=filterHandler.getPreFilter();
		ItemFilter<T> innerFilter=filterHandler.getInnerFilter();

		EnumerableFlowHandler<T> flowHandler=getFlowHandler();
		Modeler modeler=flowHandler.getModeler();
		Scorer scorer=flowHandler.getScorer();

		ItemGroupStatistics<T> itemGroupStatistics=getItemGroupStatistics();

		int batchSize=flowHandler.getBatchSize();
		BatchScoreableRestrictedBatchSample<T> batchSample=createBatchSample(batchSize);
		int currSize;
		boolean needScore=false;
		for(T item : flowHandler.getCandidates())
		{
			if(preFilter.test(item))
			{
				item.modelBy(modeler);
				currSize=batchSample.add(item);
				needScore=true;
				if(currSize==batchSize)
				{
					batchSample.scoreBy(scorer);
					needScore=false;
					for(T sample : batchSample)
					{
						if(innerFilter.test(sample))
						{
							itemGroupStatistics.increaseStatistics(sample);
							verbose(sample);
							destination.add(sample);
						}
					}
				}
			}
		}
		if(needScore)
		{
			batchSample.scoreBy(scorer);
			needScore=false;
			for(T sample : batchSample)
			{
				if(innerFilter.test(sample))
				{
					itemGroupStatistics.increaseStatistics(sample);
					verbose(sample);
					destination.add(sample);
				}
			}
		}

		itemGroupStatistics.computeStatistics(destination.subList(start, destination.size()));
		ItemFilter<T> afterFilter=filterHandler.getAfterFilter(itemGroupStatistics);
		if(afterFilter!=null)
		{
			Iterator<T> iter=destination.listIterator(start);
			while(iter.hasNext())
			{
				T item=iter.next();
				if(!afterFilter.test(item))
					iter.remove();
			}
		}
		return destination;
	}
}
