package com.eroelf.javaxsx.util.group;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.eroelf.javaxsx.util.StdLoggers;
import com.eroelf.javaxsx.util.Strings;
import com.eroelf.javaxsx.util.group.GroupingUtil.Group.GroupTask;
import com.eroelf.javaxsx.util.group.GroupingUtil.Group.Parameter;
import com.eroelf.javaxsx.util.group.GroupingUtil.Group.Result;

/**
 * This class provide entities and methods to deal with grouping issues such as grouping facets and groups organization, grouping section, and related information update interfaces.
 * 
 * @author weikun.zhong
 */
public final class GroupingUtil
{
	/**
	 * Any group object is an instance of this class.
	 * Any instance of this class holds information of the facet name and group name of the group it represents.
	 * 
	 * @author weikun.zhong
	 */
	public static final class Group
	{
		public static interface Parameter
		{}

		public static interface Result
		{}
	
		public static interface GroupTask extends Function<Parameter, Result>
		{}

		private final String facetName;
		private final String groupName;
		private transient GroupTask groupTask=GroupingUtil.IDLE;

		private Group(String facetName, String groupName)
		{
			this.facetName=facetName;
			this.groupName=groupName;
		}

		@Override
		public String toString()
		{
			return groupName;
		}

		public String getFacetName()
		{
			return facetName;
		}

		public String getGroupName()
		{
			return groupName;
		}

		/**
		 * Check if {@code this} group is the group with the given {@code name} in the facet represented by {@code facetName}.
		 * 
		 * @param facetName the name of the facet in which the group will be search.
		 * @param name the target group name.
		 * @return {@code true} if the specified group is the group that exists in the facet represented by {@code facetName}, and with its name equals the given name, otherwise {@code false}.
		 * 
		 * @see GroupingUtil#is(Group, String, String)
		 * @see Facet#is(Group, String)
		 */
		public boolean is(String facetName, String name)
		{
			return GroupingUtil.is(this, facetName, name);
		}

		/**
		 * If any project wish to delegate {@link Group} instances to handle its different logical paths ({@link Group} strategies), this method should be called to assigned a handler to this group before any call of {@link Group#apply(Parameter)}.
		 * This method should be called by calling {@link Facet#setTasks(GroupTask...)}
		 * 
		 * @param groupTask the task handler to be assigned to {@code this} {@link Group}. 
		 */
		public void setTask(GroupTask groupTask)
		{
			this.groupTask=groupTask;
		}

		/**
		 * If any project wish to delegate {@link Group} instances to handle its different logical paths ({@link Group} strategies), this method should be used with {@link Group#setTask(GroupTask)}.
		 * 
		 * @param param the input parameter.
		 * @return processed result by the strategy assigned to this group.
		 */
		public Result apply(Parameter param)
		{
			return groupTask.apply(param);
		}

		@Override
		public Group clone()
		{
			throw new UnsupportedOperationException("Group::clone: this method is not allowed!");
		}
	}

	/**
	 * Manage all groups of a specified grouping facet.
	 * 
	 * @author weikun.zhong
	 */
	public static final class Facet
	{
		private final String facetName;
		private final Map<String, Group> groupMap=new HashMap<>();
		private String suffix;
		private Group[] groups;
		private double[] sections;

		private IdentifierValidator identifierValidator;
		private HashGetter hashGetter;

		private ReadWriteLock lock=new ReentrantReadWriteLock(true);

		private Facet(String facetName, String suffix, Group[] groups, double[] sections, IdentifierValidator identifierValidator, HashGetter hashGetter, GroupTask... tasks)
		{
			this.facetName=facetName;
			this.suffix=suffix;
			this.identifierValidator=identifierValidator;
			this.hashGetter=hashGetter;
			setGroup(groups, sections, tasks);
		}

		private Facet(String facetName, String suffix, String[] groupNames, double[] sections, IdentifierValidator identifierValidator, HashGetter hashGetter, GroupTask... tasks)
		{
			this.facetName=facetName;
			this.suffix=suffix;
			this.identifierValidator=identifierValidator;
			this.hashGetter=hashGetter;
			setGroup(groupNames, sections, tasks);
		}

		private final void setGroup(Group[] groups, double[] sections, GroupTask... tasks)
		{
			lock.writeLock().lock();
			try
			{
				setSections(groups, sections);
				this.groups=groups;
				groupMap.clear();
				for(Group group : groups)
				{
					groupMap.put(group.groupName, group);
				}
			}
			finally
			{
				lock.writeLock().unlock();
			}
			setTasks(tasks);
		}

		private final void setGroup(String[] groupNames, double[] sections, GroupTask... tasks)
		{
			Group[] groups=new Group[groupNames.length];
			for(int i=0; i<groups.length; i++)
			{
				String name=groupNames[i];
				Group oldGroup=groupMap.get(name);
				if(oldGroup!=null)
					groups[i]=oldGroup;
				else
					groups[i]=new Group(facetName, name);
			}
			setGroup(groups, sections, tasks);
		}

		private final void setSections(Group[] groups, double[] sections)
		{
			lock.writeLock().lock();
			try
			{
				if(sections.length>=groups.length)
					throw new IllegalArgumentException("Facet::setSections: the length of sections must be smaller than group number!");
				this.sections=sections;
			}
			finally
			{
				lock.writeLock().unlock();
			}
		}

		public final void setSections(double[] sections)
		{
			setSections(groups, sections);
		}

		public final void setSuffix(String suffix)
		{
			this.suffix=suffix;
		}

		/**
		 * If any project wish to delegate group instances to handle its different logical paths (group strategies), this method should be called to assigned handlers to the facet groups before any call of {@link Group#apply}.
		 * 
		 * @param tasks the handlers of strategies for each groups, which will be assigned according to the order of the facet groups array.
		 */
		public final void setTasks(GroupTask... tasks)
		{
			int len=Math.min(this.groups.length, tasks.length);
			for(int i=0; i<len; i++)
			{
				this.groups[i].setTask(tasks[i]);
			}
		}

		/**
		 * Get the user group of this grouping facet via the user identifier.
		 * 
		 * @param identifier the user identifier.
		 * @return the group that this user should be assigned.
		 */
		public Group grouping(String identifier)
		{
			if(identifierValidator.isValidIdentifier(identifier))
			{
				double hash=hashGetter.hash(identifier);
				if(hash>=0 && hash<1)
				{
					lock.readLock().lock();
					try
					{
						int len=Math.min(sections.length, groups.length);
						int i=0;
						for(i=0; i<len; i++)
						{
							if(hash<sections[i])
								return groups[i];
						}
						return groups[i];
					}
					finally
					{
						lock.readLock().unlock();
					}
				}
			}
			lock.readLock().lock();
			try
			{
				return groups[groups.length-1];
			}
			finally
			{
				lock.readLock().unlock();
			}
		}

		/**
		 * Check if the specified group is the {@code group} with the given name in this facet.
		 * 
		 * @param group the group to be checked.
		 * @param name the target name.
		 * @return return {@code true} if the specified group is the group that exists in this facet and with its name equals the given name, otherwise {@code false}.
		 * 
		 * @see GroupingUtil#is(Group, String, String)
		 * @see Group#is(String, String)
		 */
		public boolean is(Group group, String name)
		{
			return group==groupMap.get(name);
		}

		/**
		 * Get the group specified by the {@code groupName} in this facet.
		 * 
		 * @param groupName the name of the group.
		 * @return the group with the specified {@code groupName} or {@link GroupingUtil#NA} if not found.
		 */
		public Group getGroup(String groupName)
		{
			Group group=groupMap.get(groupName);
			return group!=null ? group : GroupingUtil.NA;
		}

		public Group[] getAllGroup()
		{
			lock.readLock().lock();
			try
			{
				return Arrays.copyOf(groups, groups.length);
			}
			finally
			{
				lock.readLock().unlock();
			}
		}

		public double[] getSections()
		{
			lock.readLock().lock();
			try
			{
				return Arrays.copyOf(sections, sections.length);
			}
			finally
			{
				lock.readLock().unlock();
			}
		}

		public double[] getProportions()
		{
			double[] sections;
			int length;
			lock.readLock().lock();
			try
			{
				sections=getSections();
				length=groups.length;
			}
			finally
			{
				lock.readLock().unlock();
			}
			double[] proportions=new double[length];
			double cut=0;
			for(int i=0; i<length; i++)
			{
				if(i<sections.length)
				{
					proportions[i]=sections[i]-cut;
					cut=sections[i];
				}
				else
				{
					proportions[i]=1-cut;
					break;
				}
			}
			return proportions;
		}

		public Map<Group, Double> getProportionMap()
		{
			Group[] groups;
			double[] proportions;
			lock.readLock().lock();
			try
			{
				groups=getAllGroup();
				proportions=getProportions();
			}
			finally
			{
				lock.readLock().unlock();
			}
			Map<Group, Double> res=new LinkedHashMap<>();
			for(int i=0; i<groups.length; i++)
			{
				res.put(groups[i], proportions[i]);
			}
			return res;
		}

		@Override
		public String toString()
		{
			StringBuilder stringBuilder=new StringBuilder(facetName).append(":");
			StringBuilder stringBuilder2=new StringBuilder(":");
			lock.readLock().lock();
			try
			{
				stringBuilder.append(suffix).append(":");
				for(Group group : groups)
				{
					stringBuilder.append(group.groupName).append(",");
				}
				for(double d : sections)
				{
					stringBuilder2.append(d).append(",");
				}
			}
			finally
			{
				lock.readLock().unlock();
			}
			if(stringBuilder.length()>0 && stringBuilder.charAt(stringBuilder.length()-1)==',')
				stringBuilder.deleteCharAt(stringBuilder.length()-1);
			if(stringBuilder2.length()>0 && stringBuilder2.charAt(stringBuilder2.length()-1)==',')
				stringBuilder2.deleteCharAt(stringBuilder2.length()-1);
			return stringBuilder.append(stringBuilder2).toString();
		}

		@Override
		public Facet clone()
		{
			throw new UnsupportedOperationException("Facet::clone: this method is not allowed!");
		}
	}

	private static final Group NA=new Group("__UNDEFINED__", "__NA__");
	private static final GroupTask IDLE=new GroupTask() {
		@Override
		public Result apply(Parameter param)
		{
			return null;
		}
	};
	private static final Map<String, Facet> FACET_MAP=new HashMap<>();
	private static final ReadWriteLock LOCK=new ReentrantReadWriteLock(true);

	/**
	 * Get the user group of the grouping facet specified by the {@code facetName} via the user identifier.
	 * 
	 * @param facetName the grouping facet name.
	 * @param identifier identifier the user identifier.
	 * @return the group that this user should be assigned.
	 */
	public static Group getGroupFromIdentifier(String facetName, String identifier)
	{
		Facet facet=FACET_MAP.get(facetName);
		return facet!=null ? facet.grouping(identifier) : NA;
	}

	/**
	 * Check if the specified group is the group with the given {@code name} in the facet represented by {@code facetName}.
	 * 
	 * @param group the group to be checked.
	 * @param facetName the name of the facet in which the group will be search.
	 * @param name the target group name.
	 * @return return {@code true} if the specified group is the group that exists in the facet represented by {@code facetName}, and with its name equals the given name, otherwise {@code false}.
	 * 
	 * @see Facet#is(Group, String)
	 * @see Group#is(String, String)
	 */
	public static boolean is(Group group, String facetName, String name)
	{
		Facet facet=FACET_MAP.get(facetName);
		return facet!=null ? facet.is(group, name) : false;
	}

	public static Facet getFacet(String facetName)
	{
		return FACET_MAP.get(facetName);
	}

	/**
	 * Get the group specified by the {@code groupName} in the facet specified by the {@code facetName}.
	 * 
	 * @param facetName the name of the facet.
	 * @param groupName the name of the group.
	 * @return the group with the specified {@code groupName} in the facet specified by the {@code facetName}, or {@link GroupingUtil#NA} if not found.
	 */
	public static Group getGroup(String facetName, String groupName)
	{
		Facet facet=FACET_MAP.get(facetName);
		return facet!=null ? facet.getGroup(groupName) : NA;
	}

	public static Group[] getAllGroup(String facetName)
	{
		Facet facet=FACET_MAP.get(facetName);
		return facet!=null ? facet.getAllGroup() : null;
	}

	public static double[] getSections(String facetName)
	{
		Facet facet=FACET_MAP.get(facetName);
		return facet!=null ? facet.getSections() : null;
	}

	public static double[] getProportions(String facetName)
	{
		Facet facet=FACET_MAP.get(facetName);
		return facet!=null ? facet.getProportions() : null;
	}

	public static Map<Group, Double> getProportionMap(String facetName)
	{
		Facet facet=FACET_MAP.get(facetName);
		return facet!=null ? facet.getProportionMap() : null;
	}

	public static void setFacetSections(String facetName, double[] sections)
	{
		Facet facet=FACET_MAP.get(facetName);
		if(facet!=null)
			facet.setSections(sections);
		else
			throw new IllegalArgumentException("GroupingUtil::setFacetSections: no facet with facetName='"+facetName+"' found!");
	}

	public static void setFacetSuffix(String facetName, String suffix)
	{
		Facet facet=FACET_MAP.get(facetName);
		if(facet!=null)
			facet.setSuffix(suffix);
		else
			throw new IllegalArgumentException("GroupingUtil::setFacetSuffix: no facet with facetName='"+facetName+"' found!");
	}

	/**
	 * If any project wish to delegate group instances to handle its different logical paths (group strategies) for the grouping facet specified by the {@code facetName}, this method should be called to assigned handlers to the facet groups before any call of {@link Group#apply}.
	 * 
	 * @param facetName the grouping facet name.
	 * @param tasks the handlers of strategies for each groups of the facet specified by the {@code facetName}, which will be assigned according to the order of the facet groups array.
	 */
	public static void setFacetTask(String facetName, GroupTask... tasks)
	{
		Facet facet=FACET_MAP.get(facetName);
		if(facet!=null)
			facet.setTasks(tasks);
		else
			throw new IllegalArgumentException("GroupingUtil::setFacetTask: no facet with facetName='"+facetName+"' found!");
	}

	/**
	 * Configure the facet specified by the {@code facetName}.
	 * 
	 * @param facetName the grouping facet name.
	 * @param suffix the suffix add to the user identifier before performing MD5 sum, aiming to reduce interference between facets.
	 * @param groupNames names of groups
	 * @param sections indicates the quota of each group. sections.length must be less than groupNames.length.
	 * @param identifierValidator define {@link IdentifierValidator} to validate identifiers.
	 * @param hashGetter define the {@link HashGetter} object to calculate hashes.
	 * @param tasks optional, if wish to delegate group instances to handle group strategies.
	 */
	public static void configFacet(String facetName, String suffix, String[] groupNames, double[] sections, IdentifierValidator identifierValidator, HashGetter hashGetter, GroupTask... tasks)
	{
		Facet facet=FACET_MAP.get(facetName);
		if(facet!=null)
		{
			facet.suffix=suffix;
			facet.setGroup(groupNames, sections, tasks);
		}
		else
			FACET_MAP.put(facetName, new Facet(facetName, suffix, groupNames, sections, identifierValidator, hashGetter, tasks));
	}

	/**
	 * Configure the facet specified by the {@code facetName}.
	 * 
	 * @param facetName the grouping facet name.
	 * @param suffix the suffix add to the user identifier before performing MD5 sum, aiming to reduce interference between facets.
	 * @param groupNames names of groups
	 * @param sectionStrs indicates the quota of each group, will be convert to {@code double[]}. sections.length must be less than groupNames.length.
	 * @param identifierValidator define {@link IdentifierValidator} to validate identifiers.
	 * @param hashGetter define the {@link HashGetter} object to calculate hashes.
	 * @param tasks optional, if wish to delegate group instances to handle group strategies.
	 */
	public static void configFacet(String facetName, String suffix, String[] groupNames, String[] sectionStrs, IdentifierValidator identifierValidator, HashGetter hashGetter, GroupTask... tasks)
	{
		double[] sections=new double[sectionStrs.length];
		for(int i=0; i<sections.length; i++)
		{
			sections[i]=Double.parseDouble(sectionStrs[i]);
		}
		configFacet(facetName, suffix, groupNames, sections, identifierValidator, hashGetter, tasks);
	}

	/**
	 * Configure the facet specified by the {@code facetName}.
	 * 
	 * @param facetName the grouping facet name.
	 * @param suffix the suffix add to the user identifier before performing MD5 sum, aiming to reduce interference between facets.
	 * @param groupStr names of groups, will be split by ','.
	 * @param sections indicates the quota of each group, will be split by ',' and convert to {@code double[]}. The split length must be less than the split length of {@code groupStr}.
	 * @param identifierValidator define {@link IdentifierValidator} to validate identifiers.
	 * @param hashGetter define the {@link HashGetter} object to calculate hashes.
	 * @param tasks optional, if wish to delegate group instances to handle group strategies.
	 */
	public static void configFacet(String facetName, String suffix, String groupStr, String sections, IdentifierValidator identifierValidator, HashGetter hashGetter, GroupTask... tasks)
	{
		configFacet(facetName, suffix, groupStr.split(","), sections.split(","), identifierValidator, hashGetter, tasks);
	}

	/**
	 * Configure the facet specified by the {@code facetName}.
	 * 
	 * @param configInfo stores the group information, refer to {@link #configFacet(String, String, String, String, IdentifierValidator, HashGetter, GroupTask...)} for detail.
	 * @param identifierValidator define {@link IdentifierValidator} to validate identifiers.
	 * @param hashGetter define the {@link HashGetter} object to calculate hashes.
	 * @param tasks optional, if wish to delegate group instances to handle group strategies.
	 */
	public static void configFacet(ConfigInfo configInfo, IdentifierValidator identifierValidator, HashGetter hashGetter, GroupTask... tasks)
	{
		configFacet(configInfo.facetName, configInfo.suffix, configInfo.groupStr, configInfo.sections, identifierValidator, hashGetter, tasks);
	}

	/**
	 * Configure all facets represented by {@code facetConfigs}.
	 * Logs will be printed to stderr.
	 * 
	 * @param identifierValidator define {@link IdentifierValidator} to validate identifiers.
	 * @param hashGetter define the {@link HashGetter} object to calculate hashes.
	 * @param facetConfigs configuration strings. One typical format should looks like "facetName:suffix:A,B,C,D:0.2,0.4,1"
	 */
	public static void config(IdentifierValidator identifierValidator, HashGetter hashGetter, String... facetConfigs)
	{
		config(StdLoggers.STD_ERR_EXCEPTION_MSG_LOGGER, identifierValidator, hashGetter, facetConfigs);
	}

	/**
	 * Configure all facets represented by {@code facetConfigs}.
	 * 
	 * @param loggerFunc a {@link BiConsumer} instance to process {@link Exception}s and error messages.
	 * @param identifierValidator define {@link IdentifierValidator} to validate identifiers.
	 * @param hashGetter define the {@link HashGetter} object to calculate hashes.
	 * @param facetConfigs configuration strings. One typical format should looks like "facetName:suffix:A,B,C,D:0.2,0.4,1"
	 */
	public static void config(BiConsumer<? super Exception, String> loggerFunc, IdentifierValidator identifierValidator, HashGetter hashGetter, String... facetConfigs)
	{
		for(String onefacetStr : facetConfigs)
		{
			try
			{
				String[] items=onefacetStr.split(":");
				String facetName=items[0];
				String suffix=items[1];
				String[] groupNames=items[2].split(",");
				String[] sectionStrs=items[3].split(",");
				configFacet(facetName, suffix, groupNames, sectionStrs, identifierValidator, hashGetter);
			}
			catch(Exception e)
			{
				loggerFunc.accept(e, "GroupingUtil::config: error detected in ####"+onefacetStr+"#### !");
			}
		}
		loggerFunc.accept(null, "GroupingUtil::config: update finished.");
	}

	/**
	 * Configure all facets represented by {@code configStr}.
	 * Logs will be printed to stderr.
	 * 
	 * @param identifierValidator define {@link IdentifierValidator} to validate identifiers.
	 * @param hashGetter define the {@link HashGetter} object to calculate hashes.
	 * @param configStr the configuration string. One typical format should looks like "facetName1:suffix1:A,B,C,D:0.2,0.4,1#facetName2:suffix2:A1,A2,B:0.2,0.8"
	 */
	public static void config(IdentifierValidator identifierValidator, HashGetter hashGetter, String configStr)
	{
		config(StdLoggers.STD_ERR_EXCEPTION_MSG_LOGGER, identifierValidator, hashGetter, configStr);
	}

	/**
	 * Configure all facets represented by {@code configStr}.
	 * 
	 * @param loggerFunc a {@link BiConsumer} instance to process {@link Exception}s and error messages.
	 * @param identifierValidator define {@link IdentifierValidator} to validate identifiers.
	 * @param hashGetter define the {@link HashGetter} object to calculate hashes.
	 * @param configStr the configuration string. One typical format should looks like "facetName1:suffix1:A,B,C,D:0.2,0.4,1#facetName2:suffix2:A1,A2,B:0.2,0.8"
	 */
	public static void config(BiConsumer<? super Exception, String> loggerFunc, IdentifierValidator identifierValidator, HashGetter hashGetter, String configStr)
	{
		if(Strings.isValid(configStr))
			config(loggerFunc, identifierValidator, hashGetter, configStr.split("#"));
		else
			loggerFunc.accept(null, "GroupingUtil::config: invalid configStr! No change made.");
	}

	/**
	 * Configure all facets represented by {@code configInfos}.
	 * 
	 * @param identifierValidator define {@link IdentifierValidator} to validate identifiers.
	 * @param hashGetter define the {@link HashGetter} object to calculate hashes.
	 * @param configInfos array of configInfos stores the group information, refer to {@link #configFacet(String, String, String, String, IdentifierValidator, HashGetter, GroupTask...)} for detail.
	 */
	public static void config(IdentifierValidator identifierValidator, HashGetter hashGetter, ConfigInfo... configInfos)
	{
		for(ConfigInfo configInfo : configInfos)
		{
			configFacet(configInfo, identifierValidator, hashGetter);
		}
	}

	/**
	 * Configure all facets represented by {@code configInfos}.
	 * 
	 * @param identifierValidator define {@link IdentifierValidator} to validate identifiers.
	 * @param hashGetter define the {@link HashGetter} object to calculate hashes.
	 * @param configInfos {@link Iterable} object stores {@link ConfigInfo} objects which stores the group information, refer to {@link #configFacet(String, String, String, String, IdentifierValidator, HashGetter, GroupTask...)} for detail.
	 */
	public static void config(IdentifierValidator identifierValidator, HashGetter hashGetter, Iterable<ConfigInfo> configInfos)
	{
		for(ConfigInfo configInfo : configInfos)
		{
			configFacet(configInfo, identifierValidator, hashGetter);
		}
	}

	/**
	 * Get the configuration string of all facets.
	 * 
	 * @return the configuration string
	 * @see GroupingUtil#config(IdentifierValidator, HashGetter, String)
	 */
	public static String getConfig()
	{
		StringBuilder stringBuilder=new StringBuilder();
		LOCK.readLock().lock();
		try
		{
			for(Facet facet : FACET_MAP.values())
			{
				stringBuilder.append(facet.toString()).append("#");
			}
		}
		finally
		{
			LOCK.readLock().unlock();
		}
		if(stringBuilder.length()>0 && stringBuilder.charAt(stringBuilder.length()-1)=='#')
			stringBuilder.deleteCharAt(stringBuilder.length()-1);
		return stringBuilder.toString();
	}

	public static Set<String> getAllFacetName()
	{
		LOCK.readLock().lock();
		try
		{
			return new HashSet<>(FACET_MAP.keySet());
		}
		finally
		{
			LOCK.readLock().unlock();
		}
	}

	public static void removeFacet(String facetName)
	{
		LOCK.writeLock().lock();
		try
		{
			FACET_MAP.remove(facetName);
		}
		finally
		{
			LOCK.writeLock().unlock();
		}
	}

	public static void removeFacets(Iterable<String> facetNames)
	{
		LOCK.writeLock().lock();
		try
		{
			for(String facetName : facetNames)
			{
				FACET_MAP.remove(facetName);
			}
		}
		finally
		{
			LOCK.writeLock().unlock();
		}
	}

	/**
	 * Returns a mark group indicate NO group information.
	 * 
	 * @return {@link GroupingUtil#NA}
	 */
	public static Group getNA()
	{
		return NA;
	}

	/**
	 * Returns a handler that do nothing.
	 * 
	 * @return {@link GroupingUtil#IDLE}
	 */
	public static GroupTask getIdleTask()
	{
		return IDLE;
	}

	public static Map<String, Group> convertMap(Map<String, String> theMap)
	{
		Map<String, Group> res=new HashMap<>();
		for(Entry<String, String> entry : theMap.entrySet())
		{
			res.put(entry.getKey(), getGroup(entry.getKey(), entry.getValue()));
		}
		return res;
	}

	public static Map<String, Group> convertToMap(Set<Group> theSet)
	{
		Map<String, Group> res=new HashMap<>();
		for(Group Group : theSet)
		{
			res.put(Group.getFacetName(), Group);
		}
		return res;
	}

	public static Set<Group> convertToSet(Map<String, String> theMap)
	{
		return new HashSet<>(convertMap(theMap).values());
	}

	private GroupingUtil()
	{
		throw new UnsupportedOperationException("GroupingUtil::constructor: this method is not allowed!");
	}

	@Override
	public GroupingUtil clone()
	{
		throw new UnsupportedOperationException("GroupingUtil::clone: this method is not allowed!");
	}
}
