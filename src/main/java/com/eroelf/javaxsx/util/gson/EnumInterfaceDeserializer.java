package com.eroelf.javaxsx.util.gson;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * This class helps to deserialize names of instances of classes that extended {@link Enum} and implemented a specified interface T to the corresponding {@link Enum} objects.
 * Note that it is the user's responsibility to guarantee that names of different {@link Enum} instances of the interface are different, or there will be some unexpected conflicts and no exception will be thrown.
 * 
 * @author weikun.zhong
 *
 * @param <T> the type of the interface.
 */
public class EnumInterfaceDeserializer<T> implements JsonDeserializer<T>
{
	private Class<T> baseClass;
	private Map<String, T> nameToEnumMap=new HashMap<>();
	private Function<String, String> strTrans;

	public EnumInterfaceDeserializer(Class<T> baseClass)
	{
		this(baseClass, true);
	}

	public EnumInterfaceDeserializer(Class<T> baseClass, boolean caseSensitive)
	{
		if(!baseClass.isInterface())
			throw new IllegalArgumentException("An interface for Enums is expected!");
		this.baseClass=baseClass;

		if(caseSensitive)
			strTrans=Function.identity();
		else
			strTrans=str -> str.toUpperCase(Locale.ENGLISH);
	}

	public EnumInterfaceDeserializer<T> registerSubEnum(Class<? extends T> subClass)
	{
		if(!subClass.isEnum() || !baseClass.isAssignableFrom(subClass))
			throw new IllegalArgumentException("Only classes that extended the Enum<?> and implemented "+baseClass.getSimpleName()+" are acceptable.");
		for(T oneEnum : subClass.getEnumConstants())
		{
			nameToEnumMap.put(strTrans.apply(oneEnum.toString()), oneEnum);
		}
		return this;
	}

	@Override
	public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		if(!json.isJsonNull())
			return nameToEnumMap.get(strTrans.apply(json.getAsString()));
		else
			return null;
	}
}
