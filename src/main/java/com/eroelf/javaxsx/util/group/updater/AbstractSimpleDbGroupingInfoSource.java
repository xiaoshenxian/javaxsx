package com.eroelf.javaxsx.util.group.updater;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.eroelf.javaxsx.util.db.DoDb;
import com.eroelf.javaxsx.util.group.ConfigInfo;

/**
 * The abstract class for dealing with grouping information stored in a simple database.
 * 
 * @author weikun.zhong
 */
public abstract class AbstractSimpleDbGroupingInfoSource implements GroupingInfoSource
{
	protected DoDb doDb=new DoDb();
	protected Date lastModifiedTime=new Date(0);
	
	protected abstract Connection getDbConn() throws ClassNotFoundException, SQLException;
	protected abstract String lastModifiedTimeSql();
	protected abstract Date retrieveLastModifiedTime(ResultSet resultSet) throws SQLException;
	protected abstract String groupingTableName();

	@Override
	public GroupingInfoSource open() throws ClassNotFoundException, SQLException
	{
		doDb.setConnection(getDbConn());
		return this;
	}

	@Override
	public Date checkModified() throws SQLException
	{
		ResultSet resultSet=doDb.executeQuery(true, lastModifiedTimeSql());
		resultSet.next();
		Date lastModTime=retrieveLastModifiedTime(resultSet);
		if(lastModTime==null || lastModTime.after(lastModifiedTime))
		{
			Date oldModifiedTime=lastModifiedTime;
			if(lastModTime!=null)
				lastModifiedTime=lastModTime;
			return oldModifiedTime;
		}
		else
			return null;
	}

	@Override
	public List<ConfigInfo> getConfigInfoList(boolean useRegex, String... facetNames) throws SQLException
	{
		if(facetNames.length>0)
		{
			if(useRegex)
				throw new UnsupportedOperationException("useRegex does not implemented!");
			String[] posArray=new String[facetNames.length];
			Arrays.fill(posArray, "?");
			return doDb.fromQuery(ConfigInfo.class, false, true, String.format("select facet_name, suffix, group_str, sections, update_time from %s where facet_name in (%s)", groupingTableName(), String.join(", ", posArray)), (Object[])facetNames);
		}
		else
			return doDb.fromQuery(ConfigInfo.class, false, true, String.format("select facet_name, suffix, group_str, sections, update_time from %s", groupingTableName()));
	}

	@Override
	public void close() throws SQLException
	{
		doDb.close();
	}
}
