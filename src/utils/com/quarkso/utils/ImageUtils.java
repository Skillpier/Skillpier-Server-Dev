/**
 * 
 */
package com.quarkso.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * @author kingsley
 * 
 */
public class ImageUtils {

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
	}

	public void imageOp(String inFilePath, String outFilePath, int width,
			int height) {
		File tempFile = new File(inFilePath);
		Image image = null;
		try {
			image = ImageIO.read(tempFile);
		} catch (IOException e) {
			System.out.println("file path error...");
		}

		int originalImageWidth = image.getWidth(null);
		int originalImageHeight = image.getHeight(null);

		BufferedImage originalImage = new BufferedImage(originalImageWidth,
				originalImageHeight, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g2d = originalImage.createGraphics();
		g2d.drawImage(image, 0, 0, null);

		BufferedImage changedImage = new BufferedImage(width, height,
				BufferedImage.TYPE_3BYTE_BGR);

		double widthBo = (double) width / originalImageWidth;
		double heightBo = (double) width / originalImageHeight;

		AffineTransform transform = new AffineTransform();
		transform.setToScale(widthBo, heightBo);

		AffineTransformOp ato = new AffineTransformOp(transform, null);
		ato.filter(originalImage, changedImage);

		File fo = new File(outFilePath); // ��Ҫת������Сͼ�ļ�

		try {
			ImageIO.write(changedImage, "jpeg", fo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
