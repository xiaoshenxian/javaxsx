package com.eroelf.javaxsx.util.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

/**
 * Provides APIs for URL connection.
 * 
 * @author weikun.zhong
 */
public final class RequestUrl
{
	public static final int DEFAULT_CONNECT_TIMEOUT=400;
	public static final int DEFAULT_READ_TIMEOUT=2000;

	public static InputStream sendGet(String url)
	{
		return sendGet(url, null);
	}

	public static InputStream sendGet(String url, Map<?, ?> queries)
	{
		return sendGet(url, UrlUtil.DEFAULT_ENC, queries);
	}

	public static InputStream sendGet(String url, String enc, Map<?, ?> queries)
	{
		return sendGet(url, enc, queries, null, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
	}

	public static InputStream sendGet(String url, int connectTimeout, int readTimeout)
	{
		return sendGet(url, UrlUtil.DEFAULT_ENC, null, null, connectTimeout, readTimeout);
	}

	public static InputStream sendGet(String url, Map<?, ?> queries, int connectTimeout, int readTimeout)
	{
		return sendGet(url, UrlUtil.DEFAULT_ENC, queries, null, connectTimeout, readTimeout);
	}

	public static InputStream sendGet(String url, String enc, Map<?, ?> queries, int connectTimeout, int readTimeout)
	{
		return sendGet(url, enc, queries, null, connectTimeout, readTimeout);
	}

	public static InputStream sendPost(String url)
	{
		return sendPost(url, null);
	}

	public static InputStream sendPost(String url, Map<?, ?> queries)
	{
		return sendPost(url, UrlUtil.DEFAULT_ENC, queries);
	}

	public static InputStream sendPost(String url, String enc, Map<?, ?> queries)
	{
		return sendPost(url, enc, queries, null, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
	}

	public static InputStream sendPost(String url, Map<?, ?> queries, int connectTimeout, int readTimeout)
	{
		return sendPost(url, UrlUtil.DEFAULT_ENC, queries, null, connectTimeout, readTimeout);
	}

	public static InputStream sendPost(String url, String enc, Map<?, ?> queries, int connectTimeout, int readTimeout)
	{
		return sendPost(url, enc, queries, null, connectTimeout, readTimeout);
	}

	public static InputStream sendGet(String url, String enc, Map<?, ?> queries, Map<?, ?> requestProperties, int connectTimeout, int readTimeout)
	{
		try
		{
			URLConnection connection=getConnection(url, enc, queries, false, requestProperties, connectTimeout, readTimeout);
			return connect(connection);
		}
		catch(URISyntaxException e)
		{
			throw new IllegalArgumentException(e);
		}
		catch(IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	public static InputStream sendPost(String url, String enc, Map<?, ?> queries, Map<?, ?> requestProperties, int connectTimeout, int readTimeout)
	{
		try
		{
			URLConnection connection=getConnection(url, enc, queries, true, requestProperties, connectTimeout, readTimeout);
			return connect(connection);
		}
		catch(URISyntaxException e)
		{
			throw new IllegalArgumentException(e);
		}
		catch(IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	public static String sendGetAskString(String url)
	{
		return sendGetAskString(url, null);
	}

	public static String sendGetAskString(String url, Map<?, ?> queries)
	{
		return sendGetAskString(url, UrlUtil.DEFAULT_ENC, queries);
	}

	public static String sendGetAskString(String url, String enc, Map<?, ?> queries)
	{
		return sendGetAskString(url, enc, queries, null, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
	}

	public static String sendGetAskString(String url, int connectTimeout, int readTimeout)
	{
		return sendGetAskString(url, null, null, null, connectTimeout, readTimeout);
	}

	public static String sendGetAskString(String url, Map<?, ?> queries, int connectTimeout, int readTimeout)
	{
		return sendGetAskString(url, UrlUtil.DEFAULT_ENC, queries, null, connectTimeout, readTimeout);
	}

	public static String sendGetAskString(String url, String enc, Map<?, ?> queries, int connectTimeout, int readTimeout)
	{
		return sendGetAskString(url, enc, queries, null, connectTimeout, readTimeout);
	}

	public static String sendGetAskString(String url, String enc, Map<?, ?> queries, Map<?, ?> requestProperties, int connectTimeout, int readTimeout)
	{
		return joinResponseList(sendGetAskList(url, enc, queries, requestProperties, connectTimeout, readTimeout));
	}

	public static String sendPostAskString(String url)
	{
		return sendPostAskString(url, null);
	}

	public static String sendPostAskString(String url, Map<?, ?> queries)
	{
		return sendPostAskString(url, UrlUtil.DEFAULT_ENC, queries);
	}

	public static String sendPostAskString(String url, String enc, Map<?, ?> queries)
	{
		return sendPostAskString(url, enc, queries, null, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
	}

	public static String sendPostAskString(String url, Map<?, ?> queries, int connectTimeout, int readTimeout)
	{
		return sendPostAskString(url, UrlUtil.DEFAULT_ENC, queries, null, connectTimeout, readTimeout);
	}

	public static String sendPostAskString(String url, String enc, Map<?, ?> queries, int connectTimeout, int readTimeout)
	{
		return sendPostAskString(url, enc, queries, null, connectTimeout, readTimeout);
	}

	public static String sendPostAskString(String url, String enc, Map<?, ?> queries, Map<?, ?> requestProperties, int connectTimeout, int readTimeout)
	{
		return joinResponseList(sendPostAskList(url, enc, queries, requestProperties, connectTimeout, readTimeout));
	}

	public static List<String> sendGetAskList(String url)
	{
		return sendGetAskList(url, null);
	}

	public static List<String> sendGetAskList(String url, Map<?, ?> queries)
	{
		return sendGetAskList(url, UrlUtil.DEFAULT_ENC, queries);
	}

	public static List<String> sendGetAskList(String url, String enc, Map<?, ?> queries)
	{
		return sendGetAskList(url, enc, queries, null, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
	}

	public static List<String> sendGetAskList(String url, int connectTimeout, int readTimeout)
	{
		return sendGetAskList(url, null, null, null, connectTimeout, readTimeout);
	}

	public static List<String> sendGetAskList(String url, Map<?, ?> queries, int connectTimeout, int readTimeout)
	{
		return sendGetAskList(url, UrlUtil.DEFAULT_ENC, queries, null, connectTimeout, readTimeout);
	}

	public static List<String> sendGetAskList(String url, String enc, Map<?, ?> queries, int connectTimeout, int readTimeout)
	{
		return sendGetAskList(url, enc, queries, null, connectTimeout, readTimeout);
	}

	public static List<String> sendGetAskList(String url, String enc, Map<?, ?> queries, Map<?, ?> requestProperties, int connectTimeout, int readTimeout)
	{
		try
		{
			URLConnection connection=getConnection(url, enc, queries, false, requestProperties, connectTimeout, readTimeout);
			return connectAskList(connection);
		}
		catch(URISyntaxException e)
		{
			throw new IllegalArgumentException(e);
		}
		catch(IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	public static List<String> sendPostAskList(String url)
	{
		return sendPostAskList(url, null);
	}

	public static List<String> sendPostAskList(String url, Map<?, ?> queries)
	{
		return sendPostAskList(url, UrlUtil.DEFAULT_ENC, queries);
	}

	public static List<String> sendPostAskList(String url, String enc, Map<?, ?> queries)
	{
		return sendPostAskList(url, enc, queries, null, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
	}

	public static List<String> sendPostAskList(String url, Map<?, ?> queries, int connectTimeout, int readTimeout)
	{
		return sendPostAskList(url, UrlUtil.DEFAULT_ENC, queries, null, connectTimeout, readTimeout);
	}

	public static List<String> sendPostAskList(String url, String enc, Map<?, ?> queries, int connectTimeout, int readTimeout)
	{
		return sendPostAskList(url, enc, queries, null, connectTimeout, readTimeout);
	}

	public static List<String> sendPostAskList(String url, String enc, Map<?, ?> queries, Map<?, ?> requestProperties, int connectTimeout, int readTimeout)
	{
		try
		{
			URLConnection connection=getConnection(url, enc, queries, true, requestProperties, connectTimeout, readTimeout);
			return connectAskList(connection);
		}
		catch(URISyntaxException e)
		{
			throw new IllegalArgumentException(e);
		}
		catch(IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	public static URLConnection getConnection(String url, String enc, Map<?, ?> queries, boolean usePost, Map<?, ?> requestProperties, int connectTimeout, int readTimeout) throws IOException, URISyntaxException
	{
		if(!usePost && queries!=null)
		{
			url=UrlUtil.composeUrl(enc, url, queries);
		}
		URLConnection connection=new URI(url).toURL().openConnection();
		if(requestProperties!=null)
		{
			for(Entry<?, ?> entry : requestProperties.entrySet())
			{
				connection.setRequestProperty(entry.getKey().toString(), entry.getValue().toString());
			}
		}
		connection.setConnectTimeout(connectTimeout);
		connection.setReadTimeout(readTimeout);
		if(usePost)
		{
			//connection.setDoInput(true);
			connection.setDoOutput(true);
			if(queries!=null)
			{
				PrintWriter pw=new PrintWriter(connection.getOutputStream());
				pw.print(UrlUtil.appendQueries(enc, new StringBuilder(), queries).toString());
				pw.flush();
				pw.close();
			}
		}
		return connection;
	}

	public static InputStream connect(URLConnection connection) throws IOException
	{
		connection.connect();
		String type=String.valueOf(connection.getContentEncoding()).trim().toLowerCase(Locale.ENGLISH);
		if(type.equals("gzip"))
			return new GZIPInputStream(connection.getInputStream());
		else
			return connection.getInputStream();
	}

	public static List<String> connectAskList(URLConnection connection) throws IOException
	{
		List<String> returnedList=new ArrayList<String>();
		BufferedReader br=new BufferedReader(new InputStreamReader(connect(connection)));
		String line=null;
		while((line=br.readLine())!=null)
		{
			returnedList.add(line);
		}
		br.close();
		return returnedList;
	}

	public static String joinResponseList(List<String> list)
	{
		if(list==null)
			return null;
		else if(list.isEmpty())
			return "";
		else if(list.size()==1)
			return list.get(0);
		else
		{
			StringBuilder stringBuilder=new StringBuilder();
			for(String s : list)
			{
				stringBuilder.append(s);
			}
			return stringBuilder.toString();
		}
	}

	private RequestUrl()
	{}
}
