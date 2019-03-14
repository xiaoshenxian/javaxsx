package com.eroelf.javaxsx.util.monitor;

/**
 * This class provide methods for simple service monitors.
 * A monitor generally listens a specified event of a service, and provide detail information include some kind of time and event count.
 * 
 * @author weikun.zhong
 */
public abstract class Monitor
{
	protected final String service;
	protected final String name;

	/**
	 * Constructor.
	 * 
	 * @param service the service name.
	 * @param name the event name this monitor focus.
	 */
	public Monitor(String service, String name)
	{
		this.service=service;
		this.name=name;
	}

	/**
	 * Produce one record relating to time.
	 * 
	 * @param time the time amount of the event that should be recorded.
	 */
	public void time(double time)
	{
		send(new MonitorData(service, name, time, -1));
	}

	/**
	 * Produce one record relating to count.
	 * 
	 * @param count the count of the event that should be recorded.
	 */
	public void count(long count)
	{
		send(new MonitorData(service, name, -1, count));
	}

	/**
	 * Produce one record relating to count with the count value set to 1.
	 */
	public void count()
	{
		count(1);
	}

	/**
	 * Produce one record relating to both time and count.
	 * 
	 * @param time the time amount of the event that should be recorded.
	 * @param count count the count of the event that should be recorded.
	 */
	public void timeAndCount(double time, long count)
	{
		send(new MonitorData(service, name, time, count));
	}

	/**
	 * Produce one record relating to both time and count with the count value set to 1.
	 * 
	 * @param time the time amount of the event that should be recorded.
	 */
	public void timeAndCount(double time)
	{
		timeAndCount(time, 1);
	}

	/**
	 * Sends the monitor record to a certain processing handler.
	 * 
	 * @param data the monitor record to be sent.
	 */
	protected abstract void send(MonitorData data);
}
