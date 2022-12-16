package com.eroelf.javaxsx.util.reflect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ClassUtils;

/**
 * For wrapping a certain class to a Duck Type.
 * This is useful when a bunch of objects have no common interface but have identical methods.
 * Primitives and their wrapper classes are regarded as no different in method signatures.
 * 
 * @author weikun.zhong
 */
public class ClassWrapper
{
	private static class Signature
	{
		private String name;
		private Class<?>[] parameterTypes;

		private int hash;

		@Override
		public boolean equals(Object obj)
		{
			return obj instanceof Signature && Objects.equals(name, ((Signature)obj).name) && Arrays.equals(parameterTypes, ((Signature)obj).parameterTypes);
		}

		@Override
		public int hashCode()
		{
			int h=hash;
			if(h==0 && (name!=null || parameterTypes!=null))
			{
				h=31*Objects.hashCode(name)+Arrays.hashCode(parameterTypes);
				hash=h;
			}
			return h;
		}
	}

	protected Object obj;
	protected Class<?> cls;
	protected Map<Signature, Method> methodMap=new HashMap<>();

	public ClassWrapper(Object obj)
	{
		this.obj=obj;
		this.cls=this.obj.getClass();
	}

	@SuppressWarnings("unchecked")
	public <T> T invoke(String name, Object... args)
	{
		try
		{
			Class<?>[] parameterTypes=Arrays.asList(args).stream().map(arg -> arg.getClass()).collect(Collectors.toList()).toArray(new Class<?>[args.length]);
			Signature sig=getSignature(name, parameterTypes);
			Method method=methodMap.get(sig);
			if(method==null)
				method=register(sig, name, parameterTypes);
			return (T)method.invoke(obj, args);
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getWrapped()
	{
		return (T)obj;
	}

	public Method register(String name, Class<?>... parameterTypes) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, NoSuchFieldException
	{
		return register(getSignature(name, parameterTypes), name, parameterTypes);
	}

	private Method register(Signature sig, String name, Class<?>... parameterTypes) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, NoSuchFieldException
	{
		Method method=null;
		try
		{
			method=cls.getMethod(name, parameterTypes);
		}
		catch(Exception e)
		{
			boolean hasPrimitive=false;
			for(int i=0; i<parameterTypes.length; i++)
			{
				if(parameterTypes[i]==void.class || parameterTypes[i].isPrimitive())
					hasPrimitive=true;
				else if(parameterTypes[i]==Void.class)
					parameterTypes[i]=void.class;
				else if(ClassUtils.isPrimitiveWrapper(parameterTypes[i]))
					parameterTypes[i]=ClassUtils.wrapperToPrimitive(parameterTypes[i]);
			}
			try
			{
				method=cls.getMethod(name, parameterTypes);
			}
			catch(Exception e1)
			{
				e1.initCause(e);
				if(hasPrimitive)
				{
					try
					{
						method=cls.getMethod(name, sig.parameterTypes);
					}
					catch(Exception e2)
					{
						e2.initCause(e1);
						throw e2;
					}
				}
				else
					throw e1;
			}
		}
		methodMap.put(sig, method);
		return method;
	}

	private Signature getSignature(String name, Class<?>... parameterTypes) throws ClassNotFoundException
	{
		Signature sig=new Signature();
		sig.name=name;
		sig.parameterTypes=new Class[parameterTypes.length];
		for(int i=0; i<parameterTypes.length; i++)
		{
			if(parameterTypes[i]==void.class)
				sig.parameterTypes[i]=Void.class;
			else if(parameterTypes[i].isPrimitive())
				sig.parameterTypes[i]=ClassUtils.primitiveToWrapper(parameterTypes[i]);
			else
				sig.parameterTypes[i]=parameterTypes[i];
		}
		return sig;
	}
}
