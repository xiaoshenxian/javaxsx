package com.eroelf.javaxsx.util.group.updater;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import com.eroelf.javaxsx.util.Strings;
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
	protected String lastModifiedTime="0000-00-00 00:00:00";
	
	protected abstract Connection getDbConn() throws ClassNotFoundException, SQLException;
	protected abstract String lastModifiedTimeSql();
	protected abstract String retrieveLastModifiedTime(ResultSet resultSet) throws SQLException;
	protected abstract String groupingTableName();

	@Override
	public GroupingInfoSource open() throws ClassNotFoundException, SQLException
	{
		doDb.setConnection(getDbConn());
		return this;
	}

	@Override
	public String checkModified() throws SQLException
	{
		ResultSet resultSet=doDb.executeQuery(true, lastModifiedTimeSql());
		resultSet.next();
		String lastModTime=retrieveLastModifiedTime(resultSet);
		if(Strings.isValid(lastModTime) && lastModTime.compareTo(lastModifiedTime)>0)
		{
			String oldModifiedTime=lastModifiedTime;
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
			return doDb.fromQuery(ConfigInfo.class, false, true, String.format("select * from %s where facet_name in (%s)", groupingTableName(), String.join(", ", posArray)), (Object[])facetNames);
		}
		else
			return doDb.fromQuery(ConfigInfo.class, false, true, String.format("select * from %s", groupingTableName()));
	}

	@Override
	public void close()
	{
		doDb.close();
	}
}
