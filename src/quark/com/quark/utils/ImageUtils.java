/**
 * 
 */
package com.quark.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import sun.misc.BASE64Encoder;

/**
 * @author kingsley
 * 
 */
public class ImageUtils {

	/**
	 * 生成园角图片
	 * @param image
	 * @param cornerRadius
	 * @return
	 */
	 public static BufferedImage makeRoundedCorner(BufferedImage image,
	            int cornerRadius) {
	        int w = image.getWidth();
	        int h = image.getHeight();
	        BufferedImage output = new BufferedImage(w, h,
	                BufferedImage.TYPE_INT_ARGB);
	 
	        Graphics2D g2 = output.createGraphics();
	        g2.setComposite(AlphaComposite.Src);
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                RenderingHints.VALUE_ANTIALIAS_ON);
	        g2.setColor(Color.WHITE);
	        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius,
	                cornerRadius));
	        g2.setComposite(AlphaComposite.SrcAtop);
	        g2.drawImage(image, 0, 0, null);
	        g2.dispose();
	        return output;
	    }
	/**
	 * 
	 * @param urlPath
	 * @param savePath
	 * @param header
	 *            头字段，破解防盗链技术
	 * @throws Exception
	 */
	public static void getImages(String urlPath, String savePath, String header)
			throws Exception {
		URL url = new URL(urlPath);//
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		if (header != null) {
			// "http://t12.baidu.com/it/u=2263955969,2201160335&fm=58"
			conn.setRequestProperty("Referer", header);
		}
		conn.setRequestMethod("GET");
		conn.setReadTimeout(1000 * 10000);
		System.out.println("down load:" + urlPath);
		if (conn.getResponseCode() < 10000) {
			InputStream inputStream = conn.getInputStream();
			byte[] data = readStream(inputStream);
			FileOutputStream outputStream = new FileOutputStream(savePath);
			outputStream.write(data);
			outputStream.close();
		}
	}

	public static String getImageBinary(File file) {
		BufferedImage bi;
		BASE64Encoder encoder = new sun.misc.BASE64Encoder();
		try {
			bi = ImageIO.read(file);
			String fileName = file.getName();
			String type = fileName.substring(fileName.indexOf(".") + 1);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(bi, type, baos);
			byte[] bytes = baos.toByteArray();
			return encoder.encodeBuffer(bytes).trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] readStream(InputStream inputStream) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, len);
		}
		outputStream.close();
		inputStream.close();
		return outputStream.toByteArray();
	}

	public static void main(String[] args) throws Exception {
		 BufferedImage icon = ImageIO.read(new File("C:/Users/Administrator/Pictures/1.jpg"));
	        BufferedImage rounded = makeRoundedCorner(icon, 1000);
	        ImageIO.write(rounded, "png", new File("C:/Users/Administrator/Pictures/1_round.jpg"));
	}
}
