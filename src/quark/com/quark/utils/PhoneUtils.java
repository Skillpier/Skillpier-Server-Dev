/**
 * 
 */
package com.quark.utils;

/**
 * @author kingsley
 *
 */
public class PhoneUtils {

	public static String encode(String telephone){
		String prefix = telephone.substring(0,3);
		String suffix = telephone.substring(telephone.length()-4);
		return prefix+"****"+suffix;
	}
	public static void main(String[] args) {
		System.out.println(PhoneUtils.encode("18520844021"));
	}
}
