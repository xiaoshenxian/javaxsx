package com.eroelf.javaxsx.util.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.eroelf.javaxsx.util.StdLoggers;
import com.eroelf.javaxsx.util.concurrent.Consumer;
import com.eroelf.javaxsx.util.concurrent.ProducerConsumer;

/**
 * Defines the working flow of a simple database updating task by using {@link DbUpdater}s to maintain database connections and updating procedures.
 * 
 * @author weikun.zhong
 */
public class UpdateTask
{
	private BiConsumer<? super Exception, String> loggerFunc;

	public UpdateTask()
	{
		this(StdLoggers.STD_ERR_EXCEPTION_MSG_LOGGER);
	}

	public UpdateTask(BiConsumer<? super Exception, String> loggerFunc)
	{
		this.loggerFunc=loggerFunc;
	}

	/**
	 * Consumes the input data provided by the {@code iter} in a single thread.
	 * 
	 * @param iter provide the input data.
	 * @param dbUpdaters the database handlers to consume the data.
	 * 
	 * @param <T> the element type of the data provided by the {@code iter} and will be consumed by {@link DbUpdater}s.
	 * 
	 * @see #consume(Iterator, Function, DbUpdater...)
	 * @see #consume(Iterator, int, long, TimeUnit, DbUpdater...)
	 * @see #consume(Iterator, int, long, TimeUnit, Function, DbUpdater...)
	 */
	@SuppressWarnings("unchecked")
	public <T> void consume(Iterator<T> iter, DbUpdater<T>... dbUpdaters)
	{
		consume(iter, null, dbUpdaters);
	}

	/**
	 * Consumes the input data provided by the {@code iter} in a single thread.
	 * 
	 * @param iter provide the input data.
	 * @param elemAt an input data count related task handler, returns {@code true} to continue to process the following data, or {@code false} to break the entire task.
	 * @param dbUpdaters the database handlers to consume the data.
	 * 
	 * @param <T> the element type of the data provided by the {@code iter} and will be consumed by {@link DbUpdater}s.
	 * 
	 * @see #consume(Iterator, int, long, TimeUnit, DbUpdater...)
	 * @see #consume(Iterator, int, long, TimeUnit, Function, DbUpdater...)
	 */
	@SuppressWarnings("unchecked")
	public <T> void consume(Iterator<T> iter, Function<Long, Boolean> elemAt, DbUpdater<T>... dbUpdaters)
	{
		List<DbUpdater<T>> dbUpdaterList=new ArrayList<>(dbUpdaters.length);
		for(DbUpdater<T> dbUpdater : dbUpdaters)
		{
			try
			{
				updaterInit(dbUpdater);
				dbUpdaterList.add(dbUpdater);
			}
			catch(Exception e)
			{
				loggerFunc.accept(e, "UpdateTask::consume: Initialize faild! This dbUpdater will be dropped!");
			}
		}

		if(dbUpdaterList.isEmpty())
			throw new IllegalArgumentException("UpdateTask::consume: no valid dbUpdater!");

		long elemCount=0;
		if(elemAt==null)
			elemAt=c -> true;
		while(iter.hasNext())
		{
			T data=null;
			try
			{
				data=iter.next();
			}
			catch(Exception e)
			{
				loggerFunc.accept(e, "UpdateTask::consume: fetch iterator failed!");
			}
			for(DbUpdater<T> dbUpdater : dbUpdaterList)
			{
				updaterConsume(dbUpdater, data, loggerFunc);
			}
			if(!elemAt.apply(elemCount++))
				break;
		}
		for(DbUpdater<T> dbUpdater : dbUpdaterList)
		{
			updaterFinalize(dbUpdater, loggerFunc);
		}
	}

	/**
	 * Consumes the input data provided by the {@code iter} in multiple threads. Each thread are dedicated to one {@link DbUpdater} task.
	 * 
	 * @param iter provide the input data.
	 * @param queueCapacity cache size for each {@link DbUpdater} task.
	 * @param timeout the time to wait to all consumers to completing their tasks after all data are put into the cache.
	 * @param unit the unit of the {@code timeout}.
	 * @param dbUpdaters the database handlers to consume the data.
	 * 
	 * @param <T> the element type of the data provided by the {@code iter} and will be consumed by {@link DbUpdater}s.
	 * 
	 * @return {@code true} if the inner executor terminated and {@code false} if the timeout elapsed before termination.
	 * 
	 * @throws InterruptedException if interrupted while waiting {@link DbUpdater} tasks to be completed.
	 * 
	 * @see #consume(Iterator, int, long, TimeUnit, Function, DbUpdater...)
	 */
	@SuppressWarnings("unchecked")
	public <T> boolean consume(Iterator<T> iter, int queueCapacity, long timeout, TimeUnit unit, DbUpdater<T>... dbUpdaters) throws InterruptedException
	{
		return consume(iter, queueCapacity, timeout, unit, null, dbUpdaters);
	}

	/**
	 * Consumes the input data provided by the {@code iter} in multiple threads. Each thread are dedicated to one {@link DbUpdater} task.
	 * 
	 * @param iter provide the input data.
	 * @param queueCapacity cache size for each {@link DbUpdater} task.
	 * @param timeout the time to wait to all consumers to completing their tasks after all data are put into the cache.
	 * @param unit the unit of the {@code timeout}.
	 * @param elemAt an input data count related task handler, returns {@code true} to continue to process the following data, or {@code false} to break the entire task.
	 * @param dbUpdaters the database handlers to consume the data.
	 * 
	 * @param <T> the element type of the data provided by the {@code iter} and will be consumed by {@link DbUpdater}s.
	 * 
	 * @return {@code true} if the inner executor terminated and {@code false} if the timeout elapsed before termination.
	 * 
	 * @throws InterruptedException if interrupted while waiting {@link DbUpdater} tasks to be completed.
	 * 
	 * @see ProducerConsumer
	 */
	@SuppressWarnings("unchecked")
	public <T> boolean consume(Iterator<T> iter, int queueCapacity, long timeout, TimeUnit unit, Function<Long, Boolean> elemAt, DbUpdater<T>... dbUpdaters) throws InterruptedException
	{
		List<Consumer<T>> consumers=new ArrayList<>();
		for(DbUpdater<T> dbUpdater : dbUpdaters)
		{
			try
			{
				updaterInit(dbUpdater);
				Consumer<T> consumer=new Consumer<T>(queueCapacity, loggerFunc) {
					@Override
					public void accept(T elem)
					{
						updaterConsume(dbUpdater, elem, loggerFunc);
					}

					@Override
					protected void end()
					{
						updaterFinalize(dbUpdater, loggerFunc);
					}
				};
				consumers.add(consumer);
			}
			catch(Exception e)
			{
				loggerFunc.accept(e, "UpdateTask::consume: Initialize faild! This dbUpdater will be dropped!");
			}
		}

		if(consumers.isEmpty())
			throw new IllegalArgumentException("UpdateTask::consume: no valid dbUpdater!");

		return (elemAt!=null ? new ProducerConsumer(loggerFunc) {
			@Override
			protected boolean elemAt(long elemCount)
			{
				return elemAt.apply(elemCount);
			}
		} : new ProducerConsumer(loggerFunc)).consume(new Iterator<T>() {
			private T data=null;

			@Override
			public boolean hasNext()
			{
				while(data==null && iter.hasNext())
				{
					try
					{
						data=iter.next();
					}
					catch(Exception e)
					{
						loggerFunc.accept(e, "UpdateTask::consume: fetch iterator failed!");
					}
				}
				return data!=null;
			}

			@Override
			public T next()
			{
				if(data!=null)
				{
					T theData=data;
					data=null;
					return theData;
				}
				else
					throw new NoSuchElementException();
			}
		}, queueCapacity, timeout, unit, consumers.toArray(new Consumer[consumers.size()]));
	}

	protected <T> void updaterInit(DbUpdater<T> dbUpdater) throws SQLException
	{
		dbUpdater.init();
		try
		{
			dbUpdater.prepare();
		}
		catch(Exception e)
		{
			dbUpdater.close();
			throw e;
		}
	}

	protected <T> void updaterConsume(DbUpdater<T> dbUpdater, T data, BiConsumer<? super Exception, String> loggerFunc)
	{
		try
		{
			Iterable<Object[]> iter=dbUpdater.process(data);
			if(iter!=null)
			{
				for(Object[] dt : iter)
				{
					try
					{
						dbUpdater.accept(dt);
					}
					catch(Exception e)
					{
						loggerFunc.accept(e, "UpdateTask::updaterConsume: dbUpdater.accept exception!");
					}
				}
			}
		}
		catch(Exception e)
		{
			loggerFunc.accept(e, "UpdateTask::updaterConsume: dbUpdater.process failed!");
		}
	}

	protected <T> void updaterFinalize(DbUpdater<T> dbUpdater, BiConsumer<? super Exception, String> loggerFunc)
	{
		try
		{
			dbUpdater.end();
		}
		catch(SQLException e)
		{
			loggerFunc.accept(e, "UpdateTask::updaterFinalize: dbUpdater.end failed!");
		}
		finally
		{
			try
			{
				dbUpdater.close();
			}
			catch(SQLException e)
			{
				loggerFunc.accept(e, "UpdateTask::updaterFinalize: dbUpdater.close exception!");
			}
		}
	}
}
