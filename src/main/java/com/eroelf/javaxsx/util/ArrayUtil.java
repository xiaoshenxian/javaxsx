package com.eroelf.javaxsx.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.BiFunction;

/**
 * Some APIs for arrays.
 * 
 * @author weikun.zhong
 */
public final class ArrayUtil
{
	/**
	 * Create a new array with the given shape and element type.
	 * 
	 * @param <T> the type of the returned array.
	 * 
	 * @param shape the shape of the array.
	 * @param cls the element type of the array.
	 * @return a new array with the given shape and element type.
	 * 
	 * @see Array#newInstance
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newArray(int[] shape, Class<?> cls)
	{
		List<Object> stack=new ArrayList<>();
		int[] facets=getFacets(shape);
		int dim=shape.length;
		int idx=0;
		Object currArray=Array.newInstance(cls, shape[dim-1]);
		stack.add(currArray);
		while(idx<facets[0])
		{
			idx+=shape[shape.length-1];
			for(int i=facets.length-2; i>=0; i--)
			{
				if(idx%facets[i]==0)
				{
					currArray=Array.newInstance(currArray.getClass(), shape[i]);
					for(int j=shape[i]-1; j>=0; j--)
					{
						Array.set(currArray, j, stack.remove(stack.size()-1));
					}
					stack.add(currArray);
				}
			}
			if(idx<facets[0])
			{
				currArray=Array.newInstance(cls, shape[dim-1]);
				stack.add(currArray);
			}
		}
		return (T)stack.get(0);
	}

	/**
	 * Create a new array and fill it with the elements from the given {@link Iterable} instance.
	 * 
	 * @param <T> the type of the array elements.
	 * @param <U> the type of the returned array.
	 * 
	 * @param iterable the {@link Iterable} instance provides the array elements.
	 * @param shape the shape of the array.
	 * @param cls the element type of the array.
	 * @param defaultValue the default value of the array. Will be use when the {@code iterable} failed to provide enough elements.
	 * @return a new array with the given shape and element type, and filled with the elements from the {@code iterable}.
	 * 
	 * @see #toArray(Iterator, int[], Class, Object)
	 */
	public static <T, U> U toArray(Iterable<T> iterable, int[] shape, Class<T> cls, Object defaultValue)
	{
		return toArray(iterable.iterator(), shape, cls, defaultValue);
	}

	/**
	 * Create a new array and fill it with the elements from the given {@link Iterator} instance.
	 * 
	 * @param <T> the type of the array elements.
	 * @param <U> the type of the returned array.
	 * 
	 * @param iterator the {@link Iterator} instance provides the array elements.
	 * @param shape the shape of the array.
	 * @param cls the element type of the array.
	 * @param defaultValue the default value of the array. Will be use when the {@code iterable} failed to provide enough elements.
	 * @return a new array with the given shape and element type, and filled with the elements from the {@code iterator}.
	 * 
	 * @see #toArray(Iterable, int[], Class, Object)
	 */
	@SuppressWarnings("unchecked")
	public static <T, U> U toArray(Iterator<T> iterator, int[] shape, Class<T> cls, Object defaultValue)
	{
		List<Object> stack=new ArrayList<>();
		int[] facets=getFacets(shape);
		int dim=shape.length;
		int idx=0;
		Object currArray=Array.newInstance(cls, shape[dim-1]);
		stack.add(currArray);
		while(idx<facets[0])
		{
			Array.set(currArray, (idx++)%shape[dim-1], iterator.hasNext() ? iterator.next() : defaultValue);
			if(idx%shape[dim-1]==0)
			{
				for(int i=facets.length-2; i>=0; i--)
				{
					if(idx%facets[i]==0)
					{
						currArray=Array.newInstance(currArray.getClass(), shape[i]);
						for(int j=shape[i]-1; j>=0; j--)
						{
							Array.set(currArray, j, stack.remove(stack.size()-1));
						}
						stack.add(currArray);
					}
				}
				if(idx<facets[0])
				{
					currArray=Array.newInstance(cls, shape[dim-1]);
					stack.add(currArray);
				}
			}
		}
		return (U)stack.get(0);
	}

	/**
	 * Reshape an array to the given shape. A new array with its shape specified by the {@code toShape} will be created while the input {@code array} will remain unchanged.
	 * 
	 * @param <T> the type of the array elements.
	 * @param <U> the type of the returned array.
	 * 
	 * @param array the array to be reshaped.
	 * @param fromShape the original shape of the input {@code array}.
	 * @param toShape the shape of the returned array.
	 * @param cls the element type of the array.
	 * @param defaultValue the default value of the array. Will be use when the input {@code array} failed to provide enough elements.
	 * @return a new array with the given shape and element type, and filled with the elements from the input {@code array}.
	 */
	public static <T, U> U reshape(Object array, int[] fromShape, int[] toShape, Class<T> cls, Object defaultValue)
	{
		return toArray(arrayIterator(array, fromShape, 0), toShape, cls, defaultValue);
	}

	/**
	 * Get the shape of the input array.
	 * The element of the array will be regarded as the shallow-most-non-array object, or empty.
	 * If the {@code obj} does not represent an array class this method returns {@code int[0]}.
	 * 
	 * @param obj the input array.
	 * @return the shape of the input array.
	 */
	public static int[] getShape(Object obj)
	{
		List<Integer> li=new ArrayList<>();
		while(obj.getClass().isArray())
		{
			int len=Array.getLength(obj);
			li.add(len);
			if(len>0)
				obj=Array.get(obj, 0);
			else
				break;
		}
		int[] shape=new int[li.size()];
		int i=0;
		for(int x : li)
		{
			shape[i++]=x;
		}
		return shape;
	}

	/**
	 * Calculate the element size of each dimensions and its higher dimensions of the given {@code shape}, called facet here.
	 * In other word, the product of the shape components which indices are greater or equal to the current dimension index.
	 * 
	 * @param shape the input shape.
	 * @return the facets result.
	 */
	public static int[] getFacets(int[] shape)
	{
		int[] facets=new int[shape.length];
		int temp=1;
		for(int i=facets.length-1; i>=0; i--)
		{
			facets[i]=temp*shape[i];
			temp=facets[i];
		}
		return facets;
	}

	/**
	 * Get the component type of the input array.
	 * The element of the array will be regarded as the shallow-most-non-array object, or empty.
	 * If the {@code obj} does not represent an array class this method returns {@code null}.
	 * 
	 * @param obj the input array.
	 * @return the component type of the input array.
	 */
	public static Class<?> getComponentType(Object obj)
	{
		Object last=null;
		while(obj.getClass().isArray())
		{
			last=obj;
			if(Array.getLength(obj)>0)
				obj=Array.get(obj, 0);
			else
				break;
		}
		return last!=null ? last.getClass().getComponentType() : null;
	}

	/**
	 * Provide an {@link Iterator} to traverse throw all the elements of the given {@code array}.
	 * 
	 * @param <T> the type of the array elements.
	 * 
	 * @param array the input array whose elements will be passed to the returned {@link Iterator} instance.
	 * @param shape the shape of the input array.
	 * @param idx index of the first element to be returned from the iterator, with the same definition of the {@code index} defined in {@link List#listIterator(int)}.
	 * @return a {@link ListIterator} instance over the elements in the input array, starting at the specified position in the array.
	 */
	public static <T> ListIterator<T> arrayIterator(final Object array, final int[] shape, final int idx)
	{
		int[] facets=getFacets(shape);
		if(idx<0 || idx>facets[0])
			throw new IllegalArgumentException("The idx must be greater equal to 0 and less than the array size!");
		return new ListIterator<T>() {
			private int index=idx;
			private int[] indices=new int[shape.length];
			private int flag=0;

			private int[] calIndices(int idx)
			{
				int i;
				for(i=0; i<indices.length-1; i++)
				{
					indices[i]=idx/facets[i+1];
					idx-=indices[i]*facets[i+1];
				}
				indices[i]=idx;
				return indices;
			}

			@Override
			public boolean hasNext()
			{
				return index<facets[0];
			}

			@SuppressWarnings("unchecked")
			@Override
			public T next()
			{
				Object obj=array;
				for(int i : calIndices(index))
				{
					obj=Array.get(obj, i);
				}
				++index;
				flag=1;
				return (T)obj;
			}

			@Override
			public boolean hasPrevious()
			{
				return index>0;
			}

			@SuppressWarnings("unchecked")
			@Override
			public T previous()
			{
				Object obj=array;
				--index;
				for(int i : calIndices(index))
				{
					obj=Array.get(obj, i);
				}
				flag=-1;
				return (T)obj;
			}

			@Override
			public int nextIndex()
			{
				return index;
			}

			@Override
			public int previousIndex()
			{
				return index-1;
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException("This method is not allowed!");
			}

			@Override
			public void set(T e)
			{
				Object obj=array;
				int i;
				switch(flag)
				{
				case 1:
					calIndices(index-1);
					for(i=0; i<indices.length-1; i++)
					{
						obj=Array.get(obj, indices[i]);
					}
					Array.set(obj, indices[i], e);
					break;
				case -1:
					calIndices(index);
					for(i=0; i<indices.length-1; i++)
					{
						obj=Array.get(obj, indices[i]);
					}
					Array.set(obj, indices[i], e);
					break;
				default:
					throw new IllegalStateException("This method must be called after at lease one calling of next() or previous()");
				}
			}

			@Override
			public void add(T e)
			{
				throw new UnsupportedOperationException("This method is not allowed!");
			}
		};
	}

	/**
	 * Calculate the Euler distance of the two input array.
	 * 
	 * @param x one input array.
	 * @param y the other input array.
	 * @return the Euler distance.
	 */
	public static double eulerDistance(Object x, Object y)
	{
		if(x.getClass().isArray() && y.getClass().isArray())
		{
			double res=0;
			int len=Math.min(Array.getLength(x), Array.getLength(y));
			for(int i=0; i<len; i++)
			{
				res+=Math.pow(((Number)Array.get(x, i)).doubleValue()-((Number)Array.get(y, i)).doubleValue(), 2);
			}
			return Math.sqrt(res);
		}
		else
			throw new IllegalArgumentException(String.format("Both argument must be arrays! But recieved %s and %s.", x.getClass().getSimpleName(), y.getClass().getSimpleName()));
	}

	/**
	 * Calculate the cosine distance of the two input array.
	 * 
	 * @param x one input array.
	 * @param y the other input array.
	 * @return the cosine distance.
	 */
	public static double cosineDistance(Object x, Object y)
	{
		if(x.getClass().isArray() && y.getClass().isArray())
		{
			double res=0;
			double mx=0;
			double my=0;
			int len=Math.min(Array.getLength(x), Array.getLength(y));
			for(int i=0; i<len; i++)
			{
				double xi=((Number)Array.get(x, i)).doubleValue();
				double yi=((Number)Array.get(y, i)).doubleValue();
				res+=xi*yi;
				mx+=xi*xi;
				my+=yi*yi;
			}
			return res/Math.sqrt(mx*my);
		}
		else
			throw new IllegalArgumentException(String.format("Both argument must be arrays! But recieved %s and %s.", x.getClass().getSimpleName(), y.getClass().getSimpleName()));
	}

	/**
	 * Calculate the pairwise mutual distance of the input {@code vectors}, using the given distance calculation function.
	 * 
	 * @param <T> the type of the array elements.
	 * 
	 * @param vectors the input vectors.
	 * @param distFunc the distance calculation function.
	 * @return the pairwise distances in the numerical ascending order of the input indices pair. Any pair (i, j) with i&#60;j are omitted since the symmetry, in other word, a one dimensional double array stores the elements of the left upper triangular mutual distance matrix will be returned.
	 * 
	 * @see #formatMutualDistances(double[])
	 */
	@SuppressWarnings("unchecked")
	public static <T> double[] mutualDistances(Object[] vectors, BiFunction<T, T, Double> distFunc)
	{
		double[] res=new double[(1+vectors.length)*vectors.length/2];
		for(int i=0, j=0; j<vectors.length; j++)
		{
			Object v1=vectors[j];
			for(int k=j; k<vectors.length; k++)
			{
				res[i++]=distFunc.apply((T)v1, (T)vectors[k]);
			}
		}
		return res;
	}

	/**
	 * Format the input distance array which stores the elements of a left upper triangular mutual distance matrix to a {@link String} represents the two dimensional symmetric matrix.
	 * 
	 * @param distances the input distance array.
	 * @return the formatted {@link String} object.
	 * 
	 * @see #mutualDistances(Object[], BiFunction)
	 */
	public static String formatMutualDistances(double[] distances)
	{
		double sideLength=(-1+Math.sqrt(1+8*distances.length))/2;
		int length=(int)sideLength;
		if(sideLength==length)
		{
			StringBuilder stringBuilder=new StringBuilder();
			for(int i=0; i<length; i++)
			{
				for(int j=0; j<length; j++)
				{
					int ii=Math.min(i, j);
					int jj=Math.max(i, j);
					stringBuilder.append(String.format("%.4f", distances[(length+length-ii+1)*ii/2+jj-ii])).append(" ");
				}
				stringBuilder.deleteCharAt(stringBuilder.length()-1).append("\n");
			}
			return stringBuilder.deleteCharAt(stringBuilder.length()-1).toString();
		}
		else
			throw new IllegalArgumentException(String.format("Illegal array length: %d!", distances.length));
	}
}
