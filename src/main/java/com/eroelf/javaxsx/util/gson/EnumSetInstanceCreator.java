package com.eroelf.javaxsx.util.gson;

import java.lang.reflect.Type;
import java.util.EnumSet;

import com.google.gson.InstanceCreator;

/**
 * Instance creator to help to create an EnumSet for the given {@code enum} type.
 * 
 * <p>
 * 
 * Usage:
 * <pre>
 * new GsonBuilder().registerTypeAdapter(new TypeToken&lt;Set&lt;E&gt;&gt;(){}.getType(), new EnumSetInstanceCreator&lt;E&gt;(E.class));
 * </pre>
 * 
 * @author weikun.zhong
 *
 * @param <E> the {@code enum} type of elements.
 */
public class EnumSetInstanceCreator<E extends Enum<E>> implements InstanceCreator<EnumSet<E>>
{
	private final Class<E> enumClazz;

	public EnumSetInstanceCreator(final Class<E> enumClazz)
	{
		this.enumClazz=enumClazz;
	}

	@Override
	public EnumSet<E> createInstance(Type type)
	{
		return EnumSet.noneOf(enumClazz);
	}
}
