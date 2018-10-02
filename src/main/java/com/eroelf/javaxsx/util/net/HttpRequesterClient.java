package com.eroelf.javaxsx.util.net;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/**
 * An HTTP requester connection pool client implemented by using the {@link CloseableHttpClient}.
 * 
 * @author weikun.zhong
 */
public class HttpRequesterClient implements Closeable
{
	private final CloseableHttpClient client;

	public HttpRequesterClient(int maxConnTotal, int maxConnPerRoute, int defaultConnectionRequestTimeout, int defaultConnectTimeout, int defaultSocketTimeout)
	{
		this(maxConnTotal, maxConnPerRoute, defaultConnectionRequestTimeout, defaultConnectTimeout, defaultSocketTimeout, -1);
	}

	public HttpRequesterClient(int maxConnTotal, int maxConnPerRoute, int defaultConnectionRequestTimeout, int defaultConnectTimeout, int defaultSocketTimeout, long keepAliveDuration)
	{
		HttpClientBuilder builder=HttpClientBuilder.create().setMaxConnTotal(maxConnTotal).setMaxConnPerRoute(maxConnPerRoute);
		builder.setDefaultRequestConfig(RequestConfig.custom().setConnectionRequestTimeout(defaultConnectionRequestTimeout).setConnectTimeout(defaultConnectTimeout).setSocketTimeout(defaultSocketTimeout).build());
		if(keepAliveDuration>0)
		{
			builder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy() {
				@Override
				public long getKeepAliveDuration(final HttpResponse response, final HttpContext context)
				{
					long duration=super.getKeepAliveDuration(response, context);
					return duration>0 ? duration : keepAliveDuration;
				}
			});
		}
		client=builder.build();
	}

	public String sendGet(String uri, Map<String, Object> params) throws URISyntaxException, ClientProtocolException, IOException
	{
		HttpGet httpGet=new HttpGet(getUri(uri, params));
		return client.execute(httpGet, new ResponseHandler<String>() {
			@Override
			public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException
			{
				HttpEntity entity=response.getEntity();
				Header encodingHeader=entity.getContentEncoding();
				return EntityUtils.toString(entity, encodingHeader!=null ? encodingHeader.getValue() : "utf-8");
			}
		});
	}

	public String sendPost(String uri, Map<String, Object> params) throws URISyntaxException, ClientProtocolException, IOException
	{
		return sendPost(uri, params, (HttpEntity)null);
	}

	public String sendPost(String uri, Map<String, Object> params, HttpEntity httpEntity) throws URISyntaxException, ClientProtocolException, IOException
	{
		HttpPost httpPost=new HttpPost(getUri(uri, params));
		if(httpEntity!=null)
			httpPost.setEntity(httpEntity);
		return client.execute(httpPost, new ResponseHandler<String>() {
			@Override
			public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException
			{
				HttpEntity entity=response.getEntity();
				Header encodingHeader=entity.getContentEncoding();
				return EntityUtils.toString(entity, encodingHeader!=null ? encodingHeader.getValue() : "utf-8");
			}
		});
	}

	public String sendGet(String uri, Map<String, Object> params, Map<String, Object> headers) throws URISyntaxException, ClientProtocolException, IOException
	{
		HttpGet httpGet=new HttpGet(getUri(uri, params));
		configHeaders(httpGet, headers);
		return client.execute(httpGet, new ResponseHandler<String>() {
			@Override
			public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException
			{
				HttpEntity entity=response.getEntity();
				Header encodingHeader=entity.getContentEncoding();
				return EntityUtils.toString(entity, encodingHeader!=null ? encodingHeader.getValue() : "utf-8");
			}
		});
	}

	public String sendPost(String uri, Map<String, Object> params, Map<String, Object> headers) throws URISyntaxException, ClientProtocolException, IOException
	{
		return sendPost(uri, params, null, headers);
	}

	public String sendPost(String uri, Map<String, Object> params, HttpEntity httpEntity, Map<String, Object> headers) throws URISyntaxException, ClientProtocolException, IOException
	{
		HttpPost httpPost=new HttpPost(getUri(uri, params));
		configHeaders(httpPost, headers);
		if(httpEntity!=null)
			httpPost.setEntity(httpEntity);
		return client.execute(httpPost, new ResponseHandler<String>() {
			@Override
			public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException
			{
				HttpEntity entity=response.getEntity();
				Header encodingHeader=entity.getContentEncoding();
				return EntityUtils.toString(entity, encodingHeader!=null ? encodingHeader.getValue() : "utf-8");
			}
		});
	}

	public <T> T sendGet(String uri, Map<String, Object> params, ResponseHandler<T> responseHandler, Map<String, Object> headers, int connectionRequestTimeout, int connectTimeout, int socketTimeout) throws URISyntaxException, ClientProtocolException, IOException
	{
		HttpGet httpGet=new HttpGet(getUri(uri, params));
		configRequest(httpGet, headers, connectionRequestTimeout, connectTimeout, socketTimeout);
		return client.execute(httpGet, responseHandler);
	}

	public <T> T sendPost(String uri, Map<String, Object> params, HttpEntity httpEntity, ResponseHandler<T> responseHandler, Map<String, Object> headers, int connectionRequestTimeout, int connectTimeout, int socketTimeout) throws URISyntaxException, ClientProtocolException, IOException
	{
		HttpPost httpPost=new HttpPost(getUri(uri, params));
		configRequest(httpPost, headers, connectionRequestTimeout, connectTimeout, socketTimeout);
		if(httpEntity!=null)
			httpPost.setEntity(httpEntity);
		return client.execute(httpPost, responseHandler);
	}

	public static URI getUri(String uri, Map<String, Object> params) throws URISyntaxException
	{
		URIBuilder builder=new URIBuilder(uri);
		if(params!=null)
		{
			for(Entry<String, Object> entry : params.entrySet())
			{
				builder.addParameter(entry.getKey(), entry.getValue()!=null ? entry.getValue().toString() : "");
			}
		}
		return builder.build();
	}

	public static void configRequest(HttpRequestBase request, Map<String, Object> headers, int connectionRequestTimeout, int connectTimeout, int socketTimeout)
	{
		RequestConfig requestConfig=RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout).setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
		request.setConfig(requestConfig);
		configHeaders(request, headers);
	}

	public static void configHeaders(HttpRequestBase request, Map<String, Object> headers)
	{
		if(headers!=null)
		{
			for(Entry<String, Object> entry : headers.entrySet())
			{
				request.addHeader(entry.getKey(), entry.getValue()!=null ? entry.getValue().toString() : "");
			}
		}
	}

	@Override
	public void close() throws IOException
	{
		client.close();
	}
}
