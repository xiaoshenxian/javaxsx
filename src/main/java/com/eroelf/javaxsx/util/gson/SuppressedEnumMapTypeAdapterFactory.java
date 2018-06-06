package com.eroelf.javaxsx.util.gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * This type adapter factory helps to deserialize json objects contains element names that may or may not belongs to a certain enum type to an enum map whose key type is that specified enum without throw an exception.
 * Any element whose name does not belongs to the specified enum type in the original json object will be discarded during the deserializion.
 * 
 * @author weikun.zhong
 */
public class SuppressedEnumMapTypeAdapterFactory implements TypeAdapterFactory
{
	private Set<Type> acceptTypes=new HashSet<>();

	/**
	 * The constructor.
	 * If no parameter, this factory will process any map field whose key type is an enum, otherwise only those maps with key types exist in {@code classes} will be processed.
	 * 
	 * @param classes enum classes to be suppressed. Must represents enum types.
	 */
	public SuppressedEnumMapTypeAdapterFactory(Class<?>... classes)
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
		if(!rawType.isAssignableFrom(EnumMap.class))
			return null;
		Type theType=type.getType();
		Class<?> rawTypeOfSrc=$Gson$Types.getRawType(theType);
		Type[] keyAndValueTypes=$Gson$Types.getMapKeyAndValueTypes(theType, rawTypeOfSrc);
		TypeToken<?> keyTypeToken=TypeToken.get(keyAndValueTypes[0]);
		if((acceptTypes.isEmpty() && keyTypeToken.getRawType().isEnum()) || (acceptTypes.contains(keyAndValueTypes[0])))
		{
			TypeAdapter<?> keyAdapter=gson.getAdapter(TypeToken.get(keyAndValueTypes[0]));
			TypeAdapter<?> valueAdapter=gson.getAdapter(TypeToken.get(keyAndValueTypes[1]));
			@SuppressWarnings({"unchecked", "rawtypes"})
			TypeAdapter<T> result=new Adapter(keyTypeToken, keyAdapter, valueAdapter);
			return result;
		}
		else
			return null;
	}

	private static final class Adapter<K extends Enum<K>, V> extends TypeAdapter<EnumMap<K, V>> 
	{
		private final Class<K> keyClass;
		private final TypeAdapter<K> keyTypeAdapter;
		private final TypeAdapter<V> valueTypeAdapter;

		@SuppressWarnings("unchecked")
		public Adapter(TypeToken<K> keyTypeToken, TypeAdapter<K> keyTypeAdapter, TypeAdapter<V> valueTypeAdapter)
		{
			this.keyClass=(Class<K>)keyTypeToken.getRawType();
			this.keyTypeAdapter=keyTypeAdapter;
			this.valueTypeAdapter=valueTypeAdapter;
		}

		public EnumMap<K, V> read(JsonReader in) throws IOException
		{
			JsonToken peek=in.peek();
			if(peek==JsonToken.NULL)
			{
				in.nextNull();
				return null;
			}

			EnumMap<K, V> map=new EnumMap<>(keyClass);

			if(peek==JsonToken.BEGIN_ARRAY)
			{
				in.beginArray();
				while(in.hasNext())
				{
					in.beginArray(); // entry array
					K key=keyTypeAdapter.read(in);
					V value=valueTypeAdapter.read(in);
					if(key!=null)
					{
						V replaced=map.put(key, value);
						if(replaced!=null)
						{
							throw new JsonSyntaxException("duplicate key: "+key);
						}
					}
					in.endArray();
				}
				in.endArray();
			}
			else
			{
				in.beginObject();
				while(in.hasNext())
				{
					JsonReaderInternalAccess.INSTANCE.promoteNameToValue(in);
					K key=keyTypeAdapter.read(in);
					V value=valueTypeAdapter.read(in);
					if(key!=null)
					{
						V replaced=map.put(key, value);
						if(replaced!=null)
						{
							throw new JsonSyntaxException("duplicate key: "+key);
						}
					}
				}
				in.endObject();
			}
			return map;
		}

		@Override
		public void write(JsonWriter out, EnumMap<K, V> map) throws IOException
		{
			if(map==null)
				out.nullValue();
			else
			{
				out.beginObject();
				for(Map.Entry<K, V> entry : map.entrySet())
				{
					out.name(entry.getKey().name());
					valueTypeAdapter.write(out, entry.getValue());
				}
				out.endObject();
			}
		}
	}
}
