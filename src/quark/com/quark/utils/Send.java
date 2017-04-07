package com.quark.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 发短信验证码
 * @author Administrator
 */
public class Send {

	public static String SMS(String postData, String postUrl) {
		try {
			// 发送POST请求
			URL url = new URL(postUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setUseCaches(false);
			conn.setDoOutput(true);

			conn.setRequestProperty("Content-Length", "" + postData.length());
			OutputStreamWriter out = new OutputStreamWriter(
					conn.getOutputStream(), "UTF-8");
			out.write(postData);
			out.flush();
			out.close();

			// 获取响应状态
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("connect failed!");
				return "";
			}
			// 获取响应内容体
			String line, result = "";
			BufferedReader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "utf-8"));
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

	public static void main(String[] args) throws UnsupportedEncodingException {
		String codeNumber = "";
		while (codeNumber.length() < 6)
			codeNumber += (int) (Math.random() * 10);
		String content ="客官：您的验证码为" + codeNumber+ "请勿告知他人。【超级店小二】";
				String PostData = "userid=&account=mengxiaoer&password=200008&mobile=18665364794&sendTime=&content="
						+ java.net.URLEncoder.encode(content,"utf-8");
				String ret = SMS(PostData,
						"http://sms.chanzor.com:8001/sms.aspx?action=send");
				System.out.println(ret);
	}
}
