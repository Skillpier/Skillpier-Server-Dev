package com.quark.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class StringUtils {
	
	private final static Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
	
	public static String UpperCaseFirstLatter(String from) {
		return from.substring(0, 1).toUpperCase() + from.substring(1);
	}
	
	/**
	 * 判断是不是一个合法的电子邮件地址
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (email == null || email.trim().length() == 0)
			return false;
		return emailer.matcher(email).matches();
	}
	/**
	 * 去掉包含某值d
	 * @param arr
	 * @param targetValue
	 * @return
	 */
	//String[]time_index={"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24"};
	//int[]time_index2={1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24};
	public static String getContains(String time_slots) {
		int[]time_index2={1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32};
		String[] time_slot_array = time_slots.split("#");
		ArrayList array = new ArrayList();
		for(int i = 0; i < time_index2.length; i++){
			array.add(time_index2[i]);
		}
		for(int i=0;i<time_slot_array.length;i++){
			String time_slot = time_slot_array[i];
			if (!time_slot.equals("")) {
				while(array.remove(new Integer(Integer.parseInt(time_slot)))){};
			}
		}
		Object[] array2 = array.toArray();
		String StrB="";
		for(int i = 0; i < array2.length; i++){
			StrB = StrB+array2[i]+"#";
		}
		return StrB;
	}
	public static String getContainsA(String time_slots) {
		int[]time_index2={1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32};
		String[] time_slot_array = time_slots.split("A");
		ArrayList array = new ArrayList();
		for(int i = 0; i < time_index2.length; i++){
			array.add(time_index2[i]);
		}
		for(int i=0;i<time_slot_array.length;i++){
			String time_slot = time_slot_array[i];
			if (!time_slot.equals("")) {
				while(array.remove(new Integer(Integer.parseInt(time_slot)))){};
			}
		}
		Object[] array2 = array.toArray();
		String StrB="";
		for(int i = 0; i < array2.length; i++){
			StrB = StrB+array2[i]+"A";
		}
		return StrB;
	}
	public static void main(String[] args) {
		String strb = "19#20#21#22";
		System.out.println(getContainsA(strb));
	}
}
