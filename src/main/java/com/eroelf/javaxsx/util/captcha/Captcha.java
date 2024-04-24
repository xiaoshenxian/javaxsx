package com.eroelf.javaxsx.util.captcha;

import java.awt.image.BufferedImage;

import org.apache.commons.lang3.tuple.Pair;

/**
 * 
 * @author weikun.zhong
 */
public interface Captcha
{
	public String generateText();
	public String generateText(int length);
	public BufferedImage generateImage(String text);

	default public Pair<String, BufferedImage> generateCaptcha()
	{
		String text=generateText();
		BufferedImage bufferedImage=generateImage(text);
		return Pair.of(text, bufferedImage);
	}
}
