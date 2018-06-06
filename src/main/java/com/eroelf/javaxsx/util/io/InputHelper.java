package com.eroelf.javaxsx.util.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.Locale;
import java.util.Map.Entry;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.openstreetmap.osmosis.core.util.MultiMemberGZIPInputStream;

/**
 * A helper class for converting files to {@link InputStream} or {@link BufferedReader}.
 * Compressions and charsets are both taken into concern.
 * 
 * @author weikun.zhong
 */
public class InputHelper
{
	private static final InputHelper DEFAULT_INPUT_HELPER=new InputHelper("utf-8");

	public static InputHelper get()
	{
		return DEFAULT_INPUT_HELPER;
	}

	public String charsetName;

	public InputHelper()
	{
		this(null);
	}

	public InputHelper(String charsetName)
	{
		this.charsetName=charsetName;
	}

	public BufferedReader getBufferedReader() throws IOException
	{
		return getBufferedReader(System.in);
	}

	public BufferedReader getBufferedReader(int bufferSize) throws IOException
	{
		return getBufferedReader(System.in, bufferSize);
	}

	public BufferedReader getBufferedReader(File file) throws IOException
	{
		return getBufferedReader(getInputStream(file));
	}

	public BufferedReader getBufferedReader(File file, int bufferSize) throws IOException
	{
		return getBufferedReader(getInputStream(file), bufferSize);
	}

	public BufferedReader getBufferedReader(String fileNameString) throws IOException
	{
		return getBufferedReader(getInputStream(fileNameString));
	}

	public BufferedReader getBufferedReader(String fileNameString, int bufferSize) throws IOException
	{
		return getBufferedReader(getInputStream(fileNameString), bufferSize);
	}

	public <T> BufferedReader getBufferedReader(Class<T> desClass, String fileNameString) throws IOException
	{
		return getBufferedReader(getInputStream(desClass, fileNameString));
	}

	public <T> BufferedReader getBufferedReader(Class<T> desClass, String fileNameString, int bufferSize) throws IOException
	{
		return getBufferedReader(getInputStream(desClass, fileNameString), bufferSize);
	}

	public BufferedReader getBufferedReader(InputStream in) throws IOException
	{
		InputStreamReader inReader=charsetName==null ? new InputStreamReader(in) : new InputStreamReader(in, charsetName);
		try
		{
			return new BufferedReader(inReader); 
		}
		catch(Throwable throwable)
		{
			inReader.close();
			throw throwable;
		}
	}

	public BufferedReader getBufferedReader(InputStream in, int bufferSize) throws IOException
	{
		InputStreamReader inReader=charsetName==null ? new InputStreamReader(in) : new InputStreamReader(in, charsetName);
		try
		{
			return new BufferedReader(inReader, bufferSize); 
		}
		catch(Throwable throwable)
		{
			inReader.close();
			throw throwable;
		}
	}

	public InputStream getInputStream(File file) throws IOException
	{
		InputStream in=new FileInputStream(file);
		try
		{
			Entry<InputStream, String> entry=getCompressType(file.getName(), in);
			return convert(entry.getKey(), entry.getValue());
		}
		catch(Throwable throwable)
		{
			in.close();
			throw throwable;
		}
	}

	public InputStream getInputStream(String fileNameString) throws IOException
	{
		InputStream in=new FileInputStream(fileNameString);
		try
		{
			Entry<InputStream, String> entry=getCompressType(fileNameString, in);
			return convert(entry.getKey(), entry.getValue());
		}
		catch(Throwable throwable)
		{
			in.close();
			throw throwable;
		}
	}

	public <T> InputStream getInputStream(Class<T> desClass, String fileNameString) throws IOException
	{
		InputStream in=desClass.getResourceAsStream(fileNameString);
		try
		{
			Entry<InputStream, String> entry=getCompressType(fileNameString, in);
			return convert(entry.getKey(), entry.getValue());
		}
		catch(Throwable throwable)
		{
			in.close();
			throw throwable;
		}
	}

	public Entry<InputStream, String> getCompressType(String fileNameString, InputStream in)
	{
		String lowerName=fileNameString.trim().toLowerCase(Locale.ENGLISH);
		int pos=lowerName.lastIndexOf('.');
		return new AbstractMap.SimpleImmutableEntry<>(in, pos>=0 ? lowerName.substring(pos+1) : "");
	}

	public InputStream convert(InputStream in, String compressionType) throws IOException
	{
		switch(compressionType)
		{
		case "zip":
			return new ZipTextInputStream(in);
		case "gzip":
		case "gz":
			return new MultiMemberGZIPInputStream(in);
		case "bzip2":
		case "bz2":
			return new BZip2CompressorInputStream(in);
		default:
			return in;
		}
	}
}
