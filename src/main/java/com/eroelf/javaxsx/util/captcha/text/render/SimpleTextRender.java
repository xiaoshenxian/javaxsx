package com.eroelf.javaxsx.util.captcha.text.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Random;

/**
 * 
 * @author weikun.zhong
 */
public class SimpleTextRender implements TextRender
{
	private float colorBrightnessLower;
	private float colorBrightnessUpper;
	private String fontName;
	private int fontSize;
	private int space;
	private int paddingX;
	private int paddingY;
	private double translationCoe;
	private double maxRotateRadian;
	private double scalingCoe;
	private double shearingCoe;
	private Random random;

	private Font font;
	private int offset;

	public SimpleTextRender(float colorBrightnessLower, float colorBrightnessUpper, String fontName, int fontSize, int space, int paddingX, int paddingY, double translationCoe, double maxRotateRadian, double scalingCoe, double shearingCoe, Random random)
	{
		this.colorBrightnessLower=colorBrightnessLower;
		this.colorBrightnessUpper=colorBrightnessUpper;
		this.fontName=fontName;
		this.fontSize=fontSize;
		this.space=space;
		this.paddingX=paddingX;
		this.paddingY=paddingY;
		this.translationCoe=translationCoe;
		this.maxRotateRadian=maxRotateRadian;
		this.scalingCoe=scalingCoe;
		this.shearingCoe=shearingCoe;
		this.random=random;

		font=new Font(this.fontName, Font.PLAIN, this.fontSize);
		offset=this.fontSize/2+this.space;
	}

	@Override
	public void render(String text, Graphics2D graphics2d)
	{
		int len=text.length();
		graphics2d.setFont(font);
		for(int i=0; i<len; i++)
		{
			graphics2d.setColor(new Color(Color.HSBtoRGB(random.nextFloat(), random.nextFloat(), random.nextFloat(colorBrightnessLower, colorBrightnessUpper))));
			AffineTransform transform=graphics2d.getTransform();
			int baselineX=paddingX+(i*offset);
			int baselineY=fontSize+paddingY;
			graphics2d.translate(baselineX+fontSize*random.nextDouble(-translationCoe, translationCoe), baselineY+fontSize/2*random.nextDouble(-translationCoe, translationCoe));
			graphics2d.rotate(random.nextDouble(-maxRotateRadian, maxRotateRadian));
			graphics2d.scale(1+random.nextDouble(-scalingCoe, scalingCoe), 1+random.nextDouble(-scalingCoe, scalingCoe));
			graphics2d.shear(random.nextDouble(-shearingCoe, shearingCoe), random.nextDouble(-shearingCoe, shearingCoe));
			graphics2d.drawString(text.substring(i, i+1), 0, 0);
			graphics2d.setTransform(transform);
		}
	}
}
