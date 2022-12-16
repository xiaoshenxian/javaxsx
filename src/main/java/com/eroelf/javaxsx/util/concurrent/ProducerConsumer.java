package com.eroelf.javaxsx.util.concurrent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import com.eroelf.javaxsx.util.StdLoggers;

/**
 * The main task of a producer-consumer model.
 * 
 * @author weikun.zhong
 */
public class ProducerConsumer
{
	protected BiConsumer<? super Exception, String> loggerFunc;

	public ProducerConsumer()
	{
		this(StdLoggers.STD_ERR_EXCEPTION_MSG_LOGGER);
	}

	public ProducerConsumer(BiConsumer<? super Exception, String> loggerFunc)
	{
		this.loggerFunc=loggerFunc;
	}

	/**
	 * Override this method if there is a task relating to input data count.
	 * 
	 * @param elemCount the input data count.
	 * @return {@code true} to continue to process the following data, or {@code false} to break the entire task.
	 */
	protected boolean elemAt(long elemCount)
	{
		return true;
	}

	/**
	 * This method is the entrance of a common producer-consumer task in which {@code nThreads} consumers compete data produced buy the producer. Each consumer process the data in the same way.
	 * 
	 * @param <T> the type of the input.
	 * 
	 * @param iterable the producer which provide the data.
	 * @param queueCapacity cache size for consumers to compete.
	 * @param nThreads number of consumers.
	 * @param timeout the time to wait to all consumers to completing their tasks after all data are put into the cache.
	 * @param unit the unit of the {@code timeout}.
	 * @param handler defines how the consumers process the data.
	 * @return {@code true} if the inner executor terminated and {@code false} if the timeout elapsed before termination.
	 * 
	 * @throws InterruptedException if interrupted while waiting consumers to be completed.
	 */
	public <T> boolean consume(Iterable<T> iterable, int queueCapacity, int nThreads, long timeout, TimeUnit unit, java.util.function.Consumer<T> handler) throws InterruptedException
	{
		return consume(iterable.iterator(), queueCapacity, nThreads, timeout, unit, handler);
	}

	/**
	 * This method is the entrance of a special kind of producer-consumer task in which different consumers perform different approaches to all the produced data.
	 * 
	 * @param <T> the type of the input.
	 * 
	 * @param iterable the producer which provide the data.
	 * @param queueCapacity cache size for each consumers.
	 * @param timeout the time to wait to all consumers to completing their tasks after all data are put into the cache.
	 * @param unit the unit of the {@code timeout}.
	 * @param handlers defines different consumers to process all the input data.
	 * @return {@code true} if the inner executor terminated and {@code false} if the timeout elapsed before termination.
	 * 
	 * @throws InterruptedException if interrupted while waiting consumers to be completed.
	 * 
	 * @see #consume(Iterable, long, TimeUnit, Consumer...)
	 */
	public <T> boolean consume(Iterable<T> iterable, int queueCapacity, long timeout, TimeUnit unit, @SuppressWarnings("unchecked") java.util.function.Consumer<T>... handlers) throws InterruptedException
	{
		return consume(iterable.iterator(), queueCapacity, timeout, unit, handlers);
	}

	/**
	 * This method is the entrance of a special kind of producer-consumer task in which different consumers perform different approaches to all the produced data.
	 * 
	 * @param <T> the type of the input.
	 * 
	 * @param iterable the producer which provide the data.
	 * @param timeout the time to wait to all consumers to completing their tasks after all data are put into the cache.
	 * @param unit the unit of the {@code timeout}.
	 * @param consumers defines different consumers to process all the input data.
	 * @return {@code true} if the inner executor terminated and {@code false} if the timeout elapsed before termination.
	 * 
	 * @throws InterruptedException if interrupted while waiting consumers to be completed.
	 * 
	 * @see #consume(Iterable, int, long, TimeUnit, java.util.function.Consumer...)
	 */
	@SuppressWarnings("unchecked")
	public <T> boolean consume(Iterable<T> iterable, long timeout, TimeUnit unit, Consumer<T>... consumers) throws InterruptedException
	{
		return consume(iterable.iterator(), timeout, unit, consumers);
	}

	/**
	 * This method is the entrance of a common producer-consumer task in which {@code nThreads} consumers compete data produced buy the producer. Each consumer process the data in the same way.
	 * 
	 * @param <T> the type of the input.
	 * 
	 * @param iterator the producer which provide the data.
	 * @param queueCapacity cache size for consumers to compete.
	 * @param nThreads number of consumers.
	 * @param timeout the time to wait to all consumers to completing their tasks after all data are put into the cache.
	 * @param unit the unit of the {@code timeout}.
	 * @param handler defines how the consumers process the data.
	 * @return {@code true} if the inner executor terminated and {@code false} if the timeout elapsed before termination.
	 * 
	 * @throws InterruptedException if interrupted while waiting consumers to be completed.
	 */
	@SuppressWarnings("unchecked")
	public <T> boolean consume(Iterator<T> iterator, int queueCapacity, int nThreads, long timeout, TimeUnit unit, java.util.function.Consumer<T> handler) throws InterruptedException
	{
		BlockingQueue<T> taskQueue=new ArrayBlockingQueue<>(queueCapacity);
		ExecutorService es=Executors.newFixedThreadPool(nThreads);
		for(int i=0; i<nThreads; i++)
		{
			es.execute(new Consumer<>(taskQueue, handler, loggerFunc));
		}

		T elem=null;
		long elemCount=0;
		while(!Thread.interrupted() && iterator.hasNext())
		{
			try
			{
				elem=iterator.next();
				if(elem!=null)
					taskQueue.put(elem);
				if(!elemAt(elemCount++))
					break;
			}
			catch(InterruptedException e)
			{
				break;
			}
			catch(Exception e)
			{
				loggerFunc.accept(e, String.format("Exception in producing an element at %d: %s", elemCount, String.valueOf(elem)));
			}
		}
		for(int i=0; i<nThreads; i++)
		{
			((BlockingQueue<Object>)taskQueue).put(Consumer.END_TOKEN);
		}

		es.shutdown();
		return es.awaitTermination(timeout, unit);
	}

	/**
	 * This method is the entrance of a special kind of producer-consumer task in which different consumers perform different approaches to all the produced data.
	 * 
	 * @param <T> the type of the input.
	 * 
	 * @param iterator the producer which provide the data.
	 * @param queueCapacity cache size for each consumers.
	 * @param timeout the time to wait to all consumers to completing their tasks after all data are put into the cache.
	 * @param unit the unit of the {@code timeout}.
	 * @param handlers defines different consumers to process all the input data.
	 * @return {@code true} if the inner executor terminated and {@code false} if the timeout elapsed before termination.
	 * 
	 * @throws InterruptedException if interrupted while waiting consumers to be completed.
	 * 
	 * @see #consume(Iterator, long, TimeUnit, Consumer...)
	 */
	@SuppressWarnings("unchecked")
	public <T> boolean consume(Iterator<T> iterator, int queueCapacity, long timeout, TimeUnit unit, java.util.function.Consumer<T>... handlers) throws InterruptedException
	{
		if(handlers==null || handlers.length<1)
			throw new IllegalArgumentException("At lease 1 handler is needed.");

		List<Consumer<T>> consumers=new ArrayList<>(handlers.length);
		for(java.util.function.Consumer<T> handler : handlers)
		{
			try
			{
				Consumer<T> consumer=new Consumer<>(queueCapacity, handler, loggerFunc);
				consumers.add(consumer);
			}
			catch(Exception e)
			{
				loggerFunc.accept(e, "Failed to initialize a consumer! This handler will be dropped!");
			}
		}

		return consume(iterator, timeout, unit, consumers.toArray(new Consumer[consumers.size()]));
	}

	/**
	 * This method is the entrance of a special kind of producer-consumer task in which different consumers perform different approaches to all the produced data.
	 * 
	 * @param <T> the type of the input.
	 * 
	 * @param iterator the producer which provide the data.
	 * @param timeout the time to wait to all consumers to completing their tasks after all data are put into the cache.
	 * @param unit the unit of the {@code timeout}.
	 * @param consumers defines different consumers to process all the input data.
	 * @return {@code true} if the inner executor terminated and {@code false} if the timeout elapsed before termination.
	 * 
	 * @throws InterruptedException if interrupted while waiting consumers to be completed.
	 * 
	 * @see #consume(Iterator, int, long, TimeUnit, java.util.function.Consumer...)
	 */
	@SuppressWarnings("unchecked")
	public <T> boolean consume(Iterator<T> iterator, long timeout, TimeUnit unit, Consumer<T>... consumers) throws InterruptedException
	{
		ExecutorService es=Executors.newFixedThreadPool(consumers.length);
		for(Consumer<T> consumer : consumers)
		{
			es.execute(consumer);
		}

		T elem=null;
		long elemCount=0;
		while(!Thread.interrupted() && iterator.hasNext())
		{
			try
			{
				elem=iterator.next();
				if(elem!=null)
				{
					for(Consumer<T> consumer : consumers)
					{
						consumer.enqueue(elem);
					}
				}
			}
			catch(InterruptedException e)
			{
				break;
			}
			catch(Exception e)
			{
				loggerFunc.accept(e, "Failed to produce an element: "+String.valueOf(elem));
			}
			if(!elemAt(elemCount++))
				break;
		}
		for(Consumer<T> consumer : consumers)
		{
			((Consumer<Object>)consumer).enqueue(Consumer.END_TOKEN);
		}

		es.shutdown();
		return es.awaitTermination(timeout, unit);
	}
}
