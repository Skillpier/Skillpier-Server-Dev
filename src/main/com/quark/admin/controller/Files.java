package com.quark.admin.controller;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.jfinal.core.Controller;
import com.quark.common.TwoDimensionCode;
import com.quark.common.config;
import com.quark.utils.MatrixToImageWriter;

public class Files extends Controller{

	private static final int BLACK = 0xFF000000;
	private static final int WHITE = 0xFFFFFFFF;
		
	public void image() {
		String name = getPara("name");
		String fullPath = config.save_path + name;
		renderFile(new File(fullPath));
	}
	public void getUeditorImg() {
		String name = getPara("name");
		String fullPath = config.ueditor_images_path + name;
		renderFile(new File(fullPath));
	}
	public void imageBg(){
		String name = getPara("name");
		String fullPath = config.save_path + name;
		 try {  
	            resizeImage(fullPath, fullPath,750,422);  
	        } catch (IOException e) {  
	            System.out.println("图片转换出现异常！");  
	        }  
		 renderFile(new File(fullPath)); 
	}
	
	 /*** 
     * 功能 :调整图片大小 
     * @param srcImgPath 原图片路径 
     * @param distImgPath  转换大小后图片路径 
     * @param width   转换后图片宽度 
     * @param height  转换后图片高度 
     */  
    public static void resizeImage(String srcImgPath, String distImgPath,  
            int width, int height) throws IOException {  
  
        File srcFile = new File(srcImgPath);  
        Image srcImg = ImageIO.read(srcFile);  
        BufferedImage buffImg = null;  
        buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);  
        buffImg.getGraphics().drawImage(  
                srcImg.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0,  
                0, null);  
        ImageIO.write(buffImg, "png", new File(distImgPath));  
    }  
   /**
    * 二维码生成
    * @throws IOException
    * @throws WriterException
    */
    public void getQrcode() throws Exception{
    	String yy_user_id = getPara("yy_user_id");
    	String content = "http://www.uelives.com/h5/#/translators/"+yy_user_id;
		HttpServletRequest req = getRequest();
		HttpServletResponse resp = getResponse();
		req.setCharacterEncoding("utf-8");
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("image/jpeg"); // 设置输出类型为jpeg图片
		String imgPath = "c:/a.jpg"; 
		BufferedImage bufImg = TwoDimensionCode.qRCodeCommon(content, TwoDimensionCode.imgType, TwoDimensionCode.size); 
        
        //在二维码中间加入图片
		TwoDimensionCode.createPhotoAtCenter(bufImg);
        
        File imgFile = new File(imgPath);  
        // 生成二维码QRCode图片  
        ImageIO.write(bufImg, TwoDimensionCode.imgType, imgFile);  
		
		ServletOutputStream so = resp.getOutputStream(); // 得到二进制输出流
		//JPEGImageEncoder je = JPEGCodec.createJPEGEncoder(so); // 对图片进行编码成jpeg格式
		ImageIO.write(bufImg, "jpeg", so); 
		//je.encode(bi);
		so.flush(); //
		renderNull();
    }
}
