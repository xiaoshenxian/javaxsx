package com.eroelf.javaxsx.util.gson;

import java.lang.reflect.Type;
import java.util.EnumMap;

import com.google.gson.InstanceCreator;

/**
 * Instance creator to help to create an EnumMap for the given {@code enum} key and an arbitrary specified value.
 * 
 * <p>
 * 
 * Usage:
 * <pre>
 * new GsonBuilder().registerTypeAdapter(new TypeToken&lt;Map&lt;K, V&gt;&gt;(){}.getType(), new EnumMapInstanceCreator&lt;K, V&gt;(K.class));
 * </pre>
 * 
 * @author weikun.zhong
 *
 * @param <K> the {@code enum} type of keys.
 * @param <V> the type of values.
 */
public class EnumMapInstanceCreator<K extends Enum<K>, V> implements InstanceCreator<EnumMap<K, V>>
{
	private final Class<K> enumClazz;

	public EnumMapInstanceCreator(Class<K> enumClazz)
	{
		this.enumClazz=enumClazz;
	}

	@Override
	public EnumMap<K, V> createInstance(Type type)
	{
		return new EnumMap<K, V>(enumClazz);
	}
}
