package com.eroelf.javaxsx.util.group.updater;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.eroelf.javaxsx.util.group.ConfigInfo;

/**
 * The class for dealing with grouping information stored in a simple MySql database.
 * 
 * @author weikun.zhong
 */
public class MySqlGroupingInfoSource extends AbstractSimpleDbGroupingInfoSource
{
	private String url;
	private String user;
	private String password;
	private String db;
	private String table;

	public MySqlGroupingInfoSource(String url, String user, String password, String db, String table)
	{
		this.url=url;
		this.user=user;
		this.password=password;
		this.db=db;
		this.table=table;
	}

	@Override
	protected Connection getDbConn() throws ClassNotFoundException, SQLException
	{
		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection(url, user, password);
	}

	@Override
	protected String lastModifiedTimeSql()
	{
		return String.format("select update_time from information_schema.tables where table_schema='%s' and information_schema.tables.table_name='%s'", db, groupingTableName());
	}

	@Override
	protected Date retrieveLastModifiedTime(ResultSet resultSet) throws SQLException
	{
		return resultSet.getDate("update_time");
	}

	@Override
	protected String groupingTableName()
	{
		return table;
	}

	@Override
	public List<ConfigInfo> getConfigInfoList(boolean useRegex, String... facetNames) throws SQLException
	{
		if(useRegex && facetNames.length>0)
		{
			if(facetNames.length==1)
				return doDb.fromQuery(ConfigInfo.class, false, true, String.format("select facet_name, suffix, group_str, sections, update_time from %s where facet_name regexp ?", groupingTableName()), facetNames[0]);
			else
				throw new IllegalArgumentException("facetNames.length must be 1 when using regex!");
		}
		else
			return super.getConfigInfoList(useRegex, facetNames);
	}
}
