package com.eroelf.javaxsx.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import com.eroelf.javaxsx.util.NumBytes;
import com.eroelf.javaxsx.util.NumBytes.Order;

/**
 * 
 * @author weikun.zhong
 */
public class UUIDs
{
	public static UUID nil()
	{
		return new UUID(0L, 0L);
	}

	public static UUID type3(byte[] name)
	{
		return UUID.nameUUIDFromBytes(name);
	}

	public static UUID type4()
	{
		return UUID.randomUUID();
	}

	public static UUID type5(byte[] name)
	{
		MessageDigest md;
		try
		{
			md=MessageDigest.getInstance("SHA-1");
		}
		catch(NoSuchAlgorithmException nsae)
		{
			throw new InternalError("SHA-1 not supported");
		}
		byte[] sha1Bytes = md.digest(name);
		sha1Bytes[6]&=0x0f; /* clear version */
		sha1Bytes[6]|=0x50; /* set to version 5 */
		sha1Bytes[8]&=0x3f; /* clear variant */
		sha1Bytes[8]|=0x80; /* set to IETF variant */
		return new UUID(NumBytes.bytesToLong(sha1Bytes, 0, Order.BIG_ENDIAN), NumBytes.bytesToLong(sha1Bytes, 8, Order.BIG_ENDIAN));
	}

	public static UUID fromBytes(byte[] bytes)
	{
		return new UUID(NumBytes.bytesToLong(bytes, 0, Order.BIG_ENDIAN), NumBytes.bytesToLong(bytes, 8, Order.BIG_ENDIAN));
	}

	public static byte[] toBytes(UUID uuid)
	{
		byte[] bytes=new byte[16];
		NumBytes.longToBytes(uuid.getMostSignificantBits(), bytes, 0, Order.BIG_ENDIAN);
		NumBytes.longToBytes(uuid.getLeastSignificantBits(), bytes, 8, Order.BIG_ENDIAN);
		return bytes;
	}

	private UUIDs()
	{}

	@Override
	public UUIDs clone()
	{
		throw new UnsupportedOperationException("This method is not allowed!");
	}
}
