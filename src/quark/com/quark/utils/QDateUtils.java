package com.quark.utils;
/**
 * 
 */


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author kingsley 日期工具类，生成当前的年月日,格式为：2014-09-09
 * 
 */
public class QDateUtils {

	
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
	 * 计算两个日期间的分钟数
	 * 
	 * @param smdate
	 * @param bdate
	 * @return
	 * @throws ParseException
	 */
	public static int minuteBetween(String begin,String end){  
		try {
			 SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		        Calendar cal = Calendar.getInstance();    
		        cal.setTime(sdf.parse(begin));    
		        long time1 = cal.getTimeInMillis();                 
		        cal.setTime(sdf.parse(end));    
		        long time2 = cal.getTimeInMillis();         
		        long between_days=(time2-time1)/(1000*60);  
		            
		       return Integer.parseInt(String.valueOf(between_days));  
		} catch (Exception e) {
			e.printStackTrace();
		}
        return 0;
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

	public static void main(String[] args) {
		System.out.println(minuteBetween("2015-09-09 12:29:09", "2015-09-09 12:19:09"));
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
	 * 获得前一天开始的日期整点表示,参数形式：2012/09/09
	 * 
	 * @param date
	 * @return
	 */
	public static String getCurrentDateTimeBegin(int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, days); // 得到前一天
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		return sdf.format(calendar.getTime());
	}
	/**
	 * 获得当时前多少分钟日期整点表示,参数形式：
	 * 
	 * @param date
	 * @return
	 */
	public static String getCurrentDateTimeBeforeMinuts(int minuts) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, minuts); // 得到前一天
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(calendar.getTime());
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
	 * 获得当天结束的日期整点表示,参数形式：2012/09/09
	 * 
	 * @param date
	 * @return
	 */
	public static String getCurrentDateTimeEnd(int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, days); // 得到前一天
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		return sdf.format(calendar.getTime());
	}
	/**
	 * 获得前后日期整点表示,参数形式：2012/09/09
	 * 
	 * @param date
	 * @return
	 */
	public static String getCurrentDate(int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, days); // 得到前一天
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(calendar.getTime());
	}
	/**
	 * 获得前后日期整点表示,参数形式：2012/09/09
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateBeforeFromDate(String from_date,int days) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf.parse(from_date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, days); // 得到前一天
		return sdf.format(calendar.getTime());
	}
	/**
	 * 获得前后日期整点表示,参数形式：2012/09/09
	 * 
	 * @param date
	 * @return
	 */
	public static String getMonthBeforeFromCurrentMonth(String from_month,int month) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		Date date = null;
		try {
			date = sdf.parse(from_month);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, month); //
		return sdf.format(calendar.getTime());
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
	public static String getCurrentDateTime(int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, days); // 得到前一天
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

}
