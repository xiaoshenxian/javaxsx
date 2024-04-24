package com.eroelf.javaxsx.util.group.hashgetterimpl;

import com.eroelf.javaxsx.util.Strings;
import com.eroelf.javaxsx.util.group.HashGetter;

/**
 * The {@link HashGetter} sub-class which use md5 digest to generate hashes.
 * 
 * @deprecated use {@link DigestHashGetter} instead.
 * 
 * @author weikun.zhong
 */
@Deprecated
public class Md5HashGetter implements HashGetter
{
	private int bytes;
	private double maxValue;

	public Md5HashGetter(int bytes)
	{
		if(bytes>0 && bytes<=8)
		{
			this.bytes=bytes;
			maxValue=Math.pow(256, bytes);
		}
		else
			throw new IllegalArgumentException("bytes must be within (0, 8]!");
	}

	@Override
	public double hash(String identifier)
	{
		byte[] digest=Strings.md5Digest(identifier);
		if(digest.length>=bytes)
		{
			long value=0;
			for(int i=digest.length-bytes; i<digest.length; i++)
			{
				value=(value<<8) | (((long)digest[i]) & 0xff);
			}
			return value/maxValue;
		}
		else
			return 1;
	}
}
