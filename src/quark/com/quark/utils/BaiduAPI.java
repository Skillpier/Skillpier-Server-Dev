package com.quark.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class BaiduAPI {

	private static String ak = "9E967848d4e96caea7b6ab26bd556cee";

	public static String getAddress_from_lat_lng(String lat,String lng){
		URL url = null;
		try {
			url = new URL("http://api.map.baidu.com/geocoder?" + ak + "=" + ak
					+ "&location=" + lat + "," + lng + "&output=xml");
			Document document = Jsoup.connect(url.toString()).get();
			return document.select("formatted_address").first()
					.text();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	
	}
	public static Map<String, String> decode_from_lat_and_lng(String lat,
			String lng) {
		URL url = null;
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			url = new URL("http://api.map.baidu.com/geocoder?" + ak + "=" + ak
					+ "&location=" + lat + "," + lng + "&output=xml");
			Document document = Jsoup.connect(url.toString()).get();
			String address = document.select("formatted_address").first()
					.text();
			String district = document.select("district").first().text();
			map.put("address", address);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	public static void main(String[] args) throws IOException {
		Map<String, String> json = BaiduAPI.decode_from_lat_and_lng(
				"23.109817", "114.425031");
		System.out.println("address :" + json.get("address"));
	}
}