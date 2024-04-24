package com.eroelf.javaxsx.util.captcha.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 
 * @author weikun.zhong
 */
public class SimpleImageGenerator implements ImageGenerator
{
	public class SimpleImageContext implements ImageContext
	{
		private BufferedImage bufferedImage;
		private Graphics2D graphics2d;

		public SimpleImageContext()
		{
			bufferedImage=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

			graphics2d=bufferedImage.createGraphics();
			graphics2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			graphics2d.setColor(new Color(Color.HSBtoRGB(random.nextFloat(), saturation, random.nextFloat(colorBrightnessLower, colorBrightnessUpper))));
			graphics2d.fillRect(0, 0, width, height);
		}

		public Graphics2D getGraphics2d()
		{
			return graphics2d;
		}

		public BufferedImage getBufferedImage()
		{
			return bufferedImage;
		}

		@Override
		public void close()
		{
			graphics2d.dispose();
		}
	}

	private int width;
	private int height;
	private float saturation;
	private float colorBrightnessLower;
	private float colorBrightnessUpper;
	private Random random;

	public SimpleImageGenerator(int width, int height, float saturation, float colorBrightnessLower, float colorBrightnessUpper, Random random)
	{
		this.width=width;
		this.height=height;
		this.saturation=saturation;
		this.colorBrightnessLower=colorBrightnessLower;
		this.colorBrightnessUpper=colorBrightnessUpper;
		this.random=random;
	}

	@Override
	public ImageContext generate()
	{
		return new SimpleImageContext();
	}
}
