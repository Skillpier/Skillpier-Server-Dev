/**
 * 
 */
package com.quarkso.utils;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.kit.JsonKit;

/**
 * @author kingsley
 *
 */
public class ShortUrl {

	public static String encode(final String url){
		try {
			System.out.println(url+"===---------=");
			Document doc = Jsoup.connect("http://dwz.cn/create.php").data(new HashMap<String, String>(){{
				put("url",url);
			}}).post();
			String shortUrl = doc.select("body").first().text();
			System.out.println(shortUrl+"===-shortUrl-------=");
			JSONObject json = new JSONObject(shortUrl);
			String url_tt = json.getString("longurl");
			if (url_tt!=null||!url_tt.equals("")) {
				shortUrl = json.getString("longurl");
				return shortUrl;
			}
			String url_aa = json.getString("tinyurl");
			if (url_aa!=null||!url_aa.equals("")) {
				shortUrl = json.getString("tinyurl");
				return shortUrl;
			}
			System.out.println(url+"===a=");
			return url;
			//shortUrl = url;//json.getString("tinyurl");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(url+"==d=a=");
			return url;
		}
	}
	public static void main(String[] args) {
		encode("http://www.uelives.com");
	}
}
