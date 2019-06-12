package com.eroelf.javaxsx.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * A class aims to monitoring how much the time cost in a procedure.
 * 
 * @author weikun.zhong
 */
public class TimeLasted
{
	private Date date;
	private SimpleDateFormat sdfDay=new SimpleDateFormat("D");
	private SimpleDateFormat sdfTime=new SimpleDateFormat(" HH:mm:ss.SSS");
	private long endNano;
	private long beginNano;

	private static enum Status
	{
		UNUSED, TIMING, FINISHED;
	}

	private Status status=Status.UNUSED;

	public TimeLasted(boolean startNow)
	{
		sdfDay.setTimeZone(TimeZone.getTimeZone("GMT"));
		sdfTime.setTimeZone(TimeZone.getTimeZone("GMT"));
		if(startNow)
			start();
	}

	public void start()
	{
		status=Status.TIMING;
		beginNano=System.nanoTime();
	}

	public long current()
	{
		long theTime=System.nanoTime();
		return theTime-beginNano;
	}

	public long end()
	{
		long theTime=System.nanoTime();
		if(isTiming())
		{
			endNano=theTime;
			status=Status.FINISHED;
		}
		return endNano-beginNano;
	}

	public double getLapseMilli()
	{
		return getLapseNano()/1e6;
	}

	public long getLapseNano()
	{
		return end();
	}

	public String getLapse()
	{
		return format(end());
	}

	public double getIntervalMilli()
	{
		return getIntervalNano()/1e6;
	}

	public long getIntervalNano()
	{
		long span=getLapseNano();
		start();
		return span;
	}

	public String getInterval()
	{
		String spanStr=getLapse();
		start();
		return spanStr;
	}

	public double getCurrentMilli()
	{
		return getCurrentNano()/1e6;
	}

	public long getCurrentNano()
	{
		return current();
	}

	public String getCurrent()
	{
		return format(current());
	}

	private String format(long timeSpan)
	{
		date=new Date((int)((double)(timeSpan)/1000000d));
		return String.format("%d%s (%d ns in total)", Integer.valueOf(sdfDay.format(date))-1, sdfTime.format(date), timeSpan);
	}

	private boolean isTiming()
	{
		switch (status)
		{
		case UNUSED:
			throw new RuntimeException("Not started.");
		case TIMING:
			return true;
		case FINISHED:
			return false;
		}
		return false;
	}
}
