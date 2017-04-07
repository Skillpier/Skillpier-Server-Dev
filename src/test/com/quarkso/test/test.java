package com.quarkso.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.quark.utils.DateUtils;
import com.sun.org.apache.bcel.internal.generic.NEW;

public class test {

	public static void main(String[] args) throws Exception {
		String time_slots = "2015-06-11@1@2#2015-06-12@21@22#2015-06-14@23@24";
		String[] time_slot_array = time_slots.split("#");
		for (int i = 0; i < time_slot_array.length; i++) {
			
			System.out.println(time_slot_array[i]+"=="+i);
		}
		String rate = "1.2199",RMB = "24",ddString="";
		double rate2 = Double.parseDouble(RMB)*Double.parseDouble(rate);
		System.out.println(rate2);
		DecimalFormat df = new DecimalFormat(".##");
		ddString = df.format(rate2);
		System.out.println(DateUtils.getCurrentDateTime());
	}

	public static boolean useSet(String[] arr, String targetValue) {
		Set<String> set = new HashSet<String>(Arrays.asList(arr));
		return set.contains(targetValue);
	}

	/**
	 * @author jerry.chen
	 * @param brithday
	 * @return
	 * @throws ParseException
	 *             根据生日获取年龄;
	 */
	public static int getCurrentAgeByBirthdate(String brithday) {
		try {
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
			String currentTime = formatDate.format(calendar.getTime());
			Date today = formatDate.parse(currentTime);
			Date brithDay = formatDate.parse(brithday);

			return today.getYear() - brithDay.getYear();
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 在指定文件路径搜索含有keyword关键字文件，找到后将文件路径输出到控制台。
	 * 先搜索文件名，如果文件名不含keyword关键字时再搜索文件内容是否含有keyword关键字。
	 * 
	 * @param file
	 * @param keyword
	 * @param bufferSize
	 */
	public static void searchBuffer(File file, String keyword, int bufferSize) {
		if (file.isFile()) {
			if (file.getName().contains(keyword.trim())) {
				System.out.println(file.getPath() + "=");

			} else {
				BufferedReader in = null;
				try {
					try {
						in = new BufferedReader(new FileReader(file),
								bufferSize);
						String line = "";
						while ((line = in.readLine()) != null) {
							if (line.contains(keyword.trim())) {
								System.err.println(file.getPath() + "==");
								break;
							}
						}
					} finally {
						if (in != null) {
							in.close();
						}
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		} else {
			File[] filenames = file.listFiles();
			if (filenames == null) {
				return;
			}
			for (File f : filenames) {
				searchBuffer(f, keyword, bufferSize);
			}

		}
	}

	/**
	 * 判断当前日期是星期几
	 * 
	 * @param pTime
	 *            修要判断的时间
	 * @return dayForWeek 判断结果
	 * @Exception 发生异常
	 */
	public static int dayForWeek(String pTime) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(format.parse(pTime));
		int dayForWeek = c.get(Calendar.DAY_OF_WEEK);
		return dayForWeek;
	}

}
