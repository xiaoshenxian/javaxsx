package com.eroelf.javaxsx.util.db;

import java.sql.SQLException;

/**
 * Defines methods for different stages during a database updating task.
 * Use this interface with the {@link UpdateTask} can easily code a simple database updating task that converts an input data stream into a database required data stream and updates the database.
 * 
 * @author weikun.zhong
 *
 * @param <T> the input data type.
 */
public interface DbUpdater<T>
{
	/**
	 * Defines the initialization stage that will happen at the very beginning.
	 * The database connection should be built here.
	 * 
	 * @throws SQLException if the initialization failed due to database exceptions.
	 */
	public void init() throws SQLException;

	/**
	 * Defines some data task that should be done after the database connected but before the updating task.
	 * This method will be called after the {@link #init()}.
	 * 
	 * @throws SQLException if any SQL failed in this stage.
	 */
	default public void prepare() throws SQLException
	{}

	/**
	 * Defines how to convert one record of input data into a series of database records.
	 * 
	 * @param data one record of input data.
	 * @return an {@link Iterable} object contains all database records formed from the input data and about to be used to update the database.
	 */
	public Iterable<Object[]> process(T data);

	/**
	 * Consumes one record returned by the {@link #process(Object)} method, either updates the database by this data, or just rejects it.
	 * The {@link #prepareStatement(Object[])} should be called by this method only once at the first time this method "accepted" the input data.
	 * 
	 * @param data a database record returned by the {@link #process(Object)} method.
	 * 
	 * @throws SQLException if any SQL failed in this stage.
	 */
	public void accept(Object[] data) throws SQLException;

	/**
	 * Defines and prepares the update SQL.
	 * 
	 * @param data a record returned by the {@link #process(Object)} method and will be used to update the database.
	 * @throws SQLException if any SQL failed in this stage.
	 */
	public void prepareStatement(Object[] data) throws SQLException;

	/**
	 * Defines some data task that should be done after the updating task completed but before closing the connection.
	 * This method will be called before the {@link #close()}.
	 * 
	 * @throws SQLException if any SQL failed in this stage.
	 */
	public void end() throws SQLException;

	/**
	 * Defines the finalize stage that will happen after all job completed.
	 * The database connection should be closed here.
	 * 
	 * @throws SQLException if the finalization failed due to database exceptions.
	 */
	public void close() throws SQLException;
}
