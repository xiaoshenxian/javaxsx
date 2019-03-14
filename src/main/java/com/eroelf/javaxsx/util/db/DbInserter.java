package com.eroelf.javaxsx.util.db;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

import com.eroelf.javaxsx.util.db.DoDb;

/**
 * A database inserter defines methods for different stages related to insert data into a database.
 * 
 * @author weikun.zhong
 *
 * @param <T> the input data type.
 * 
 * @see DbUpdater
 * @see DoDb
 */
public abstract class DbInserter<T> implements DbUpdater<T>
{
	protected DoDb doDb;
	protected String dbHost;
	protected int dbPort;
	protected String dbUser;
	protected String dbPassword;
	protected String dbName;
	protected String dbTable;
	protected int batchSize;

	private int acceptCount=0;

	public DbInserter(String dbHost, int dbPort, String dbUser, String dbPassword, String dbName, String dbTable, int batchSize)
	{
		this.dbHost=dbHost;
		this.dbPort=dbPort;
		this.dbUser=dbUser;
		this.dbPassword=dbPassword;
		this.dbName=dbName;
		this.dbTable=dbTable;
		this.batchSize=batchSize;
	}

	@Override
	public void init() throws SQLException
	{
		doDb=new DoDb(DriverManager.getConnection(String.format("jdbc:mysql://%s:%d/%s?characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Shanghai", dbHost, dbPort, dbName), dbUser, dbPassword));
	}

	@Override
	public void prepareStatement(Object[] data) throws SQLException
	{
		String[] placeholders=new String[data.length];
		Arrays.fill(placeholders, "?");
		doDb.prepareStatement(true, String.format("insert into %s values (%s)", dbTable, String.join(",", placeholders)));
	}

	@Override
	public void accept(Object[] data) throws SQLException
	{
		if(data!=null)
		{
			if(acceptCount==0)
				prepareStatement(data);
			doDb.addBatch(data);
			if(++acceptCount%batchSize==0)
				doDb.executeBatch();
		}
	}

	@Override
	public void end() throws SQLException
	{
		if(doDb!=null)
		{
			if(acceptCount>0 && acceptCount%batchSize!=0)
				doDb.executeBatch();
		}
	}

	@Override
	public void close() throws SQLException
	{
		if(doDb!=null)
			doDb.close();
	}
}
