package com.eroelf.javaxsx.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * APIs to deal with time.
 * 
 * @author weikun.zhong
 */
public class TimeUtil
{
	public static ZonedDateTime getUTC(String dateStr, String fmt)
	{
		return getUTC(dateStr, DateTimeFormatter.ofPattern(fmt));
	}

	public static ZonedDateTime getUTC(String dateStr, DateTimeFormatter formatter)
	{
		return getZoned(dateStr, formatter, ZoneId.of("UTC"));
	}

	public static ZonedDateTime getUTC(LocalDate ld)
	{
		return getZoned(ld, ZoneId.of("UTC"));
	}

	public static ZonedDateTime getUTC(LocalDateTime ldt)
	{
		return getZoned(ldt, ZoneId.of("UTC"));
	}

	public static ZonedDateTime getUTC(Date date)
	{
		return getZoned(date, ZoneId.of("UTC"));
	}

	public static ZonedDateTime getUTCFromDate(String dateStr, String fmt)
	{
		return getUTCFromDate(dateStr, DateTimeFormatter.ofPattern(fmt));
	}

	public static ZonedDateTime getUTCFromDate(String dateStr, DateTimeFormatter formatter)
	{
		return getZonedFromDate(dateStr, formatter, ZoneId.of("UTC"));
	}

	public static ZonedDateTime getZoned(String dateTimeStr, String fmt)
	{
		return getZoned(dateTimeStr, DateTimeFormatter.ofPattern(fmt));
	}

	public static ZonedDateTime getZoned(String dateTimeStr, DateTimeFormatter formatter)
	{
		return getZoned(dateTimeStr, formatter, null);
	}

	public static ZonedDateTime getZoned(String dateTimeStr, String fmt, ZoneId zoneId)
	{
		return getZoned(dateTimeStr, DateTimeFormatter.ofPattern(fmt), zoneId);
	}

	public static ZonedDateTime getZoned(String dateTimeStr, DateTimeFormatter formatter, ZoneId zoneId)
	{
		try
		{
			ZonedDateTime zonedDateTime=ZonedDateTime.parse(dateTimeStr, formatter);
			return zoneId==null ? zonedDateTime : zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
		}
		catch(Exception e)
		{
			return ZonedDateTime.of(getLocal(dateTimeStr, formatter), zoneId);
		}
	}

	public static ZonedDateTime getZoned(LocalDate ld, ZoneId zoneId)
	{
		return getZoned(getLocalFromDate(ld), zoneId);
	}

	public static ZonedDateTime getZoned(LocalDateTime ldt, ZoneId zoneId)
	{
		return ZonedDateTime.of(ldt, zoneId);
	}

	public static ZonedDateTime getZoned(Date date, ZoneId zoneId)
	{
		return ZonedDateTime.ofInstant(date.toInstant(), zoneId);
	}

	public static ZonedDateTime getZonedFromDate(String dateStr, String fmt, ZoneId zoneId)
	{
		return getZonedFromDate(dateStr, DateTimeFormatter.ofPattern(fmt), zoneId);
	}

	public static ZonedDateTime getZonedFromDate(String dateStr, DateTimeFormatter formatter, ZoneId zoneId)
	{
		return ZonedDateTime.of(getLocalFromDate(dateStr, formatter), zoneId);
	}

	public static ZonedDateTime getUTCFromLocal(LocalDate ld)
	{
		return getZonedFromLocal(ld, ZoneId.of("UTC"));
	}

	public static ZonedDateTime getUTCFromLocal(LocalDateTime ldt)
	{
		return getZonedFromLocal(ldt, ZoneId.of("UTC"));
	}

	public static ZonedDateTime getUTCFromLocal(Date date)
	{
		return getZonedFromLocal(date, ZoneId.of("UTC"));
	}

	public static ZonedDateTime getUTCFromZoned(LocalDate ld, ZoneId original)
	{
		return getZonedFromZoned(ld, original, ZoneId.of("UTC"));
	}

	public static ZonedDateTime getUTCFromZoned(LocalDateTime ldt, ZoneId original)
	{
		return getZonedFromZoned(ldt, original, ZoneId.of("UTC"));
	}

	public static ZonedDateTime getUTCFromZoned(Date date, ZoneId original)
	{
		return getZonedFromZoned(date, original, ZoneId.of("UTC"));
	}

	public static ZonedDateTime getZonedFromLocal(LocalDate ld, ZoneId target)
	{
		return getZonedFromZoned(ld, ZoneId.systemDefault(), target);
	}

	public static ZonedDateTime getZonedFromLocal(LocalDateTime ldt, ZoneId target)
	{
		return getZonedFromZoned(ldt, ZoneId.systemDefault(), target);
	}

	public static ZonedDateTime getZonedFromLocal(Date date, ZoneId target)
	{
		return getZonedFromZoned(date, ZoneId.systemDefault(), target);
	}

	public static ZonedDateTime getZonedFromZoned(LocalDate ld, ZoneId original, ZoneId target)
	{
		return getZonedFromZoned(getLocalFromDate(ld), original, target);
	}

	public static ZonedDateTime getZonedFromZoned(LocalDateTime ldt, ZoneId original, ZoneId target)
	{
		return ZonedDateTime.of(ldt, original).withZoneSameInstant(target);
	}

	public static ZonedDateTime getZonedFromZoned(Date date, ZoneId original, ZoneId target)
	{
		return ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).withZoneSameLocal(original).withZoneSameInstant(target);
	}

	public static LocalDateTime getUTCLocal(ZonedDateTime zdt)
	{
		return getLocal(zdt, ZoneId.of("UTC"));
	}

	public static LocalDateTime getLocal(String dateTimeStr, String fmt)
	{
		return getLocal(dateTimeStr, DateTimeFormatter.ofPattern(fmt));
	}

	public static LocalDateTime getLocal(String dateTimeStr, DateTimeFormatter formatter)
	{
		return LocalDateTime.parse(dateTimeStr, formatter);
	}

	public static LocalDateTime getLocal(ZonedDateTime zdt)
	{
		return zdt.toLocalDateTime();
	}

	public static LocalDateTime getLocal(ZonedDateTime zdt, ZoneId zoneId)
	{
		return zdt.withZoneSameInstant(zoneId).toLocalDateTime();
	}

	public static LocalDateTime getLocal(Date date)
	{
		return LocalDateTime.from(date.toInstant());
	}

	public static LocalDateTime getLocalFromDate(String dateStr, String fmt)
	{
		return getLocalFromDate(dateStr, DateTimeFormatter.ofPattern(fmt));
	}

	public static LocalDateTime getLocalFromDate(String dateStr, DateTimeFormatter formatter)
	{
		return getLocalFromDate(LocalDate.parse(dateStr, formatter));
	}

	public static LocalDateTime getLocalFromDate(LocalDate ld)
	{
		return ld.atTime(LocalTime.MIN);
	}

	public static LocalDate getLocalDate(String dateStr, String fmt)
	{
		return getLocalDate(dateStr, DateTimeFormatter.ofPattern(fmt));
	}

	public static LocalDate getLocalDate(String dateStr, DateTimeFormatter formatter)
	{
		return LocalDate.parse(dateStr, formatter);
	}

	public static LocalDate getLocalDate(ZonedDateTime zdt)
	{
		return getLocal(zdt).toLocalDate();
	}

	public static LocalDate getLocalDate(ZonedDateTime zdt, ZoneId zoneId)
	{
		return getLocal(zdt, zoneId).toLocalDate();
	}

	public static LocalDate getLocalDate(Date date)
	{
		return LocalDate.from(date.toInstant());
	}

	public static boolean isBeforeEqual(ZonedDateTime zdt1, ZonedDateTime zdt2)
	{
		return !zdt1.isAfter(zdt2);
	}

	public static boolean isAfterEqual(ZonedDateTime zdt1, ZonedDateTime zdt2)
	{
		return !zdt1.isBefore(zdt2);
	}

	public static boolean isBeforeEqual(LocalDateTime ldt1, LocalDateTime ldt2)
	{
		return !ldt1.isAfter(ldt2);
	}

	public static boolean isAfterEqual(LocalDateTime ldt1, LocalDateTime ldt2)
	{
		return !ldt1.isBefore(ldt2);
	}

	public static boolean isBeforeEqual(LocalDate ld1, LocalDate ld2)
	{
		return !ld1.isAfter(ld2);
	}

	public static boolean isAfterEqual(LocalDate ld1, LocalDate ld2)
	{
		return !ld1.isBefore(ld2);
	}

	public static String formatUTC(ZonedDateTime zdt, String fmt)
	{
		return formatUTC(zdt, DateTimeFormatter.ofPattern(fmt));
	}

	public static String formatUTC(ZonedDateTime zdt, DateTimeFormatter formatter)
	{
		return formatZoned(zdt, formatter, ZoneId.of("UTC"));
	}

	public static String formatZoned(ZonedDateTime zdt, String fmt, ZoneId zoneId)
	{
		return formatZoned(zdt, DateTimeFormatter.ofPattern(fmt), zoneId);
	}

	public static String formatZoned(ZonedDateTime zdt, DateTimeFormatter formatter, ZoneId zoneId)
	{
		return zdt.withZoneSameInstant(zoneId).format(formatter);
	}

	public Date toDateWithSameInstant(ZonedDateTime zonedDateTime)
	{
		return Date.from(zonedDateTime.toInstant());
	}

	public Date toDateWithSameLocal(ZonedDateTime zonedDateTime)
	{
		return Date.from(zonedDateTime.withZoneSameLocal(ZoneId.systemDefault()).toInstant());
	}

	public Date toDate(LocalDateTime localDateTime)
	{
		return toDate(localDateTime, ZoneId.systemDefault());
	}

	public Date toDate(LocalDateTime localDateTime, ZoneId zoneId)
	{
		return Date.from(localDateTime.atZone(zoneId).toInstant());
	}

	public ZonedDateTime toZonedDateTimeWithSameInstant(Date date)
	{
		return toZonedDateTimeWithSameInstant(date, ZoneId.systemDefault());
	}

	public ZonedDateTime toZonedDateTimeWithSameInstant(Date date, ZoneId zoneId)
	{
		return date.toInstant().atZone(zoneId);
	}

	public ZonedDateTime toZonedDateTimeWithSameLocal(Date date, ZoneId zoneId)
	{
		return date.toInstant().atZone(ZoneId.systemDefault()).withZoneSameLocal(zoneId);
	}

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
