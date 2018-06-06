package com.eroelf.javaxsx.util.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

/**
 * Help to compress a text {@link OutputStream} to a ZIP {@link OutputStream} with a default {@link ZipEntry}.
 * 
 * @author weikun.zhong
 */
public class ZipTextOutputStream extends ZipOutputStream
{
	private String defaultEntryName;

	public ZipTextOutputStream(OutputStream out)
	{
		this(out, "_");
	}

	public ZipTextOutputStream(OutputStream out, Charset charset)
	{
		this(out, charset, "_");
	}

	public ZipTextOutputStream(OutputStream out, String defaultEntryName)
	{
		super(out);
		this.defaultEntryName=defaultEntryName;
	}

	public ZipTextOutputStream(OutputStream out, Charset charset, String defaultEntryName)
	{
		super(out, charset);
		this.defaultEntryName=defaultEntryName;
	}

	@Override
	public synchronized void write(byte[] b, int off, int len) throws IOException
	{
		try
		{
			super.write(b, off, len);
		}
		catch(ZipException e)
		{
			if("no current ZIP entry".equals(e.getMessage()))
			{
				putNextEntry(new ZipEntry(defaultEntryName));
				super.write(b, off, len);
			}
			else
				throw e;
		}
	}
}
