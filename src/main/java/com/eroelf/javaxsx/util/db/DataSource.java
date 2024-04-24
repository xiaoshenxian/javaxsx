package com.eroelf.javaxsx.util.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import com.eroelf.javaxsx.util.StdLoggers;

/**
 * Database data source with connection pool. 
 * 
 * @author weikun.zhong
 */
public class DataSource
{
	public class Conn
	{
		public final TimedDoDb timedDoDb;
		public final Consumer<String> logger;

		public Conn() throws InterruptedException
		{
			timedDoDb=DataSource.this.take();
			logger=s -> loggerFunc.accept(s, null);
		}

		public void release() throws SQLException, InterruptedException
		{
			DataSource.this.put(timedDoDb);
		}

		public ResultSet executeQuery(boolean ifOnceForAllData, String querySql, Object... objects) throws SQLException
		{
			return timedDoDb.doDb.executeQuery(ifOnceForAllData, querySql, objects);
		}

		public ResultSet executeNewQuery(boolean ifOnceForAllData, String querySql, Object... objects) throws SQLException
		{
			return timedDoDb.doDb.executeNewQuery(ifOnceForAllData, querySql, objects);
		}

		public int executeUpdate(String updateSql, Object... objects) throws SQLException
		{
			return timedDoDb.doDb.executeUpdate(false, updateSql, objects);
		}

		public int executeNewUpdate(String updateSql, Object... objects) throws SQLException
		{
			return timedDoDb.doDb.executeNewUpdate(updateSql, objects);
		}

		public void prepareStatement(boolean ifOnceForAllData, String sql) throws SQLException
		{
			timedDoDb.doDb.prepareStatement(ifOnceForAllData, sql);
		}

		public PreparedStatement newPrepareStatement(boolean ifOnceForAllData, String sql) throws SQLException
		{
			return timedDoDb.doDb.newPrepareStatement(ifOnceForAllData, sql);
		}

		public ResultSet executeQueryAgain(Object... objects) throws SQLException
		{
			return timedDoDb.doDb.executeQueryAgain(objects);
		}

		public int executeUpdateAgain(Object... objects) throws SQLException
		{
			return timedDoDb.doDb.executeUpdateAgain(false, objects);
		}

		public ResultSet executeStatementQuery(boolean ifOnceForAllData, String querySql) throws SQLException
		{
			return timedDoDb.doDb.executeStatementQuery(ifOnceForAllData, querySql);
		}

		public ResultSet executeNewStatementQuery(boolean ifOnceForAllData, String querySql) throws SQLException
		{
			return timedDoDb.doDb.executeNewStatementQuery(ifOnceForAllData, querySql);
		}

		public int executeStatementUpdate(String updateSql) throws SQLException
		{
			return timedDoDb.doDb.executeStatementUpdate(false, updateSql);
		}

		public int executeNewStatementUpdate(String updateSql) throws SQLException
		{
			return timedDoDb.doDb.executeNewStatementUpdate(updateSql);
		}

		public void addBatch(Object... objects) throws SQLException
		{
			timedDoDb.doDb.addBatch(objects);
		}

		public int[] executeBatch() throws SQLException
		{
			return timedDoDb.doDb.executeBatch();
		}

		public boolean getAutoCommit() throws SQLException
		{
			return timedDoDb.doDb.getAutoCommit();
		}

		public void setAutoCommit(boolean autoCommit) throws SQLException
		{
			timedDoDb.doDb.setAutoCommit(autoCommit);
		}

		public void commit() throws SQLException
		{
			timedDoDb.doDb.commit(false);
		}

		public void rollback() throws SQLException
		{
			timedDoDb.doDb.rollback(false);
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
					timedDoDb.doDb.closeAllStatements();
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
			return timedDoDb.doDb.preparedStatementToString();
		}

		public String statementToString()
		{
			return timedDoDb.doDb.statementToString();
		}

		public <T> List<T> fromQuery(Class<T> clazz, boolean ifOnceForAllData, String querySql, Object... objects) throws SQLException
		{
			return timedDoDb.doDb.fromQuery(logger, clazz, false, ifOnceForAllData, querySql, objects);
		}

		public <T> List<T> fromNewQuery(Class<T> clazz, boolean ifOnceForAllData, String querySql, Object... objects) throws SQLException
		{
			return timedDoDb.doDb.fromNewQuery(logger, clazz, ifOnceForAllData, querySql, objects);
		}

		public <T> Iterable<T> queryIter(Class<T> clazz, boolean ifOnceForAllData, String querySql, Object... objects) throws SQLException
		{
			return timedDoDb.doDb.queryIter(logger, clazz, ifOnceForAllData, querySql, objects);
		}

		public <T> Iterable<T> newQueryIter(Class<T> clazz, boolean ifOnceForAllData, String querySql, Object... objects) throws SQLException
		{
			return timedDoDb.doDb.newQueryIter(logger, clazz, ifOnceForAllData, querySql, objects);
		}

		public <T> List<T> fromResultSet(Class<T> clazz) throws SQLException
		{
			return timedDoDb.doDb.fromResultSet(logger, clazz, false);
		}

		public <T> Iterable<T> resultSetIter(Class<T> clazz)
		{
			return timedDoDb.doDb.resultSetIter(logger, clazz);
		}
	}

	private static class TimedDoDb
	{
		public final DoDb doDb;
		public long lastUseTimestamp;

		public TimedDoDb(DoDb doDb)
		{
			this.doDb=doDb;
			this.lastUseTimestamp=System.currentTimeMillis();
		}
	}

	private static final int DEFAULT_RECONNECT_SEC=28800;

	private LinkedBlockingQueue<TimedDoDb> pool;
	private int size;
	private String url;
	private String user;
	private String pwd;
	private long reconnectMilliSec;
	private BiConsumer<String, Throwable> loggerFunc;

	private boolean initialized;

	public DataSource(int size, String url, String user, String pwd)
	{
		this(size, url, user, pwd, DEFAULT_RECONNECT_SEC, StdLoggers.STD_ERR_MSG_EXCEPTION_LOGGER);
	}

	public DataSource(int size, String url, String user, String pwd, int reconnectSec)
	{
		this(size, url, user, pwd, reconnectSec, StdLoggers.STD_ERR_MSG_EXCEPTION_LOGGER);
	}

	public DataSource(int size, String url, String user, String pwd, BiConsumer<String, Throwable> logger)
	{
		this(size, url, user, pwd, DEFAULT_RECONNECT_SEC, logger);
	}

	public DataSource(int size, String url, String user, String pwd, int reconnectSec, BiConsumer<String, Throwable> logger)
	{
		this.pool=new LinkedBlockingQueue<>(size);
		this.size=size;
		this.url=url;
		this.user=user;
		this.pwd=pwd;
		this.reconnectMilliSec=reconnectSec*1000;
		this.loggerFunc=logger;

		initialized=false;
	}

	public void init() throws InterruptedException, SQLException
	{
		if(close()>0)
			throw new SQLException("Not all old connection closed!");
		for(int i=0; i<size; i++)
		{
			put(new TimedDoDb(new DoDb(newConnection())));
		}
		initialized=true;
	}

	public int close() throws InterruptedException
	{
		if(initialized)
		{
			initialized=false;
			if(size>0)
			{
				int closed=0;
				for(int i=0; i<size; i++)
				{
					TimedDoDb timedDoDb=pool.take();
					try
					{
						timedDoDb.doDb.close();
						++closed;
					}
					catch(Exception e)
					{
						loggerFunc.accept("Close failed on connection "+i, e);
						pool.put(timedDoDb);
					}
				}
				size=size-closed;
			}
			return size;
		}
		else
			return 0;
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
				conn.timedDoDb.doDb.closePreparedStatement();
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
				conn.timedDoDb.doDb.closePreparedStatement();
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
				conn.timedDoDb.doDb.closeStatement();
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
				conn.timedDoDb.doDb.closeStatement();
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
				conn.timedDoDb.doDb.closePreparedStatement();
			}
			finally
			{
				conn.release();
			}
		}
	}

	protected TimedDoDb take() throws InterruptedException
	{
		if(initialized)
		{
			TimedDoDb timedDoDb=pool.take();
			testReconnect(timedDoDb);
			return timedDoDb;
		}
		else
			throw new InterruptedException("This data source has been closed!");
	}

	protected void put(TimedDoDb timedDoDb) throws InterruptedException
	{
		pool.put(timedDoDb);
	}

	protected void testReconnect(TimedDoDb timedDoDb) throws InterruptedException
	{
		long now=System.currentTimeMillis();
		if(now-timedDoDb.lastUseTimestamp>=reconnectMilliSec)
		{
			try
			{
				timedDoDb.doDb.close();
				timedDoDb.doDb.setConnection(newConnection());
				timedDoDb.lastUseTimestamp=now;
			}
			catch(SQLException e)
			{
				loggerFunc.accept(null, e);
			}
		}
	}

	protected Connection newConnection() throws SQLException
	{
		return DriverManager.getConnection(url, user, pwd);
	}
}
