package com.eroelf.javaxsx.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang3.tuple.Pair;

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

	public static short reverse(short num)
	{
		return (short)(((num & 0xff)<<8) | ((num>>>8) & 0xff));
	}

	public static int reverse(int num)
	{
		return ((num & 0xff)<<24)
			 | (((num>>>8) & 0xff)<<16)
			 | (((num>>>16) & 0xff)<<8)
			 | ((num>>>24) & 0xff);
	}

	public static long reverse(long num)
	{
		return ((num & 0xffL)<<56)
			 | (((num>>>8) & 0xffL)<<48)
			 | (((num>>>16) & 0xffL)<<40)
			 | (((num>>>24) & 0xffL)<<32)
			 | (((num>>>32) & 0xffL)<<24)
			 | (((num>>>40) & 0xffL)<<16)
			 | (((num>>>48) & 0xffL)<<8)
			 | ((num>>>56) & 0xffL);
	}

	public static float reverse(float num)
	{
		return Float.intBitsToFloat(reverse(Float.floatToIntBits(num)));
	}

	public static double reverse(double num)
	{
		return Double.longBitsToDouble(reverse(Double.doubleToLongBits(num)));
	}

	public static int shortToBytes(short value, byte[] des, int start, Order order)
	{
		switch(order)
		{
		case BIG_ENDIAN:
			des[start]=(byte)(value>>>8);
			des[start+1]=(byte)value;
			break;
		case LITTLE_ENDIAN:
			des[start]=(byte)value;
			des[start+1]=(byte)(value>>>8);
			break;
		}
		return start+2;
	}

	public static int intToBytes(int value, byte[] des, int start, Order order)
	{
		switch(order)
		{
		case BIG_ENDIAN:
			des[start]=(byte)(value>>>24);
			des[start+1]=(byte)(value>>>16);
			des[start+2]=(byte)(value>>>8);
			des[start+3]=(byte)value;
			break;
		case LITTLE_ENDIAN:
			des[start]=(byte)value;
			des[start+1]=(byte)(value>>>8);
			des[start+2]=(byte)(value>>>16);
			des[start+3]=(byte)(value>>>24);
			break;
		}
		return start+4;
	}

	public static int longToBytes(long value, byte[] des, int start, Order order)
	{
		switch(order)
		{
		case BIG_ENDIAN:
			des[start]=(byte)(value>>>56);
			des[start+1]=(byte)(value>>>48);
			des[start+2]=(byte)(value>>>40);
			des[start+3]=(byte)(value>>>32);
			des[start+4]=(byte)(value>>>24);
			des[start+5]=(byte)(value>>>16);
			des[start+6]=(byte)(value>>>8);
			des[start+7]=(byte)value;
			break;
		case LITTLE_ENDIAN:
			des[start]=(byte)value;
			des[start+1]=(byte)(value>>>8);
			des[start+2]=(byte)(value>>>16);
			des[start+3]=(byte)(value>>>24);
			des[start+4]=(byte)(value>>>32);
			des[start+5]=(byte)(value>>>40);
			des[start+6]=(byte)(value>>>48);
			des[start+7]=(byte)(value>>>56);
			break;
		}
		return start+8;
	}

	public static int floatToBytes(float value, byte[] des, int start, Order order)
	{
		return intToBytes(Float.floatToIntBits(value), des, start, order);
	}

	public static int doubleToBytes(double value, byte[] des, int start, Order order)
	{
		return longToBytes(Double.doubleToLongBits(value), des, start, order);
	}

	public static int base128UVarintToBytes(int value, byte[] des, int start)
	{
		return base128UVarlongToBytes(value, des, start);
	}

	public static int base128SVarintToBytes(int value, byte[] des, int start)
	{
		return base128UVarlongToBytes((value << 1) ^ (value >> 31), des, start);
	}

	public static int base128VarintToBytes(int value, byte[] des, int start)
	{
		return base128VarlongToBytes(value, des, start);
	}

	public static int base128UVarlongToBytes(long value, byte[] des, int start)
	{
		byte b;
		int digit=7;
		do
		{
			b=(byte)(value & 0x7f);
			value>>>=digit;
			if(value!=0)
				b|=0x80;
			des[start++]=b;
			digit+=7;
		}while(value!=0);
		return start;
	}

	public static int base128SVarlongToBytes(long value, byte[] des, int start)
	{
		return base128UVarlongToBytes((value << 1) ^ (value >> 63), des, start);
	}

	public static int base128VarlongToBytes(long value, byte[] des, int start)
	{
		byte b;
		int digit=6;
		boolean first=true;
		byte isNegative=0;
		if(value<0)
		{
			isNegative=0x40;
			value=-value;
		}
		do
		{
			if(first)
			{
				first=false;
				b=(byte)((value & 0x3f) | isNegative);
				value>>>=digit;
				if(value!=0)
					b|=0x80;
				des[start++]=b;
				digit+=6;
			}
			else
			{
				b=(byte)(value & 0x7f);
				value>>>=digit;
				if(value!=0)
					b|=0x80;
				des[start++]=b;
				digit+=7;
			}
		}while(value!=0);
		return start;
	}

	public static int shortArrayToBytes(short[] src, byte[] des, int srcStart, int desStart, int size, Order order)
	{
		for(int i=srcStart; i<srcStart+size; i++)
		{
			desStart=shortToBytes(src[i], des, desStart, order);
		}
		return desStart;
	}

	public static int intArrayToBytes(int[] src, byte[] des, int srcStart, int desStart, int size, Order order)
	{
		for(int i=srcStart; i<srcStart+size; i++)
		{
			desStart=intToBytes(src[i], des, desStart, order);
		}
		return desStart;
	}

	public static int longArrayToBytes(long[] src, byte[] des, int srcStart, int desStart, int size, Order order)
	{
		for(int i=srcStart; i<srcStart+size; i++)
		{
			desStart=longToBytes(src[i], des, desStart, order);
		}
		return desStart;
	}

	public static int floatArrayToBytes(float[] src, byte[] des, int srcStart, int desStart, int size, Order order)
	{
		for(int i=srcStart; i<srcStart+size; i++)
		{
			desStart=floatToBytes(src[i], des, desStart, order);
		}
		return desStart;
	}

	public static int doubleArrayToBytes(double[] src, byte[] des, int srcStart, int desStart, int size, Order order)
	{
		for(int i=srcStart; i<srcStart+size; i++)
		{
			desStart=doubleToBytes(src[i], des, desStart, order);
		}
		return desStart;
	}

	public static int base128UVarintArrayToBytes(int[] src, byte[] des, int srcStart, int desStart, int size)
	{
		for(int i=srcStart; i<srcStart+size; i++)
		{
			desStart=base128UVarintToBytes(src[i], des, desStart);
		}
		return desStart;
	}

	public static int base128SVarintArrayToBytes(int[] src, byte[] des, int srcStart, int desStart, int size)
	{
		for(int i=srcStart; i<srcStart+size; i++)
		{
			desStart=base128SVarintToBytes(src[i], des, desStart);
		}
		return desStart;
	}

	public static int base128VarintArrayToBytes(int[] src, byte[] des, int srcStart, int desStart, int size)
	{
		for(int i=srcStart; i<srcStart+size; i++)
		{
			desStart=base128VarintToBytes(src[i], des, desStart);
		}
		return desStart;
	}

	public static int base128UVarlongArrayToBytes(long[] src, byte[] des, int srcStart, int desStart, int size)
	{
		for(int i=srcStart; i<srcStart+size; i++)
		{
			desStart=base128UVarlongToBytes(src[i], des, desStart);
		}
		return desStart;
	}

	public static int base128SVarlongArrayToBytes(long[] src, byte[] des, int srcStart, int desStart, int size)
	{
		for(int i=srcStart; i<srcStart+size; i++)
		{
			desStart=base128SVarlongToBytes(src[i], des, desStart);
		}
		return desStart;
	}

	public static int base128VarlongArrayToBytes(long[] src, byte[] des, int srcStart, int desStart, int size)
	{
		for(int i=srcStart; i<srcStart+size; i++)
		{
			desStart=base128VarlongToBytes(src[i], des, desStart);
		}
		return desStart;
	}

	public static int shortListToBytes(List<Short> src, byte[] des, int srcStart, int desStart, int size, Order order)
	{
		ListIterator<Short> iter=src.listIterator(srcStart);
		for(int i=0; i<size; i++)
		{
			desStart=shortToBytes(iter.next(), des, desStart, order);
		}
		return desStart;
	}

	public static int intListToBytes(List<Integer> src, byte[] des, int srcStart, int desStart, int size, Order order)
	{
		ListIterator<Integer> iter=src.listIterator(srcStart);
		for(int i=0; i<size; i++)
		{
			desStart=intToBytes(iter.next(), des, desStart, order);
		}
		return desStart;
	}

	public static int longListToBytes(List<Long> src, byte[] des, int srcStart, int desStart, int size, Order order)
	{
		ListIterator<Long> iter=src.listIterator(srcStart);
		for(int i=0; i<size; i++)
		{
			desStart=longToBytes(iter.next(), des, desStart, order);
		}
		return desStart;
	}

	public static int floatListToBytes(List<Float> src, byte[] des, int srcStart, int desStart, int size, Order order)
	{
		ListIterator<Float> iter=src.listIterator(srcStart);
		for(int i=0; i<size; i++)
		{
			desStart=floatToBytes(iter.next(), des, desStart, order);
		}
		return desStart;
	}

	public static int doubleListToBytes(List<Double> src, byte[] des, int srcStart, int desStart, int size, Order order)
	{
		ListIterator<Double> iter=src.listIterator(srcStart);
		for(int i=0; i<size; i++)
		{
			desStart=doubleToBytes(iter.next(), des, desStart, order);
		}
		return desStart;
	}

	public static int base128UVarintListToBytes(List<Integer> src, byte[] des, int srcStart, int desStart, int size)
	{
		ListIterator<Integer> iter=src.listIterator(srcStart);
		for(int i=0; i<size; i++)
		{
			desStart=base128UVarintToBytes(iter.next(), des, desStart);
		}
		return desStart;
	}

	public static int base128SVarintListToBytes(List<Integer> src, byte[] des, int srcStart, int desStart, int size)
	{
		ListIterator<Integer> iter=src.listIterator(srcStart);
		for(int i=0; i<size; i++)
		{
			desStart=base128SVarintToBytes(iter.next(), des, desStart);
		}
		return desStart;
	}

	public static int base128VarintListToBytes(List<Integer> src, byte[] des, int srcStart, int desStart, int size)
	{
		ListIterator<Integer> iter=src.listIterator(srcStart);
		for(int i=0; i<size; i++)
		{
			desStart=base128VarintToBytes(iter.next(), des, desStart);
		}
		return desStart;
	}

	public static int base128UVarlongListToBytes(List<Long> src, byte[] des, int srcStart, int desStart, int size)
	{
		ListIterator<Long> iter=src.listIterator(srcStart);
		for(int i=0; i<size; i++)
		{
			desStart=base128UVarlongToBytes(iter.next(), des, desStart);
		}
		return desStart;
	}

	public static int base128SVarlongListToBytes(List<Long> src, byte[] des, int srcStart, int desStart, int size)
	{
		ListIterator<Long> iter=src.listIterator(srcStart);
		for(int i=0; i<size; i++)
		{
			desStart=base128SVarlongToBytes(iter.next(), des, desStart);
		}
		return desStart;
	}

	public static int base128VarlongListToBytes(List<Long> src, byte[] des, int srcStart, int desStart, int size)
	{
		ListIterator<Long> iter=src.listIterator(srcStart);
		for(int i=0; i<size; i++)
		{
			desStart=base128VarlongToBytes(iter.next(), des, desStart);
		}
		return desStart;
	}

	public static short bytesToShort(byte[] src, int start, Order order)
	{
		short v=0;
		switch(order)
		{
		case BIG_ENDIAN:
			v=(short)(((src[start] & 0xff)<<8) | (src[start+1] & 0xff));
			break;
		case LITTLE_ENDIAN:
			v=(short)((src[start] & 0xff) | ((src[start+1] & 0xff)<<8));
			break;
		}
		return v;
	}

	public static int bytesToInt(byte[] src, int start, Order order)
	{
		int v=0;
		switch(order)
		{
		case BIG_ENDIAN:
			v= ((src[start] & 0xff)<<24)
			 | ((src[start+1] & 0xff)<<16)
			 | ((src[start+2] & 0xff)<<8)
			 | (src[start+3] & 0xff);
			break;
		case LITTLE_ENDIAN:
			v= (src[start] & 0xff)
			 | ((src[start+1] & 0xff)<<8)
			 | ((src[start+2] & 0xff)<<16)
			 | ((src[start+3] & 0xff)<<24);
			break;
		}
		return v;
	}

	public static long bytesToLong(byte[] src, int start, Order order)
	{
		long v=0;
		switch(order)
		{
		case BIG_ENDIAN:
			v= ((src[start] & 0xffL)<<56)
			 | ((src[start+1] & 0xffL)<<48)
			 | ((src[start+2] & 0xffL)<<40)
			 | ((src[start+3] & 0xffL)<<32)
			 | ((src[start+4] & 0xffL)<<24)
			 | ((src[start+5] & 0xffL)<<16)
			 | ((src[start+6] & 0xffL)<<8)
			 | (src[start+7] & 0xffL);
			break;
		case LITTLE_ENDIAN:
			v= (src[start] & 0xffL)
			 | ((src[start+1] & 0xffL)<<8)
			 | ((src[start+2] & 0xffL)<<16)
			 | ((src[start+3] & 0xffL)<<24)
			 | ((src[start+4] & 0xffL)<<32)
			 | ((src[start+5] & 0xffL)<<40)
			 | ((src[start+6] & 0xffL)<<48)
			 | ((src[start+7] & 0xffL)<<56);
			break;
		}
		return v;
	}

	public static float bytesToFloat(byte[] src, int start, Order order)
	{
		return Float.intBitsToFloat(bytesToInt(src, start, order));
	}

	public static double bytesToDouble(byte[] src, int start, Order order)
	{
		return Double.longBitsToDouble(bytesToLong(src, start, order));
	}

	public static Pair<Integer, Integer> bytesToBase128UVarint(byte[] src, int start)
	{
		Pair<Long, Integer> pair=bytesToBase128UVarlong(src, start);
		return Pair.of(pair.getLeft().intValue(), pair.getRight());
	}

	public static Pair<Integer, Integer> bytesToBase128SVarint(byte[] src, int start)
	{
		Pair<Long, Integer> pair=bytesToBase128UVarlong(src, start);
		int v=pair.getLeft().intValue();
		return Pair.of((v ^ ((v << 31) >> 31)) >> 1, pair.getRight());
	}

	public static Pair<Integer, Integer> bytesToBase128Varint(byte[] src, int start)
	{
		Pair<Long, Integer> pair=bytesToBase128Varlong(src, start);
		return Pair.of(pair.getLeft().intValue(), pair.getRight());
	}

	public static Pair<Long, Integer> bytesToBase128UVarlong(byte[] src, int start)
	{
		long v=0;
		int digit=0;
		byte b;
		do
		{
			b=src[start++];
			v|=(b & 0x7f)<<digit;
			digit+=7;
		}while((b & 0x80)!=0);
		return Pair.of(v, start);
	}

	public static Pair<Long, Integer> bytesToBase128SVarlong(byte[] src, int start)
	{
		Pair<Long, Integer> pair=bytesToBase128UVarlong(src, start);
		long v=pair.getLeft();
		return Pair.of((v ^ ((v << 63) >> 63)) >> 1, pair.getRight());
	}

	public static Pair<Long, Integer> bytesToBase128Varlong(byte[] src, int start)
	{
		long v=0;
		int digit=0;
		byte b;
		boolean first=true;
		boolean negative=false;
		do
		{
			if(first)
			{
				first=false;
				b=src[start++];
				v|=(b & 0x3f)<<digit;
				negative=(b & 0x40)!=0;
				digit+=6;
			}
			else
			{
				b=src[start++];
				v|=(b & 0x7f)<<digit;
				digit+=7;
			}
		}while((b & 0x80)!=0);
		return Pair.of(negative ? -v : v, start);
	}

	public static int bytesToShortArray(byte[] src, short[] des, int srcStart, int desStart, int size, Order order)
	{
		for(int i=desStart; i<desStart+size; i++)
		{
			des[i]=bytesToShort(src, srcStart, order);
			srcStart+=2;
		}
		return srcStart;
	}

	public static int bytesToIntArray(byte[] src, int[] des, int srcStart, int desStart, int size, Order order)
	{
		for(int i=desStart; i<desStart+size; i++)
		{
			des[i]=bytesToInt(src, srcStart, order);
			srcStart+=4;
		}
		return srcStart;
	}

	public static int bytesToLongArray(byte[] src, long[] des, int srcStart, int desStart, int size, Order order)
	{
		for(int i=desStart; i<desStart+size; i++)
		{
			des[i]=bytesToLong(src, srcStart, order);
			srcStart+=8;
		}
		return srcStart;
	}

	public static int bytesToFloatArray(byte[] src, float[] des, int srcStart, int desStart, int size, Order order)
	{
		for(int i=desStart; i<desStart+size; i++)
		{
			des[i]=bytesToFloat(src, srcStart, order);
			srcStart+=4;
		}
		return srcStart;
	}

	public static int bytesToDoubleArray(byte[] src, double[] des, int srcStart, int desStart, int size, Order order)
	{
		for(int i=desStart; i<desStart+size; i++)
		{
			des[i]=bytesToDouble(src, srcStart, order);
			srcStart+=8;
		}
		return srcStart;
	}

	public static int bytesToBase128UVarintArray(byte[] src, int[] des, int srcStart, int desStart, int size)
	{
		for(int i=desStart; i<desStart+size; i++)
		{
			Pair<Integer, Integer> pair=bytesToBase128UVarint(src, srcStart);
			des[i]=pair.getLeft();
			srcStart=pair.getRight();
		}
		return srcStart;
	}

	public static int bytesToBase128SVarintArray(byte[] src, int[] des, int srcStart, int desStart, int size)
	{
		for(int i=desStart; i<desStart+size; i++)
		{
			Pair<Integer, Integer> pair=bytesToBase128SVarint(src, srcStart);
			des[i]=pair.getLeft();
			srcStart=pair.getRight();
		}
		return srcStart;
	}

	public static int bytesToBase128VarintArray(byte[] src, int[] des, int srcStart, int desStart, int size)
	{
		for(int i=desStart; i<desStart+size; i++)
		{
			Pair<Integer, Integer> pair=bytesToBase128Varint(src, srcStart);
			des[i]=pair.getLeft();
			srcStart=pair.getRight();
		}
		return srcStart;
	}

	public static int bytesToBase128UVarlongArray(byte[] src, long[] des, int srcStart, int desStart, int size)
	{
		for(int i=desStart; i<desStart+size; i++)
		{
			Pair<Long, Integer> pair=bytesToBase128UVarlong(src, srcStart);
			des[i]=pair.getLeft();
			srcStart=pair.getRight();
		}
		return srcStart;
	}

	public static int bytesToBase128SVarlongArray(byte[] src, long[] des, int srcStart, int desStart, int size)
	{
		for(int i=desStart; i<desStart+size; i++)
		{
			Pair<Long, Integer> pair=bytesToBase128SVarlong(src, srcStart);
			des[i]=pair.getLeft();
			srcStart=pair.getRight();
		}
		return srcStart;
	}

	public static int bytesToBase128VarlongArray(byte[] src, long[] des, int srcStart, int desStart, int size)
	{
		for(int i=desStart; i<desStart+size; i++)
		{
			Pair<Long, Integer> pair=bytesToBase128Varlong(src, srcStart);
			des[i]=pair.getLeft();
			srcStart=pair.getRight();
		}
		return srcStart;
	}

	public static int bytesToShortList(byte[] src, List<Short> des, int srcStart, int desStart, int size, Order order)
	{
		ListIterator<Short> iter=null;
		if(desStart<des.size())
			iter=des.listIterator(desStart);
		for(int i=desStart; i<desStart+size; i++)
		{
			if(i<des.size())
			{
				iter.next();
				iter.set(bytesToShort(src, srcStart, order));
			}
			else
				des.add(bytesToShort(src, srcStart, order));
			srcStart+=2;
		}
		return srcStart;
	}

	public static int bytesToIntList(byte[] src, List<Integer> des, int srcStart, int desStart, int size, Order order)
	{
		ListIterator<Integer> iter=null;
		if(desStart<des.size())
			iter=des.listIterator(desStart);
		for(int i=desStart; i<desStart+size; i++)
		{
			if(i<des.size())
			{
				iter.next();
				iter.set(bytesToInt(src, srcStart, order));
			}
			else
				des.add(bytesToInt(src, srcStart, order));
			srcStart+=4;
		}
		return srcStart;
	}

	public static int bytesToLongList(byte[] src, List<Long> des, int srcStart, int desStart, int size, Order order)
	{
		ListIterator<Long> iter=null;
		if(desStart<des.size())
			iter=des.listIterator(desStart);
		for(int i=desStart; i<desStart+size; i++)
		{
			if(i<des.size())
			{
				iter.next();
				iter.set(bytesToLong(src, srcStart, order));
			}
			else
				des.add(bytesToLong(src, srcStart, order));
			srcStart+=8;
		}
		return srcStart;
	}

	public static int bytesToFloatList(byte[] src, List<Float> des, int srcStart, int desStart, int size, Order order)
	{
		ListIterator<Float> iter=null;
		if(desStart<des.size())
			iter=des.listIterator(desStart);
		for(int i=desStart; i<desStart+size; i++)
		{
			if(i<des.size())
			{
				iter.next();
				iter.set(bytesToFloat(src, srcStart, order));
			}
			else
				des.add(bytesToFloat(src, srcStart, order));
			srcStart+=4;
		}
		return srcStart;
	}

	public static int bytesToDoubleList(byte[] src, List<Double> des, int srcStart, int desStart, int size, Order order)
	{
		ListIterator<Double> iter=null;
		if(desStart<des.size())
			iter=des.listIterator(desStart);
		for(int i=desStart; i<desStart+size; i++)
		{
			if(i<des.size())
			{
				iter.next();
				iter.set(bytesToDouble(src, srcStart, order));
			}
			else
				des.add(bytesToDouble(src, srcStart, order));
			srcStart+=8;
		}
		return srcStart;
	}

	public static int bytesToBase128UVarintList(byte[] src, List<Integer> des, int srcStart, int desStart, int size)
	{
		ListIterator<Integer> iter=null;
		if(desStart<des.size())
			iter=des.listIterator(desStart);
		for(int i=0; i<size; i++)
		{
			Pair<Integer, Integer> pair=bytesToBase128UVarint(src, srcStart);
			if(iter.hasNext())
			{
				iter.next();
				iter.set(pair.getLeft());
			}
			else
				iter.add(pair.getLeft());
			srcStart=pair.getRight();
		}
		return srcStart;
	}

	public static int bytesToBase128SVarintList(byte[] src, List<Integer> des, int srcStart, int desStart, int size)
	{
		ListIterator<Integer> iter=null;
		if(desStart<des.size())
			iter=des.listIterator(desStart);
		for(int i=0; i<size; i++)
		{
			Pair<Integer, Integer> pair=bytesToBase128SVarint(src, srcStart);
			if(iter.hasNext())
			{
				iter.next();
				iter.set(pair.getLeft());
			}
			else
				iter.add(pair.getLeft());
			srcStart=pair.getRight();
		}
		return srcStart;
	}

	public static int bytesToBase128VarintList(byte[] src, List<Integer> des, int srcStart, int desStart, int size)
	{
		ListIterator<Integer> iter=null;
		if(desStart<des.size())
			iter=des.listIterator(desStart);
		for(int i=0; i<size; i++)
		{
			Pair<Integer, Integer> pair=bytesToBase128Varint(src, srcStart);
			if(iter.hasNext())
			{
				iter.next();
				iter.set(pair.getLeft());
			}
			else
				iter.add(pair.getLeft());
			srcStart=pair.getRight();
		}
		return srcStart;
	}

	public static int bytesToBase128UVarlongList(byte[] src, List<Long> des, int srcStart, int desStart, int size)
	{
		ListIterator<Long> iter=null;
		if(desStart<des.size())
			iter=des.listIterator(desStart);
		for(int i=0; i<size; i++)
		{
			Pair<Long, Integer> pair=bytesToBase128UVarlong(src, srcStart);
			if(iter.hasNext())
			{
				iter.next();
				iter.set(pair.getLeft());
			}
			else
				iter.add(pair.getLeft());
			srcStart=pair.getRight();
		}
		return srcStart;
	}

	public static int bytesToBase128SVarlongList(byte[] src, List<Long> des, int srcStart, int desStart, int size)
	{
		ListIterator<Long> iter=null;
		if(desStart<des.size())
			iter=des.listIterator(desStart);
		for(int i=0; i<size; i++)
		{
			Pair<Long, Integer> pair=bytesToBase128SVarlong(src, srcStart);
			if(iter.hasNext())
			{
				iter.next();
				iter.set(pair.getLeft());
			}
			else
				iter.add(pair.getLeft());
			srcStart=pair.getRight();
		}
		return srcStart;
	}

	public static int bytesToBase128VarlongList(byte[] src, List<Long> des, int srcStart, int desStart, int size)
	{
		ListIterator<Long> iter=null;
		if(desStart<des.size())
			iter=des.listIterator(desStart);
		for(int i=0; i<size; i++)
		{
			Pair<Long, Integer> pair=bytesToBase128Varlong(src, srcStart);
			if(iter.hasNext())
			{
				iter.next();
				iter.set(pair.getLeft());
			}
			else
				iter.add(pair.getLeft());
			srcStart=pair.getRight();
		}
		return srcStart;
	}

	public static void writeBase128UVarint(DataOutput out, int value) throws IOException
	{
		writeBase128UVarlong(out, value);
	}

	public static void writeBase128SVarint(DataOutput out, int value) throws IOException
	{
		writeBase128UVarlong(out, (value << 1) ^ (value >> 31));
	}

	public static void writeBase128Varint(DataOutput out, int value) throws IOException
	{
		writeBase128Varlong(out, value);
	}

	public static void writeBase128UVarlong(DataOutput out, long value) throws IOException
	{
		byte b;
		int digit=7;
		do
		{
			b=(byte)(value & 0x7f);
			value>>>=digit;
			if(value!=0)
				b|=0x80;
			out.writeByte(b);
			digit+=7;
		}while(value!=0);
	}

	public static void writeBase128SVarlong(DataOutput out, long value) throws IOException
	{
		writeBase128UVarlong(out, (value << 1) ^ (value >> 63));
	}

	public static void writeBase128Varlong(DataOutput out, long value) throws IOException
	{
		byte b;
		int digit=6;
		boolean first=true;
		byte isNegative=0;
		if(value<0)
		{
			isNegative=0x40;
			value=-value;
		}
		do
		{
			if(first)
			{
				first=false;
				b=(byte)((value & 0x3f) | isNegative);
				value>>>=digit;
				if(value!=0)
					b|=0x80;
				out.writeByte(b);
				digit+=6;
			}
			else
			{
				b=(byte)(value & 0x7f);
				value>>>=digit;
				if(value!=0)
					b|=0x80;
				out.writeByte(b);
				digit+=7;
			}
		}while(value!=0);
	}

	public static int readBase128UVarint(DataInput in) throws IOException
	{
		return (int)readBase128UVarlong(in);
	}

	public static int readBase128SVarint(DataInput in) throws IOException
	{
		int v=(int)readBase128UVarlong(in);
		return (v ^ ((v << 31) >> 31)) >> 1;
	}

	public static int readBase128Varint(DataInput in) throws IOException
	{
		return (int)readBase128Varlong(in);
	}

	public static long readBase128UVarlong(DataInput in) throws IOException
	{
		long v=0;
		int digit=0;
		byte b;
		do
		{
			b=in.readByte();
			v|=(b & 0x7f)<<digit;
			digit+=7;
		}while((b & 0x80)!=0);
		return v;
	}

	public static long readBase128SVarlong(DataInput in) throws IOException
	{
		long v=readBase128UVarlong(in);
		return (v ^ ((v << 63) >> 63)) >> 1;
	}

	public static long readBase128Varlong(DataInput in) throws IOException
	{
		long v=0;
		int digit=0;
		byte b;
		boolean first=true;
		boolean negative=false;
		do
		{
			if(first)
			{
				first=false;
				b=in.readByte();
				v|=(b & 0x3f)<<digit;
				negative=(b & 0x40)!=0;
				digit+=6;
			}
			else
			{
				b=in.readByte();
				v|=(b & 0x7f)<<digit;
				digit+=7;
			}
		}while((b & 0x80)!=0);
		return negative ? -v : v;
	}
}
