package com.eroelf.javaxsx.util.gson;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Type adapter for {@link ZonedDateTime}.
 * 
 * @author weikun.zhong
 */
public class ZonedDateTimeTypeAdapter implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime>
{
	private DateTimeFormatter formatter;
	private ZoneId defaultZoneId;

	public ZonedDateTimeTypeAdapter(String pattern)
	{
		this(DateTimeFormatter.ofPattern(pattern));
	}

	public ZonedDateTimeTypeAdapter(String pattern, Locale locale)
	{
		this(DateTimeFormatter.ofPattern(pattern, locale));
	}

	public ZonedDateTimeTypeAdapter(String pattern, ZoneId defaultZoneId)
	{
		this(DateTimeFormatter.ofPattern(pattern), defaultZoneId);
	}

	public ZonedDateTimeTypeAdapter(String pattern, Locale locale, ZoneId defaultZoneId)
	{
		this(DateTimeFormatter.ofPattern(pattern, locale), defaultZoneId);
	}

	public ZonedDateTimeTypeAdapter(DateTimeFormatter formatter)
	{
		this(formatter, null);
	}

	public ZonedDateTimeTypeAdapter(DateTimeFormatter formatter, ZoneId defaultZoneId)
	{
		this.formatter=formatter;
		this.defaultZoneId=defaultZoneId;
	}

	@Override
	public JsonElement serialize(ZonedDateTime src, Type typeOfSrc, JsonSerializationContext context)
	{
		if(src!=null)
			return new JsonPrimitive((defaultZoneId==null ? src : src.withZoneSameInstant(defaultZoneId)).format(formatter));
		else
			return JsonNull.INSTANCE;
	}

	@Override
	public ZonedDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		if(!json.isJsonNull())
		{
			String jsonStr=json.getAsString();
			if(!jsonStr.isEmpty())
			{
				try
				{
					return ZonedDateTime.parse(jsonStr, formatter);
				}
				catch(DateTimeParseException e)
				{
					if(defaultZoneId!=null)
					{
						try
						{
							return ZonedDateTime.of(LocalDateTime.parse(jsonStr, formatter), defaultZoneId);
						}
						catch(Exception e2)
						{
							e2.initCause(e);
							throw e2;
						}
					}
					else
						throw e;
				}
			}
		}
		return null;
	}
}
