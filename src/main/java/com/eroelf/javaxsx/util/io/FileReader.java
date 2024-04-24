package com.eroelf.javaxsx.util.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.stream.Stream;

/**
 * A convenient class for processing files.
 * 
 * @author weikun.zhong
 */
public class FileReader
{
	private InputHelper inputHelper;
	private BufferedReader br=null;

	public FileReader(boolean loadSystemIn)
	{
		if(loadSystemIn)
			load(InputHelper.get());
	}

	public FileReader(boolean loadSystemIn, InputHelper inputHelper)
	{
		if(loadSystemIn)
			load(inputHelper);
		else
			this.inputHelper=inputHelper;
	}

	public FileReader(int bufferSize)
	{
		load(InputHelper.get(), bufferSize);
	}

	public FileReader(InputHelper inputHelper, int bufferSize)
	{
		load(inputHelper, bufferSize);
	}

	public FileReader(File file)
	{
		load(InputHelper.get(), file);
	}

	public FileReader(InputHelper inputHelper, File file)
	{
		load(inputHelper, file);
	}

	public FileReader(File file, int bufferSize)
	{
		load(InputHelper.get(), file, bufferSize);
	}

	public FileReader(InputHelper inputHelper, File file, int bufferSize)
	{
		load(inputHelper, file, bufferSize);
	}

	public FileReader(String fileNameString)
	{
		load(InputHelper.get(), fileNameString);
	}

	public FileReader(InputHelper inputHelper, String fileNameString)
	{
		load(inputHelper, fileNameString);
	}

	public FileReader(String fileNameString, int bufferSize)
	{
		load(InputHelper.get(), fileNameString, bufferSize);
	}

	public FileReader(InputHelper inputHelper, String fileNameString, int bufferSize)
	{
		load(inputHelper, fileNameString, bufferSize);
	}

	public <T> FileReader(Class<T> desClass, String fileNameString)
	{
		load(InputHelper.get(), desClass, fileNameString);
	}

	public <T> FileReader(InputHelper inputHelper, Class<T> desClass, String fileNameString)
	{
		load(inputHelper, desClass, fileNameString);
	}

	public <T> FileReader(Class<T> desClass, String fileNameString, int bufferSize)
	{
		load(InputHelper.get(), desClass, fileNameString, bufferSize);
	}

	public <T> FileReader(InputHelper inputHelper, Class<T> desClass, String fileNameString, int bufferSize)
	{
		load(inputHelper, desClass, fileNameString, bufferSize);
	}

	public FileReader(InputStream in)
	{
		load(InputHelper.get(), in);
	}

	public FileReader(InputHelper inputHelper, InputStream in)
	{
		load(inputHelper, in);
	}

	public FileReader(InputStream in, int bufferSize)
	{
		load(InputHelper.get(), in, bufferSize);
	}

	public FileReader(InputHelper inputHelper, InputStream in, int bufferSize)
	{
		load(inputHelper, in, bufferSize);
	}

	public void load()
	{
		load(InputHelper.get());
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
		load(InputHelper.get(), bufferSize);
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
		load(InputHelper.get(), file);
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
		load(InputHelper.get(), file, bufferSize);
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
		load(InputHelper.get(), fileNameString);
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
		load(InputHelper.get(), fileNameString, bufferSize);
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
		load(InputHelper.get(), desClass, fileNameString);
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
		load(InputHelper.get(), desClass, fileNameString, bufferSize);
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
		load(InputHelper.get(), in);
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
		load(InputHelper.get(), in, bufferSize);
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

	public String readLine()
	{
		String line=null;
		try
		{
			line=br.readLine();
			if(line==null)
				close();
		}
		catch(IOException e)
		{
			throw new UncheckedIOException(e);
		}
		return line;
	}

	public String[] readLineItems(String regex)
	{
		String line=readLine();
		if(line!=null)
			return line.split(regex);
		else
			return null;
	}

	public void close() throws IOException
	{
		if(br!=null)
		{
			br.close();
			br=null;
		}
	}

	public Stream<String> lines()
	{
		return br!=null ? br.lines().onClose(() -> {
			try
			{
				close();
			}
			catch(IOException e)
			{
				throw new UncheckedIOException(e);
			}
		}) : null;
	}
}
