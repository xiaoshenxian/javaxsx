package com.eroelf.javaxsx.util.phonetic.kana;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KanaRomaji
{
	private static final char[] HIRAGANA="ぁあぃいぅうぇえぉおかがきぎくぐけげこごさざしじすずせぜそぞただちぢっつづてでとどなにぬねのはばぱひびぴふぶぷへべぺほぼぽまみむめもゃやゅゆょよらりるれろゎわゐゑをんゔゕゖ".toCharArray();
	private static final char[] KATAKANA="ァアィイゥウェエォオカガキギクグケゲコゴサザシジスズセゼソゾタダチヂッツヅテデトドナニヌネノハバパヒビピフブプヘベペホボポマミムメモャヤュユョヨラリルレロヮワヰヱヲンヴヵヶヷヸヹヺ".toCharArray();
	private static final String[] ROMAJI="a,a,i,i,u,u,e,e,o,o,ka,ga,ki,gi,ku,gu,ke,ge,ko,go,sa,za,si,zi,su,zu,se,ze,so,zo,ta,da,ti,di,tu,tu,du,te,de,to,do,na,ni,nu,ne,no,ha,ba,pa,hi,bi,pi,hu,bu,pu,he,be,pe,ho,bo,po,ma,mi,mu,me,mo,ya,ya,yu,yu,yo,yo,ra,ri,ru,re,ro,wa,wa,wi,we,wo,n,vu,ka,ke,va,vi,ve,vo".split(",");

	private static final Map<Character, String> KANA_TO_ROMAJI=new HashMap<>();
	private static final Map<String, String> KUNREI_TO_HEBON=new HashMap<>();
	private static final Set<String> VOWEL_ROMAJI=new HashSet<>();

	static
	{
		for(int i=0; i<ROMAJI.length; i++)
		{
			if(i<HIRAGANA.length)
				KANA_TO_ROMAJI.put(HIRAGANA[i], ROMAJI[i]);
			if(i<KATAKANA.length)
				KANA_TO_ROMAJI.put(KATAKANA[i], ROMAJI[i]);
		}
		KUNREI_TO_HEBON.put("si", "shi");
		KUNREI_TO_HEBON.put("zi", "ji");
		KUNREI_TO_HEBON.put("ti", "chi");
		KUNREI_TO_HEBON.put("di", "ji");
		KUNREI_TO_HEBON.put("tu", "tsu");
		KUNREI_TO_HEBON.put("hu", "fu");

		KUNREI_TO_HEBON.put("sya", "sha");
		KUNREI_TO_HEBON.put("syu", "shu");
		KUNREI_TO_HEBON.put("syo", "sho");
		KUNREI_TO_HEBON.put("tya", "cha");
		KUNREI_TO_HEBON.put("tyu", "chu");
		KUNREI_TO_HEBON.put("tyo", "cho");
		KUNREI_TO_HEBON.put("zya", "ja");
		KUNREI_TO_HEBON.put("zyu", "ju");
		KUNREI_TO_HEBON.put("zyo", "jo");
		KUNREI_TO_HEBON.put("dya", "ja");
		KUNREI_TO_HEBON.put("dyu", "ju");
		KUNREI_TO_HEBON.put("dyo", "jo");

		VOWEL_ROMAJI.add("a");
		VOWEL_ROMAJI.add("i");
		VOWEL_ROMAJI.add("u");
		VOWEL_ROMAJI.add("e");
		VOWEL_ROMAJI.add("o");
	}

	public static List<String> romaji(String kana)
	{
		return romaji(kana, false);
	}

	public static List<String> romaji(String kana, boolean useHebon)
	{
		List<String> res=new ArrayList<>();
		StringBuilder buffer=new StringBuilder();
		boolean prolong=false;
		char lastChar=0;
		for(char ch : kana.toCharArray())
		{
			String curr=KANA_TO_ROMAJI.get(ch);
			if(curr==null)
			{
				String bufferStr=buffer.toString();
				if(buffer.length()>0)
				{
					buildRomaji(res, bufferStr, curr, prolong, useHebon);
					buffer.delete(0, buffer.length());
				}
				else if(lastChar=='っ' || lastChar=='ッ')
					res.add(checkHebon(KANA_TO_ROMAJI.get(lastChar), useHebon));
				prolong=false;
				if(ch=='ー')
					res.add("\u0304");
				else if(ch=='・')
					res.add("·");
				else if(ch=='ゝ' || ch=='ヽ')
				{
					if(!bufferStr.isEmpty())
						buildRomaji(res, devoice(bufferStr), curr, prolong, useHebon);
				}
				else if(ch=='ゞ' || ch=='ヾ')
				{
					if(!bufferStr.isEmpty())
						buildRomaji(res, voice(bufferStr), curr, prolong, useHebon);
				}
				else
					res.add(String.valueOf(ch));
			}
			else
			{
				if(curr.charAt(0)=='y' && buffer.length()>1 && buffer.charAt(buffer.length()-1)=='i')
					buffer.deleteCharAt(buffer.length()-1).append(curr);
				else
				{
					if((lastChar=='っ' || lastChar=='ッ') && (ch=='っ' || ch=='ッ' || ch=='ん' || ch=='ン' || VOWEL_ROMAJI.contains(curr)))
					{
						res.add(checkHebon(KANA_TO_ROMAJI.get(lastChar), useHebon));
						if(curr.length()<=1)
							prolong=false;
					}
					if(buffer.length()>0)
					{
						buildRomaji(res, buffer.toString(), curr, prolong, useHebon);
						buffer.delete(0, buffer.length());
						prolong=false;
					}
					if(ch=='っ' || ch=='ッ')
						prolong=true;
					else
						buffer.append(curr);
				}
			}
			lastChar=ch;
		}
		if(buffer.length()>0)
			buildRomaji(res, buffer.toString(), null, prolong, useHebon);
		if(lastChar=='っ' || lastChar=='ッ')
			res.add(checkHebon(KANA_TO_ROMAJI.get(lastChar), useHebon));
		return res;
	}

	private static String devoice(String bufferStr)
	{
		return bufferStr.replace('g', 'k').replace('z', 's').replace('d', 't').replace('b', 'h');
	}

	private static String voice(String bufferStr)
	{
		return bufferStr.replace('k', 'g').replace('s', 'z').replace('t', 'd').replace('h', 'b');
	}

	private static void buildRomaji(List<String> des, String buffer, String curr, boolean prolong, boolean useHebon)
	{
		String romaji=checkHebon(buffer, useHebon);
		if(prolong)
		{
			if(romaji.startsWith("ch"))
				romaji='t'+romaji;
			else
				romaji=romaji.charAt(0)+romaji;
		}
		des.add(romaji);
		if(VOWEL_ROMAJI.contains(curr) && buffer.charAt(buffer.length()-1)=='n')
			des.add("\'");
	}

	private static String checkHebon(String romaji, boolean useHebon)
	{
		if(useHebon && KUNREI_TO_HEBON.containsKey(romaji))
			romaji=KUNREI_TO_HEBON.get(romaji);
		return romaji;
	}
}
