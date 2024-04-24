package com.eroelf.javaxsx.util.captcha.image.noise;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Random;

/**
 * 
 * @author weikun.zhong
 */
public class SimpleNoiseRender implements NoiseRender
{
	private int width;
	private int height;
	private int stainNum;
	private int stainWidth;
	private int stainHeight;
	private int lineNum;
	private int lineWidth;
	private float colorBrightnessLower;
	private float colorBrightnessUpper;
	private int alpha;
	private double transCoe;
	private Random random;

	private int halfWidth;
	private int halfHeight;
	private int halfStainWidth;
	private int halfStainHeight;

	public SimpleNoiseRender(int width, int height, int stainNum, int stainWidth, int stainHeight, int lineNum, int lineWidth, float colorBrightnessLower, float colorBrightnessUpper, int alpha, double transCoe, Random random)
	{
		this.width=width;
		this.height=height;
		this.stainNum=stainNum;
		this.stainWidth=stainWidth;
		this.stainHeight=stainHeight;
		this.lineNum=lineNum;
		this.lineWidth=lineWidth;
		this.colorBrightnessLower=colorBrightnessLower;
		this.colorBrightnessUpper=colorBrightnessUpper;
		this.alpha=((0xff & alpha) << 24) | 0xffffff;
		this.transCoe=transCoe;
		this.random=random;

		halfWidth=this.width/2;
		halfHeight=this.height/2;
		halfStainWidth=this.stainWidth/2;
		halfStainHeight=this.stainHeight/2;
	}

	@Override
	public void render(Graphics2D graphics2d)
	{
		for(int i=0; i<stainNum; i++)
		{
			graphics2d.setColor(new Color(Color.HSBtoRGB(random.nextFloat(), random.nextFloat(), random.nextFloat(colorBrightnessLower, colorBrightnessUpper)) & alpha, true));
			int centerX=random.nextInt(width);
			int centerY=random.nextInt(height);
			AffineTransform transform=graphics2d.getTransform();
			graphics2d.translate(centerX, centerY);
			graphics2d.rotate(random.nextDouble(-Math.PI, Math.PI));
			graphics2d.fillOval(-halfStainWidth, -halfStainHeight, stainWidth, stainHeight);
			graphics2d.setTransform(transform);
		}

		graphics2d.setStroke(new BasicStroke(lineWidth));
		for(int i=0; i<lineNum; i++)
		{
			graphics2d.setColor(new Color(Color.HSBtoRGB(random.nextFloat(), random.nextFloat(), random.nextFloat(colorBrightnessLower, colorBrightnessUpper)) & alpha, true));
			AffineTransform transform=graphics2d.getTransform();
			graphics2d.translate(halfWidth, halfHeight);
			graphics2d.transform(new AffineTransform(random.nextFloat()*transCoe, random.nextFloat()*transCoe, random.nextFloat()*transCoe, random.nextFloat()*transCoe, random.nextFloat()*transCoe, random.nextFloat()*transCoe));
			if(random.nextBoolean())
				graphics2d.drawLine(-halfWidth, random.nextInt(-height, height), halfWidth, random.nextInt(-height, height));
			else
				graphics2d.drawLine(random.nextInt(-width, width), -halfHeight, random.nextInt(-width, width), halfHeight);
			graphics2d.setTransform(transform);
		}
	}
}
