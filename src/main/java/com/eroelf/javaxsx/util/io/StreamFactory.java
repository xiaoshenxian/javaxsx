package com.eroelf.javaxsx.util.io;

import java.io.IOException;

@FunctionalInterface
public interface StreamFactory<T>
{
	public T get(T t) throws IOException;
}
