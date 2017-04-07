package com.quarkso.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MoveFile {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		copyFile("D:/360/贵阳-50-24-6-家用-二手车.html", "D:/360Downloads/贵阳-50-24-6-家用-二手车.html") ;
		

	}
	   public static void delFile(String filePathAndName) { 
	       try { 
	           String filePath = filePathAndName; 
	           filePath = filePath.toString(); 
	           File myDelFile = new File(filePath); 
	           myDelFile.delete(); 

	       } 
	       catch (Exception e) { 
	           System.out.println("删除文件操作出错"); 
	           e.printStackTrace(); 

	       } 

	   } 
	   /** 
	     * 复制单个文件 
	     * @param oldPath String 原文件路径 如：c:/fqf.txt 
	     * @param newPath String 复制后路径 如：f:/fqf.txt 
	     * @return boolean 
	     */ 
	   public static void copyFile(String oldPath, String newPath) { 
	       try { 
	           int bytesum = 0; 
	           int byteread = 0; 
	           File oldfile = new File(oldPath); 
	           if (oldfile.exists()) { //文件存在时 
	               InputStream inStream = new FileInputStream(oldPath); //读入原文件 
	               FileOutputStream fs = new FileOutputStream(newPath); 
	               byte[] buffer = new byte[1444]; 
	               int length; 
	               while ( (byteread = inStream.read(buffer)) != -1) { 
	                   bytesum += byteread; //字节数 文件大小 
	                   fs.write(buffer, 0, byteread); 
	               } 
	               inStream.close(); 
	               delFile(oldPath);//删除文件
	           } 
	       } 
	       catch (Exception e) { 
	           System.out.println("复制单个文件操作出错"); 
	           e.printStackTrace(); 
	       } 

	   } 

}
