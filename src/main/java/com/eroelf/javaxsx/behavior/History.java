package com.eroelf.javaxsx.behavior;

import java.util.Date;

/**
 * Base class of all class which records some information related to a historical event.
 * 
 * @author weikun.zhong
 */
public class History implements Comparable<History>
{
	public Date actionTime;

	public final static String DATE_FORMAT="yyyyMMdd HH:mm:ss.SSS";

	@Override
	public int compareTo(History obj)
	{
		return this.actionTime.compareTo(obj.actionTime);
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof History && actionTime.equals(((History)obj).actionTime);
	}

	@Override
	public int hashCode()
	{
		return actionTime.hashCode();
	}
}
