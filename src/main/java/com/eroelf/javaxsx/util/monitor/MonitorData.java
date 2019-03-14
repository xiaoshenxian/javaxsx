package com.eroelf.javaxsx.util.monitor;

import java.util.Date;

/**
 * Contains information of a service monitor record. Generally, a simple monitor of a service listening a specified event, and produces detailed information (usually some kind of time and event count) when the event happens.
 * 
 * @author weikun.zhong
 */
public class MonitorData
{
	public long logTime;
	public String service;
	public String name;
	public double time;
	public long count;

	public MonitorData(long logTime, String service, String name, double time, long count)
	{
		this.logTime=logTime;
		this.service=service;
		this.name=name;
		this.time=time;
		this.count=count;
	}

	public MonitorData(String service, String name, double time, long count)
	{
		this(new Date().getTime(), service, name, time, count);
	}
}
