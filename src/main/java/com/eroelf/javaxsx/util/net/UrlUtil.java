package com.eroelf.javaxsx.util.net;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.eroelf.javaxsx.util.Strings;

/**
 * Provide APIs for processing url strings.
 * 
 * @author weikun.zhong
 */
public class UrlUtil
{
	public static final String DEFAULT_ENC="UTF-8";

	public static class UrlComponent
	{
		public String domain;
		public Map<String, String> queries=new HashMap<>();
		public String hash;

		@Override
		public String toString()
		{
			return UrlUtil.composeUrl(this);
		}
	}

	public static String composeUrl(String domain, Map<?, ?> queries)
	{
		return composeUrl(domain, queries, null);
	}

	public static String composeUrl(String domain, Map<?, ?> queries, String hash)
	{
		try
		{
			return composeUrl(DEFAULT_ENC, domain, queries, hash);
		}
		catch(UnsupportedEncodingException e)
		{
			throw new Error(e);
		}
	}

	public static String composeUrl(String enc, String domain, Map<?, ?> queries) throws UnsupportedEncodingException
	{
		return composeUrl(enc, domain, queries, null);
	}

	public static String composeUrl(UrlComponent urlComponent)
	{
		return composeUrl(urlComponent.domain, urlComponent.queries, urlComponent.hash);
	}

	public static String composeUrl(String enc, UrlComponent urlComponent) throws UnsupportedEncodingException
	{
		return composeUrl(enc, urlComponent.domain, urlComponent.queries, urlComponent.hash);
	}

	public static String composeUrl(String enc, String domain, Map<?, ?> queries, String hash) throws UnsupportedEncodingException
	{
		StringBuilder urlStr=new StringBuilder();
		urlStr.append(domain);
		if(!queries.isEmpty())
		{
			if(domain.length()==0 || domain.charAt(domain.length()-1)!='?')
				urlStr.append('?');
			urlStr=appendQueries(enc, urlStr, queries);
		}
		if(Strings.isValid(hash))
			urlStr.append('#').append(hash);
		return urlStr.toString();
	}

	public static StringBuilder appendQueries(StringBuilder stringBuilder, Map<?, ?> queries)
	{
		try
		{
			stringBuilder=appendQueries(UrlUtil.DEFAULT_ENC, stringBuilder, queries);
		}
		catch(UnsupportedEncodingException e)
		{
			throw new Error(e);
		}
		return stringBuilder;
	}

	public static StringBuilder appendQueries(String enc, StringBuilder stringBuilder, Map<?, ?> queries) throws UnsupportedEncodingException
	{
		for (Entry<?, ?> entry : queries.entrySet())
		{
			stringBuilder.append(URLEncoder.encode(entry.getKey().toString(), enc)).append("=").append(URLEncoder.encode(entry.getValue().toString(), enc)).append("&");
		}
		return stringBuilder;
	}

	public static UrlComponent parseUrl(String url)
	{
		try
		{
			return parseUrl(url, DEFAULT_ENC);
		}
		catch(UnsupportedEncodingException e)
		{
			throw new Error(e);
		}
	}

	public static UrlComponent parseUrl(String url, String enc) throws UnsupportedEncodingException
	{
		UrlComponent res=new UrlComponent();
		if(url!=null)
		{
			int start=url.indexOf('?');
			res.domain=start>=0 ? url.substring(0, start) : "";
			int end=url.indexOf('#');
			res.hash=end>=0 ? url.substring(end+1) : "";
			String[] items=url.substring(start+1, end>=0 ? end : url.length()).split("&");
			for(String item : items)
			{
				int idx=item.indexOf("=");
				if(idx>=0)
					res.queries.put(URLDecoder.decode(item.substring(0, idx), enc), URLDecoder.decode(item.substring(idx+1), enc));
			}
		}
		return res;
	}

	public static String replaceUrlParam(String url, String key, String valueForReplacing)
	{
		return replaceUrlParam(url, key, valueForReplacing, "\\w+");
	}

	public static String replaceUrlParam(String url, String key, String valueForReplacing, String originalValuePattern)
	{
		return replaceUrlParam(url, key.replace("(", "\\(").replace(")", "\\)"), valueForReplacing, originalValuePattern, 0);
	}

	public static String replaceUrlParam(String url, String keyPattern, String valueForReplacing, String originalValuePattern, int kegGroupCount)
	{
		int groupNum=12+kegGroupCount;
		String regex="((\\?)|(%3[Ff])|(\\\\[Uu]003[Ff])|(&)|(%26)|(\\\\[Uu]0026))"+keyPattern+"((=)|(%3[Dd])|(\\\\[Uu]003[Dd]))("+originalValuePattern+")(($)|(&)|(%26)|(\\\\[Uu]0026))";
		Matcher matcher=Pattern.compile(regex).matcher(url);
		if(matcher.find() && matcher.group(groupNum)!=null)
			return url.substring(0, matcher.start(groupNum))+valueForReplacing+url.substring(matcher.end(groupNum));
		else
			return null;
	}

	private UrlUtil()
	{}

	@Override
	public UrlUtil clone()
	{
		throw new UnsupportedOperationException("This method is not allowed.");
	}
}
