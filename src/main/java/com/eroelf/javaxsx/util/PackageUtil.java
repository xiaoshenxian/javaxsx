package com.eroelf.javaxsx.util;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Referred to an open source code on csdn.
 * Modified some detail for compatibility.
 * 
 * @author weikun.zhong
 * @see <a href="http://blog.csdn.net/wangpeng047/article/details/8206427">http://blog.csdn.net/wangpeng047/article/details/8206427</a>
 */
public class PackageUtil
{
	public static Collection<String> getClassNames(Collection<String> classNamesReceiver, String packageName)
	{
		return getClassNames(classNamesReceiver, packageName, -1);
	}

	public static Collection<String> getClassNames(Collection<String> classNamesReceiver, String packageName, int level)
	{
		ClassLoader loader=Thread.currentThread().getContextClassLoader();
		String packagePath=packageName.replace(".", "/");
		URL url=loader.getResource(packagePath);
		if(url!=null)
		{
			String protocol=url.getProtocol();
			if(protocol.equals("file"))
				getClassNamesByFile(classNamesReceiver, url.getPath(), null, level);
			else if(protocol.equals("jar"))
				getClassNamesByJar(classNamesReceiver, url.getPath(), level);
			else
				throw new RuntimeException("Unexpected url protocol '"+protocol+"' for package '"+packageName+"'!");
		}
		else
			getClassNamesByJars(classNamesReceiver, ((URLClassLoader)loader).getURLs(), packagePath, level);
		return classNamesReceiver;
	}

	private static Collection<String> getClassNamesByFile(Collection<String> classNamesReceiver, String dirPath, List<String> className, int level)
	{
		for(File file : FileSysUtil.getFiles(dirPath, true, false, level))
		{
			String filePath=file.getPath();
			if(filePath.endsWith(".class"))
				classNamesReceiver.add(filePath.substring(filePath.indexOf("\\classes")+9, filePath.lastIndexOf(".")).replaceAll("\\\\|/", "."));
		}
		return classNamesReceiver;
	}

	private static Collection<String> getClassNamesByJar(Collection<String> classNamesReceiver, String jarPath, int level)
	{
		String[] jarInfo=jarPath.split("!");
		String jarFilePath=jarInfo[0].substring(jarInfo[0].indexOf("/"));
		String packagePath=jarInfo[1].substring(1);
		try(JarFile jarFile=new JarFile(jarFilePath);)
		{
			Enumeration<JarEntry> entrys=jarFile.entries();
			while(entrys.hasMoreElements())
			{
				JarEntry jarEntry=entrys.nextElement();
				String entryName=jarEntry.getName();
				if(entryName.endsWith(".class"))
				{
					if(entryName.startsWith(packagePath))
					{
						String[] subPackage=entryName.substring(packagePath.length()+1).split("/");
						if(subPackage.length<=level+1 || level<0)
							classNamesReceiver.add(entryName.substring(0, entryName.lastIndexOf(".")).replace("/", "."));
					}
				}
			}
		}
		catch(IOException e)
		{
			throw new UncheckedIOException(e);
		}
		return classNamesReceiver;
	}

	private static void getClassNamesByJars(Collection<String> classNamesReceiver, URL[] urls, String packagePath, int level)
	{
		if(urls!=null)
		{
			for(int i=0; i<urls.length; i++)
			{
				URL url=urls[i];
				String urlPath=url.getPath();
				if(!urlPath.endsWith("classes/"))
					getClassNamesByJar(classNamesReceiver, urlPath+"!/"+packagePath, level);
			}
		}
	}
}
