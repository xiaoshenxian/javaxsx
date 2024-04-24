package com.eroelf.javaxsx.util.captcha.text.generator;

import java.util.Random;

/**
 * 
 * @author weikun.zhong
 */
public class SimpleTextGenerator implements TextGenerator
{
	private int length;
	private char[] availableText;
	private int availableTextLen;
	private Random random;

	public SimpleTextGenerator(int length, char[] availableText, Random random)
	{
		this.length=length;
		if(availableText==null || availableText.length==0)
			this.availableText="23456789abcdefghijkmnopqrstuvwxyzABCDEFGHIJKLMNPQRSTUVWXYZ".toCharArray();//no 1, 0, l, O
		else
			this.availableText=availableText;
		availableTextLen=this.availableText.length;
		this.random=random;
	}

	@Override
	public String generate()
	{
		return generate(length);
	}

	@Override
	public String generate(int length)
	{
		char[] choices=new char[length];
		for(int i=0; i<choices.length; i++)
		{
			choices[i]=availableText[random.nextInt(availableTextLen)];
		}
		return new String(choices);
	}
}
