package com.quark.app.test;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.quark.app.bean.ScheduleBean2;
import com.quark.common.config;
import com.quark.utils.DateUtils;
import com.quark.utils.FileUtils;
import com.quark.utils.MD5Util;

public class test {
	public static void main(String[] args) throws ParseException {
		double average_star = 0;int total_star = 849;
		average_star = total_star/5;
		DecimalFormat df=new DecimalFormat("#");
		average_star=Double.parseDouble(df.format(average_star));
		System.out.println(average_star);
	}
	
}
