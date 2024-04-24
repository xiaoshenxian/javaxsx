package com.eroelf.javaxsx.util.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorInputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream;

/**
 * Provide methods to compress and decompress data by specified compression algorithms.
 * Encodings are needed if the data to be compress or decompress are {@link String}s.
 * 
 * @author weikun.zhong
 */
public class Compressor
{
	public static class CompressorBuilder
	{
		public boolean suppressNull;
		public int bufferSize;
		public String uncompressedEncoding;
		public String compressedEncoding;
		public StreamFactory<InputStream> inCompressorFactory;
		public StreamFactory<OutputStream> outCompressorFactory;

		public CompressorBuilder()
		{
			this(true, 1024, "UTF-8", "ISO-8859-1", "gzip");
		}

		public CompressorBuilder(boolean suppressNull, int bufferSize, String uncompressedEncoding, String compressedEncoding, String compressionType)
		{
			this.suppressNull=suppressNull;
			this.bufferSize=bufferSize;
			this.uncompressedEncoding=uncompressedEncoding;
			this.compressedEncoding=compressedEncoding;
			setCompression(compressionType);
		}

		public CompressorBuilder(boolean suppressNull, int bufferSize, String uncompressedEncoding, String compressedEncoding, StreamFactory<InputStream> inCompressorFactory, StreamFactory<OutputStream> outCompressorFactory)
		{
			this.suppressNull=suppressNull;
			this.bufferSize=bufferSize;
			this.uncompressedEncoding=uncompressedEncoding;
			this.compressedEncoding=compressedEncoding;
			this.inCompressorFactory=inCompressorFactory;
			this.outCompressorFactory=outCompressorFactory;
		}

		public void setCompression(String compressionType)
		{
			compressionType=compressionType.trim().toLowerCase(Locale.ENGLISH);
			switch(compressionType)
			{
			case "zip":
				inCompressorFactory=ZipTextInputStream::new;
				outCompressorFactory=ZipTextOutputStream::new;
				break;
			case "gzip":
			case "gz":
				inCompressorFactory=GZIPInputStream::new;
				outCompressorFactory=GZIPOutputStream::new;
				break;
			case "bzip2":
			case "bz2":
				inCompressorFactory=BZip2CompressorInputStream::new;
				outCompressorFactory=BZip2CompressorOutputStream::new;
			case "zstd":
			case "zst":
				inCompressorFactory=ZstdCompressorInputStream::new;
				outCompressorFactory=ZstdCompressorOutputStream::new;
				break;
			default:
				throw new IllegalArgumentException("Unsupported compression type: "+compressionType);
			}
		}
	}

	private final boolean suppressNull;
	private final int bufferSize;
	private final String uncompressedEncoding;
	private final String compressedEncoding;
	private final StreamFactory<InputStream> inCompressorFactory;
	private final StreamFactory<OutputStream> outCompressorFactory;

	public Compressor()
	{
		this(new CompressorBuilder());
	}

	public Compressor(CompressorBuilder builder)
	{
		suppressNull=builder.suppressNull;
		bufferSize=builder.bufferSize;
		uncompressedEncoding=builder.uncompressedEncoding;
		compressedEncoding=builder.compressedEncoding;
		inCompressorFactory=builder.inCompressorFactory;
		outCompressorFactory=builder.outCompressorFactory;

		if(bufferSize<=0)
			throw new IllegalArgumentException("bufferSize must be greater than 0!");
		if(inCompressorFactory==null)
			throw new IllegalArgumentException("inCompressorFactory must not be null!");
		if(outCompressorFactory==null)
			throw new IllegalArgumentException("outCompressorFactory must not be null!");
	}

	public String compress(final String str) throws IOException
	{
		String s=str;
		if(s==null)
		{
			if(suppressNull)
				s="";
			else
				return null;
		}
		return compress(s.getBytes(uncompressedEncoding));
	}

	public String compress(final byte[] b) throws IOException
	{
		if(b==null)
		{
			if(suppressNull)
				return compress("");
			else
				return null;
		}
		byte[] bytes=compressToBytes(b);
		return new String(bytes, compressedEncoding);
	}

	public byte[] compressToBytes(final String str) throws IOException
	{
		String s=str;
		if(s==null)
		{
			if(suppressNull)
				s="";
			else
				return null;
		}
		return compressToBytes(s.getBytes(uncompressedEncoding));
	}

	public byte[] compressToBytes(final byte[] b) throws IOException
	{
		if(b==null)
		{
			if(suppressNull)
				return compressToBytes("");
			else
				return null;
		}
		try(ByteArrayOutputStream out=new ByteArrayOutputStream())
		{
			try(OutputStream compressed=outCompressorFactory.get(out))
			{
				compressed.write(b);
				compressed.flush();
			}
			return out.toByteArray();
		}
	}

	public String decompress(final String str) throws IOException
	{
		String s=str;
		if(s==null)
		{
			if(suppressNull)
				s="";
			else
				return null;
		}
		return decompress(s.getBytes(compressedEncoding));
	}

	public String decompress(final byte[] b) throws IOException
	{
		if(b==null)
		{
			if(suppressNull)
				return decompress("");
			else
				return null;
		}
		byte[] bytes=decompressToBytes(b);
		return new String(bytes, uncompressedEncoding);
	}

	public byte[] decompressToBytes(final String str) throws IOException
	{
		String s=str;
		if(s==null)
		{
			if(suppressNull)
				s="";
			else
				return null;
		}
		return decompressToBytes(s.getBytes(compressedEncoding));
	}

	public byte[] decompressToBytes(final byte[] b) throws IOException
	{
		if(b==null)
		{
			if(suppressNull)
				return decompressToBytes("");
			else
				return null;
		}
		try(ByteArrayOutputStream out=new ByteArrayOutputStream())
		{
			try(InputStream compressed=inCompressorFactory.get(new ByteArrayInputStream(b)))
			{
				byte[] buffer=new byte[bufferSize];
				int n;
				while((n=compressed.read(buffer))>=0)
				{
					out.write(buffer, 0, n);
				}
				out.flush();
			}
			return out.toByteArray();
		}
	}
}
