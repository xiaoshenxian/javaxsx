package com.eroelf.javaxsx.util.group.hashgetterimpl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.eroelf.javaxsx.util.group.HashGetter;

/**
 * The {@link HashGetter} sub-class which use a specified digest to generate hashes.
 * 
 * @author weikun.zhong
 */
public class DigestHashGetter implements HashGetter
{
	private String algorithm;
	private MessageDigest md;
	private int bytes;
	private double maxValue;

	public DigestHashGetter(String algorithm, int bytes)
	{
		this.algorithm=algorithm;
		try
		{
			md=MessageDigest.getInstance(this.algorithm);
		}
		catch(NoSuchAlgorithmException e)
		{
			throw new InternalError(String.format("Algorithm `%s` is not supported", algorithm));
		}
		int digestLen=md.getDigestLength();
		if(digestLen<=0)
			throw new IllegalArgumentException(String.format("Cannot get the digest length for algorithm `%s`", algorithm));
		if(bytes>0 && bytes<=digestLen && bytes<=8)
		{
			this.bytes=bytes;
			maxValue=Math.pow(256, bytes);
		}
		else
			throw new IllegalArgumentException(String.format("bytes must be within (0, %d]!", Math.min(digestLen, 8)));
	}

	@Override
	public double hash(String identifier)
	{
		byte[] digest=this.md.digest(identifier.getBytes(StandardCharsets.UTF_8));
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
