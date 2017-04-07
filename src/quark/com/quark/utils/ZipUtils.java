/**
 * 
 */
package com.quark.utils;
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileOutputStream;  
import java.util.zip.ZipInputStream;  
import java.util.zip.ZipEntry;  
import java.util.zip.ZipOutputStream;
  
/**
 * @author kingsley
 *
 * @datetime 2014年11月24日 下午5:18:17
 */

public class ZipUtils {  
  
    /**//* 
    * path 输入一个文件夹 
    * zipFileName 输出一个压缩文件夹 
    */  
    public static void zip(String path,String zipFileName) throws Exception {  
        zip(zipFileName, new File(path));  
    }  
  
    private static void zip(String zipFileName, File inputFile) throws Exception {  
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));  
        zip(out, inputFile, "");  
        System.out.println("zip done");  
        out.close();  
    }  
  
    private static void zip(ZipOutputStream out, File f, String base) throws Exception {  
        if (f.isDirectory()) {  
           File[] fl = f.listFiles();  
           out.putNextEntry(new ZipEntry(base + "/"));  
           base = base.length() == 0 ? "" : base + "/";  
           for (int i = 0; i < fl.length; i++) {  
           zip(out, fl[i], base + fl[i].getName());  
         }  
        }else {  
           out.putNextEntry(new ZipEntry(base));  
           FileInputStream in = new FileInputStream(f);  
           int b;  
           System.out.println(base);  
           while ( (b = in.read()) != -1) {  
            out.write(b);  
         }  
         in.close();  
       }  
    }  
  
    public static void main(String [] temp){  
    }  
}  