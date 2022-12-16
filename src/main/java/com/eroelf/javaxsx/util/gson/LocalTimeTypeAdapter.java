package com.eroelf.javaxsx.util.gson;

import java.lang.reflect.Type;
import java.time.LocalTime;
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
 * Type adapter for {@link LocalTime}.
 * 
 * @author weikun.zhong
 */
public class LocalTimeTypeAdapter implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime>
{
	private DateTimeFormatter formatter;

	public LocalTimeTypeAdapter(String pattern)
	{
		this(DateTimeFormatter.ofPattern(pattern));
	}

	public LocalTimeTypeAdapter(String pattern, Locale locale)
	{
		this(DateTimeFormatter.ofPattern(pattern, locale));
	}

	public LocalTimeTypeAdapter(DateTimeFormatter formatter)
	{
		this.formatter=formatter;
	}

	@Override
	public JsonElement serialize(LocalTime src, Type typeOfSrc, JsonSerializationContext context)
	{
		if(src!=null)
			return new JsonPrimitive(src.format(formatter));
		else
			return JsonNull.INSTANCE;
	}

	@Override
	public LocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		if(!json.isJsonNull())
		{
			String jsonStr=json.getAsString();
			if(!jsonStr.isEmpty())
				return LocalTime.parse(jsonStr, formatter);
		}
		return null;
	}
}
