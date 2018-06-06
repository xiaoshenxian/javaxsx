package com.eroelf.javaxsx.util.gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * This type adapter factory helps to deserialize json arrays contains elements that may or may not belongs to a certain enum type to an enum set whose element type is that specified enum without throw an exception.
 * Any element that does not belongs to the specified enum type in the original json array will be discarded during the deserializion.
 * 
 * @author weikun.zhong
 */
public class SuppressedEnumSetTypeAdapterFactory implements TypeAdapterFactory
{
	private Set<Type> acceptTypes=new HashSet<>();

	/**
	 * The constructor.
	 * If no parameter, this factory will process any set field whose element type is an enum, otherwise only those sets with element types exist in {@code classes} will be processed.
	 * 
	 * @param classes enum classes to be suppressed. Must represents enum types.
	 */
	public SuppressedEnumSetTypeAdapterFactory(Class<?>... classes)
	{
		for(Class<?> cls : classes)
		{
			if(cls.isEnum())
				acceptTypes.add(cls);
			else
				throw new IllegalArgumentException("Set element must be Enum instance! Recieved "+cls.getName());
		}
	}

	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type)
	{
		Class<? super T> rawType=type.getRawType();
		if(!rawType.isAssignableFrom(EnumSet.class))
			return null;
		Type elementType=$Gson$Types.getCollectionElementType(type.getType(), rawType);
		TypeToken<?> elementTypeToken=TypeToken.get(elementType);
		if((acceptTypes.isEmpty() && elementTypeToken.getRawType().isEnum()) || (acceptTypes.contains(elementType)))
		{
			TypeAdapter<?> elementTypeAdapter=gson.getAdapter(elementTypeToken);
			@SuppressWarnings({"unchecked", "rawtypes"})
			TypeAdapter<T> result=new Adapter(elementTypeToken, elementTypeAdapter);
			return result;
		}
		else
			return null;
	}

	private static final class Adapter<E extends Enum<E>> extends TypeAdapter<EnumSet<E>>
	{
		private final Class<E> elementClass;
		private final TypeAdapter<E> elementTypeAdapter;

		@SuppressWarnings("unchecked")
		public Adapter(TypeToken<E> elementTypeToken, TypeAdapter<E> elementTypeAdapter)
		{
			this.elementClass=(Class<E>)elementTypeToken.getRawType();
			this.elementTypeAdapter=elementTypeAdapter;
		}

		public EnumSet<E> read(JsonReader in) throws IOException
		{
			if(in.peek()==JsonToken.NULL)
			{
				in.nextNull();
				return null;
			}

			EnumSet<E> set=EnumSet.noneOf(elementClass);
			in.beginArray();
			while(in.hasNext())
			{
				E instance=elementTypeAdapter.read(in);
				if(instance!=null)
					set.add(instance);
			}
			in.endArray();
			return set;
		}

		@Override
		public void write(JsonWriter out, EnumSet<E> set) throws IOException
		{
			if(set==null)
				out.nullValue();
			else
			{
				out.beginArray();
				for(E element : set)
				{
					elementTypeAdapter.write(out, element);
				}
				out.endArray();
			}
		}
	}
}
