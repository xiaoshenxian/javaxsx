package com.eroelf.javaxsx.util.trie;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

/**
 * An hash map Trie.
 * 
 * @author weikun.zhong
 */
public class HashMapTrie implements Trie
{
	private static final long serialVersionUID=7173302425077684642L;

	private boolean isWord=false;
	private Map<Character, HashMapTrie> children=new HashMap<Character, HashMapTrie>();

	@Override
	public void insert(String word)
	{
		if(word!=null)
		{
			char[] wordChars=word.toCharArray();
			HashMapTrie currentNode=this;
			for(int i=0; i<wordChars.length; i++)
			{
				if(currentNode.children.containsKey(wordChars[i]))
					currentNode=currentNode.children.get(wordChars[i]);
				else
				{
					HashMapTrie tn=new HashMapTrie();
					currentNode.children.put(wordChars[i], tn);
					currentNode=tn;
				}
			}
			currentNode.isWord=true;
		}
	}

	@Override
	public void delete(String word)
	{
		if(word!=null)
		{
			LinkedList<HashMapTrie> nodeQueue=new LinkedList<HashMapTrie>();
			char[] prefixChars=word.toCharArray();
			HashMapTrie currentNode=this;
			for(int i=0; i<prefixChars.length; i++)
			{
				nodeQueue.push(currentNode);
				currentNode=currentNode.children.get(prefixChars[i]);
				if(currentNode==null)
					return;
			}
			if(currentNode.isWord)
			{
				if(currentNode.children.isEmpty())
				{
					for(int i=prefixChars.length-1; i>=0; i--)
					{
						currentNode=nodeQueue.pop();
						currentNode.children.remove(prefixChars[i]);
						if(!currentNode.children.isEmpty())
							break;
					}
				}
				else
					currentNode.isWord=false;
			}
		}
	}

	@Override
	public boolean contains(String word)
	{
		if(word!=null)
		{
			char[] prefixChars=word.toCharArray();
			HashMapTrie currentNode=this;
			for(int i=0; i<prefixChars.length; i++)
			{
				currentNode=currentNode.children.get(prefixChars[i]);
				if(currentNode==null)
					return false;
			}
			if(currentNode.isWord)
				return true;
			else
				return false;
		}
		else
			return false;
	}

	@Override
	public List<String> matchPrefix(String prefix, List<String> resultList)
	{
		if(prefix!=null)
		{
			char[] prefixChars=prefix.toCharArray();
			HashMapTrie currentNode=this;
			for(int i=0; i<prefixChars.length; i++)
			{
				currentNode=currentNode.children.get(prefixChars[i]);
				if(currentNode==null)
					return resultList;
			}

			Queue<HashMapTrie> nodeQueue=new LinkedList<HashMapTrie>();
			Queue<String> suffixQueue=new LinkedList<String>();
			nodeQueue.add(currentNode);
			suffixQueue.add("");
			while(!nodeQueue.isEmpty())
			{
				currentNode=nodeQueue.remove();
				String suffix=suffixQueue.remove();
				if(currentNode.isWord)
					resultList.add(prefix+suffix);
				for(Entry<Character, HashMapTrie> entry : currentNode.children.entrySet())
				{
					nodeQueue.add(entry.getValue());
					suffixQueue.add(suffix+entry.getKey());
				}
			}
		}
		return resultList;
	}

	@Override
	public List<String> getPrefixes(String word, List<String> resultList)
	{
		if(word!=null)
		{
			char[] prefixChars=word.toCharArray();
			HashMapTrie currentNode=this;
			for(int i=0; i<prefixChars.length; i++)
			{
				currentNode=currentNode.children.get(prefixChars[i]);
				if(currentNode==null)
					break;
				else if(currentNode.isWord)
					resultList.add(word.substring(0, i+1));
			}
		}
		return resultList;
	}
}
