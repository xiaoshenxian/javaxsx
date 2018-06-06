package com.eroelf.javaxsx.behavior;

import java.util.Date;

import com.eroelf.javaxsx.util.Strings;

/**
 * Base class of all user behaviors.
 * Contains the specified user's identifier.
 * 
 * @author weikun.zhong
 */
public class UserBehavior extends Behavior
{
	public String identifier;

	protected UserBehavior()
	{}

	public UserBehavior(Date actionTime, String identifier)
	{
		super(actionTime);
		this.identifier=identifier;
	}

	public UserBehavior(String line)
	{
		parse(line);
	}

	@Override
	public int compareTo(History obj)
	{
		int re=obj instanceof UserBehavior ? this.identifier.compareTo(((UserBehavior)obj).identifier) : 0;
		return re!=0 ? re : super.compareTo(obj);
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof UserBehavior && identifier.equals(((UserBehavior)obj).identifier) && actionTime.equals(((UserBehavior)obj).actionTime);
	}

	@Override
	public int hashCode()
	{
		return super.hashCode()*31+identifier.hashCode();
	}

	@Override
	protected StringBuilder attachTo(StringBuilder stringBuilder)
	{
		return super.attachTo(stringBuilder).append("\t").append(identifier);
	}

	@Override
	public int parse(String line)
	{
		int fromIndex=super.parse(line)+1;
		int pos=line.indexOf("\t", fromIndex);
		identifier=line.substring(fromIndex, pos);
		if(!Strings.isValid(identifier))
			identifier=null;
		return pos;
	}
}
