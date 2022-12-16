package com.eroelf.javaxsx.util.gson;

import java.lang.reflect.Type;
import java.util.UUID;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Type adapter for {@link UUID}.
 * 
 * @author weikun.zhong
 */
public class UUIDTypeAdapter implements JsonSerializer<UUID>, JsonDeserializer<UUID>
{
	@Override
	public JsonElement serialize(UUID src, Type typeOfSrc, JsonSerializationContext context)
	{
		return src!=null ? new JsonPrimitive(src.toString()) : JsonNull.INSTANCE;
	}

	@Override
	public UUID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		return json.isJsonNull() ? null : UUID.fromString(json.getAsString());
	}
}
