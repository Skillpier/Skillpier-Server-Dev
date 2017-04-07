package com.quarkso.utils;

import java.util.Random;

public class RandomUtils {

	public static String getRandomString(int length) { //length表示生成字符串的长度  
	    String base = "abcdefghijklmnopqrstuvwxyz0123456789";     
	    Random random = new Random();     
	    StringBuffer sb = new StringBuffer();     
	    for (int i = 0; i < length; i++) {     
	        int number = random.nextInt(base.length());     
	        sb.append(base.charAt(number));     
	    }     
	    return sb.toString();     
	 } 
	
	/*
	 *  获取随机数
	 */
	public static  String getCode(){
		return randString(6);
	}
	
	/*
	 * 生成随机数
	 */
	private static String randString (int length)
    {
			 String ssource = "0123456789";
			 char[] src = ssource.toCharArray();
            char[] buf = new char[length];
            Random r = new Random();
            int rnd;
            for(int i=0;i<length;i++)
            {
                    rnd = Math.abs(r.nextInt()) % src.length;
                    buf[i] = src[rnd];
            }
            return new String(buf);
    }
}
