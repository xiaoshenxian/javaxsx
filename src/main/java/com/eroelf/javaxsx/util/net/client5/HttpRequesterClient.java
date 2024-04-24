package com.eroelf.javaxsx.util.net.client5;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

/**
 * An HTTP requester connection pool client implemented by using the {@link CloseableHttpClient}.
 * 
 * @author weikun.zhong
 */
public class HttpRequesterClient implements Closeable
{
	public static PoolingHttpClientConnectionManagerBuilder fastConnectionManagerBuilder(int maxConnTotal, int maxConnPerRoute, int defaultSocketTimeout, int defaultConnectTimeout)
	{
		return fastConnectionManagerBuilder(maxConnTotal, maxConnPerRoute, defaultConnectTimeout, defaultSocketTimeout, -1, null);
	}

	public static PoolingHttpClientConnectionManagerBuilder fastConnectionManagerBuilder(int maxConnTotal, int maxConnPerRoute, int defaultSocketTimeout, int defaultConnectTimeout, long keepAliveDuration)
	{
		return fastConnectionManagerBuilder(maxConnTotal, maxConnPerRoute, defaultConnectTimeout, defaultSocketTimeout, keepAliveDuration, null);
	}

	public static PoolingHttpClientConnectionManagerBuilder fastConnectionManagerBuilder(int maxConnTotal, int maxConnPerRoute, int defaultSocketTimeout, int defaultConnectTimeout, long keepAliveDuration, SSLContext sslContext)
	{
		SocketConfig.Builder socketConfigBuilder=SocketConfig.custom();
		if(defaultSocketTimeout>0)
			socketConfigBuilder.setSoTimeout(Timeout.ofMilliseconds(defaultSocketTimeout));

		ConnectionConfig.Builder connectionConfigBuilder=ConnectionConfig.custom();
		if(defaultConnectTimeout>0)
			connectionConfigBuilder.setConnectTimeout(Timeout.ofMilliseconds(defaultConnectTimeout));
		if(keepAliveDuration>=0)
			connectionConfigBuilder.setTimeToLive(TimeValue.ofMilliseconds(keepAliveDuration));

		PoolingHttpClientConnectionManagerBuilder builder=PoolingHttpClientConnectionManagerBuilder.create()
				.setMaxConnTotal(maxConnTotal)
				.setMaxConnPerRoute(maxConnPerRoute)
				.setDefaultSocketConfig(socketConfigBuilder.build())
				.setDefaultConnectionConfig(connectionConfigBuilder.build());

		SSLConnectionSocketFactoryBuilder socketFactoryBuilder=SSLConnectionSocketFactoryBuilder.create().setTlsVersions(TLS.V_1_3, TLS.V_1_2);
		if(sslContext!=null)
			socketFactoryBuilder.setSslContext(sslContext);
		builder.setSSLSocketFactory(socketFactoryBuilder.build());

		return builder;
	}

	public static HttpClientBuilder fastBuilder(int maxConnTotal, int maxConnPerRoute, int defaultSocketTimeout, int defaultConnectTimeout, int defaultConnectionRequestTimeout, int defaultResponseTimeout)
	{
		return fastBuilder(maxConnTotal, maxConnPerRoute, defaultSocketTimeout, defaultConnectTimeout, defaultConnectionRequestTimeout, defaultResponseTimeout, -1, null);
	}

	public static HttpClientBuilder fastBuilder(int maxConnTotal, int maxConnPerRoute, int defaultSocketTimeout, int defaultConnectTimeout, int defaultConnectionRequestTimeout, int defaultResponseTimeout, long keepAliveDuration)
	{
		return fastBuilder(maxConnTotal, maxConnPerRoute, defaultSocketTimeout, defaultConnectTimeout, defaultConnectionRequestTimeout, defaultResponseTimeout, keepAliveDuration, null);
	}

	public static HttpClientBuilder fastBuilder(int maxConnTotal, int maxConnPerRoute, int defaultSocketTimeout, int defaultConnectTimeout, int defaultConnectionRequestTimeout, int defaultResponseTimeout, long keepAliveDuration, SSLContext sslContext)
	{
		return fastBuilder(fastConnectionManagerBuilder(maxConnTotal, maxConnPerRoute, defaultSocketTimeout, defaultConnectTimeout, keepAliveDuration, sslContext), defaultConnectionRequestTimeout, defaultResponseTimeout);
	}

	public static HttpClientBuilder fastBuilder(PoolingHttpClientConnectionManagerBuilder connectionManagerBuilder, int defaultConnectionRequestTimeout, int defaultResponseTimeout)
	{
		RequestConfig.Builder requestConfigBuilder=RequestConfig.custom();
		if(defaultConnectionRequestTimeout>0)
			requestConfigBuilder.setConnectionRequestTimeout(Timeout.ofMilliseconds(defaultConnectionRequestTimeout));
		if(defaultResponseTimeout>0)
			requestConfigBuilder.setResponseTimeout(Timeout.ofMilliseconds(defaultResponseTimeout));
		
		return HttpClients.custom()
				.setConnectionManager(connectionManagerBuilder.build())
				.setDefaultRequestConfig(requestConfigBuilder.build());
	}

	public static final HttpClientResponseHandler<String> DEFAULT_HANDLER=new HttpClientResponseHandler<String>() {
		@Override
		public String handleResponse(ClassicHttpResponse response) throws HttpException, IOException
		{
			HttpEntity entity=response.getEntity();
			String encoding=entity.getContentEncoding();
			return EntityUtils.toString(entity, encoding!=null ? encoding : "utf-8");
		}
	};

	private final CloseableHttpClient client;

	public HttpRequesterClient(int maxConnTotal, int maxConnPerRoute, int defaultSocketTimeout, int defaultConnectTimeout, int defaultConnectionRequestTimeout, int defaultResponseTimeout)
	{
		this(maxConnTotal, maxConnPerRoute, defaultSocketTimeout, defaultConnectTimeout, defaultConnectionRequestTimeout, defaultResponseTimeout, -1, null);
	}

	public HttpRequesterClient(int maxConnTotal, int maxConnPerRoute, int defaultSocketTimeout, int defaultConnectTimeout, int defaultConnectionRequestTimeout, int defaultResponseTimeout, long keepAliveDuration)
	{
		this(maxConnTotal, maxConnPerRoute, defaultSocketTimeout, defaultConnectTimeout, defaultConnectionRequestTimeout, defaultResponseTimeout, keepAliveDuration, null);
	}

	public HttpRequesterClient(int maxConnTotal, int maxConnPerRoute, int defaultSocketTimeout, int defaultConnectTimeout, int defaultConnectionRequestTimeout, int defaultResponseTimeout, long keepAliveDuration, SSLContext sslContext)
	{
		this(fastBuilder(maxConnTotal, maxConnPerRoute, defaultSocketTimeout, defaultConnectTimeout, defaultConnectionRequestTimeout, defaultResponseTimeout, keepAliveDuration, sslContext));
	}

	public HttpRequesterClient(HttpClientBuilder builder)
	{
		client=builder.build();
	}

	public String sendGet(String uri) throws URISyntaxException, ClientProtocolException, IOException
	{
		return sendGet(uri, (Map<String, Object>)null);
	}

	public String sendPost(String uri) throws URISyntaxException, ClientProtocolException, IOException
	{
		return sendPost(uri, null, (HttpEntity)null);
	}

	public String sendGet(String uri, Map<String, Object> params) throws URISyntaxException, ClientProtocolException, IOException
	{
		return sendGet(uri, params, (Map<String, Object>)null);
	}

	public String sendPost(String uri, Map<String, Object> params) throws URISyntaxException, ClientProtocolException, IOException
	{
		return sendPost(uri, params, (HttpEntity)null);
	}

	public String sendPost(String uri, HttpEntity httpEntity) throws URISyntaxException, ClientProtocolException, IOException
	{
		return sendPost(uri, null, httpEntity);
	}

	public String sendPost(String uri, Map<String, Object> params, HttpEntity httpEntity) throws URISyntaxException, ClientProtocolException, IOException
	{
		return sendPost(uri, params, httpEntity, (Map<String, Object>)null);
	}

	public String sendGet(String uri, Map<String, Object> params, Map<String, Object> headers) throws URISyntaxException, ClientProtocolException, IOException
	{
		return sendGet(uri, params, headers, DEFAULT_HANDLER);
	}

	public String sendPost(String uri, Map<String, Object> params, Map<String, Object> headers) throws URISyntaxException, ClientProtocolException, IOException
	{
		return sendPost(uri, params, null, headers);
	}

	public String sendPost(String uri, HttpEntity httpEntity, Map<String, Object> headers) throws URISyntaxException, ClientProtocolException, IOException
	{
		return sendPost(uri, null, httpEntity, headers);
	}

	public String sendPost(String uri, Map<String, Object> params, HttpEntity httpEntity, Map<String, Object> headers) throws URISyntaxException, ClientProtocolException, IOException
	{
		return sendPost(uri, params, httpEntity, headers, DEFAULT_HANDLER);
	}

	public <T> T sendGet(String uri, HttpClientResponseHandler<T> responseHandler) throws URISyntaxException, ClientProtocolException, IOException
	{
		return sendGet(uri, null, responseHandler);
	}

	public <T> T sendPost(String uri, HttpClientResponseHandler<T> responseHandler) throws URISyntaxException, ClientProtocolException, IOException
	{
		return sendPost(uri, null, (HttpEntity)null, responseHandler);
	}

	public <T> T sendGet(String uri, Map<String, Object> params, HttpClientResponseHandler<T> responseHandler) throws URISyntaxException, ClientProtocolException, IOException
	{
		return sendGet(uri, params, null, responseHandler);
	}

	public <T> T sendPost(String uri, Map<String, Object> params, HttpClientResponseHandler<T> responseHandler) throws URISyntaxException, ClientProtocolException, IOException
	{
		return sendPost(uri, params, (HttpEntity)null, responseHandler);
	}

	public <T> T sendPost(String uri, HttpEntity httpEntity, HttpClientResponseHandler<T> responseHandler) throws URISyntaxException, ClientProtocolException, IOException
	{
		return sendPost(uri, null, httpEntity, responseHandler);
	}

	public <T> T sendPost(String uri, Map<String, Object> params, HttpEntity httpEntity, HttpClientResponseHandler<T> responseHandler) throws URISyntaxException, ClientProtocolException, IOException
	{
		return sendPost(uri, params, httpEntity, null, responseHandler);
	}

	public <T> T sendPost(String uri, Map<String, Object> params, Map<String, Object> headers, HttpClientResponseHandler<T> responseHandler) throws URISyntaxException, ClientProtocolException, IOException
	{
		return sendPost(uri, params, null, headers, responseHandler);
	}

	public <T> T sendPost(String uri, HttpEntity httpEntity, Map<String, Object> headers, HttpClientResponseHandler<T> responseHandler) throws URISyntaxException, ClientProtocolException, IOException
	{
		return sendPost(uri, null, httpEntity, headers, responseHandler);
	}

	public <T> T sendGet(String uri, Map<String, Object> params, Map<String, Object> headers, HttpClientResponseHandler<T> responseHandler) throws URISyntaxException, ClientProtocolException, IOException
	{
		return sendGet(uri, params, headers, null, responseHandler);
	}

	public <T> T sendPost(String uri, Map<String, Object> params, HttpEntity httpEntity, Map<String, Object> headers, HttpClientResponseHandler<T> responseHandler) throws URISyntaxException, ClientProtocolException, IOException
	{
		return sendPost(uri, params, httpEntity, headers, null, responseHandler);
	}

	public <T> T sendGet(String uri, Map<String, Object> params, Map<String, Object> headers, HttpClientResponseHandler<T> responseHandler, int connectionRequestTimeout, int responseTimeout) throws URISyntaxException, ClientProtocolException, IOException
	{
		return sendGet(uri, params, headers, configContext(connectionRequestTimeout, responseTimeout), responseHandler);
	}

	public <T> T sendPost(String uri, Map<String, Object> params, HttpEntity httpEntity, Map<String, Object> headers, HttpClientResponseHandler<T> responseHandler, int connectionRequestTimeout, int responseTimeout) throws URISyntaxException, ClientProtocolException, IOException
	{
		return sendPost(uri, params, httpEntity, headers, configContext(connectionRequestTimeout, responseTimeout), responseHandler);
	}

	public <T> T sendGet(String uri, Map<String, Object> params, Map<String, Object> headers, HttpContext context, HttpClientResponseHandler<T> responseHandler) throws URISyntaxException, ClientProtocolException, IOException
	{
		return send(HttpGet::new, uri, params, null, headers, context, responseHandler);
	}

	public <T> T sendPost(String uri, Map<String, Object> params, HttpEntity httpEntity, Map<String, Object> headers, HttpContext context, HttpClientResponseHandler<T> responseHandler) throws URISyntaxException, ClientProtocolException, IOException
	{
		return send(HttpPost::new, uri, params, httpEntity, headers, context, responseHandler);
	}

	public <T> T send(Function<URI, HttpUriRequestBase> method, String uri, Map<String, Object> params, HttpEntity httpEntity, Map<String, Object> headers, HttpContext context, HttpClientResponseHandler<T> responseHandler) throws URISyntaxException, ClientProtocolException, IOException
	{
		HttpUriRequestBase request=method.apply(getUri(uri, params));
		configHeaders(request, headers);
		if(httpEntity!=null)
			request.setEntity(httpEntity);
		return client.execute(request, context, responseHandler);
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

	public static HttpClientContext configContext(int connectionRequestTimeout, int responseTimeout)
	{
		HttpClientContext context=HttpClientContext.create();
		context.setRequestConfig(RequestConfig.custom()
				.setConnectionRequestTimeout(Timeout.ofMilliseconds(connectionRequestTimeout))
				.setResponseTimeout(Timeout.ofMilliseconds(responseTimeout))
				.build());
		return context;
	}

	public static void configHeaders(HttpUriRequestBase request, Map<String, Object> headers)
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
