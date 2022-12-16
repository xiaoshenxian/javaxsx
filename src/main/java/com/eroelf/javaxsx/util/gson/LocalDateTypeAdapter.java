package com.eroelf.javaxsx.util.gson;

import java.lang.reflect.Type;
import java.time.LocalDate;
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
 * Type adapter for {@link LocalDate}.
 * 
 * @author weikun.zhong
 */
public class LocalDateTypeAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate>
{
	private DateTimeFormatter formatter;

	public LocalDateTypeAdapter(String pattern)
	{
		this(DateTimeFormatter.ofPattern(pattern));
	}

	public LocalDateTypeAdapter(String pattern, Locale locale)
	{
		this(DateTimeFormatter.ofPattern(pattern, locale));
	}

	public LocalDateTypeAdapter(DateTimeFormatter formatter)
	{
		this.formatter=formatter;
	}

	@Override
	public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context)
	{
		if(src!=null)
			return new JsonPrimitive(src.format(formatter));
		else
			return JsonNull.INSTANCE;
	}

	@Override
	public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		if(!json.isJsonNull())
		{
			String jsonStr=json.getAsString();
			if(!jsonStr.isEmpty())
				return LocalDate.parse(jsonStr, formatter);
		}
		return null;
	}
}
