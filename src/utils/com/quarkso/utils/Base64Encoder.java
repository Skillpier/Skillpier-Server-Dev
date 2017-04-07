/**
 * 
 */
package com.quarkso.utils;

import com.sun.mail.util.BASE64EncoderStream;

/**
 * @author kingsley
 * 
 *         base64编码，生成字符串，不可逆
 * 
 */
public class Base64Encoder {

	public static String encode(String str) {
		return new String(BASE64EncoderStream.encode(str.getBytes()));
	}
}
