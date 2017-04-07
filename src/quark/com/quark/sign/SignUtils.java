package com.quark.sign;

import java.util.SortedMap;
import java.util.TreeMap;

import com.quark.common.config;

public class SignUtils {

	/**
	 * 规则是:按参数名称a-z排序,遇到空值的参数不参加签名。
	 * @param timestamp
	 * @param nonce_str
	 * @param productId
	 * @return
	 */
	public static String SetSign(String timestamp,String nonce_str,String productId){//签名
		// 随机字符串
		RequestHandler reqHandler = new RequestHandler(null, null);
		reqHandler.init(config.app_quark_id, config.app_quark_secret, config.app_quark_key);
		SortedMap<String, String> packageParams = new TreeMap<String, String>();
		packageParams.put("appid", config.app_quark_key);
		packageParams.put("nonce_str", nonce_str);
		packageParams.put("product_id", productId);
		packageParams.put("time_stamp", timestamp);
		String sign = reqHandler.createSign(packageParams);
		System.out.println(sign);
		return sign;
	}
	public static void main(String[] args) 
	{
		
	}
}
