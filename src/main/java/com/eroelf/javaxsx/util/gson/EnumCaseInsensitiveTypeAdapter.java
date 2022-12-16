package com.eroelf.javaxsx.util.gson;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Case insensitive type adapter for {@code enum}s.
 * 
 * @author weikun.zhong
 */
public class EnumCaseInsensitiveTypeAdapter<T extends Enum<T>> implements JsonSerializer<T>, JsonDeserializer<T>
{
	private Map<String, T> nameToEnumMap=new HashMap<>();

	public EnumCaseInsensitiveTypeAdapter(Class<T> enumClass)
	{
		for(T oneEnum : enumClass.getEnumConstants())
		{
			nameToEnumMap.put(oneEnum.toString().toUpperCase(Locale.ENGLISH), oneEnum);
		}
	}

	@Override
	public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context)
	{
		if(src!=null)
			return new JsonPrimitive(src.toString());
		else
			return JsonNull.INSTANCE;
	}

	@Override
	public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		if(!json.isJsonNull())
			return nameToEnumMap.get(json.getAsString().toUpperCase(Locale.ENGLISH));
		else
			return null;
	}
}
