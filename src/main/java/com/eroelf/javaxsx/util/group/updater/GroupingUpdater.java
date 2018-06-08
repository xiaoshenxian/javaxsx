package com.eroelf.javaxsx.util.group.updater;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import com.eroelf.javaxsx.util.StdLoggers;
import com.eroelf.javaxsx.util.group.ConfigInfo;
import com.eroelf.javaxsx.util.group.GroupingUtil;
import com.eroelf.javaxsx.util.group.GroupingUtil.Facet;
import com.eroelf.javaxsx.util.group.GroupingUtil.Group;

/**
 * Provide methods to synchronize {@link Facet} and {@link Group} information for {@link GroupingUtil} via a data source.
 * 
 * @author weikun.zhong
 */
public final class GroupingUpdater
{
	private static final ScheduledExecutorService SES=Executors.newScheduledThreadPool(1);
	private static GroupingInfoSource source;
	private static IdentifierValidatorFactory identifierValidatorFactory;
	private static HashGetterFactory hashGetterFactory;

	public static void config(GroupingInfoSource source, IdentifierValidatorFactory identifierValidatorFactory, HashGetterFactory hashGetterFactory)
	{
		GroupingUpdater.source=source;
		GroupingUpdater.identifierValidatorFactory=identifierValidatorFactory;
		GroupingUpdater.hashGetterFactory=hashGetterFactory;
	}

	public static void config(GroupingInfoSource source)
	{
		GroupingUpdater.source=source;
	}

	public static void config(IdentifierValidatorFactory identifierValidatorFactory)
	{
		GroupingUpdater.identifierValidatorFactory=identifierValidatorFactory;
	}

	public static void config(HashGetterFactory hashGetterFactory)
	{
		GroupingUpdater.hashGetterFactory=hashGetterFactory;
	}

	public static Set<String> updateGroupingConfig(List<ConfigInfo> configInfoList, String oldModifiedTime)
	{
		Set<String> facetNameSet=new HashSet<>();
		for(ConfigInfo configInfo : configInfoList)
		{
			facetNameSet.add(configInfo.facetName);
			if(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(configInfo.updateTime).compareTo(oldModifiedTime)>0)
				GroupingUtil.configFacet(configInfo, identifierValidatorFactory.create(configInfo.facetName), hashGetterFactory.create(configInfo.facetName));
		}
		return facetNameSet;
	}

	/**
	 * Update {@link GroupingUtil} information immediately.
	 * 
	 * @param needDelete if {@code true}, any {@link Facet} instance that already exits in the {@link GroupingUtil} but does NOT present in the database query result will be removed, otherwise not.
	 * @param useRegex if {@code true}, {@code facetNames} should have length 1 and the only element will be considered as a regular expression, otherwise all {@code facetNames} are considered as the exact facet names.
	 * @param facetNames facets that to be updated. If not supply, updates all facets queried from the database.
	 * @return {@code true} if updated from source otherwise {@code false}.
	 * @throws Exception the type of {@link Exception} depends on the data source.
	 */
	public static boolean update(boolean needDelete, boolean useRegex, String... facetNames) throws Exception
	{
		List<ConfigInfo> configInfoList=null;
		String oldModifiedTime=null;
		source.open();
		try
		{
			oldModifiedTime=source.checkModified();
			if(oldModifiedTime!=null)
				configInfoList=source.getConfigInfoList(useRegex, facetNames);
		}
		finally
		{
			source.close();
		}
		if(configInfoList!=null)
		{
			Set<String> updatedSet=updateGroupingConfig(configInfoList, oldModifiedTime);
			if(needDelete)
			{
				Set<String> removeSet=GroupingUtil.getAllFacetName();
				removeSet.removeAll(updatedSet);
				GroupingUtil.removeFacets(removeSet);
			}
			return true;
		}
		return false;
	}

	/**
	 * Update {@link GroupingUtil} information immediately, and schedule an update task in every 1 day.
	 * 
	 * @param needDelete if {@code true}, any {@link Facet} instance that already exits in the {@link GroupingUtil} but does NOT present in the database query result will be removed, otherwise not.
	 * @param useRegex if {@code true}, {@code facetNames} should have length 1 and the only element will be considered as a regular expression, otherwise all {@code facetNames} are considered as the exact facet names.
	 * @param facetNames facets that to be updated. If not supply, updates all facets queried from the database.
	 */
	public static void schedule(boolean needDelete, boolean useRegex, String... facetNames)
	{
		schedule(1, TimeUnit.DAYS, needDelete, useRegex, facetNames);
	}

	/**
	 * Update {@link GroupingUtil} information immediately, and schedule an update task at a fixed rate.
	 * 
	 * @param period same as it defined at {@link ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)}
	 * @param timeUnit same as it defined at {@link ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)}
	 * @param needDelete if {@code true}, any {@link Facet} instance that already exits in the {@link GroupingUtil} but does NOT present in the database query result will be removed, otherwise not.
	 * @param useRegex if {@code true}, {@code facetNames} should have length 1 and the only element will be considered as a regular expression, otherwise all {@code facetNames} are considered as the exact facet names.
	 * @param facetNames facets that to be updated. If not supply, updates all facets queried from the database.
	 */
	public static void schedule(long period, TimeUnit timeUnit, boolean needDelete, boolean useRegex, String... facetNames)
	{
		schedule(0, period, timeUnit, needDelete, useRegex, facetNames);
	}

	/**
	 * Update {@link GroupingUtil} information after a specified {@code initialDelay}, and schedule an update task at fixed rate.
	 * Logs will be printed to stderr.
	 * 
	 * @param initialDelay same as it defined at {@link ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)}
	 * @param period same as it defined at {@link ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)}
	 * @param timeUnit same as it defined at {@link ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)}
	 * @param needDelete if {@code true}, any {@link Facet} instance that already exits in the {@link GroupingUtil} but does NOT present in the database query result will be removed, otherwise not.
	 * @param useRegex if {@code true}, {@code facetNames} should have length 1 and the only element will be considered as a regular expression, otherwise all {@code facetNames} are considered as the exact facet names.
	 * @param facetNames facets that to be updated. If not supply, updates all facets queried from the database.
	 */
	public static void schedule(long initialDelay, long period, TimeUnit timeUnit, boolean needDelete, boolean useRegex, final String... facetNames)
	{
		schedule(StdLoggers.STD_ERR_EXCEPTION_MSG_LOGGER, initialDelay, period, timeUnit, needDelete, useRegex, facetNames);
	}

	/**
	 * Update {@link GroupingUtil} information after a specified {@code initialDelay}, and schedule an update task at fixed rate.
	 * 
	 * @param loggerFunc a {@link BiConsumer} instance to process {@link Exception}s and error messages.
	 * @param initialDelay same as it defined at {@link ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)}
	 * @param period same as it defined at {@link ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)}
	 * @param timeUnit same as it defined at {@link ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)}
	 * @param needDelete if {@code true}, any {@link Facet} instance that already exits in the {@link GroupingUtil} but does NOT present in the database query result will be removed, otherwise not.
	 * @param useRegex if {@code true}, {@code facetNames} should have length 1 and the only element will be considered as a regular expression, otherwise all {@code facetNames} are considered as the exact facet names.
	 * @param facetNames facets that to be updated. If not supply, updates all facets queried from the database.
	 */
	public static void schedule(BiConsumer<? super Exception, String> loggerFunc, long initialDelay, long period, TimeUnit timeUnit, boolean needDelete, boolean useRegex, final String... facetNames)
	{
		if(timeUnit.toSeconds(period)<20)
		{
			period=20;
			timeUnit=TimeUnit.SECONDS;
		}
		SES.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run()
			{
				try
				{
					if(update(needDelete, useRegex, facetNames))
						loggerFunc.accept(null, "GroupingUpdater::schedule: grouping config updated. config="+GroupingUtil.getConfig());
				}
				catch(Exception e)
				{
					loggerFunc.accept(e, "GroupingUpdater::schedule: grouping config update failed!");
				}
			}
		}, initialDelay, period, timeUnit);
	}

	/**
	 * Stop the scheduled update task.
	 * 
	 * @see #schedule(boolean, boolean, String...)
	 * @see #schedule(long, TimeUnit, boolean, boolean, String...)
	 * @see #schedule(long, long, TimeUnit, boolean, boolean, String...)
	 */
	public static void stop()
	{
		SES.shutdownNow();
	}

	private GroupingUpdater()
	{
		throw new UnsupportedOperationException("GroupingUpdater::clone: this method is not allowed!");
	}

	@Override
	public GroupingUpdater clone()
	{
		throw new UnsupportedOperationException("GroupingUpdater::clone: this method is not allowed!");
	}
}
