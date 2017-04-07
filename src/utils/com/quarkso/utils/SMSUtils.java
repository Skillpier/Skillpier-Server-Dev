package com.quarkso.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class SMSUtils {
	
	
	
	public static void sendContent(String content,String toTel) throws IOException{
				// 创建StringBuffer对象用来操作字符串
				StringBuffer sb = new StringBuffer("http://api.cnsms.cn/?encode=utf8");
				// 向StringBuffer追加用户名
				sb.append("ac=send&uid=118928");
				// 向StringBuffer追加密码（密码采用MD5 32位 小写）
				sb.append("&pwd=e9bc0e13a8a16cbb07b175d92a113126");
				// 向StringBuffer追加手机号码
				sb.append("&mobile="+toTel);
				// 向StringBuffer追加消息内容转URL标准码
				sb.append("&content=" + URLEncoder.encode(content));
				// 创建url对象
				URL url = new URL(sb.toString());
				// 打开url连接
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				// 设置url请求方式 ‘get’ 或者 ‘post’
				connection.setRequestMethod("GET");
				// 发送
				BufferedReader in = new BufferedReader(new InputStreamReader(
						url.openStream()));
				// 返回发送结果
				String inputline = in.readLine();
				// 返回结果为‘100’ 发送成功
				System.out.println(inputline);
	}
	
	public static void acceptContent() throws IOException{
		// 创建StringBuffer对象用来操作字符串
		StringBuffer sb = new StringBuffer("http://api.cnsms.cn/");
		// 向StringBuffer追加用户名
		sb.append("ac=gr&uid=118928");
		// 向StringBuffer追加密码（密码采用MD5 32位 小写）
		sb.append("&pwd=e9bc0e13a8a16cbb07b175d92a113126");
		URL url = new URL(sb.toString());
		// 打开url连接
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		// 设置url请求方式 ‘get’ 或者 ‘post’
		connection.setRequestMethod("POST");
		// 发送
		BufferedReader in = new BufferedReader(new InputStreamReader(
				url.openStream()));
		// 返回发送结果
		String inputline = in.readLine();
		System.out.println(inputline);
}
}
