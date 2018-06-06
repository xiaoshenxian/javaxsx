package com.eroelf.javaxsx.util.ml.flow.convert;

import java.util.List;
import java.util.ListIterator;

import com.eroelf.javaxsx.util.ml.feature.Item;
import com.eroelf.javaxsx.util.ml.flow.info.Info;
import com.eroelf.javaxsx.util.ml.flow.info.InfoFactory;
import com.eroelf.javaxsx.util.ml.flow.log.InfoLog;
import com.eroelf.javaxsx.util.ml.flow.log.InfoLogFactory;

/**
 * Converts a list of {@link Item} objects into a list of {@link Info} objects for a specified customer and a list of {@link InfoLog} objects for logging information.
 * This class instances are typically used for separating data structures in and out of an modeling and scoring flow, as well as for logging necessary information.
 * 
 * @author weikun.zhong
 *
 * @param <T> the type of the data structure into the flow.
 * @param <I> the type of the data structure out of the flow.
 * @param <L> the type of the data structure for logging.
 */
public abstract class Converter<T extends Item, I extends Info, L extends InfoLog>
{
	/**
	 * Get an {@link InfoFactory} to create instances for the specified customer.
	 * 
	 * @return an {@link InfoFactory} instance.
	 */
	public abstract InfoFactory<T, I> getInfoFactory();

	/**
	 * Get an {@link InfoLogFactory} to create instances for logging.
	 * 
	 * @return an {@link InfoLogFactory} instance.
	 */
	public abstract InfoLogFactory<T, L> getInfoLogFactory();

	/**
	 * Do the converting.
	 * Objects filled into the {@code infoList} are one-to-one corresponded to objects filled into the {@code infoLogList} via their array indices.
	 * 
	 * @param itemList the list contains {@link Item} objects to be convert.
	 * @param infoList the list to receive {@link Info} objects to be used out of the flow.
	 * @param infoLogList the list to receive {@link InfoLog} objects for logging.
	 * @param start the index of the first element in the {@code itemList} that is about to be convert.
	 * @param length indicates how many {@link Item} objects including an after the {@code start} {@link Item} objects need to be convert.
	 * @param verboseInfo if keep more information in the {@link Info} objects.
	 * @param verboseLog if keep more information in the {@link InfoLog} objects.
	 * @return an {@code int[]} contains three elements which the first one is the true ending index, the second one is the number of {@link Item} objects which are successfully converted, and the third one is the number of {@link Item} objects which failed during converting.
	 * @see {@link #convert(List, List, List, InfoFactory, InfoLogFactory, int, int, boolean, boolean)}
	 */
	public int[] convert(List<? extends T> itemList, List<? super I> infoList, List<? super L> infoLogList, int start, int length, boolean verboseInfo, boolean verboseLog)
	{
		return convert(itemList, infoList, infoLogList, getInfoFactory(), getInfoLogFactory(), start, length, verboseInfo, verboseLog);
	}

	/**
	 * Do the converting, the static version.
	 * Objects filled into the {@code infoList} are one-to-one corresponded to objects filled into the {@code infoLogList} via their array indices.
	 * 
	 * @param <T> the type of the data structure into the flow.
	 * @param <I> the type of the data structure out of the flow.
	 * @param <L> the type of the data structure for logging.
	 * 
	 * @param itemList the list contains {@link Item} objects to be convert.
	 * @param infoList the list to receive {@link Info} objects to be used out of the flow.
	 * @param infoLogList the list to receive {@link InfoLog} objects for logging.
	 * @param infoFactory an {@link InfoFactory} to create instances for the specified customer.
	 * @param infoLogFactory an {@link InfoLogFactory} to create instances for logging.
	 * @param start the index of the first element in the {@code itemList} that is about to be convert.
	 * @param length indicates how many {@link Item} objects including an after the {@code start} {@link Item} objects need to be convert.
	 * @param verboseInfo if keep more information in the {@link Info} objects.
	 * @param verboseLog if keep more information in the {@link InfoLog} objects.
	 * @return an {@code int[]} contains three elements which the first one is the true ending index, the second one is the number of {@link Item} objects which are successfully converted, and the third one is the number of {@link Item} objects which failed during converting.
	 * @see {@link #convert(List, List, List, int, int, boolean, boolean)}
	 */
	public static <T extends Item, I extends Info, L extends InfoLog> int[] convert(List<? extends T> itemList, List<? super I> infoList, List<? super L> infoLogList, InfoFactory<T, I> infoFactory, InfoLogFactory<T, L> infoLogFactory, int start, int length, boolean verboseInfo, boolean verboseLog)
	{
		int validCount=0;
		int invalidCount=0;
		if(start<itemList.size())
		{
			ListIterator<? extends T> iter=itemList.listIterator(start);
			while(iter.hasNext())
			{
				T item=iter.next();
				I info=infoFactory.create(item, verboseInfo);
				if(info!=null)
				{
					info.convertFrom(item, verboseInfo);
					if(info.isValid() || verboseInfo)
					{
						L infoLog=infoLogFactory.create(item, verboseLog);
						infoLog.logFrom(item, verboseLog);
						infoList.add(info);
						infoLogList.add(infoLog);
						if(++validCount>=length)
							break;
					}
				}
				else
					++invalidCount;
			}
		}

		return new int[]{start+validCount+invalidCount, validCount, invalidCount};
	}
}
