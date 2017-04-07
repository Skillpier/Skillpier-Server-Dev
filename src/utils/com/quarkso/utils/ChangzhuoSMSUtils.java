package com.quarkso.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author Administrator
 */
public class ChangzhuoSMSUtils {

    public static String SMS(String postData, String postUrl) {
        try {
            //发送POST请求
            URL url = new URL(postUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setUseCaches(false);
            conn.setDoOutput(true);

            conn.setRequestProperty("Content-Length", "" + postData.length());
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            out.write(postData);
            out.flush();
            out.close();

            //获取响应状态
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.out.println("connect failed!");
                return "";
            }
            //获取响应内容体
            String line, result = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            while ((line = in.readLine()) != null) {
                result += line + "\n";
            }
            in.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        return "";
    }
    /**
     * @param content 短信内容
     * @param toTel   电话号码
     * @throws UnsupportedEncodingException
     */
    
    public static String acceptContent(String content,String toTel) throws UnsupportedEncodingException{
    	 String PostData = "userid=&account=kzjiajiao&password=23879&mobile="+toTel+"&sendTime=&content="+java.net.URLEncoder.encode(content,"utf-8");
         System.out.println(PostData);
         String ret = ChangzhuoSMSUtils.SMS(PostData, "http://sms.chanzor.com:8001/sms.aspx?action=send");
         System.out.println(ret);
         return ret;
    }
    public static void main(String[] args) throws UnsupportedEncodingException{
    	acceptContent("您好，感谢您的注册，验证码为0204【快找家教】","18665364794");
    }
}
