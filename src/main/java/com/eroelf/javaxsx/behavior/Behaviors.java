package com.eroelf.javaxsx.behavior;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.eroelf.javaxsx.util.gson.ZonedDateTimeTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public final class Behaviors
{
	private static final Gson GSON=new GsonBuilder()
			.serializeSpecialFloatingPointValues()
			.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
			.registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdapter("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", ZoneId.of("UTC")))
			.create();

	private static final TypeAdapter<Behavior> BEHAVIOR_ADAPTER=new TypeAdapter<Behavior>() {
		@Override
		public Behavior read(JsonReader in) throws IOException
		{
			if(in.peek()==JsonToken.NULL)
			{
				in.nextNull();
				return null;
			}
			return Behaviors.fromJson(in.nextString());
		}

		@Override
		public void write(JsonWriter out, Behavior value) throws IOException
		{
			if(value==null)
			{
				out.nullValue();
				return;
			}
			out.value(Behaviors.toJson(value));
		}
	};

	public static TypeAdapter<Behavior> getAdapter()
	{
		return BEHAVIOR_ADAPTER;
	}

	public static String toJson(Behavior behavior)
	{
		Class<?> clazz=behavior.getClass();
		try
		{
			Field gsonField=clazz.getDeclaredField("GSON");
			gsonField.setAccessible(true);
			return new StringBuilder().append(((Gson)gsonField.get(null)).toJson(behavior)).append("\t").append(clazz.getName()).toString();
		}
		catch(Exception e)
		{
			try
			{
				return new StringBuilder().append(GSON.toJson(behavior)).append("\t").append(clazz.getName()).toString();
			}
			catch(Exception e2)
			{
				e2.initCause(e);
				throw e2;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Behavior> T fromJson(String line)
	{
		try
		{
			int pos=line.lastIndexOf("\t");
			Class<T> clazz=(Class<T>)Class.forName(line.substring(pos+1));
			try
			{
				Field gsonField=clazz.getDeclaredField("GSON");
				gsonField.setAccessible(true);
				return ((Gson)gsonField.get(null)).fromJson(line.substring(0, pos), clazz);
			}
			catch(Exception e)
			{
				try
				{
					return GSON.fromJson(line.substring(0, pos), clazz);
				}
				catch(Exception e2)
				{
					e2.initCause(e);
					throw e2;
				}
			}
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException(e);
		}
	}

	private Behaviors()
	{}
}
