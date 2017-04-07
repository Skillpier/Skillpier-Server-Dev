package com.quarkso.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtils {
	public static String getNumber(String value){
		String result = null ;
		Pattern pattern = Pattern.compile("[0-9]+([\\.]?[0-9]*)?");
		Matcher matcher = pattern.matcher(value.trim().replace(" ", ""));
		if (matcher.find()) {
			
//			result = matcher.group();
			result =  matcher.group(0) ;
		}
		return result ;
	}
}
