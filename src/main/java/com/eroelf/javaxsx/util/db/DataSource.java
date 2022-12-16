package com.eroelf.javaxsx.util.db;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Database data source with connection pool. 
 * 
 * @author weikun.zhong
 */
public class DataSource
{
	public class Conn
	{
		public final DoDb doDb;
		public final Consumer<String> logger;

		public Conn() throws InterruptedException
		{
			doDb=DataSource.this.take();
			logger=s -> loggerFunc.accept(null, s);
		}

		public void release() throws InterruptedException
		{
			DataSource.this.put(doDb);
		}

		public ResultSet executeQuery(boolean ifOnceForAllData, String querySql, Object... objects) throws SQLException
		{
			return doDb.executeQuery(ifOnceForAllData, querySql, objects);
		}

		public ResultSet executeNewQuery(boolean ifOnceForAllData, String querySql, Object... objects) throws SQLException
		{
			return doDb.executeNewQuery(ifOnceForAllData, querySql, objects);
		}

		public int executeUpdate(String updateSql, Object... objects) throws SQLException
		{
			return doDb.executeUpdate(false, updateSql, objects);
		}

		public int executeNewUpdate(String updateSql, Object... objects) throws SQLException
		{
			return doDb.executeNewUpdate(updateSql, objects);
		}

		public void prepareStatement(boolean ifOnceForAllData, String sql) throws SQLException
		{
			doDb.prepareStatement(ifOnceForAllData, sql);
		}

		public PreparedStatement newPrepareStatement(boolean ifOnceForAllData, String sql) throws SQLException
		{
			return doDb.newPrepareStatement(ifOnceForAllData, sql);
		}

		public ResultSet executeQueryAgain(Object... objects) throws SQLException
		{
			return doDb.executeQueryAgain(objects);
		}

		public int executeUpdateAgain(Object... objects) throws SQLException
		{
			return doDb.executeUpdateAgain(false, objects);
		}

		public ResultSet executeStatementQuery(boolean ifOnceForAllData, String querySql) throws SQLException
		{
			return doDb.executeStatementQuery(ifOnceForAllData, querySql);
		}

		public ResultSet executeNewStatementQuery(boolean ifOnceForAllData, String querySql) throws SQLException
		{
			return doDb.executeNewStatementQuery(ifOnceForAllData, querySql);
		}

		public int executeStatementUpdate(String updateSql) throws SQLException
		{
			return doDb.executeStatementUpdate(false, updateSql);
		}

		public int executeNewStatementUpdate(String updateSql) throws SQLException
		{
			return doDb.executeNewStatementUpdate(updateSql);
		}

		public void addBatch(Object... objects) throws SQLException
		{
			doDb.addBatch(objects);
		}

		public int[] executeBatch() throws SQLException
		{
			return doDb.executeBatch();
		}

		public boolean getAutoCommit() throws SQLException
		{
			return doDb.getAutoCommit();
		}

		public void setAutoCommit(boolean autoCommit) throws SQLException
		{
			doDb.setAutoCommit(autoCommit);
		}

		public void commit() throws SQLException
		{
			doDb.commit(false);
		}

		public void rollback() throws SQLException
		{
			doDb.rollback(false);
		}

		public void executeTransaction(Consumer<Conn> transaction) throws SQLException
		{
			boolean autoCommit=getAutoCommit();
			try
			{
				if(autoCommit)
					setAutoCommit(false);
				transaction.accept(this);
				commit();
			}
			catch(Exception e)
			{
				rollback();
				throw e;
			}
			finally
			{
				try
				{
					doDb.closePreparedStatement();
					doDb.closeStatement();
					doDb.closePreStatList();
					doDb.closeStatList();
				}
				finally
				{
					if(autoCommit)
						setAutoCommit(autoCommit);
				}
			}
		}

		public String preparedStatementToString()
		{
			return doDb.preparedStatementToString();
		}

		public String statementToString()
		{
			return doDb.statementToString();
		}

		public <T> List<T> fromQuery(Class<T> clazz, boolean ifOnceForAllData, String querySql, Object... objects) throws SQLException
		{
			return doDb.fromQuery(logger, clazz, false, ifOnceForAllData, querySql, objects);
		}

		public <T> List<T> fromNewQuery(Class<T> clazz, boolean ifOnceForAllData, String querySql, Object... objects) throws SQLException
		{
			return doDb.fromNewQuery(logger, clazz, ifOnceForAllData, querySql, objects);
		}

		public <T> Iterable<T> queryIter(Class<T> clazz, boolean ifOnceForAllData, String querySql, Object... objects) throws SQLException
		{
			return doDb.queryIter(logger, clazz, ifOnceForAllData, querySql, objects);
		}

		public <T> Iterable<T> newQueryIter(Class<T> clazz, boolean ifOnceForAllData, String querySql, Object... objects) throws SQLException
		{
			return doDb.newQueryIter(logger, clazz, ifOnceForAllData, querySql, objects);
		}

		public <T> List<T> fromResultSet(Class<T> clazz) throws SQLException
		{
			return doDb.fromResultSet(logger, clazz, false);
		}

		public <T> Iterable<T> resultSetIter(Class<T> clazz)
		{
			return doDb.resultSetIter(logger, clazz);
		}
	}

	private LinkedBlockingQueue<DoDb> pool;
	private int size;
	private String url;
	private String user;
	private String pwd;
	private BiConsumer<Throwable, String> loggerFunc;

	private boolean initialized;

	public DataSource(int size, String url, String user, String pwd, BiConsumer<Throwable, String> logger)
	{
		this.size=size;
		this.url=url;
		this.user=user;
		this.pwd=pwd;
		this.loggerFunc=logger;

		initialized=false;
	}

	public void init() throws InterruptedException, SQLException
	{
		if(close()>0)
			throw new SQLException("Not all old connection closed!");
		for(int i=0; i<size; i++)
		{
			put(new DoDb(DriverManager.getConnection(url, user, pwd)));
		}
		initialized=true;
	}

	public int close() throws InterruptedException
	{
		initialized=false;
		if(size>0)
		{
			int closed=0;
			for(int i=0; i<size; i++)
			{
				DoDb doDb=take();
				try
				{
					doDb.close();
					++closed;
				}
				catch(Exception e)
				{
					loggerFunc.accept(e, "Close failed on connection "+i);
					pool.put(doDb);
				}
			}
			size=size-closed;
		}
		return size;
	}

	public Conn takeConn() throws InterruptedException
	{
		return this.new Conn();
	}

	public <T> T executeQuery(Function<ResultSet, T> resultMapping, boolean ifOnceForAllData, String querySql, Object... objects) throws SQLException, InterruptedException
	{
		Conn conn=takeConn();
		try
		{
			return resultMapping.apply(conn.executeQuery(ifOnceForAllData, querySql, objects));
		}
		finally
		{
			try
			{
				conn.doDb.closePreparedStatement();
			}
			finally
			{
				conn.release();
			}
		}
	}

	public int executeUpdate(String updateSql, Object... objects) throws SQLException, InterruptedException
	{
		Conn conn=takeConn();
		try
		{
			return conn.executeUpdate(updateSql, objects);
		}
		finally
		{
			try
			{
				conn.doDb.closePreparedStatement();
			}
			finally
			{
				conn.release();
			}
		}
	}

	public <T> T executeStatementQuery(Function<ResultSet, T> resultMapping, boolean ifOnceForAllData, String querySql) throws SQLException, InterruptedException
	{
		Conn conn=takeConn();
		try
		{
			return resultMapping.apply(conn.executeStatementQuery(ifOnceForAllData, querySql));
		}
		finally
		{
			try
			{
				conn.doDb.closeStatement();
			}
			finally
			{
				conn.release();
			}
		}
	}

	public int executeStatementUpdate(String updateSql) throws SQLException, InterruptedException
	{
		Conn conn=takeConn();
		try
		{
			return conn.executeStatementUpdate(updateSql);
		}
		finally
		{
			try
			{
				conn.doDb.closeStatement();
			}
			finally
			{
				conn.release();
			}
		}
	}

	public void executeTransaction(Consumer<Conn> transaction) throws SQLException, InterruptedException
	{
		Conn conn=takeConn();
		try
		{
			conn.executeTransaction(transaction);
		}
		finally
		{
			conn.release();
		}
	}

	public <T> List<T> fromQuery(Class<T> clazz, boolean ifOnceForAllData, String querySql, Object... objects) throws SQLException, InterruptedException
	{
		Conn conn=takeConn();
		try
		{
			return conn.fromQuery(clazz, ifOnceForAllData, querySql, objects);
		}
		finally
		{
			try
			{
				conn.doDb.closePreparedStatement();
			}
			finally
			{
				conn.release();
			}
		}
	}

	protected DoDb take() throws InterruptedException
	{
		if(initialized)
			return pool.take();
		else
			throw new InterruptedException("This data source has been closed!");
	}

	protected void put(DoDb doDb) throws InterruptedException
	{
		pool.put(doDb);
	}
}
