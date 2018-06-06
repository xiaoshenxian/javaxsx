package com.eroelf.javaxsx.util.net;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.eroelf.javaxsx.util.Strings;
import com.google.gson.Gson;

/**
 * <p>This class defines a criterion as well as provides convenient methods for specified url construction, and also aiming to help developers get rid of those confusing parameters in a mass of urls.</p>
 * 
 * <p>Usage:</p>
 * 
 * <pre>
 * &#47;&#47; define a sub-class for a specified url.
 * public class TheParamInfo extends ParamInfo
 * {
 *     &#47;&#47; url parameters
 *     public String someParam;
 *     public int someId;
 *     
 *     &#47;&#47; define the url domain.
 *     public TheParamInfo()
 *     {
 *         super("domain/doc?");
 *     }
 * 
 *     &#47;&#47; methods for parameter setting.
 *     public TheParamInfo someParam(String someParam) {this.someParam=someParam; return this;}
 *     public TheParamInfo someId(int someId) {this.someId=someId; return this;}
 * }
 * 
 * &#47;&#47; instantiate
 * TheParamInfo o=new TheParamInfo();
 * &#47;&#47; assignment
 * o.someParam="abcd";
 * o.someId=1;
 * &#47;&#47; get the url
 * String url=o.getUrl();&#47;&#47; the url will be "domain&#47;doc?someParam=abcd&#38;someId=1&#38;"
 * 
 * &#47;&#47; this is more convenient
 * String urlConvenient=o.someParam("abcd").someId(1).getUrl();&#47;&#47; the urlConvenient will be "domain&#47;doc?someParam=abcd&#38;someId=1&#38;"
 * </pre>
 * 
 * <p>If sometimes there is already some parameters has been changed for a specified pre-defined {@link ParamInfo} sub-class but the jar has not been updated in time, one can do below if still want to use the {@link ParamInfo} sub-class:</p>
 * <pre>
 * TheParamInfo o1=new TheParamInfo();
 * o1.putAsTemp("newParam", "wxyz");
 * String urlTemp=o1.getUrl();&#47;&#47; the urlTemp will be "domain&#47;doc?newParam=wxyz&#38;"
 * </pre>
 * 
 * <p>If a secondary directory is in need:</p>
 * <pre>
 * TheParamInfo o2=new TheParamInfo();
 * o2.setDoc("secondary");
 * String urlDoc=o2.someParam("abcd").getUrl();&#47;&#47; the urlDoc will be "domain&#47;doc&#47;secondary?someParam=abcd&#38;"
 * </pre>
 * 
 * <p>url hash is also supported and can be set by using {@link #setHash(Object)} method:</p>
 * <pre>
 * TheParamInfo o3=new TheParamInfo();
 * o3.putAsTemp("newParam", "wxyz");
 * o3.setHash("abcd");
 * String urlWithHash=o3.getUrl();&#47;&#47; the urlWithHash will be "domain&#47;doc?newParam=wxyz&#38;#abcd"
 * </pre>
 * 
 * @author weikun.zhong
 */
public class ParamInfo
{
	public static Gson __gson=new Gson();

	private static final Map<Class<? extends ParamInfo>, Field[]> SUBCLASS_FIELDS_MAP=new HashMap<>();

	private String __domain;
	private String __doc;
	private Map<String, Object> __tempParamMap;
	private Object __hash;

	public ParamInfo()
	{
		this("");
	}

	public ParamInfo(String domain)
	{
		__domain=domain;
	}

	public ParamInfo(String domain, String doc)
	{
		__domain=domain;
		__doc=doc;
	}

	public String urlBody()
	{
		if(Strings.isValid(__doc))
		{
			StringBuilder stringBuilder=new StringBuilder(__domain);
			int i=stringBuilder.length()-1;
			while(i>=0)
			{
				char theChar=stringBuilder.charAt(i);
				if(theChar!='?' && theChar!='/')
					break;
				--i;
			}
			stringBuilder.delete(i+1, stringBuilder.length());
			stringBuilder.append("/").append(__doc);
			return stringBuilder.toString();
		}
		else
			return __domain;
	}

	public ParamInfo config()
	{
		return this;
	}

	/**
	 * Encodes and appends the so far configured url parameters to the given {@code stringBuilder}.
	 * 
	 * @param stringBuilder the {@link StringBuilder} object to which the parameters are to be appended.
	 * @return the exactly same {@code stringBuilder} object but with the parameters appended.
	 */
	public StringBuilder appendAsUrlParam(StringBuilder stringBuilder)
	{
		try
		{
			Class<? extends ParamInfo> cls=this.getClass();
			Field[] fields;
			if(SUBCLASS_FIELDS_MAP.containsKey(cls))
				fields=SUBCLASS_FIELDS_MAP.get(cls);
			else
			{
				List<Field> fieldList=new ArrayList<>(Arrays.asList(cls.getFields()));
				Iterator<Field> iter=fieldList.iterator();
				while(iter.hasNext())
				{
					Field field=iter.next();
					if(Modifier.isStatic(field.getModifiers()))
						iter.remove();
					else
						field.setAccessible(true);
				}
				fields=fieldList.toArray(new Field[fieldList.size()]);
				SUBCLASS_FIELDS_MAP.put(cls, fields);
			}
			Map<String, Object> queries=new HashMap<>();
			for(Field field : fields)
			{
				Object value=field.get(this);
				if(value!=null)
				{
					queries.put(field.getName(), getStringable(value));
				}
			}
			if(__tempParamMap!=null)
			{
				for(Entry<String, Object> entry : __tempParamMap.entrySet())
				{
					queries.put(entry.getKey(), getStringable(entry.getValue()));
				}
			}
			if(!queries.isEmpty())
			{
				if(stringBuilder.length()==0 || stringBuilder.charAt(stringBuilder.length()-1)!='?')
					stringBuilder.append('?');
				stringBuilder=UrlUtil.appendQueries(stringBuilder, queries);
			}
			if(__hash!=null)
				stringBuilder.append("#").append(getStringable(__hash));
		}
		catch(IllegalArgumentException|IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
		return stringBuilder;
	}

	public ParamInfo setDomain(String domain)
	{
		__domain=domain;
		return this;
	}

	public ParamInfo setDoc(String doc)
	{
		__doc=doc;
		return this;
	}

	public ParamInfo setHash(Object hash)
	{
		__hash=hash;
		return this;
	}

	/**
	 * Configures a parameter that is not defined in this class for special circumstances.
	 * 
	 * @param key the parameter name.
	 * @param value the parameter value.
	 * @return {@code this} object.
	 */
	public ParamInfo putAsTemp(String key, Object value)
	{
		if(__tempParamMap==null)
			__tempParamMap=new HashMap<>();
		__tempParamMap.put(key, value);
		return this;
	}

	/**
	 * Remove a parameter that is not defined in this class for special circumstances.
	 * 
	 * @param key the parameter name.
	 * @return {@code this} object.
	 */
	public ParamInfo removeFromTemp(String key)
	{
		if(__tempParamMap!=null)
			__tempParamMap.remove(key);
		return this;
	}

	/**
	 * Get the url with the so far configured parameters appended.
	 * 
	 * @return the url with parameters appended.
	 */
	public String getUrl()
	{
		return this.appendAsUrlParam(new StringBuilder(urlBody())).toString();
	}

	@Override
	public String toString()
	{
		return getUrl();
	}

	/**
	 * Checks whether the specified object is "stringable": the object can be serialized to a valid {@link String} object containing all required information by using its "toString" method.
	 * 
	 * @param value the object to be judged.
	 * @return {@code true} if the object can be serialized to a valid {@link String} object containing all required information by using its "toString" method, otherwise {@code false}.
	 */
	public boolean isStringable(Object value)
	{
		return value.getClass().isPrimitive() || value.getClass().isEnum() || value instanceof CharSequence || value instanceof Number || value instanceof Boolean || value instanceof Character;
	}

	/**
	 * Serializes the specified object to a {@link String} object if the {@link #isStringable(Object)} method returns {@code false}.
	 * 
	 * @param value the object to be serialized
	 * @return the {@link String} object represents all information of the input {@code value}.
	 */
	public String serialize(Object value)
	{
		if(value instanceof ParamInfo)
    		return ((ParamInfo)value).getUrl();
		else
    		return __gson.toJson(value, value.getClass());
	}

	public Object getStringable(Object value)
	{
		return isStringable(value) ? value : serialize(value);
	}
}
