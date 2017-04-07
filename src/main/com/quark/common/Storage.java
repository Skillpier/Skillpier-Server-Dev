/**
 * 
 */
package com.quark.common;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author kingsley
 *
 */
public class Storage implements Serializable {

	private static HashMap<String, String> values = new HashMap<String, String>();
	
	public static void put(String key,String value){
		values.put(key, value);
	}
	public static String get(String key){
		return values.get(key);
	}
}
