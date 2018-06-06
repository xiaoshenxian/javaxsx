package com.eroelf.javaxsx.util;

import java.util.List;

/**
 * Methods to deal with number-byte converting.
 * 
 * @author weikun.zhong
 */
public class NumBytes
{
	public static int short2Bytes(short value, byte[] des, int start)//big-endian
	{
		des[start]=(byte)(value>>8);
		des[start+1]=(byte)value;
		return start+2;
	}

	public static int int2Bytes(int value, byte[] des, int start)//big-endian
	{
		des[start]=(byte)(value>>24);
		des[start+1]=(byte)(value>>16);
		des[start+2]=(byte)(value>>8);
		des[start+3]=(byte)value;
		return start+4;
	}

	public static int long2Bytes(long value, byte[] des, int start)//big-endian
	{
		des[start]=(byte)(value>>56);
		des[start+1]=(byte)(value>>48);
		des[start+2]=(byte)(value>>40);
		des[start+3]=(byte)(value>>32);
		des[start+4]=(byte)(value>>24);
		des[start+5]=(byte)(value>>16);
		des[start+6]=(byte)(value>>8);
		des[start+7]=(byte)value;
		return start+8;
	}

	public static int float2Bytes(float value, byte[] des, int start)//big-endian
	{
		return int2Bytes(Float.floatToIntBits(value), des, start);
	}

	public static int double2Bytes(double value, byte[] des, int start)//big-endian
	{
		return long2Bytes(Double.doubleToLongBits(value), des, start);
	}

	public static int shortList2Bytes(List<Short> list, byte[] des, int start)
	{
		for(short value : list)
		{
			start=short2Bytes(value, des, start);
		}
		return start;
	}

	public static int intList2Bytes(List<Integer> list, byte[] des, int start)
	{
		for(int value : list)
		{
			start=int2Bytes(value, des, start);
		}
		return start;
	}

	public static int longList2Bytes(List<Long> list, byte[] des, int start)
	{
		for(Long value : list)
		{
			start=long2Bytes(value, des, start);
		}
		return start;
	}

	public static int floatList2Bytes(List<Float> list, byte[] des, int start)
	{
		for(float value : list)
		{
			start=float2Bytes(value, des, start);
		}
		return start;
	}

	public static int doubleList2Bytes(List<Double> list, byte[] des, int start)
	{
		for(double value : list)
		{
			start=double2Bytes(value, des, start);
		}
		return start;
	}

	public static short bytes2Short(byte[] src, int start)//big-endian
	{
		short v=0;
		v|=((((short)src[start]) & 0xff)<<8)
		 | (((short)src[start+1]) & 0xff);
		return v;
	}

	public static int bytes2Int(byte[] src, int start)//big-endian
	{
		int v=0;
		v|=((((int)src[start]) & 0xff)<<24)
		 | ((((int)src[start+1]) & 0xff)<<16)
		 | ((((int)src[start+2]) & 0xff)<<8)
		 | (((int)src[start+3]) & 0xff);
		return v;
	}

	public static long bytes2Long(byte[] src, int start)//big-endian
	{
		long v=0;
		v|=((((long)src[start]) & 0xff)<<56)
		 | ((((long)src[start+1]) & 0xff)<<48)
		 | ((((long)src[start+2]) & 0xff)<<40)
		 | ((((long)src[start+3]) & 0xff)<<32)
		 | ((((long)src[start+4]) & 0xff)<<24)
		 | ((((long)src[start+5]) & 0xff)<<16)
		 | ((((long)src[start+6]) & 0xff)<<8)
		 | (((long)src[start+7]) & 0xff);
		return v;
	}

	public static float bytes2Float(byte[] src, int start)//big-endian
	{
		return Float.intBitsToFloat(bytes2Int(src, start));
	}

	public static double bytes2Double(byte[] src, int start)//big-endian
	{
		return Double.longBitsToDouble(bytes2Long(src, start));
	}

	public static int bytes2ShortList(byte[] src, int start, List<Short> list, int size)
	{
		size=size>0 ? Math.min(start+(src.length-start)/4*4, start+size*4) : start+(src.length-start)/4*4;
		while(start<size)
		{
			list.add(bytes2Short(src, start));
			start+=2;
		}
		return start;
	}

	public static int bytes2IntList(byte[] src, int start, List<Integer> list, int size)
	{
		size=size>0 ? Math.min(start+(src.length-start)/4*4, start+size*4) : start+(src.length-start)/4*4;
		while(start<size)
		{
			list.add(bytes2Int(src, start));
			start+=4;
		}
		return start;
	}

	public static int bytes2LongList(byte[] src, int start, List<Long> list, int size)
	{
		size=size>0 ? Math.min(start+(src.length-start)/8*8, start+size*8) : start+(src.length-start)/8*8;
		while(start<size)
		{
			list.add(bytes2Long(src, start));
			start+=8;
		}
		return start;
	}

	public static int bytes2FloatList(byte[] src, int start, List<Float> list, int size)
	{
		size=size>0 ? Math.min(start+(src.length-start)/4*4, start+size*4) : start+(src.length-start)/4*4;
		while(start<size)
		{
			list.add(bytes2Float(src, start));
			start+=4;
		}
		return start;
	}

	public static int bytes2DoubleList(byte[] src, int start, List<Double> list, int size)
	{
		size=size>0 ? Math.min(start+(src.length-start)/8*8, start+size*8) : start+(src.length-start)/8*8;
		while(start<size)
		{
			list.add(bytes2Double(src, start));
			start+=8;
		}
		return start;
	}
}
