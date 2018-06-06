package com.eroelf.javaxsx.behavior;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

	public Behavior(Date actionTime)
	{
		this.actionTime=actionTime;
	}

	public Behavior(String line)
	{
		parse(line);
	}

	protected StringBuilder attachTo(StringBuilder stringBuilder)
	{
		return stringBuilder.append(new SimpleDateFormat(DATE_FORMAT).format(actionTime));
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
			actionTime=new SimpleDateFormat(DATE_FORMAT).parse(line.substring(0, pos));
		}
		catch(ParseException e)
		{
			throw new IllegalArgumentException(String.format("The first part of the line must be the actionTime with the format of '%s'", DATE_FORMAT), e);
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
