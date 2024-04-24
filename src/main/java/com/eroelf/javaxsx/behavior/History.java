package com.eroelf.javaxsx.behavior;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Base class of all class which records some information related to a historical event.
 * 
 * @author weikun.zhong
 */
public class History implements Comparable<History>
{
	public ZonedDateTime actionTime;

	public final static String DATE_TIME_FORMAT="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	public final static DateTimeFormatter DATE_TIME_FORMATTER=DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

	protected History()
	{}

	public History(ZonedDateTime actionTime)
	{
		this.actionTime=actionTime;
	}

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
