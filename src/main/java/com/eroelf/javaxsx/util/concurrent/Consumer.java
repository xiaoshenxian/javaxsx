package com.eroelf.javaxsx.util.concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.BiConsumer;

import com.eroelf.javaxsx.util.StdLoggers;

/**
 * Implements the consumer role in a producer-consumer model.
 * The overridden {@link Runnable#run()} method contains full consumer behavior which can be directly called by a consumer thread.
 * The consume strategy can be implemented by either override the {@link #accept(Object) accept(T)} method of this class or pass a {@link java.util.function.Consumer} instance to the constructor.
 * 
 * @author weikun.zhong
 *
 * @param <T> the type of the input.
 */
public class Consumer<T> implements java.util.function.Consumer<T>, Runnable
{
	protected BlockingQueue<T> taskQueue;
	protected java.util.function.Consumer<T> consumeFunc;
	protected BiConsumer<? super Exception, String> loggerFunc;

	public Consumer(BlockingQueue<T> taskQueue)
	{
		this(taskQueue, StdLoggers.STD_ERR_EXCEPTION_MSG_LOGGER);
	}

	public Consumer(BlockingQueue<T> taskQueue, BiConsumer<? super Exception, String> loggerFunc)
	{
		this.taskQueue=taskQueue;
		this.consumeFunc=this;
		this.loggerFunc=loggerFunc;
	}

	public Consumer(BlockingQueue<T> taskQueue, java.util.function.Consumer<T> consumeFunc)
	{
		this(taskQueue, consumeFunc, StdLoggers.STD_ERR_EXCEPTION_MSG_LOGGER);
	}

	public Consumer(BlockingQueue<T> taskQueue, java.util.function.Consumer<T> consumeFunc, BiConsumer<? super Exception, String> loggerFunc)
	{
		this.taskQueue=taskQueue;
		this.consumeFunc=consumeFunc;
		this.loggerFunc=loggerFunc;
	}

	public Consumer(int taskQueueCapacity)
	{
		this(taskQueueCapacity, StdLoggers.STD_ERR_EXCEPTION_MSG_LOGGER);
	}

	public Consumer(int taskQueueCapacity, BiConsumer<? super Exception, String> loggerFunc)
	{
		this.taskQueue=new ArrayBlockingQueue<>(taskQueueCapacity);;
		this.consumeFunc=this;
		this.loggerFunc=loggerFunc;
	}

	public Consumer(int taskQueueCapacity, java.util.function.Consumer<T> consumeFunc)
	{
		this(taskQueueCapacity, consumeFunc, StdLoggers.STD_ERR_EXCEPTION_MSG_LOGGER);
	}

	public Consumer(int taskQueueCapacity, java.util.function.Consumer<T> consumeFunc, BiConsumer<? super Exception, String> loggerFunc)
	{
		this.taskQueue=new ArrayBlockingQueue<>(taskQueueCapacity);
		this.consumeFunc=consumeFunc;
		this.loggerFunc=loggerFunc;
	}

	@Override
	public void run()
	{
		begin();
		T elem=null;
		while(!Thread.interrupted())
		{
			try
			{
				elem=taskQueue.take();
				consumeFunc.accept(elem);
			}
			catch(InterruptedException e)
			{
				break;
			}
			catch(Exception e)
			{
				loggerFunc.accept(e, "Failed to consume an element: "+String.valueOf(elem));
			}
		}
		while((elem=taskQueue.poll())!=null)
			consumeFunc.accept(elem);
		end();
	}

	@Override
	public void accept(T elem)
	{}

	protected void begin()
	{}

	protected void end()
	{}

	public BlockingQueue<T> getQueue()
	{
		return taskQueue;
	}

	public void enqueue(T elem) throws InterruptedException
	{
		taskQueue.put(elem);
	}
}
