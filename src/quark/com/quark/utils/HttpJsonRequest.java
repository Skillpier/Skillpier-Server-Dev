/**
 * 
 */
package com.quark.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.util.UrlEncoded;
import org.eclipse.jetty.util.security.Credential;
import org.json.JSONObject;

import jdk.nashorn.internal.ir.ObjectNode;

/**
 * @author kingsley
 * 
 * @info http使用json请求
 *
 * @datetime 2014年12月4日 下午3:58:28
 */
public class HttpJsonRequest {

	private static String request(String request_url, String request_method,
			HashMap<String, String> header, HashMap<String, String> data) {
		StringBuffer sbf = null;
		try {
			URL url = new URL(request_url);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod(request_method.toUpperCase());
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			Set<String> header_keys = header.keySet();
			for (String key : header_keys) {
				connection.setRequestProperty(key, header.get(key));
			}
			connection.connect();
			DataOutputStream out = new DataOutputStream(
					connection.getOutputStream());
			JSONObject obj = new JSONObject();
			Set<String> keys = data.keySet();
			for (String key : keys) {
				obj.put(key, data.get(key));
			}
			System.out.println(obj.toString());
			out.writeBytes(obj.toString());
			out.flush();
			out.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String lines;
			sbf = new StringBuffer();
			while ((lines = reader.readLine()) != null) {
				lines = new String(lines.getBytes(), "utf-8");
				sbf.append(lines);
			}
			reader.close();
			// 断开连接
			connection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sbf.toString();
	}
	
	

	public static String delete(String request_url,
			HashMap<String, String> header, HashMap<String, String> data) {
		if (header == null) {
			header = new HashMap<String, String>();
		}
		if (data == null) {
			data = new HashMap<String, String>();
		}
		return request(request_url, "DELETE", header, data);
	}

	public static String post(String request_url,
			HashMap<String, String> header, HashMap<String, String> data) {
		if (header == null) {
			header = new HashMap<String, String>();
		}
		if (data == null) {
			data = new HashMap<String, String>();
		}
		return request(request_url, "POST", header, data);
	}

	public static String get(String request_url,
			HashMap<String, String> header, HashMap<String, String> data) {
		if (header == null) {
			header = new HashMap<String, String>();
		}
		if (data == null) {
			data = new HashMap<String, String>();
		}
		return request(request_url, "GET", header, data);
	}
	public static String put(String request_url,
			HashMap<String, String> header, HashMap<String, String> data) {
		if (header == null) {
			header = new HashMap<String, String>();
		}
		if (data == null) {
			data = new HashMap<String, String>();
		}
		return request(request_url, "GET", header, data);
	}
	public static String put2(String request_url,
			HashMap<String, String> header, HashMap<String, String> data) {
		if (header == null) {
			header = new HashMap<String, String>();
		}
		if (data == null) {
			data = new HashMap<String, String>();
		}
		return request(request_url, "PUT", header, data);
	}
}
