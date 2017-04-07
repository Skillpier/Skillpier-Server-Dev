package com.quarkso.utils;

import java.io.ByteArrayOutputStream;  
import java.io.File;  
import java.io.FileOutputStream;  
import java.io.InputStream;  
import java.net.HttpURLConnection;  
import java.net.URL;  
import java.util.Date;
/** 
 * @说明 从网络获取图片到本地 
 * @author C罗
 * @version 1.0 
 * @since 
 */  
public class ImgSavePathUtils {  
    /** 
     * 测试 
     * @param args 
     */  
    public static void main(String[] args) {  
        String url = "http://wx.qlogo.cn/mmopen/4CUbibDQuyfx6pBtwJribNSmMyQCJG8sFxWRTtt6friaicrrrVqKS61wRZfqkVQzkSda2sDdwk5uM0D4icfHRmreXFtvSz0T9cSXl/0";  
        byte[] btImg = getImageFromNetByUrl(url);  
        if(null != btImg && btImg.length > 0){  
            System.out.println("读取到：" + btImg.length + " 字节");  
            Date d = new Date();
            String fileName = "331"+".jpg";    
            writeImageToDisk(btImg, fileName,"C:\\");  
        }else{  
            System.out.println("没有从该连接获得内容");  
        }  
    }
    public static void getImages(String urlPath,String fileName, String savePath){
         byte[] btImg = getImageFromNetByUrl(urlPath);  
         if(null != btImg && btImg.length > 0){  
             System.out.println("读取到：" + btImg.length + " 字节");  
             //Date d = new Date();
             //d.getTime()
             String fileName2 = fileName+".jpg";  
             writeImageToDisk(btImg, fileName2,savePath);  
         }else{  
             System.out.println("没有从该连接获得内容");  
         }  
    }
    /** 
     * 将图片写入到磁盘 
     * @param img 图片数据流 
     * @param fileName 文件保存时的名称 
     */  
    public static void writeImageToDisk(byte[] img, String fileName,String savePath){  
        try {  
            File file = new File(savePath + fileName);  
            FileOutputStream fops = new FileOutputStream(file);  
            fops.write(img);  
            fops.flush();  
            fops.close();  
            System.out.println("图片已经写入到C盘");  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
    /** 
     * 根据地址获得数据的字节流 
     * @param strUrl 网络连接地址 
     * @return 
     */  
    public static byte[] getImageFromNetByUrl(String strUrl){  
        try {  
            URL url = new URL(strUrl);  
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
            conn.setRequestMethod("GET");  
            conn.setConnectTimeout(5 * 1000);  
            InputStream inStream = conn.getInputStream();//通过输入流获取图片数据  
            byte[] btImg = readInputStream(inStream);//得到图片的二进制数据  
            return btImg;  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
    /** 
     * 从输入流中获取数据 
     * @param inStream 输入流 
     * @return 
     * @throws Exception 
     */  
    public static byte[] readInputStream(InputStream inStream) throws Exception{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] buffer = new byte[1024];  
        int len = 0;  
        while( (len=inStream.read(buffer)) != -1 ){  
            outStream.write(buffer, 0, len);  
        }  
        inStream.close();  
        return outStream.toByteArray();  
    }  
} 