/**
 * 
 */
package com.quark.utils;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

public class Base64Util {

	/**
	 * 将二进制数据编码为BASE64字符串
	 * 
	 * @param binaryData
	 * @return
	 */
	public static String encode(String data) {
		try {
			return new String(Base64.encodeBase64(data.getBytes()), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	/**
	 * 将BASE64字符串恢复为二进制数据
	 * 
	 * @param base64String
	 * @return
	 */
	public static String decode(String code) {
		return new String(Base64.decodeBase64(code));
	}

	public static void main(String[] args) {
		System.out.println(encode("user_10000"));
		System.out.println(decode(encode("user_10000")));
	}
}