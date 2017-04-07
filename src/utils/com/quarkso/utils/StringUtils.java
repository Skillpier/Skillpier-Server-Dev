package com.quarkso.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class StringUtils {
	public final static String TMALL_HTML_PATH = "F:\\bijia\\tmall_html\\";
	public final static String TAOBAO_HTML_PATH = "F:\\bijia\\taobao_html\\";
	public final static String SUNING_HTML_PATH = "F:\\bijia\\suning_html\\";
	
	
	public final static String URL_HTML_PATH = "F:\\bijia\\url_html\\";
	/**
     * 验证邮箱
     * @param email
     * @return
     */
    public static boolean checkEmail(String email){
        boolean flag = false;
        try{
                String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
                Pattern regex = Pattern.compile(check);
                Matcher matcher = regex.matcher(email);
                flag = matcher.matches();
            }catch(Exception e){
                flag = false;
            }
        return flag;
    }
}
