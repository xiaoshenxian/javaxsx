package com.eroelf.javaxsx.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * APIs to deal with time.
 * 
 * @author weikun.zhong
 */
public class TimeUtil
{
	public static Date getDate(String dateStr, String fmt, int delta, int field)
	{
		try
		{
			return getDate(new SimpleDateFormat(fmt).parse(dateStr), delta, field);
		}
		catch(ParseException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static Date getDate(Date date, int delta, int field)
	{
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(field, delta);
		return calendar.getTime();
	}

	public static String format(Date date, String fmt)
	{
		return new SimpleDateFormat(fmt).format(date);
	}
}
