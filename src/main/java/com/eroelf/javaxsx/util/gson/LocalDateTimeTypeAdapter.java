package com.eroelf.javaxsx.util.gson;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
 * Type adapter for {@link LocalDateTime}.
 * 
 * @author weikun.zhong
 */
public class LocalDateTimeTypeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime>
{
	private DateTimeFormatter formatter;

	public LocalDateTimeTypeAdapter(String pattern)
	{
		this(DateTimeFormatter.ofPattern(pattern));
	}

	public LocalDateTimeTypeAdapter(String pattern, Locale locale)
	{
		this(DateTimeFormatter.ofPattern(pattern, locale));
	}

	public LocalDateTimeTypeAdapter(DateTimeFormatter formatter)
	{
		this.formatter=formatter;
	}

	@Override
	public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context)
	{
		if(src!=null)
			return new JsonPrimitive(src.format(formatter));
		else
			return JsonNull.INSTANCE;
	}

	@Override
	public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		if(!json.isJsonNull())
		{
			String jsonStr=json.getAsString();
			if(!jsonStr.isEmpty())
				return LocalDateTime.parse(jsonStr, formatter);
		}
		return null;
	}
}
