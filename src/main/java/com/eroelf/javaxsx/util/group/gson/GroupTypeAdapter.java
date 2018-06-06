package com.eroelf.javaxsx.util.group.gson;

import java.lang.reflect.Type;

import com.eroelf.javaxsx.util.group.GroupingUtil;
import com.eroelf.javaxsx.util.group.GroupingUtil.Group;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Class to help to serialize and deserialize {@link Group} instances via {@link Gson}.
 * 
 * @author weikun.zhong
 */
public class GroupTypeAdapter implements JsonSerializer<Group>, JsonDeserializer<Group>
{
	private boolean suppress;

	/**
	 * The constructor.
	 * 
	 * @param suppress when there is no such an {@link Group} instance in the {@link GroupingUtil} pool, the {@link GroupingUtil#getNA() GroupingUtil.NA} will be returned if {@code suppress} is {@code true} otherwise an exception will be thrown. 
	 */
	public GroupTypeAdapter(boolean suppress)
	{
		this.suppress=suppress;
	}

	@Override
	public JsonElement serialize(Group src, Type typeOfSrc, JsonSerializationContext context)
	{
		JsonObject jsonObject=new JsonObject();
		jsonObject.add("facetName", new JsonPrimitive(src.getFacetName()));
		jsonObject.add("groupName", new JsonPrimitive(src.getGroupName()));
		return jsonObject;
	}

	@Override
	public Group deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		JsonObject jsonObject=json.getAsJsonObject();
		String facetName=jsonObject.get("facetName").getAsString();
		String groupName=jsonObject.get("groupName").getAsString();
		Group group=GroupingUtil.getGroup(facetName, groupName);
		if(suppress || (group.getGroupName().equals(groupName) && group.getGroupName().equals(groupName)))
			return group;
		else
			throw new JsonParseException("cannot deserialize Group with facetName '"+facetName+"' and groupName '"+groupName+"'; no this Group information found!");
	}
}
