package com.eroelf.javaxsx.util;

import java.util.List;
import java.util.ListIterator;

/**
 * Methods to deal with number-byte converting.
 * 
 * @author weikun.zhong
 */
public class NumBytes
{
	public static enum Order
	{
		BIG_ENDIAN, LITTLE_ENDIAN
	}

	public static int short2Bytes(short value, byte[] des, int start, Order order)
	{
		switch(order)
		{
		case BIG_ENDIAN:
			des[start]=(byte)(value>>8);
			des[start+1]=(byte)value;
			break;
		case LITTLE_ENDIAN:
			des[start]=(byte)value;
			des[start+1]=(byte)(value>>8);
			break;
		}
		return start+2;
	}

	public static int int2Bytes(int value, byte[] des, int start, Order order)
	{
		switch(order)
		{
		case BIG_ENDIAN:
			des[start]=(byte)(value>>24);
			des[start+1]=(byte)(value>>16);
			des[start+2]=(byte)(value>>8);
			des[start+3]=(byte)value;
			break;
		case LITTLE_ENDIAN:
			des[start]=(byte)value;
			des[start+1]=(byte)(value>>8);
			des[start+2]=(byte)(value>>16);
			des[start+3]=(byte)(value>>24);
			break;
		}
		return start+4;
	}

	public static int long2Bytes(long value, byte[] des, int start, Order order)
	{
		switch(order)
		{
		case BIG_ENDIAN:
			des[start]=(byte)(value>>56);
			des[start+1]=(byte)(value>>48);
			des[start+2]=(byte)(value>>40);
			des[start+3]=(byte)(value>>32);
			des[start+4]=(byte)(value>>24);
			des[start+5]=(byte)(value>>16);
			des[start+6]=(byte)(value>>8);
			des[start+7]=(byte)value;
			break;
		case LITTLE_ENDIAN:
			des[start]=(byte)value;
			des[start+1]=(byte)(value>>8);
			des[start+2]=(byte)(value>>16);
			des[start+3]=(byte)(value>>24);
			des[start+4]=(byte)(value>>32);
			des[start+5]=(byte)(value>>40);
			des[start+6]=(byte)(value>>48);
			des[start+7]=(byte)(value>>56);
			break;
		}
		return start+8;
	}

	public static int float2Bytes(float value, byte[] des, int start, Order order)
	{
		return int2Bytes(Float.floatToIntBits(value), des, start, order);
	}

	public static int double2Bytes(double value, byte[] des, int start, Order order)
	{
		return long2Bytes(Double.doubleToLongBits(value), des, start, order);
	}


	public static int shortArray2Bytes(short[] src, byte[] des, int srcStart, int desStart, int size, Order order)
	{
		for(int i=srcStart; i<srcStart+size; i++)
		{
			desStart=short2Bytes(src[i], des, desStart, order);
		}
		return desStart;
	}

	public static int intArray2Bytes(int[] src, byte[] des, int srcStart, int desStart, int size, Order order)
	{
		for(int i=srcStart; i<srcStart+size; i++)
		{
			desStart=int2Bytes(src[i], des, desStart, order);
		}
		return desStart;
	}

	public static int longArray2Bytes(long[] src, byte[] des, int srcStart, int desStart, int size, Order order)
	{
		for(int i=srcStart; i<srcStart+size; i++)
		{
			desStart=long2Bytes(src[i], des, desStart, order);
		}
		return desStart;
	}

	public static int floatArray2Bytes(float[] src, byte[] des, int srcStart, int desStart, int size, Order order)
	{
		for(int i=srcStart; i<srcStart+size; i++)
		{
			desStart=float2Bytes(src[i], des, desStart, order);
		}
		return desStart;
	}

	public static int doubleArray2Bytes(double[] src, byte[] des, int srcStart, int desStart, int size, Order order)
	{
		for(int i=srcStart; i<srcStart+size; i++)
		{
			desStart=double2Bytes(src[i], des, desStart, order);
		}
		return desStart;
	}

	public static int shortList2Bytes(List<Short> src, byte[] des, int srcStart, int desStart, int size, Order order)
	{
		ListIterator<Short> iter=src.listIterator(srcStart);
		for(int i=0; i<size; i++)
		{
			desStart=short2Bytes(iter.next(), des, desStart, order);
		}
		return desStart;
	}

	public static int intList2Bytes(List<Integer> src, byte[] des, int srcStart, int desStart, int size, Order order)
	{
		ListIterator<Integer> iter=src.listIterator(srcStart);
		for(int i=0; i<size; i++)
		{
			desStart=int2Bytes(iter.next(), des, desStart, order);
		}
		return desStart;
	}

	public static int longList2Bytes(List<Long> src, byte[] des, int srcStart, int desStart, int size, Order order)
	{
		ListIterator<Long> iter=src.listIterator(srcStart);
		for(int i=0; i<size; i++)
		{
			desStart=long2Bytes(iter.next(), des, desStart, order);
		}
		return desStart;
	}

	public static int floatList2Bytes(List<Float> src, byte[] des, int srcStart, int desStart, int size, Order order)
	{
		ListIterator<Float> iter=src.listIterator(srcStart);
		for(int i=0; i<size; i++)
		{
			desStart=float2Bytes(iter.next(), des, desStart, order);
		}
		return desStart;
	}

	public static int doubleList2Bytes(List<Double> src, byte[] des, int srcStart, int desStart, int size, Order order)
	{
		ListIterator<Double> iter=src.listIterator(srcStart);
		for(int i=0; i<size; i++)
		{
			desStart=double2Bytes(iter.next(), des, desStart, order);
		}
		return desStart;
	}

	public static short bytes2Short(byte[] src, int start, Order order)
	{
		short v=0;
		switch(order)
		{
		case BIG_ENDIAN:
			v|=((((short)src[start]) & 0xff)<<8)
			 | (((short)src[start+1]) & 0xff);
			break;
		case LITTLE_ENDIAN:
			v|=(((short)src[start]) & 0xff)
			 | ((((short)src[start+1]) & 0xff)<<8);
			break;
		}
		return v;
	}

	public static int bytes2Int(byte[] src, int start, Order order)
	{
		int v=0;
		switch(order)
		{
		case BIG_ENDIAN:
			v|=((((int)src[start]) & 0xff)<<24)
			 | ((((int)src[start+1]) & 0xff)<<16)
			 | ((((int)src[start+2]) & 0xff)<<8)
			 | (((int)src[start+3]) & 0xff);
			break;
		case LITTLE_ENDIAN:
			v|=(((int)src[start]) & 0xff)
			 | ((((int)src[start+1]) & 0xff)<<8)
			 | ((((int)src[start+2]) & 0xff)<<16)
			 | ((((int)src[start+3]) & 0xff)<<24);
			break;
		}
		return v;
	}

	public static long bytes2Long(byte[] src, int start, Order order)
	{
		long v=0;
		switch(order)
		{
		case BIG_ENDIAN:
			v|=((((long)src[start]) & 0xff)<<56)
			 | ((((long)src[start+1]) & 0xff)<<48)
			 | ((((long)src[start+2]) & 0xff)<<40)
			 | ((((long)src[start+3]) & 0xff)<<32)
			 | ((((long)src[start+4]) & 0xff)<<24)
			 | ((((long)src[start+5]) & 0xff)<<16)
			 | ((((long)src[start+6]) & 0xff)<<8)
			 | (((long)src[start+7]) & 0xff);
			break;
		case LITTLE_ENDIAN:
			v|=(((long)src[start]) & 0xff)
			 | ((((long)src[start+1]) & 0xff)<<8)
			 | ((((long)src[start+2]) & 0xff)<<16)
			 | ((((long)src[start+3]) & 0xff)<<24)
			 | ((((long)src[start+4]) & 0xff)<<32)
			 | ((((long)src[start+5]) & 0xff)<<40)
			 | ((((long)src[start+6]) & 0xff)<<48)
			 | ((((long)src[start+7]) & 0xff)<<56);
			break;
		}
		return v;
	}

	public static float bytes2Float(byte[] src, int start, Order order)
	{
		return Float.intBitsToFloat(bytes2Int(src, start, order));
	}

	public static double bytes2Double(byte[] src, int start, Order order)
	{
		return Double.longBitsToDouble(bytes2Long(src, start, order));
	}

	public static int bytes2ShortArray(byte[] src, short[] des, int srcStart, int desStart, int size, Order order)
	{
		for(int i=desStart; i<desStart+size; i++)
		{
			des[i]=bytes2Short(src, srcStart, order);
			srcStart+=2;
		}
		return srcStart;
	}

	public static int bytes2IntArray(byte[] src, int[] des, int srcStart, int desStart, int size, Order order)
	{
		for(int i=desStart; i<desStart+size; i++)
		{
			des[i]=bytes2Int(src, srcStart, order);
			srcStart+=4;
		}
		return srcStart;
	}

	public static int bytes2LongArray(byte[] src, long[] des, int srcStart, int desStart, int size, Order order)
	{
		for(int i=desStart; i<desStart+size; i++)
		{
			des[i]=bytes2Long(src, srcStart, order);
			srcStart+=8;
		}
		return srcStart;
	}

	public static int bytes2FloatArray(byte[] src, float[] des, int srcStart, int desStart, int size, Order order)
	{
		for(int i=desStart; i<desStart+size; i++)
		{
			des[i]=bytes2Float(src, srcStart, order);
			srcStart+=4;
		}
		return srcStart;
	}

	public static int bytes2DoubleArray(byte[] src, double[] des, int srcStart, int desStart, int size, Order order)
	{
		for(int i=desStart; i<desStart+size; i++)
		{
			des[i]=bytes2Double(src, srcStart, order);
			srcStart+=8;
		}
		return srcStart;
	}

	public static int bytes2ShortList(byte[] src, List<Short> des, int srcStart, int desStart, int size, Order order)
	{
		ListIterator<Short> iter=null;
		if(desStart<des.size())
			iter=des.listIterator(desStart);
		for(int i=desStart; i<desStart+size; i++)
		{
			if(i<des.size())
			{
				iter.next();
				iter.set(bytes2Short(src, srcStart, order));
			}
			else
				des.add(bytes2Short(src, srcStart, order));
			srcStart+=2;
		}
		return srcStart;
	}

	public static int bytes2IntList(byte[] src, List<Integer> des, int srcStart, int desStart, int size, Order order)
	{
		ListIterator<Integer> iter=null;
		if(desStart<des.size())
			iter=des.listIterator(desStart);
		for(int i=desStart; i<desStart+size; i++)
		{
			if(i<des.size())
			{
				iter.next();
				iter.set(bytes2Int(src, srcStart, order));
			}
			else
				des.add(bytes2Int(src, srcStart, order));
			srcStart+=4;
		}
		return srcStart;
	}

	public static int bytes2LongList(byte[] src, List<Long> des, int srcStart, int desStart, int size, Order order)
	{
		ListIterator<Long> iter=null;
		if(desStart<des.size())
			iter=des.listIterator(desStart);
		for(int i=desStart; i<desStart+size; i++)
		{
			if(i<des.size())
			{
				iter.next();
				iter.set(bytes2Long(src, srcStart, order));
			}
			else
				des.add(bytes2Long(src, srcStart, order));
			srcStart+=8;
		}
		return srcStart;
	}

	public static int bytes2FloatList(byte[] src, List<Float> des, int srcStart, int desStart, int size, Order order)
	{
		ListIterator<Float> iter=null;
		if(desStart<des.size())
			iter=des.listIterator(desStart);
		for(int i=desStart; i<desStart+size; i++)
		{
			if(i<des.size())
			{
				iter.next();
				iter.set(bytes2Float(src, srcStart, order));
			}
			else
				des.add(bytes2Float(src, srcStart, order));
			srcStart+=4;
		}
		return srcStart;
	}

	public static int bytes2DoubleList(byte[] src, List<Double> des, int srcStart, int desStart, int size, Order order)
	{
		ListIterator<Double> iter=null;
		if(desStart<des.size())
			iter=des.listIterator(desStart);
		for(int i=desStart; i<desStart+size; i++)
		{
			if(i<des.size())
			{
				iter.next();
				iter.set(bytes2Double(src, srcStart, order));
			}
			else
				des.add(bytes2Double(src, srcStart, order));
			srcStart+=8;
		}
		return srcStart;
	}
}
