/**
 * 
 */
package com.quark.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author kingsley 日期工具类，生成当前的年月日,格式为：2014-09-09
 * java.util.Date current = new java.util.Date();
 * java.text.SimpleDateFormat sdf = new  java.text.SimpleDateFormat("MMM dd,yyyy HH:mm:ss",Locale.US);
 * String currentDateTime = sdf.format(current);
 */
public class DateUtils {

	private static Calendar stringToCalendar(String strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}
	/**
	 * 
	 * @param date
	 * @return
	 */
	public static String getDefinedDateTime(String definedTime) {
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(definedTime);
		return sdf.format(dt);
	}
	/**
	 * c1相等c2 ==0
	 * c1小于c2 <0
	 * c1大于c2 >0
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static int comString3(String s1, String s2) {

		java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Calendar c1 = java.util.Calendar.getInstance();
		java.util.Calendar c2 = java.util.Calendar.getInstance();
		try{
			c1.setTime(df.parse(s1));
			c2.setTime(df.parse(s2));
		} catch (java.text.ParseException e) {
			System.err.println("格式不正确");
		}
		int result = c1.compareTo(c2);
		return result;
	}
	/**
	 * 获取两个日期之前的所有日期
	 * 
	 * @param p_start
	 * @param p_end
	 * @return
	 */
	public static List<String> getDates(String start, String end) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		List<String> result = new ArrayList<String>();
		result.add(start);

		Calendar startDay = Calendar.getInstance();
		Calendar endDay = Calendar.getInstance();
		try {
			startDay.setTime(df.parse(start));
			endDay.setTime(df.parse(end));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// 现在打印中的日期
		Calendar currentPrintDay = startDay;
		while (true) {
			// 日期加一
			currentPrintDay.add(Calendar.DATE, 1);
			// 日期加一后判断是否达到终了日，达到则终止打印
			if (currentPrintDay.compareTo(endDay) >= 0) {
				break;
			}
			// 打印日期
			result.add(df.format(currentPrintDay.getTime()));
		}
		if (!start.equals(end)) {
			result.add(end);
		}
		return result;
	}

	public static int getDaysOfMonth(String year_month) {
		Calendar cal = new GregorianCalendar();
		// 或者用Calendar cal = Calendar.getInstance();

		/** 设置date **/
		SimpleDateFormat oSdf = new SimpleDateFormat("", Locale.ENGLISH);
		oSdf.applyPattern("yyyy-MM");
		try {
			cal.setTime(oSdf.parse(year_month));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		/** 或者设置月份，注意月是从0开始计数的，所以用实际的月份-1才是你要的月份 **/
		// 一月份: cal.set( 2009, 1-1, 1 );

		/** 如果要获取上个月的 **/
		// cal.set(Calendar.DAY_OF_MONTH, 1);
		// 日期减一,取得上月最后一天时间对象
		// cal.add(Calendar.DAY_OF_MONTH, -1);
		// 输出上月最后一天日期
		// System.out.println(cal.get(Calendar.DAY_OF_MONTH));
		/** 开始用的这个方法获取月的最大天数，总是得到是31天 **/
		// int num = cal.getMaximum(Calendar.DAY_OF_MONTH);
		/** 开始用的这个方法获取实际月的最大天数 **/
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 
	 * @param s1
	 * @param s2
	 * @return -1:小于，0-等于，1-大于
	 */
	public static int comString2(String s1, String s2) {

		java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");

		java.util.Calendar c1 = java.util.Calendar.getInstance();

		java.util.Calendar c2 = java.util.Calendar.getInstance();

		try
		{
			c1.setTime(df.parse(s1));

			c2.setTime(df.parse(s2));

		} catch (java.text.ParseException e) {

			System.err.println("格式不正确");

		}
		int result = c1.compareTo(c2);
		
		/*if (result == 0){
			System.out.println(c1+"相等"+c2);
		}else if (result < 0){
			System.out.println(c1+"小于"+c2);
		}else {
			System.out.println(c1+"大于"+c2);
		}
		System.out.println(result);*/
		return result;
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
	 * 计算两个日期间的天数
	 * 
	 * @param smdate
	 * @param bdate
	 * @return
	 * @throws ParseException
	 */
	public static int daysBetween(String begin, String end) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(begin));
			long time1 = cal.getTimeInMillis();
			cal.setTime(sdf.parse(end));
			long time2 = cal.getTimeInMillis();
			long between_days = (time2 - time1) / (1000 * 3600 * 24);

			return Integer.parseInt(String.valueOf(between_days));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 计算两个日期间的周末数
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int getSundayNum(String startDate, String endDate) {
		String format = "yyyy-MM-dd";
		List yearMonthDayList = new ArrayList();
		Date start = null, stop = null;
		try {
			start = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
			stop = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (start.after(stop)) {
			Date tmp = start;
			start = stop;
			stop = tmp;
		}
		// 将起止时间中的所有时间加到List中
		Calendar calendarTemp = Calendar.getInstance();
		calendarTemp.setTime(start);
		while (calendarTemp.getTime().getTime() <= stop.getTime()) {
			yearMonthDayList.add(new SimpleDateFormat(format).format(calendarTemp.getTime()));
			calendarTemp.add(Calendar.DAY_OF_YEAR, 1);
		}
		Collections.sort(yearMonthDayList);
		int num = 0;// 周六，周日的总天数
		int size = yearMonthDayList.size();
		int week = 0;
		for (int i = 0; i < size; i++) {
			String day = (String) yearMonthDayList.get(i);
			System.out.println(day);
			week = getWeek(day, format);
			if (week == 6 || week == 0) {// 周六，周日
				num++;
			}
		}
		return num;
	}

	/**
	 * 获取某个日期是星期几
	 * 
	 * @param date
	 * @param format
	 * @return 0-星期日
	 * @author zhaigx
	 * @date 2013-3-13
	 */
	public static int getWeek(String date, String format) {
		Calendar calendarTemp = Calendar.getInstance();
		try {
			calendarTemp.setTime(new SimpleDateFormat(format).parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int i = calendarTemp.get(Calendar.DAY_OF_WEEK);
		int value = i - 1;// 0-星期日
		// System.out.println(value);
		return value;
	}

	/**
	 * 从生日计算岁数
	 * 
	 * @param birthday
	 * @return
	 */
	public static int getAageFromDate(Date birthday) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date currentDate = new Date();
		return currentDate.getYear() - birthday.getYear();
	}

	/**
	 * 从生日计算岁数
	 * 
	 * @param birthday
	 * @return
	 */
	public static int getAageFromDate(String birthday) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date currentDate = new Date();
		Date birthdayDate = new Date(birthday);
		return currentDate.getYear() - birthdayDate.getYear();
	}

	/**
	 * @author cluo
	 * @param brithday
	 *            ,参数形式："1988-10-03"
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
			// TODO: handle exception
			return 0;
		}
	}

	public static int getHourFromDateTime(String datetime) {
		String sufix = datetime.split("\\s")[1];
		int end = sufix.indexOf(":");
		return Integer.valueOf(sufix.substring(0, end));
	}

	/**
	 * datetime转化为date格式
	 * 
	 * @param datetime
	 * @return
	 */
	public static String DateTimeToDate(String datetime) {
		return datetime.split("\\s")[0];
	}

	/**
	 * datetime 格式化
	 * 
	 * @param datetime
	 * @return
	 */
	public static String formatDatetime(String datetime) {
		Date dt = new Date(datetime);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		return sdf.format(dt);
	}

	/**
	 * datetime 格式化
	 * 
	 * @param datetime
	 *            :yyyy年MM月dd日
	 * @return
	 */
	public static String formatDatetime(String datetime, String formatter) {
		if (datetime.contains("-"))
			datetime = datetime.replace("-", "/");
		Date dt = new Date(datetime);
		SimpleDateFormat sdf = new SimpleDateFormat(formatter);
		return sdf.format(dt);
	}

	public static String forMatToSQLDateTime(String datetime) {
		if (datetime.contains("-"))
			datetime = datetime.replace("-", "/");
		Date dt = new Date(datetime);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(dt);
	}

	public static String forMatToSQLDate(String date) {
		if (date.contains("-"))
			date = date.replace("-", "/");
		Date dt = new Date(date);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(dt);
	}

	/**
	 * 获得当前日期：yyyy-MM-dd HH
	 * 
	 * @return
	 */
	public static String getCurrentDateHours() {
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
		return sdf.format(dt);
	}
	
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
	 * 获得当前日期：yyyy-MM-dd
	 * 
	 * @return
	 */
	public static String getCurrentMonth() {
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		return sdf.format(dt);
	}

	/**
	 * 获得当前日期：yyyy-MM-dd
	 * 
	 * @return
	 */
	public static String getCurrentYear() {
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
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
	 * 获得当天开始的日期整点表示,参数形式：2012/09/09
	 * 
	 * @param date
	 * @return
	 */
	public static String getCurrentDateTimeBegin() {
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		return sdf.format(dt);
	}

	/**
	 * 获得当天结束的日期整点表示,参数形式：2012/09/09
	 * 
	 * @param date
	 * @return
	 */
	public static String getCurrentDateTimeEnd() {
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
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
		/*Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(dt);*/
		//美国时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		sdf.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
		System.out.println("Los_Angeles :"+sdf.format(calendar.getTime()));
		return sdf.format(calendar.getTime());
	}

	/**
	 * 获得当前日期：yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 */
	public static String getCurrentDateTimeWithFormat(String format) {
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

	/**
	 * 获取活跃时间
	 * @param begin_time ：2016-01-20 20:07:23
	 * @param end_time ：2016-01-22 20:07:23
	 * @return
	 * @throws ParseException 
	 */
	/*public static String getActiveTime(String begin_time,String end_time,String comment_hour) throws ParseException {
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
		java.util.Date begin=dfs.parse(begin_time);   
	    java.util.Date end = dfs.parse(end_time);   
	    long between=(end.getTime()-begin.getTime())/1000;//除以1000是为了转换成秒   
	    long day1=between/(24*3600);   
	    long hour1=between%(24*3600)/3600;   
	    long minute1=between%3600/60;   
	    long second1=between%60/60;   
	    System.out.println(""+day1+"天"+hour1+"小时"+minute1+"分"+second1+"秒"); 
	    String message = comment_hour;
	    if (day1>0) {
	    	if (day1<7) {
	    		message = day1+" days ago";
			}
		}
	    if (day1==0&&hour1>0) {
	    	message = hour1+" hours ago";
		}
	    if (day1==0&&hour1==0&&minute1>0) {
	    	message = minute1+" minute ago";
		}
	    if (day1==0&&hour1==0&&minute1==0) {
	    	message = "one hours ago";
		}
	    return message;
	}*/
	public static String getAddDaysString(int days,String validatetime){
		try
		 {
		  Calendar fromCal=Calendar.getInstance();
		  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		  Date date=dateFormat.parse(validatetime);
		  fromCal.setTime(date);
		  fromCal.add(Calendar.DATE, days);
		 
		  System.out.println(dateFormat.format(fromCal.getTime()));
		  return dateFormat.format(fromCal.getTime());
		 }
		 catch(Exception e)
		 {
		  e.printStackTrace();
		  return "";
		 }
	}
	public static String getAddDays2(int days,String validatetime){
		try
		 {
		  Calendar fromCal=Calendar.getInstance();
		  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
		  Date date=dateFormat.parse(validatetime);
		  fromCal.setTime(date);
		  fromCal.add(Calendar.DATE, days-1);
		 
		  return dateFormat.format(fromCal.getTime());
		 }
		 catch(Exception e)
		 {
		  e.printStackTrace();
		  return "";
		 }
	}
	public static String getAddHourString(int hours,String validatetime){
		try
		 {
		  Calendar fromCal=Calendar.getInstance();
		  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		  Date date=dateFormat.parse(validatetime);
		  fromCal.setTime(date);
		  fromCal.add(Calendar.HOUR, hours);
		 
		  System.out.println(dateFormat.format(fromCal.getTime()));
		  return dateFormat.format(fromCal.getTime());
		 }
		 catch(Exception e)
		 {
		  e.printStackTrace();
		  return "";
		 }
	}
	/**
	   * 获取时间 小时:分;秒 HH:mm:ss
	   * 
	   * @return
	   */
	public static String getTimeShort() {
	   SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
	   Date currentTime = new Date();
	   String dateString = formatter.format(currentTime);
	   return dateString;
	}
	public static String getActiveTime(String begin_time,String end_time) throws ParseException {
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
		java.util.Date begin=dfs.parse(begin_time);   
	    java.util.Date end = dfs.parse(end_time);   
	    long between=(end.getTime()-begin.getTime())/1000;//除以1000是为了转换成秒   
	    long day1=between/(24*3600);   
	    long hour1=between%(24*3600)/3600;   
	    long minute1=between%3600/60;   
	    long second1=between%60/60;   
	    System.out.println(""+day1+"天"+hour1+"小时"+minute1+"分"+second1+"秒"); 
	    String message = "a few years ago";
	    if (day1>0) {
	    	if (day1<30&&day1>0) {
	    		message = day1+" days ago";
			}
	    	if(day1>=30&&day1<60){
	    		message = "one month ago";
	    	}
	    	if(day1>=60&&day1<360){
	    		int day2 = (int)day1/30;
	    		message = day2+" months ago";
	    	}
		}
	    if (day1==0&&hour1>0) {
	    	message = hour1+" hours ago";
		}
	    if (day1==0&&hour1==0&&minute1>0) {
	    	message = minute1+" minute ago";
		}
	    if (day1==0&&hour1==0&&minute1==0) {
	    	message = "one second ago";//"现在活跃";
		}
	    return message;
	}
	public static String getTimeStampNo(){
		String aa  = getTimeStamp();
		return aa.substring(aa.length()-8,aa.length());
	}
	public static String getTimeStamp() {
		return String.valueOf(System.currentTimeMillis() / 1000);
	}
	/**
	 * 1-星期日，2-星期一，3-星期二，4-星期三，5-星期四，6-星期五，7-星期六
	 * @return
	 */
	public static int WhatDayCurrent(){
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(System.currentTimeMillis()));
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		return dayOfWeek;
	}
	/**
	 * 1-星期日，2-星期一，3-星期二，4-星期三，5-星期四，6-星期五，7-星期六
	 * @return
	 * @throws ParseException 
	 */
	public static int WhatDay(String pTime) throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(format.parse(pTime));
		int dayForWeek = c.get(Calendar.DAY_OF_WEEK);
		return dayForWeek;
	}
	
	/**
	 * 1-星期日，2-星期一，3-星期二，4-星期三，5-星期四，6-星期五，7-星期六
	 * @return
	 * @throws ParseException 
	 */
	public static String WhatDayStr(String pTime) throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(format.parse(pTime));
		String dayString="";
		int dayForWeek = c.get(Calendar.DAY_OF_WEEK);
		if (dayForWeek==1) {
			dayString="Sunday";
		}if (dayForWeek==2) {
			dayString="Monday";
		}if (dayForWeek==3) {
			dayString="Tuesday";
		}if (dayForWeek==4) {
			dayString="Wednesday";
		}if (dayForWeek==5) {
			dayString="Thursday";
		}if (dayForWeek==6) {
			dayString="Friday";
		}if (dayForWeek==7) {
			dayString="Saturday";
		}
		return dayString;
	}
	
	public static void main(String[] args) throws ParseException {
		System.out.println(WhatDay("2016-09-19"));
	}
}
