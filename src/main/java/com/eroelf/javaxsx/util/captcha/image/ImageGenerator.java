package com.eroelf.javaxsx.util.captcha.image;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * 
 * @author weikun.zhong
 */
public interface ImageGenerator
{
	public interface ImageContext extends AutoCloseable
	{
		public Graphics2D getGraphics2d();
		public BufferedImage getBufferedImage();

		@Override
		void close();
	}

	public ImageContext generate();
}
