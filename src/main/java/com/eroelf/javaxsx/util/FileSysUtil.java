package com.eroelf.javaxsx.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Some APIs related to file system operations.
 * 
 * @author weikun.zhong
 */
public class FileSysUtil
{
	public static List<File> getFiles(String path, boolean needFile, boolean needDir)
	{
		return getFiles(path, needFile, needDir, 0);
	}

	public static List<File> getFiles(File root, boolean needFile, boolean needDir)
	{
		return getFiles(root, needFile, needDir, 0);
	}

	public static List<File> getFiles(String path, boolean needFile, boolean needDir, int level)
	{
		return getFiles(path, new String[0], new String[0], needFile, needDir, level);
	}

	public static List<File> getFiles(File root, boolean needFile, boolean needDir, int level)
	{
		return getFiles(root, new String[0], new String[0], needFile, needDir, level);
	}

	public static List<File> getFiles(String path, String[] acceptRegex, String[] refuseRegex, boolean needFile, boolean needDir)
	{
		return getFiles(path, acceptRegex, refuseRegex, needFile, needDir, 0);
	}

	public static List<File> getFiles(File root, String[] acceptRegex, String[] refuseRegex, boolean needFile, boolean needDir)
	{
		return getFiles(root, acceptRegex, refuseRegex, needFile, needDir, 0);
	}

	public static List<File> getFiles(String path, String[] acceptRegex, String[] refuseRegex, boolean needFile, boolean needDir, int level)
	{
		List<File> des=new ArrayList<>();
		return getFiles(path, des, acceptRegex, refuseRegex, needFile, needDir, level);
	}

	public static List<File> getFiles(File root, String[] acceptRegex, String[] refuseRegex, boolean needFile, boolean needDir, int level)
	{
		List<File> des=new ArrayList<>();
		return getFiles(root, des, acceptRegex, refuseRegex, needFile, needDir, level);
	}

	public static List<File> getFiles(String path, List<File> des, String[] acceptRegex, String[] refuseRegex, boolean needFile, boolean needDir, int level)
	{
		return getFiles(new File(path), des, acceptRegex, refuseRegex, needFile, needDir, level);
	}

	public static List<File> getFiles(File root, List<File> des, String[] acceptRegex, String[] refuseRegex, boolean needFile, boolean needDir, int level)
	{
		Pattern[] acceptPattern=new Pattern[acceptRegex.length];
		for(int i=0; i<acceptPattern.length; i++)
		{
			acceptPattern[i]=Pattern.compile(acceptRegex[i]);
		}
		Pattern[] refusePattern=new Pattern[refuseRegex.length];
		for(int i=0; i<refusePattern.length; i++)
		{
			refusePattern[i]=Pattern.compile(refuseRegex[i]);
		}
		return getFiles(root, des, acceptPattern, refusePattern, needFile, needDir, level);
	}

	public static List<File> getFiles(String path, List<File> des, Pattern[] acceptPattern, Pattern[] refusePattern, boolean needFile, boolean needDir, int level)
	{
		return getFiles(new File(path), des, acceptPattern, refusePattern, needFile, needDir, level);
	}

	/**
	 * This method traverses a directory defined by {@code root} and returns all files in that directory.
	 * The sub-directory levels for traversing is defined by {@code level}.
	 * 
	 * @param root a {@link File} object defining the root directory for traversal.
	 * @param des the destination to receive the files under the {@code root} directory.
	 * @param acceptPattern a filter that only those files whose names match any of the patterns in this array will be accept.
	 * @param refusePattern a filter that any files whose names match any of the patterns in this array will be reject.
	 * @param needFile indicates whether retrieve files under the {@code root} directory.
	 * @param needDir indicates whether retrieve directories under the {@code root} directory.
	 * @param level indicates how many levels of sub-directories will be traversed. for example, 0 for files and folders only in the root directory, 1 for files and folders in the root directory as well as in all the folders in the root directory, and negative value for traversing all sub-directories. 
	 * @return the exact {@code des} object with all files and directories found added.
	 */
	public static List<File> getFiles(File root, List<File> des, Pattern[] acceptPattern, Pattern[] refusePattern, boolean needFile, boolean needDir, int level)
	{
		File[] files=root.listFiles();
		if(files!=null)
		{
			for(File file : files)
			{
				boolean isDir=file.isDirectory();
				boolean retainFile=needFile && file.isFile();
				if(retainFile || isDir)
				{
					String name=file.getName();
					boolean accept=true;
					if(acceptPattern.length>0)
					{
						accept=false;
						for(Pattern p : acceptPattern)
						{
							if(p.matcher(name).find())
							{
								accept=true;
								break;
							}
						}
					}
					if(accept)
					{
						for(Pattern p : refusePattern)
						{
							if(p.matcher(name).find())
							{
								accept=false;
								break;
							}
						}
						if(accept)
						{
							if(retainFile || (needDir && isDir))
								des.add(file);
							if(file.isDirectory())
							{
								if(level>0)
									getFiles(file.getAbsolutePath(), des, acceptPattern, refusePattern, needFile, needDir, level-1);
								else if(level<0)
									getFiles(file.getAbsolutePath(), des, acceptPattern, refusePattern, needFile, needDir, level);
							}
						}
					}
				}
			}
		}
		return des;
	}

	private FileSysUtil()
	{}

	@Override
	public FileSysUtil clone()
	{
		throw new UnsupportedOperationException("This method is not allowed.");
	}
}
