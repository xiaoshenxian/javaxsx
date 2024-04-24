package com.eroelf.javaxsx.util.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This class converts an input file to an iterator whose next() method returns the processed result of each line.
 * A simple usage is to invoke the {@link #forEachRemaining(java.util.function.Consumer) forEachRemaining} method to process each line.
 * 
 * @author weikun.zhong
 */
public class FileIterator implements Iterator<String>
{
	protected InputHelper inputHelper;
	protected String line;
	protected BufferedReader br=null;

	public FileIterator(boolean loadSystemIn)
	{
		if(loadSystemIn)
			load();
	}

	public FileIterator(boolean loadSystemIn, InputHelper inputHelper)
	{
		if(loadSystemIn)
			load(inputHelper);
		else
			this.inputHelper=inputHelper;
	}

	public FileIterator(int bufferSize)
	{
		load(bufferSize);
	}

	public FileIterator(InputHelper inputHelper, int bufferSize)
	{
		load(inputHelper, bufferSize);
	}

	public FileIterator(File file)
	{
		load(file);
	}

	public FileIterator(InputHelper inputHelper, File file)
	{
		load(inputHelper, file);
	}

	public FileIterator(File file, int bufferSize)
	{
		load(file, bufferSize);
	}

	public FileIterator(InputHelper inputHelper, File file, int bufferSize)
	{
		load(inputHelper, file, bufferSize);
	}

	public FileIterator(String fileNameString)
	{
		load(fileNameString);
	}

	public FileIterator(InputHelper inputHelper, String fileNameString)
	{
		load(inputHelper, fileNameString);
	}

	public FileIterator(String fileNameString, int bufferSize)
	{
		load(fileNameString, bufferSize);
	}

	public FileIterator(InputHelper inputHelper, String fileNameString, int bufferSize)
	{
		load(inputHelper, fileNameString, bufferSize);
	}

	public <T> FileIterator(Class<T> desClass, String fileNameString)
	{
		load(desClass, fileNameString);
	}

	public <T> FileIterator(InputHelper inputHelper, Class<T> desClass, String fileNameString)
	{
		load(inputHelper, desClass, fileNameString);
	}

	public <T> FileIterator(Class<T> desClass, String fileNameString, int bufferSize)
	{
		load(desClass, fileNameString, bufferSize);
	}

	public <T> FileIterator(InputHelper inputHelper, Class<T> desClass, String fileNameString, int bufferSize)
	{
		load(inputHelper, desClass, fileNameString, bufferSize);
	}

	public FileIterator(InputStream in)
	{
		load(in);
	}

	public FileIterator(InputHelper inputHelper, InputStream in)
	{
		load(inputHelper, in);
	}

	public FileIterator(InputStream in, int bufferSize)
	{
		load(in, bufferSize);
	}

	public FileIterator(InputHelper inputHelper, InputStream in, int bufferSize)
	{
		load(inputHelper, in, bufferSize);
	}

	public void load()
	{
		load(inputHelper==null ? InputHelper.get() : inputHelper);
	}

	public void load(InputHelper inputHelper)
	{
		this.inputHelper=inputHelper;
		try
		{
			close();
			br=this.inputHelper.getBufferedReader();
		}
		catch(IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	public void load(int bufferSize)
	{
		load(inputHelper==null ? InputHelper.get() : inputHelper, bufferSize);
	}

	public void load(InputHelper inputHelper, int bufferSize)
	{
		this.inputHelper=inputHelper;
		try
		{
			close();
			br=this.inputHelper.getBufferedReader(bufferSize);
		}
		catch(IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	public void load(File file)
	{
		load(inputHelper==null ? InputHelper.get() : inputHelper, file);
	}

	public void load(InputHelper inputHelper, File file)
	{
		this.inputHelper=inputHelper;
		try
		{
			close();
			br=this.inputHelper.getBufferedReader(file);
		}
		catch(IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	public void load(File file, int bufferSize)
	{
		load(inputHelper==null ? InputHelper.get() : inputHelper, file, bufferSize);
	}

	public void load(InputHelper inputHelper, File file, int bufferSize)
	{
		this.inputHelper=inputHelper;
		try
		{
			close();
			br=this.inputHelper.getBufferedReader(file, bufferSize);
		}
		catch(IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	public void load(String fileNameString)
	{
		load(inputHelper==null ? InputHelper.get() : inputHelper, fileNameString);
	}

	public void load(InputHelper inputHelper, String fileNameString)
	{
		this.inputHelper=inputHelper;
		try
		{
			close();
			br=this.inputHelper.getBufferedReader(fileNameString);
		}
		catch(IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	public void load(String fileNameString, int bufferSize)
	{
		load(inputHelper==null ? InputHelper.get() : inputHelper, fileNameString, bufferSize);
	}

	public void load(InputHelper inputHelper, String fileNameString, int bufferSize)
	{
		this.inputHelper=inputHelper;
		try
		{
			close();
			br=this.inputHelper.getBufferedReader(fileNameString, bufferSize);
		}
		catch(IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	public <T> void load(Class<T> desClass, String fileNameString)
	{
		load(inputHelper==null ? InputHelper.get() : inputHelper, desClass, fileNameString);
	}

	public <T> void load(InputHelper inputHelper, Class<T> desClass, String fileNameString)
	{
		this.inputHelper=inputHelper;
		try
		{
			close();
			br=this.inputHelper.getBufferedReader(desClass, fileNameString);
		}
		catch(IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	public <T> void load(Class<T> desClass, String fileNameString, int bufferSize)
	{
		load(inputHelper==null ? InputHelper.get() : inputHelper, bufferSize);
	}

	public <T> void load(InputHelper inputHelper, Class<T> desClass, String fileNameString, int bufferSize)
	{
		this.inputHelper=inputHelper;
		try
		{
			close();
			br=this.inputHelper.getBufferedReader(desClass, fileNameString, bufferSize);
		}
		catch(IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	public void load(InputStream in)
	{
		load(inputHelper==null ? InputHelper.get() : inputHelper, in);
	}

	public void load(InputHelper inputHelper, InputStream in)
	{
		this.inputHelper=inputHelper;
		try
		{
			close();
			br=this.inputHelper.getBufferedReader(in);
		}
		catch(IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	public void load(InputStream in, int bufferSize)
	{
		load(inputHelper==null ? InputHelper.get() : inputHelper, in, bufferSize);
	}

	public void load(InputHelper inputHelper, InputStream in, int bufferSize)
	{
		this.inputHelper=inputHelper;
		try
		{
			close();
			br=this.inputHelper.getBufferedReader(in, bufferSize);
		}
		catch(IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	public void close() throws IOException
	{
		if(br!=null)
		{
			br.close();
			br=null;
		}
	}

	@Override
	public boolean hasNext()
	{
		if(line==null)
		{
			try
			{
				line=br.readLine();
				if(line!=null)
					return true;
				else
				{
					close();
					return false;
				}
			}
			catch(IOException e)
			{
				throw new UncheckedIOException(e);
			}
		}
		else
			return true;
	}

	@Override
	public String next()
	{
		if(line!=null || hasNext())
		{
			String ln=line;
			line=null;
			return ln;
		}
		else
			throw new NoSuchElementException();
	}

	public Stream<String> lines()
	{
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED|Spliterator.NONNULL), false);
	}
}
