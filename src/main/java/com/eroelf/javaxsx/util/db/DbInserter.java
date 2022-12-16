package com.eroelf.javaxsx.util.db;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.google.common.collect.Iterables;

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
	protected String url;
	protected String user;
	protected String pwd;
	protected String dbTable;
	protected int batchSize;

	private int acceptCount=0;

	public DbInserter(String url, String user, String pwd, String dbTable, int batchSize)
	{
		this.url=url;
		this.user=user;
		this.pwd=pwd;
		this.dbTable=dbTable;
		this.batchSize=batchSize;
	}

	@Override
	public void init() throws SQLException
	{
		doDb=new DoDb(DriverManager.getConnection(url, user, pwd));
	}

	@Override
	public void prepareStatement(Object[] data) throws SQLException
	{
		doDb.prepareStatement(true, String.format("insert into %s values (%s)", dbTable, String.join(",", Iterables.limit(Iterables.cycle("?"), data.length))));
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
