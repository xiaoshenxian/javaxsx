package com.eroelf.javaxsx.behavior;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

/**
 * Provide methods to deal with {@link History} instances and collections.
 * 
 * @author weikun.zhong
 */
public final class Histories
{
	/**
	 * Trim the {@link History} list by deleting all elements that happened before baseTime-beforeTime and at or after baseTime+afterTime.
	 * In other words, keep only the {@link History} objects happened within the interval: [baseTime-beforeTime, baseTime+afterTime).
	 * The units of beforeTime and afterTime are defined by timeUnit.
	 * If needSort is <tt>true</tt>, the list will be sorted into ascending order, according to the {@link History}'s {@linkplain History natural ordering}.
	 * 
	 * @param list The list to be processing.
	 * @param needSorting The list will be sorted first if this parameter is <tt>true</tt>. If this parameter is <tt>false</tt>, it would be the user's responsibility to guarantee that the list is sorted into ascending order, according to the {@link History}'s {@linkplain History natural ordering}.
	 * @param baseTime The base time for trim.
	 * @param beforeTime Lower time span to the baseTime.
	 * @param afterTime Upper time span to the baseTime.
	 * @param timeUnit Define the units of both beforeTime and afterTime.
	 * @return <tt>true</tt> if the list was trimmed, otherwise <tt>false</tt>.
	 */
	public static boolean trimList(List<? extends History> list, boolean needSorting, Date baseTime, long beforeTime, long afterTime, TimeUnit timeUnit)
	{
		long base=baseTime.getTime();
		Date beforeDate=new Date(base-timeUnit.toMillis(beforeTime));
		Date afterDate=new Date(base+timeUnit.toMillis(afterTime));
		return trimList(list, needSorting, beforeDate, afterDate);
	}

	/**
	 * Trim the {@link History} list by deleting all elements that happened before beforeDate and at or after afterDate.
	 * In other words, keep only the {@link History} objects happened within the interval: [beforeDate, afterDate).
	 * The list will be sorted into ascending order, according to the {@link History}'s {@linkplain History natural ordering}.
	 * 
	 * @param list The list to be processing.
	 * @param needSorting The list will be sorted first if this parameter is <tt>true</tt>. If this parameter is <tt>false</tt>, it would be the user's responsibility to guarantee that the list is sorted into ascending order, according to the {@link History}'s {@linkplain History natural ordering}.
	 * @param beforeDate The lower bound of the retained interval (inclusive).
	 * @param afterDate The upper bound of the retained interval (exclusive).
	 * @return <tt>true</tt> if the list was trimmed, otherwise <tt>false</tt>.
	 */
	public static boolean trimList(List<? extends History> list, boolean needSorting, Date beforeDate, Date afterDate)
	{
		if(list!=null && !list.isEmpty())
		{
			if(needSorting)
				Collections.sort(list);

			History history=new History();
			history.actionTime=beforeDate;
			int i=Collections.binarySearch(list, history);
			if(i<0)
				i=-i-1;
			history.actionTime=afterDate;
			int j=Collections.binarySearch(list, history);
			if(j<0)
				j=-j-1;

			int size=list.size();
			if(i<j)
			{
				list.subList(j, size).clear();
				list.subList(0, i).clear();
			}
			else
				list.clear();

			return list.size()<size;
		}
		return false;
	}

	/**
	 * Combine two {@link History} lists into one, with the combined list sorted into ascending order, according to the {@link History}'s {@linkplain History natural ordering}.
	 * 
	 * @param l1 One list to be combining.
	 * @param l2 The other list to be combining.
	 * @return The combined list sorted into ascending order, according to the {@link History}'s {@linkplain History natural ordering}, or {@code null} if both l1 and l2 are {@code null}.
	 */
	public static List<? extends History> combineLists(List<? extends History> l1, List<? extends History> l2)
	{
		if(l1!=null && l2!=null)
		{
			TreeSet<History> set=new TreeSet<>(l1);
			set.addAll(l2);
			return new ArrayList<>(set);
		}
		else if(l1!=null)
			return new ArrayList<>(l1);
		else if(l2!=null)
			return new ArrayList<>(l2);
		else
			return null;
	}

	/**
	 * The fuse strategy interface for {@link Histories#fuseHistories}.
	 * 
	 * @author weikun.zhong
	 *
	 * @param <H1> the type of {@link History} objects in the first input list.
	 * @param <H2> the type of {@link History} objects in the second input list.
	 * @param <H3> the type of {@link History} objects in the output list.
	 * 
	 * @see Histories#fuseHistories
	 */
	public static interface FuseStrategy<H1 extends History, H2 extends History, H3 extends History>
	{
		public boolean accept(H1 h1, H2 h2, H3 h3);
		public H3 buildFromH1(H1 h1);
		public H3 buildFromH2(H2 h2);
		public H3 combineH3H2(H3 h3, H2 h2);
	}

	/**
	 * Fuse two {@link History} list by the given strategy.
	 * The two lists will first be combined and sorted, and then any H2 object between two adjacent H1 objects and satisfied the given {@link FuseStrategy} object will be fused to the first H1 object and generate a H3 object.
	 * Any single H1 or H2 object will be changed into H3 object according to the given strategy.
	 * 
	 * @param <H1> the type of {@link History} objects in {@code list1}.
	 * @param <H2> the type of {@link History} objects in {@code list2}.
	 * @param <H3> the type of {@link History} objects in the ouput list.
	 * 
	 * @param list1 The H1 object list.
	 * @param list2 The H2 object list.
	 * @param strategy The fuse strategy.
	 * @return The fused H3 list.
	 */
	public static <H1 extends History, H2 extends History, H3 extends History> List<H3> fuseHistories(List<H1> list1, List<H2> list2, FuseStrategy<H1, H2, H3> strategy)
	{
		List<H3> res=new ArrayList<>();
		final class __Temp extends History
		{
			private H1 h1;
			private H2 h2;
			private __Temp(H1 h1, H2 h2)
			{
				if(h1!=null && h2==null)
				{
					actionTime=h1.actionTime;
					this.h1=h1;
				}
				else if(h1==null && h2!=null)
				{
					actionTime=h2.actionTime;
					this.h2=h2;
				}
				else
					throw new IllegalArgumentException("Only one value in h1 or h2 can be and must be null!");
			}
		}
		List<__Temp> list=new ArrayList<>();
		if(list1!=null)
			list1.forEach((h1) -> {list.add(new __Temp(h1, null));});
		if(list2!=null)
			list2.forEach((h2) -> {list.add(new __Temp(null, h2));});
		Collections.sort(list);
		H1 currH1=null;
		H3 currH3=null;
		for(__Temp temp : list)
		{
			if(temp.h1!=null)
			{
				if(currH3!=null)
					res.add(currH3);
				currH1=temp.h1;
				currH3=strategy.buildFromH1(temp.h1);
			}
			else if(temp.h2!=null)
			{
				if(currH3!=null)
				{
					if(strategy.accept(currH1, temp.h2, currH3))
						currH3=strategy.combineH3H2(currH3, temp.h2);
					else
					{
						res.add(currH3);
						currH1=null;
						currH3=strategy.buildFromH2(temp.h2);
					}
				}
				else
					currH3=strategy.buildFromH2(temp.h2);
			}
		}
		if(currH3!=null)
			res.add(currH3);
		return res;
	}

	private Histories()
	{}
}
