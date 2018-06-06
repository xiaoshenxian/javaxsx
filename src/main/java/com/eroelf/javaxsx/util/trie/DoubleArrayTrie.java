package com.eroelf.javaxsx.util.trie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A double-array Trie.
 * 
 * @author weikun.zhong
 */
public class DoubleArrayTrie implements Trie
{
	private static final long serialVersionUID=5821768635376919780L;

	private static final int DEFAULT_INITIAL_SIZE=Character.MAX_VALUE;
	private static final int DA_LIST_HEAD=0;
	private static final int DA_POOL_HEAD=1;
	private static final int DA_POOL_OFFSET=2;

	private int[] base;
	private int[] check;
	private String[] tail;
	private StringBuilder[] children;

//	private Map<Character, Integer> charMap = new HashMap<Character, Integer>();
//	private Set<Character> charSet = new HashSet<Character>();
	private List<Character> charList = new ArrayList<Character>();

	public DoubleArrayTrie()
	{
		this(DEFAULT_INITIAL_SIZE);
	}

	public DoubleArrayTrie(int initialSize)
	{
		if(initialSize<=0)
			initialSize=DEFAULT_INITIAL_SIZE;
		initialSize+=DA_POOL_OFFSET;
		base=new int[initialSize];
		check=new int[initialSize];
		tail=new String[initialSize];
		children=new StringBuilder[initialSize];
		initEmptyCellIdx(DA_POOL_OFFSET, base.length);
		base[DA_POOL_HEAD]=DA_LIST_HEAD;
//		charList.add(null);
	}

	private void extendArray(int toLength)
	{
		if(toLength>base.length)
		{
			int begin=base.length;
			base=Arrays.copyOf(base, toLength);
			check=Arrays.copyOf(check, toLength);
			tail=Arrays.copyOf(tail, toLength);
			children=Arrays.copyOf(children, toLength);
			initEmptyCellIdx(begin, toLength);
		}
	}

	private void initEmptyCellIdx(int begin, int end)
	{
		for(int i=begin; i<base.length-1; i++)
		{
			check[i]=-i-1;
			base[i+1]=-i;
		}
		check[-base[DA_LIST_HEAD]]=-begin;
		check[base.length-1]=DA_LIST_HEAD;
		base[begin]=base[DA_LIST_HEAD];
		base[DA_LIST_HEAD]=-base.length+1;
	}

//	private boolean hasChild(int s)
//	{
//		int baseValue=base[s];
//		if (baseValue>DA_LIST_HEAD)
//		{
//			//int maxC=Math.min(charMap.size(), check.length-baseValue-1);
//			//for (int i = 1; i <= maxC; i++)
//			for (Character c : charList)
//			{
//				int next=baseValue+c;
//				if (next>=check.length)
//				{
//					break;
//				}
//				if (check[next]==s)
//				{
//					return true;
//				}
//			}
//		}
//		return false;
//	}
//
//	private List<Integer> getChildEdges(int s)
//	{
//		List<Integer> res = new ArrayList<Integer>();
//		int baseValue=base[s];
//		if (baseValue>DA_LIST_HEAD)
//		{
//			//int maxC=Math.min(charMap.size(), check.length-baseValue-1);
//			//for (int i = 1; i <= maxC; i++)
//			for (Character c : charList)
//			{
//				int next=baseValue+c;
//				if (next>=check.length)
//				{
//					break;
//				}
//				if (check[next]==s)
//				{
//					res.add((int)c);
//				}
//			}
//		}
//		return res;
//	}

	private boolean hasChild(int s)
	{
		return children[s]!=null;
	}

	private List<Integer> getChildren(int s)
	{
		List<Integer> res=new ArrayList<Integer>();
		if(children[s]!=null)
		{
			for(int i=0; i<children[s].length(); i++)
			{
				res.add((int)children[s].charAt(i));
			}
		}
		return res;
	}

	private void allocCell(int pos)
	{
		int prev=-base[pos];
		int next=-check[pos];

		check[prev]=-next;
		base[next]=-prev;
	}

	private void allocCell(int pos, int baseValue, int checkValue, String tailValue, StringBuilder childrenValue)
	{
		allocCell(pos);
		base[pos]=baseValue;
		check[pos]=checkValue;
		tail[pos]=tailValue;
		children[pos]=childrenValue;
	}

	private void freeCell(int pos)
	{
		int t=-check[DA_LIST_HEAD];
		while(check[t]!=DA_LIST_HEAD && t<pos)
		{
			t=-check[t];
		}

		check[pos]=-t;
		check[-base[t]]=-pos;
		base[pos]=base[t];
		base[t]=-pos;
		tail[pos]=null;
		children[pos]=null;
	}

	private boolean checkFreeCell(int pos)
	{
		if(pos+DA_POOL_OFFSET>=check.length)
		{
			extendArray(Math.max(pos+DA_POOL_OFFSET, check.length*2));
		}
		return check[pos]<DA_LIST_HEAD;
	}

	private int searchASlot(List<Integer> cList)
	{
		int s=-check[DA_LIST_HEAD];
		int c0=cList.get(0);
		while(s!=DA_LIST_HEAD && s<=c0+DA_POOL_OFFSET)
		{
			s=-check[s];
		}
		if(s==DA_LIST_HEAD)
		{
			s=base.length;
			extendArray(Math.max(base.length+cList.size(), base.length*2));
		}

		while(true)
		{
			int i=1;
			while(i<cList.size() && checkFreeCell(s-c0+cList.get(i)))
			{
				++i;
			}
			if(i==cList.size())
				return s-c0;
			else
			{
				if(-check[s]==DA_LIST_HEAD)
					extendArray(Math.max(base.length+cList.size()-i, base.length*2));
				s=-check[s];
			}
		}
	}

	private void relocate(int stateIdx, int newIdx)
	{
		int oldBaseIdx=base[stateIdx];
		for(int c : getChildren(stateIdx))
		{
			int oldNext=oldBaseIdx+c;
			int oldNextBase=base[oldNext];
			allocCell(newIdx+c, oldNextBase, stateIdx, tail[oldNext], children[oldNext]);
			if(oldNextBase>0)
			{
				for (int d : getChildren(oldNext))
				{
					check[oldNextBase+d]=newIdx+c;
				}
			}
			freeCell(oldNext);
		}
		base[stateIdx]=newIdx;
	}

	private int insertBranch(int s, int c)
	{
		int baseValue=base[s];
		int next;

		if(baseValue>DA_LIST_HEAD)
		{
			next=baseValue+c;
			
			/* if already there, do not actually insert */
			if(next<check.length && check[next]==s)
				return next;

			/* if (base + c) > TRIE_INDEX_MAX which means 'next' is overflow,
			 * or cell [next] is not free, relocate to a free slot
			 */
			List<Integer> cList=getChildren(s);
			int pos=Collections.binarySearch(cList, c);
			if(next>base.length || !checkFreeCell(next))
			{
				cList.add(-pos-1, c);
				int newBaseIdx=searchASlot(cList);
				relocate(s, newBaseIdx);
				next=newBaseIdx+c;
			}
			children[s].insert(-pos-1, (char)c);
		}
		else
		{
			List<Integer> cList=new ArrayList<Integer>();
			cList.add(c);
			int newBaseIdx=searchASlot(cList);
			base[s]=newBaseIdx;
			next=newBaseIdx+c;
			children[s]=new StringBuilder().append((char)c);
		}
		allocCell(next);
		check[next]=s;

		return next;
	}

	private int prune(int s)
	{
		return pruneUpto(DA_POOL_HEAD, s);
	}

	private int pruneUpto(int toParent, int s)
	{
		while(s!=toParent && !hasChild(s) && tail[s]==null)
		{
			int parent=check[s];
			if (children[parent].length()==1)
				children[parent]=null;
			else
				children[parent].deleteCharAt(children[parent].indexOf(String.valueOf((char)(s-base[parent]))));
			freeCell(s);
			s=parent;
		}
		return s;
	}

//	private boolean canWalk(int s, int c)
//	{
//		int next=base[s]+c;
//		return next>DA_POOL_HEAD && next<check.length && check[next]==s;
//	}

	private int getAndAddCharCode(char c)
	{
//		if (!charMap.containsKey(c))
//		{
//			charMap.put(c, charMap.size()+1);
//			charList.add(c);
//		}
//		return charMap.get(c);
		int pos=Collections.binarySearch(charList, c);
		if(pos<0)
			charList.add(-pos-1, c);
		return (int)c;
	}

//	private int getCharCode(char c)
//	{
//		if (charMap.containsKey(c))
//		{
//			return charMap.get(c);
//		}
//		return -1;
//	}

	@Override
	public void insert(String word)
	{
		if(word!=null)
		{
			int s=DA_POOL_HEAD;
			int c=-1;
			int i;
			for(i=0; i<word.length(); i++)
			{
				c=getAndAddCharCode(word.charAt(i));
				int next=base[s]+c;
				if(next>DA_POOL_HEAD && next<check.length && check[next]==s)
					s=next;
				else
					break;
			}
	
			if(i>=word.length())
			{
				if("".equals(tail[s]))
					return;
				else if(tail[s]!=null)
				{
					int last=insertBranch(s, getAndAddCharCode(tail[s].charAt(0)));
					tail[last]=tail[s].substring(1);
					tail[s]="";
				}
				else
					tail[s]="";
			}
			else
			{
				if(tail[s]==null)
				{
					s=insertBranch(s, c);
					tail[s]=word.substring(i+1);
				}
				else
				{
					String thisTail=word.substring(i);
					int oldS=s;
					if(!thisTail.equals(tail[oldS]))
					{
						int len=Math.min(thisTail.length(), tail[oldS].length());
						int j;
						for(j=0; j<len; j++)
						{
							char chr=thisTail.charAt(j);
							if(chr==tail[oldS].charAt(j))
								s=insertBranch(s, getAndAddCharCode(chr));
							else
								break;
						}
						if(j<len)
						{
							int last=insertBranch(s, getAndAddCharCode(thisTail.charAt(j)));
							tail[last]=thisTail.substring(j+1);
							last=insertBranch(s, getAndAddCharCode(tail[oldS].charAt(j)));
							tail[last]=tail[oldS].substring(j+1);
							tail[oldS]=null;
						}
						else
						{
							if(len==thisTail.length())
							{
								tail[s]="";
								s=insertBranch(s, getAndAddCharCode(tail[oldS].charAt(j)));
								tail[s]=tail[oldS].substring(j+1);
								tail[oldS]=null;
							}
							else
							{
								tail[oldS]=null;
								tail[s]="";
								s=insertBranch(s, getAndAddCharCode(thisTail.charAt(j)));
								tail[s]=thisTail.substring(j+1);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void delete(String word)
	{
		if(word!=null)
		{
			int s=DA_POOL_HEAD;
			int c=-1;
			int i;
			for(i=0; i<word.length(); i++)
			{
				//c=getCharCode(word.charAt(i));
				c=(int)word.charAt(i);
				if(c>0)
				{
					int next=base[s]+c;
					if(next>DA_POOL_HEAD && next<check.length && check[next]==s)
						s=next;
					else
						break;
				}
				else
					break;
			}
			if(word.substring(i).equals(tail[s]))
			{
				tail[s]=null;
				s=prune(s);
				while (hasChild(s) && children[s].length()==1 && tail[s]==null)
				{
					s=base[s]+(int)children[s].charAt(0);
				}
				if((tail[s]!=null && !tail[s].isEmpty()) || !hasChild(s))
				{
					StringBuilder theTail=new StringBuilder();
					theTail.append(tail[s]);
					int parent=check[s];
					while(parent!=DA_POOL_HEAD && children[parent].length()==1 && tail[parent]==null)
					{
						char ch=(char)(s-base[parent]);
						theTail.insert(0, ch);
						freeCell(s);
						s=parent;
						parent=check[s];
					}
					children[s]=null;
					tail[s]=theTail.toString();
				}
//				return true;
			}
//			else
//			{
//				return false;
//			}
		}
	}

	@Override
	public boolean contains(String word)
	{
		if(word!=null)
		{
			int s=DA_POOL_HEAD;
			int c=-1;
			int i;
			for(i=0; i<word.length(); i++)
			{
				//c=getCharCode(word.charAt(i));
				c=(int)word.charAt(i);
				if(c>0)
				{
					int next=base[s]+c;
					if (next>DA_POOL_HEAD && next<check.length && check[next]==s)
						s=next;
					else
						break;
				}
				else
					break;
			}
			if(word.substring(i).equals(tail[s]))
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
			int s=DA_POOL_HEAD;
			int c=-1;
			int i;
			for(i=0; i<prefix.length(); i++)
			{
				//c=getCharCode(prefix.charAt(i));
				c=(int)prefix.charAt(i);
				if(c>0)
				{
					int next=base[s]+c;
					if(next>DA_POOL_HEAD && next<check.length && check[next]==s)
						s=next;
					else
						break;
				}
				else
					break;
			}
			if(tail[s]!=null && tail[s].startsWith(prefix.substring(i)))
				resultList.add(prefix.substring(0, i)+tail[s]);
			if(i==prefix.length())
			{
				LinkedList<Integer> cStack=new LinkedList<Integer>();
				LinkedList<Integer> layerNumStack=new LinkedList<Integer>();
				StringBuilder suffix=new StringBuilder();
				List<Integer> childrenList=getChildren(s);
				for(Integer cc : childrenList)
				{
					cStack.push(cc);
				}
				int pos=base[s];
				int layerNum=childrenList.size();
				while(!cStack.isEmpty())
				{
					int parentC=cStack.peek();
					--layerNum;
					pos+=parentC;
					//suffix.append(charList.get(parentC));
					suffix.append((char)parentC);
					if(tail[pos]!=null)
						resultList.add(prefix+suffix+tail[pos]);
					if(hasChild(pos))
					{
						List<Integer> subChildrenList=getChildren(pos);
						layerNumStack.push(layerNum);
						for(Integer cc : subChildrenList)
						{
							cStack.push(cc);
						}
						layerNum=subChildrenList.size();
						pos=base[pos];
					}
					else
					{
						suffix.deleteCharAt(suffix.length()-1);
						cStack.pop();
						if(layerNum==0)
						{
							while(layerNum==0 && !layerNumStack.isEmpty())
							{
								suffix.deleteCharAt(suffix.length()-1);
								layerNum=layerNumStack.pop();
								pos=base[check[check[pos]]]+cStack.pop();
							}
							pos=base[check[pos]];
						}
						else
							pos-=parentC;
					}
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
			int s=DA_POOL_HEAD;
			int c=-1;
			int i;
			for(i=0; i<word.length(); i++)
			{
				c=(int)word.charAt(i);
				if(c>0)
				{
					int next=base[s]+c;
					if(next>DA_POOL_HEAD && next<check.length && check[next]==s)
					{
						if("".equals(tail[s]))
							resultList.add(word.substring(0, i));
						s=next;
					}
					else
						break;
				}
				else
					break;
			}
			if(tail[s]!=null)
			{
				String lastCandidate=word.substring(0, i)+tail[s];
				if(word.startsWith(lastCandidate))
					resultList.add(lastCandidate);
			}
		}
		return resultList;
	}
}
