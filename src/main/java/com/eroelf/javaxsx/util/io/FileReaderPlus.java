package com.eroelf.javaxsx.util.io;

import java.io.File;
import java.io.InputStream;
import java.util.function.Function;

/**
 * A more convenient class than {@link FileReader} for processing files line by line.
 * 
 * @author weikun.zhong
 */
public final class FileReaderPlus extends FileReader
{
	public static interface LineProcessor extends Function<String, Boolean>
	{
		public boolean process(String line);

		@Override
		default Boolean apply(String line)
		{
			return process(line);
		}

		default public void begin()
		{}

		default public void end()
		{}
	}

	public static void readAFile(LineProcessor lineProcessor)
	{
		FileReaderPlus frp=new FileReaderPlus();
		frp.read(lineProcessor);
	}

	public static void readAFile(File file, LineProcessor lineProcessor)
	{
		FileReaderPlus frp=new FileReaderPlus(file);
		frp.read(lineProcessor);
	}

	public static void readAFile(File file, int bufferSize, LineProcessor lineProcessor)
	{
		FileReaderPlus frp=new FileReaderPlus(file, bufferSize);
		frp.read(lineProcessor);
	}

	public static void readAFile(String fileNameString, LineProcessor lineProcessor)
	{
		FileReaderPlus frp=new FileReaderPlus(fileNameString);
		frp.read(lineProcessor);
	}

	public static void readAFile(String fileNameString, int bufferSize, LineProcessor lineProcessor)
	{
		FileReaderPlus frp=new FileReaderPlus(fileNameString, bufferSize);
		frp.read(lineProcessor);
	}

	public static <T> void readAFile(Class<T> desClass, String fileNameString, LineProcessor lineProcessor)
	{
		FileReaderPlus frp=new FileReaderPlus(desClass, fileNameString);
		frp.read(lineProcessor);
	}

	public static <T> void readAFile(Class<T> desClass, String fileNameString, int bufferSize, LineProcessor lineProcessor)
	{
		FileReaderPlus frp=new FileReaderPlus(desClass, fileNameString, bufferSize);
		frp.read(lineProcessor);
	}

	public static void readAFile(InputStream in, LineProcessor lineProcessor)
	{
		FileReaderPlus frp=new FileReaderPlus(in);
		frp.read(lineProcessor);
	}

	public static void readAFile(InputStream in, int bufferSize, LineProcessor lineProcessor)
	{
		FileReaderPlus frp=new FileReaderPlus(in, bufferSize);
		frp.read(lineProcessor);
	}

	private FileReaderPlus()
	{
		super(true);
	}

	private FileReaderPlus(File file)
	{
		super(file);
	}

	private FileReaderPlus(File file, int bufferSize)
	{
		super(file, bufferSize);
	}

	private FileReaderPlus(String fileNameString)
	{
		super(fileNameString);
	}

	private FileReaderPlus(String fileNameString, int bufferSize)
	{
		super(fileNameString, bufferSize);
	}

	private <T> FileReaderPlus(Class<T> desClass, String fileNameString)
	{
		super(desClass, fileNameString);
	}

	private <T> FileReaderPlus(Class<T> desClass, String fileNameString, int bufferSize)
	{
		super(desClass, fileNameString, bufferSize);
	}

	private FileReaderPlus(InputStream in)
	{
		super(in);
	}

	private FileReaderPlus(InputStream in, int bufferSize)
	{
		super(in, bufferSize);
	}

	private void read(LineProcessor lineProcessor)
	{
		lineProcessor.begin();
		String line;
		while((line=readLine())!=null)
		{
			if(!lineProcessor.process(line))
				break;
		}
		lineProcessor.end();
	}
}
