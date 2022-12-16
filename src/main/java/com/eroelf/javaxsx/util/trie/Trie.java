package com.eroelf.javaxsx.util.trie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * An interface defines methods of a Trie.
 * 
 * @author weikun.zhong
 */
public interface Trie extends Serializable
{
	/**
	 * Inserts a word to the Trie.
	 * 
	 * @param word the word to be inserted.
	 */
	public void insert(String word);

	/**
	 * Deletes a word from the Trie.
	 * 
	 * @param word the word to be deleted.
	 */
	public void delete(String word);

	/**
	 * Checks if the specified word is contained in the Trie.
	 * 
	 * @param word the word to be checked.
	 * @return {@code true} if the word is in the Trie, otherwise {@code false}.
	 */
	public boolean contains(String word);

	/**
	 * Finds all words which have the same specified prefix in the Trie.
	 * 
	 * @param prefix the specified prefix.
	 * @param resultList receives the result.
	 * @return the input {@code resultList} with all found words appended.
	 */
	public List<String> matchPrefix(String prefix, List<String> resultList);

	/**
	 * Finds all words that can be a prefix of the specified word.
	 * 
	 * @param word the word to be checked.
	 * @param resultList receives the result.
	 * @return the input {@code resultList} with all found prefixes appended.
	 */
	public List<String> getPrefixes(String word, List<String> resultList);

	/**
	 * Finds all words that can be a substring of the specified word.
	 * 
	 * @param word the word to be checked.
	 * @param resultList receives the result.
	 * @return the input {@code resultList} with all found sub-words appended.
	 */
	default public List<String> getSubWords(String word, List<String> resultList)
	{
		List<String> tempList=new ArrayList<String>();
		for(int i=0; i<word.length(); i++)
		{
			getPrefixes(word.substring(i), tempList);
		}
		resultList.addAll(new HashSet<String>(tempList));
		return resultList;
	}
}
