package com.eroelf.javaxsx.util;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class StdLoggers
{
	public static final BiConsumer<Throwable, String> STD_OUT_EXCEPTION_MSG_LOGGER=(e, msg) -> {
		if(e!=null)
			e.printStackTrace(System.out);
		if(msg!=null)
			System.out.println(msg);
	};

	public static final BiConsumer<Throwable, String> STD_ERR_EXCEPTION_MSG_LOGGER=(e, msg) -> {
		if(e!=null)
			e.printStackTrace(System.err);
		if(msg!=null)
			System.err.println(msg);
	};

	public static final Consumer<String> STD_OUT_MSG_LOGGER=(msg) -> {
		System.out.println(msg);
	};

	public static final Consumer<String> STD_ERR_MSG_LOGGER=(msg) -> {
		System.err.println(msg);
	};
}
