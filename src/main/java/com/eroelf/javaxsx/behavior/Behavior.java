package com.eroelf.javaxsx.behavior;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

import com.eroelf.javaxsx.util.TimeUtil;

/**
 * Base class of all behaviors.
 * Records the time when this behavior happened.
 * 
 * @author weikun.zhong
 */
public class Behavior extends History implements BehaviorInterface
{
	protected Behavior()
	{}

	public Behavior(ZonedDateTime actionTime)
	{
		super(actionTime);
	}

	public Behavior(String line)
	{
		parse(line);
	}

	protected StringBuilder attachTo(StringBuilder stringBuilder)
	{
		return stringBuilder.append(TimeUtil.formatUTC(actionTime, DATE_TIME_FORMATTER));
	}

	@Override
	public String toString()
	{
		return attachTo(new StringBuilder()).append("\t").append(this.getClass().getName()).toString();
	}

	public int parse(String line)
	{
		int pos=line.indexOf("\t");
		try
		{
			actionTime=TimeUtil.getUTC(line.substring(0, pos), DATE_TIME_FORMATTER);
		}
		catch(DateTimeParseException e)
		{
			throw new IllegalArgumentException(String.format("The first part of the line must be the actionTime with the format of '%s'", DATE_TIME_FORMAT), e);
		}
		return pos;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Behavior> T assignFrom(String line)
	{
		try
		{
			Class<T> clazz=(Class<T>)Class.forName(line.substring(line.lastIndexOf("\t")+1));
			return clazz.getConstructor(String.class).newInstance(line);
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException(e);
		}
	}
}
