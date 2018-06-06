package com.eroelf.javaxsx.util.group.updater;

import java.util.List;

import com.eroelf.javaxsx.util.group.ConfigInfo;

/**
 * The interface for dealing with grouping information.
 * 
 * @author weikun.zhong
 */
public interface GroupingInfoSource extends AutoCloseable
{
	public GroupingInfoSource open() throws Exception;
	public String checkModified() throws Exception;
	public List<ConfigInfo> getConfigInfoList(boolean useRegex, String... facetNames) throws Exception;
}
