/**
 * 
 */
package com.quarkso.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author kingsley 日期工具类，生成当前的年月日,格式为：2014-09-09
 * 
 */
public class DateUitls {

	/**
	 * 获得当前日期：yyyy-MM-dd
	 * 
	 * @return
	 */
	public static String getCurrentDate() {
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(dt);
	}

	/**
	 * 获得某一天的日期整点表示
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateTime(String date) {
		Date dt = new Date(date);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		return sdf.format(dt);
	}

	/**
	 * 获得某一天的日期整点表示,参数形式：2012/09/09
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateTimeBegin(String date) {
		Date dt = new Date(date);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		return sdf.format(dt);
	}

	/**
	 * 获得某一天的日期整点表示,参数形式：2012/09/09
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateTimeEnd(String date) {
		Date dt = new Date(date);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		return sdf.format(dt);
	}

	/**
	 * 获得当前日期：yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 */
	public static String getCurrentDateTime() {
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(dt);
	}

	/**
	 * 获得当前日期：yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 */
	public static String getCurrentDateTime(String format) {
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(dt);
	}

	/**
	 * 获得当天日期的整点表示 ：2014-07-30 00:00:00
	 * 
	 * @return
	 */
	public static String getCurrentDateTimePointBegin() {
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		return sdf.format(dt);
	}

	/**
	 * 获得当天日期的整点表示 ：2014-07-30 00:00:00
	 * 
	 * @return
	 */
	public static String getCurrentDateTimePointBegin(String date) {
		Date dt = new Date(date);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		return sdf.format(dt);
	}

	/**
	 * 获得当天日期的最后整点表示 ：2014-07-30 23:59:59
	 * 
	 * @return
	 */
	public static String getCurrentDateTimePointEnd() {
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		return sdf.format(dt);
	}

	/**
	 * 得到上一周的日期
	 * 
	 * @return
	 */
	public static String getLastWeek() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -7); // 得到前一天
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(calendar.getTime());
	}

	/**
	 * 得到上一个月的日期
	 * 
	 * @return
	 */
	public static String getLastMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1); // 得到前一个月
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(calendar.getTime());
	}

	public static long getNextsDay(String dateString, int nextDayNum) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, nextDayNum); // 得到前一天
		return calendar.getTimeInMillis();
	}

	/**
	 * 获取指定月的前一月（年）或后一月（年）
	 * 
	 * @param dateStr
	 * @param addYear
	 * @param addMonth
	 * @param addDate
	 * @return 输入的时期格式为yyyy-MM，输出的日期格式为yyyy-MM
	 * @throws Exception
	 */
	public static long getLastMonth(String dateStr, int addYear, int addMonth, int addDate) throws Exception {
		try {
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy年MM月dd日");
			java.util.Date sourceDate = sdf.parse(dateStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(sourceDate);
			cal.add(Calendar.YEAR, addYear);
			cal.add(Calendar.MONTH, addMonth);
			cal.add(Calendar.DATE, addDate);

			long dateTmp = cal.getTimeInMillis();
			// java.util.Date returnDate = returnSdf.parse(dateTmp);
			return dateTmp;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	public static void main(String[] args) {
		System.out.println(getTwoDateTimeSeparated("2015-09-09 12:08:01", "2015-09-09 12:12:01"));
	}

	/**
	 * 
	 * @param end
	 * @param start
	 * @return true:end>start、false:start>end
	 */
	public static boolean diff(String end, String start) {
		Timestamp startDate = null;
		Timestamp endDate = null;
		try {
			startDate = java.sql.Timestamp.valueOf(start);
			endDate = java.sql.Timestamp.valueOf(end);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (startDate.before(endDate)) {
			System.out.println("start is before the endDate");
			return true;
		}
		if (startDate.after(endDate)) {
			System.out.println("start is after the endDate");
			return false;
		}
		if (startDate.equals(endDate)) {
			System.out.println("start is equals the endDate");
			return false;
		}
		return false;
	}

	/**
	 * 获取指定月的前一月（年）或后一月（年）
	 * 
	 * @param dateStr
	 * @param addYear
	 * @param addMonth
	 * @param addDate
	 * @return 输入的时期格式为yyyy-MM-dd，输出的日期格式为yyyy-MM-dd
	 * @throws Exception
	 */
	public static String getLastDay(String dateStr, int addYear, int addMonth, int addDate) throws Exception {
		try {
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
			java.util.Date sourceDate = sdf.parse(dateStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(sourceDate);
			cal.add(Calendar.YEAR, addYear);
			cal.add(Calendar.MONTH, addMonth);
			cal.add(Calendar.DATE, addDate);

			java.text.SimpleDateFormat returnSdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
			String dateTmp = returnSdf.format(cal.getTime());
			java.util.Date returnDate = returnSdf.parse(dateTmp);
			return dateTmp;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	public static long timeMillis() {
		return System.currentTimeMillis();
	}

	/*
	 * 得到这两个时间相差的分钟数
	 */
	public static long getTwoDateTimeSeparated(String startDatetime, String endDatetime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long minutes = 0;
		try {
			minutes = (sdf.parse(endDatetime).getTime() - sdf.parse(startDatetime).getTime()) / 1000 / 60;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return minutes;
	}

	// 获得当前周- 周一的日期
	public static String getCurrentMonday() {
		int mondayPlus = getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus);
		Date monday = currentDate.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		String preMonday = dateFormat.format(monday);
		return preMonday;
	}

	public static String getCurrentMondayDF() {
		int mondayPlus = getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus);
		Date monday = currentDate.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
		String preMonday = dateFormat.format(monday);
		return preMonday;
	}

	// 获得当前周- 周日 的日期
	public static String getPreviousSunday() {
		int mondayPlus = getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 6);
		Date monday = currentDate.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		String preMonday = dateFormat.format(monday);
		return preMonday;

	}

	public static String getPreviousSundayDF() {
		int mondayPlus = getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 6);
		Date monday = currentDate.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
		String preMonday = dateFormat.format(monday);
		return preMonday;

	}

	// 获得当前日期与本周一相差的天数
	public static int getMondayPlus() {
		Calendar cd = Calendar.getInstance();
		// 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
		int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == 1) {
			return -6;
		} else {
			return 2 - dayOfWeek;
		}
	}

	// 获取一周的每天
	public static ArrayList printWeekdays() {
		ArrayList days = new ArrayList();
		final int FIRST_DAY = Calendar.MONDAY;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();
		while (calendar.get(Calendar.DAY_OF_WEEK) != FIRST_DAY) {
			calendar.add(Calendar.DATE, -1);
		}
		for (int i = 0; i < 7; i++) {
			days.add(dateFormat.format(calendar.getTime()));
			calendar.add(Calendar.DATE, 1);
		}
		return days;
	}

	/**
	 * 当月第一天
	 * 
	 * @return
	 */
	public static String getFirstDay() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		Date theDate = calendar.getTime();

		GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
		gcLast.setTime(theDate);
		gcLast.set(Calendar.DAY_OF_MONTH, 1);
		String day_first = df.format(gcLast.getTime());
		StringBuffer str = new StringBuffer().append(day_first).append(" 00:00:00");
		return str.toString();
	}

	public static String getFirstDayDF() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
		Calendar calendar = Calendar.getInstance();
		Date theDate = calendar.getTime();

		GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
		gcLast.setTime(theDate);
		gcLast.set(Calendar.DAY_OF_MONTH, 1);
		String day_first = df.format(gcLast.getTime());
		return day_first;
	}

	/**
	 * 当月最后一天
	 * 
	 * @return
	 */
	public static String getLastDay() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		Date theDate = calendar.getTime();
		String s = df.format(theDate);
		StringBuffer str = new StringBuffer().append(s).append(" 23:59:59");
		return str.toString();
	}

	public static String getLastDayDF() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
		DateFormat format = new SimpleDateFormat("yyyy.MM.dd");
		String s = format.format(calendar.getTime());
		return s;
	}

	/**
	 * 获取某年第一天日期
	 * 
	 * @param year
	 *            年份
	 * @return Date
	 */
	public static String getCurrYearFirst() {
		Calendar currCal = Calendar.getInstance();
		int currentYear = currCal.get(Calendar.YEAR);
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, currentYear);
		Date currYearFirst = calendar.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String preYearFirst = dateFormat.format(currYearFirst);
		return preYearFirst;
	}

	/**
	 * 获取某年最后一天日期
	 * 
	 * @param year
	 *            年份
	 * @return Date
	 */
	public static String getYearLast() {
		Calendar currCal = Calendar.getInstance();
		int currentYear = currCal.get(Calendar.YEAR);
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, currentYear);
		calendar.roll(Calendar.DAY_OF_YEAR, -1);
		Date currYearLast = calendar.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		String preYearLast = dateFormat.format(currYearLast);
		return preYearLast;
	}
}
