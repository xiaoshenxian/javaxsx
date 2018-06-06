package com.eroelf.javaxsx.util.trie;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import com.eroelf.javaxsx.util.StdLoggers;

/**
 * An class used to split data into specified number of Tries.
 * 
 * @author weikun.zhong
 */
public class IndexedTrie implements Serializable
{
	private static final long serialVersionUID=6187718942527593011L;

	private Trie[] tries;
	private int[][] indeses=new int[(int)Character.MAX_VALUE+1][];
	private int avgTireSize;
	private BiConsumer<? super Exception, String> loggerFunc;

	public <T extends Trie> IndexedTrie(Class<T> trieClass) throws InstantiationException, IllegalAccessException
	{
		this(trieClass, StdLoggers.STD_ERR_EXCEPTION_MSG_LOGGER);
	}

	@SuppressWarnings("unchecked")
	public <T extends Trie> IndexedTrie(Class<T> trieClass, BiConsumer<? super Exception, String> loggerFunc) throws InstantiationException, IllegalAccessException
	{
		setLoggerFunc(loggerFunc);
		tries=(T[])Array.newInstance(trieClass, 1);
		tries[0]=trieClass.newInstance();
		for(int i=0; i<indeses.length; i++)
		{
			indeses[i]=new int[1];
			indeses[i][0]=0;
		}
	}

	public <T extends Trie> IndexedTrie(Class<T> trieClass, int size) throws InstantiationException, IllegalAccessException
	{
		this(trieClass, size, StdLoggers.STD_ERR_EXCEPTION_MSG_LOGGER);
	}

	@SuppressWarnings("unchecked")
	public <T extends Trie> IndexedTrie(Class<T> trieClass, int size, BiConsumer<? super Exception, String> loggerFunc) throws InstantiationException, IllegalAccessException
	{
		setLoggerFunc(loggerFunc);
		if(size<=0)
			size=1;
		tries=(T[])Array.newInstance(trieClass, size);
		for(int i=0; i<size; i++)
		{
			tries[i]=trieClass.newInstance();
		}
	}

	private char getFirstCharacter(String s)
	{
		return s.isEmpty() ? Character.MIN_VALUE : s.charAt(0);
	}

	private void constructIndeses(String fileNameString, int fieldIdx, int residueThreshold)
	{
		final Map<Character, Integer> numMap=new HashMap<Character, Integer>();

		avgTireSize=0;
		try
		{
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(fileNameString), "utf-8")); 
			String line;
			int count=0;
			String lastField=null;
			while((line=br.readLine())!=null)
			{
				if(++count%1000000==0)
					loggerFunc.accept(null, "constructIndeses::count="+count);
				String field=fieldIdx>=0 ? line.split("\t")[fieldIdx] : line;
				if(!field.equals(lastField))
				{
					lastField=field;
					Character ch=getFirstCharacter(field);
					if(numMap.containsKey(ch))
						numMap.put(ch, numMap.get(ch)+1);
					else
						numMap.put(ch, 1);
					++avgTireSize;
				}
			}
			br.close();
		}
		catch(Exception e)
		{
			loggerFunc.accept(e, null);
		}
		avgTireSize=(int)Math.ceil((double)avgTireSize/(double)tries.length);
		if(residueThreshold<0)
			residueThreshold=0;
		else if(residueThreshold>avgTireSize)
			residueThreshold=avgTireSize;

		List<Entry<Character, Integer>> list=new ArrayList<Entry<Character, Integer>>(numMap.entrySet());
		Collections.sort(list, new Comparator<Entry<Character, Integer>>() {
			@Override
			public int compare(Entry<Character, Integer> o1, Entry<Character, Integer> o2)
			{
				int v1=o1.getValue();
				int v2=o2.getValue();
				return v1<v2 ? 1 : (v1==v2 ? 0 : -1);
			}
		});

		class Pair
		{
			private int size;
			private int idx;
			private Pair(int size, int idx)
			{
				this.size=size;
				this.idx=idx;
			}
		}
		final Queue<Pair> heap=new PriorityQueue<Pair>((int)Character.MAX_VALUE, new Comparator<Pair>() {
			@Override
			public int compare(Pair o1, Pair o2)
			{
				int v1=o1.size;
				int v2=o2.size;
				if(v1<v2)
					return -1;
				else if(v1==v2)
				{
					int idx1=o1.idx;
					int idx2=o2.idx;
					return idx1<idx2 ? -1 : (idx1==idx2 ? 0 : 1);
				}
				else
					return 1;
			}
		});
		for(int i=0; i<tries.length; i++)
		{
			heap.add(new Pair(0, i));
		}

		for(Entry<Character, Integer> entry : list)
		{
			int idx=(int)entry.getKey();
			if(indeses[idx]==null)
			{
				int frequency=entry.getValue();
				int trieNum=frequency/avgTireSize;
				int residue=frequency%avgTireSize;
				if(trieNum==0 || residue>residueThreshold)
					++trieNum;
				indeses[idx]=new int[trieNum];
				if(trieNum==1)
					indeses[idx][0]=frequency;
				else
				{
					Arrays.fill(indeses[idx], avgTireSize);
					if(residue>residueThreshold)
						indeses[idx][trieNum-1]=residue;
					else
						indeses[idx][trieNum-1]+=residue;
				}
				for(int i=0; i<trieNum; i++)
				{
					Pair pair=heap.poll();
					pair.size+=indeses[idx][i];
					indeses[idx][i]=pair.idx;
					heap.add(pair);
				}
			}
			else
			{
				loggerFunc.accept(null, "constructIndeses::file ("+fileNameString+") format error!");
				break;
			}
		}
		loggerFunc.accept(null, "Indeses constructed.");
	}

	public void batchAdd(String fileNameString, int fieldIdx, int residueThreshold)
	{
		constructIndeses(fileNameString, fieldIdx, residueThreshold);
		class Inserter implements Runnable
		{
			private LinkedBlockingQueue<String> wordsQueue=new LinkedBlockingQueue<String>();
			private Trie theTrie;
			private int idx;

			private Inserter(Trie theTrie, int idx)
			{
				this.theTrie=theTrie;
				this.idx=idx;
			}

			private void insertWord(String word) throws InterruptedException
			{
				wordsQueue.put(word);
			}

			@Override
			public void run()
			{
				String word=null;
				int count=0;
				try
				{
					while(!Thread.interrupted())
					{
						word=wordsQueue.take();
						theTrie.insert(word);
						++count;
					}
				}
				catch(InterruptedException e)
				{}
				while(!wordsQueue.isEmpty())
				{
					word=wordsQueue.remove();
					theTrie.insert(word);
					++count;
				}
				loggerFunc.accept(null, this.toString()+" finished with "+count+" records loaded.");
			}

			@Override
			public String toString()
			{
				return String.format("%s % 4d:", getClass().getSimpleName(), idx);
			}
		}

		try
		{
			ExecutorService es=Executors.newFixedThreadPool(tries.length);
			Inserter[] inserters=new Inserter[tries.length];
			for(int i=0; i<inserters.length; i++)
			{
				inserters[i]=new Inserter(tries[i], i);
				es.execute(inserters[i]);
			}

			try(BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(fileNameString), "utf-8")))
			{
				String line;
				String lastfield=null;
				int lastFirstCharIntValue=Integer.MAX_VALUE;
				int count=0;
				int[] idxArray=null;
				int charIdx=0;
				int charSize=0;
				while((line=br.readLine())!=null)
				{
					String field=fieldIdx>=0 ? line.split("\t")[fieldIdx] : line;
					if(++count%100000==0)
						loggerFunc.accept(null, "batchAdd::count="+count);
					if(!field.equals(lastfield))
					{
						lastfield=field;
						char firstChar=getFirstCharacter(field);
						if(firstChar!=lastFirstCharIntValue)
						{
							lastFirstCharIntValue=firstChar;
							if(indeses[(int)getFirstCharacter(field)]==null)
							{
								int idx=(int)firstChar%tries.length;
								indeses[(int)firstChar]=new int[1];
								indeses[(int)firstChar][0]=idx;
								loggerFunc.accept(null, "batchAdd::Unindexed first character: "+getFirstCharacter(field)+", set to "+idx+".");
							}
							idxArray=indeses[(int)firstChar];
							charIdx=0;
							charSize=0;
						}
						inserters[idxArray[charIdx]].insertWord(field);
						if(++charSize>=avgTireSize && charIdx<idxArray.length-1)
						{
							++charIdx;
							charSize=0;
						}
					}
				}
			}
			finally
			{
				do
				{
					es.shutdownNow();
				} while(!es.awaitTermination(1, TimeUnit.SECONDS));
			}
			loggerFunc.accept(null, "batchAdd finished.");
		}
		catch(Exception e)
		{
			loggerFunc.accept(e, null);
		}
	}

	public boolean contains(String text)
	{
		int[] idxArray=indeses[(int)getFirstCharacter(text)];
		if(idxArray!=null)
		{
			for(int idx : idxArray)
			{
				if(tries[idx].contains(text))
				{
					return true;
				}
			}
		}
		return false;
	}

//	@Override
//	public List<String> matchesFromStart(String text, int n)
//	{
//		if(n<=0)
//		{
//			n=Integer.MAX_VALUE;
//		}
//		int[] idxArray=indeses[(int)getFirstCharacter(text)];
//		if(idxArray!=null)
//		{
//			if(idxArray.length==1)
//			{
//				return tries[idxArray[0]].matchesFromStart(text, n);
//			}
//			else
//			{
//				List<String> res=new ArrayList<String>();
//				for(int idx : idxArray)
//				{
//					List<String> tempList=tries[idx].matchesFromStart(text, n);
//					res.addAll(tempList);
//					n-=tempList.size();
//					if (n<=0)
//						break;
//				}
//				return res;
//			}
//		}
//		else
//		{
//			return new ArrayList<String>();
//		}
//	}
//
//	@Override
//	public List<String> matchesFromAnywhere(String text)
//	{
//		Set<String> wordSet=new HashSet<String>();
//		for(int i = 0; i < text.length(); i++)
//		{
//			wordSet.addAll(matchesFromStart(text.substring(i), 0));	
//		}
//		return new ArrayList<String>(wordSet);
//	}

	public void matchPrefix(String text, List<String> resultList)
	{
		int[] idxArray=indeses[(int)getFirstCharacter(text)];
		if(idxArray!=null)
		{
			for(int idx : idxArray)
			{
				tries[idx].matchPrefix(text, resultList);
			}
		}
	}

	public void setLoggerFunc(BiConsumer<? super Exception, String> loggerFunc)
	{
		if(loggerFunc==null)
			throw new NullPointerException("setLoggerFunc::loggerFunc should not be null");
		this.loggerFunc=loggerFunc;
	}

	public void saveToFile(String fileNameString)
	{
		try
		{
			ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(fileNameString));
			try
			{
				oos.writeObject(this);
			}
			finally
			{
				oos.close();
			}
		}
		catch(Exception e)
		{
			loggerFunc.accept(e, null);
		}
	}

	public static IndexedTrie readFromFile(String fileNameString)
	{
		return readFromFile(fileNameString, StdLoggers.STD_ERR_EXCEPTION_MSG_LOGGER);
	}

	public static IndexedTrie readFromFile(String fileNameString, BiConsumer<? super Exception, String> loggerFunc)
	{
		IndexedTrie indexedDATrie=null;
		try
		{
			ObjectInputStream ois=new ObjectInputStream(new FileInputStream(fileNameString));
			try
			{
				indexedDATrie=(IndexedTrie)ois.readObject();
				if(indexedDATrie.loggerFunc==null)
					indexedDATrie.setLoggerFunc(loggerFunc);
			}
			finally
			{
				ois.close();
			}
		}
		catch(Exception e)
		{
			loggerFunc.accept(e, null);
		}
		return indexedDATrie;
	}
}
