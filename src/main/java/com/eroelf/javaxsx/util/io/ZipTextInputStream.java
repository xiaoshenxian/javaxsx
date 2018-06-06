package com.eroelf.javaxsx.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.eroelf.javaxsx.util.Strings;

/**
 * Help to continuously read all entry text in a ZIP file.
 * 
 * @author weikun.zhong
 */
public class ZipTextInputStream extends PushbackInputStream
{
	private static final byte[] LINE_SEP=Strings.LINE_SEPARATOR.getBytes();

	private byte[] last=Arrays.copyOf(LINE_SEP, LINE_SEP.length);

	public ZipTextInputStream(InputStream in)
	{
		super(in instanceof ZipInputStream ? in : new ZipInputStream(in), LINE_SEP.length);
	}

	public ZipTextInputStream(InputStream in, Charset charset)
	{
		super(in instanceof ZipInputStream ? in : new ZipInputStream(in, charset), LINE_SEP.length);
	}

	@Override
	public int read() throws IOException
	{
		int re=super.read();
		while(re==-1)
		{
			ZipEntry entry=((ZipInputStream)in).getNextEntry();
			if(entry==null)
				break;
			if(!entry.isDirectory())
			{
				if(!Arrays.equals(last, LINE_SEP))
					unread(LINE_SEP, 0, LINE_SEP.length);
				re=super.read();
			}
		}
		if(re>0)
		{
			for(int i=0; i<LINE_SEP.length-1; i++)
			{
				last[i]=last[i+1];
			}
			last[LINE_SEP.length-1]=(byte)re;
		}
		return re;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		int re=super.read(b, off, len);
		while(re==-1)
		{
			ZipEntry entry=((ZipInputStream)in).getNextEntry();
			if(entry==null)
				break;
			if(!entry.isDirectory())
			{
				if(!Arrays.equals(last, LINE_SEP))
					unread(LINE_SEP, 0, LINE_SEP.length);
				re=super.read(b, off, len);
			}
		}
		if(re>0)
		{
			int lastLen=Math.min(LINE_SEP.length, re);
			for(int i=0; i<LINE_SEP.length-lastLen; i++)
			{
				last[i]=last[i+lastLen];
			}
			for(int i=LINE_SEP.length-lastLen; i<LINE_SEP.length; i++)
			{
				last[i]=b[off+re-lastLen+i];
			}
		}
		return re;
	}
}
