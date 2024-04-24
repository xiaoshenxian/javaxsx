package com.eroelf.javaxsx.util.captcha;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.eroelf.javaxsx.util.captcha.image.ImageGenerator;
import com.eroelf.javaxsx.util.captcha.image.ImageGenerator.ImageContext;
import com.eroelf.javaxsx.util.captcha.image.SimpleImageGenerator;
import com.eroelf.javaxsx.util.captcha.image.noise.NoiseRender;
import com.eroelf.javaxsx.util.captcha.image.noise.SimpleNoiseRender;
import com.eroelf.javaxsx.util.captcha.text.generator.SimpleTextGenerator;
import com.eroelf.javaxsx.util.captcha.text.generator.TextGenerator;
import com.eroelf.javaxsx.util.captcha.text.render.SimpleTextRender;
import com.eroelf.javaxsx.util.captcha.text.render.TextRender;

/**
 * 
 * @author weikun.zhong
 */
public class SimpleCaptcha implements Captcha
{
	private Random random;
	private TextGenerator textGenerator;
	private ImageGenerator imageGenerator;
	private TextRender textRender;
	private NoiseRender noiseRender;

	public SimpleCaptcha(int captchaLength, int width, int height)
	{
		this(captchaLength, width, height, "Serif", 64, 8);
	}

	public SimpleCaptcha(
		int captchaLength, int width, int height,
		String fontName,
		int stainNum, int lineNum
	)
	{
		this(
			captchaLength, width, height, 0.1f, 0.98f, 1f,
			0f, 0.8f, fontName, height/5*4, height/10, (width-height/2*captchaLength)/2, 0, 0.1d, Math.PI/6, 0.2d, 0.4d,
			stainNum, width/10, height/20, lineNum, height/4, 0.85f, 0.98f, 63, 1d
		);
	}

	public SimpleCaptcha(
		int captchaLength, int width, int height, float bgSaturation, float bgColorBrightnessLower, float bgColorBrightnessUpper,
		float textColorBrightnessLower, float textColorBrightnessUpper, String fontName, int fontSize, int space, int paddingX, int paddingY, double textTranslationCoe, double textMaxRotateRadian, double textScalingCoe, double textShearingCoe,
		int stainNum, int stainWidth, int stainHeight, int lineNum, int lineWidth, float noiseColorBrightnessLower, float noiseColorBrightnessUpper, int noiseAlpha, double transCoe
	)
	{
		random=new Random();
		textGenerator=new SimpleTextGenerator(captchaLength, null, random);
		imageGenerator=new SimpleImageGenerator(width, height, bgSaturation, bgColorBrightnessLower, bgColorBrightnessUpper, random);
		textRender=new SimpleTextRender(textColorBrightnessLower, textColorBrightnessUpper, fontName,  fontSize, space, paddingX, paddingY, textTranslationCoe, textMaxRotateRadian, textScalingCoe, textShearingCoe, random);
		noiseRender=new SimpleNoiseRender(width, height, stainNum, stainWidth, stainHeight, lineNum, lineWidth, noiseColorBrightnessLower, noiseColorBrightnessUpper, noiseAlpha, transCoe, random);
	}

	@Override
	public String generateText()
	{
		return textGenerator.generate();
	}

	@Override
	public String generateText(int length)
	{
		return textGenerator.generate(length);
	}

	@Override
	public BufferedImage generateImage(String text)
	{
		try(ImageContext context=imageGenerator.generate())
		{
			Graphics2D graphics2d=context.getGraphics2d();
			textRender.render(text, graphics2d);
			noiseRender.render(graphics2d);
			return context.getBufferedImage();
		}
	}
}
