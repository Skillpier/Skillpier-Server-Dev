package com.quark.app.bean;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.quark.model.extend.Constant;
import com.quark.model.extend.Course;
import com.quark.model.extend.Schedule;
import com.quark.model.extend.User;
import com.quark.utils.DateUtils;

public class ScheduleBean2 {
	//一天24小格【1表09：00-09：30,2表10：00-10：30,3表11：00-11：30......依次类推】
	public static String [][]schedule_time_array1={
			{"0","00:00","09:00"},
			{"1","09:00","09:30"},
			{"2","09:30","10:00"},
			{"3","10:00","10:30"},
			{"4","10:30","11:00"},
			{"5","11:00","11:30"},
			{"6","11:30","12:00"},
			{"7","12:00","12:30"},
			{"8","12:30","13:00"},
			{"9","13:00","13:30"},
			{"10","13:30","14:00"},
			{"11","14:00","14:30"},
			{"12","14:30","15:00"},
			{"13","15:00","15:30"},
			{"14","15:30","16:00"},
			{"15","16:00","16:30"},
			{"16","16:30","17:00"},
			{"17","17:00","17:30"},
			{"18","17:30","18:00"},
			{"19","18:00","18:30"},
			{"20","18:30","19:00"},
			{"21","19:00","19:30"},
			{"22","19:30","20:00"},
			{"23","20:00","20:30"},
			{"24","20:30","21:00"}
		};
	//时间表从早上6点到晚上10点
	//一天24小格【1表06：00-06：30,2表6：30-10：30,3表11：00-11：30......依次类推】
	public static String [][]schedule_time_array2={
			{"0","00:00","06:00"},
			{"1","06:00","06:30"},
			{"2","06:30","07:00"},
			{"3","07:00","07:30"},
			{"4","07:30","08:00"},
			{"5","08:00","08:30"},
			{"6","08:30","09:00"},
			{"7","09:00","09:30"},
			{"8","09:30","10:00"},
			{"9","10:00","10:30"},
			{"10","10:30","11:00"},
			{"11","11:00","11:30"},
			{"12","11:30","12:00"},
			{"13","12:00","12:30"},
			{"14","12:30","13:00"},
			{"15","13:00","13:30"},
			{"16","13:30","14:00"},
			{"17","14:00","14:30"},
			{"18","14:30","15:00"},
			{"19","15:00","15:30"},
			{"20","15:30","16:00"},
			{"21","16:00","16:30"},
			{"22","16:30","17:00"},
			{"23","17:00","17:30"},
			{"24","17:30","18:00"},
			{"25","18:00","18:30"},
			{"26","18:30","19:00"},
			{"27","19:00","19:30"},
			{"28","19:30","20:00"},
			{"29","20:00","20:30"},
			{"30","20:30","21:00"},
			{"31","21:00","21:30"},
			{"32","21:30","22:00"}
		};
	public static void main(String[] args) throws ParseException {
		/*String [][]schedule_time_array2= schedule_time_array1;
		 for(int i=0 ; i <schedule_time_array1.length ; i++) { 
	            for(int j=0 ; j<schedule_time_array1[i].length ; j++) { 
	                System.out.println("a[" + i + "][" + j + "]=" + schedule_time_array1[i][j]) ; 
	            } 
	        } */
		String currentDate = DateUtils.getCurrentDate();
		System.out.println(DateUtils.getAddDays2(1,currentDate));
	}
	////
	public static String freeCourseDay(int course_id) throws ParseException{
		Course course = Course.dao.findById(course_id);
		String user_id = course.getStr(course.user_id);
		User user = User.dao.findById(user_id);
		int is_stop_course = user.get(user.is_stop_course);
		String choice_day = "";
		if (is_stop_course==0) {
			int z = 1;
			String currect_time = DateUtils.getCurrentDateTime(); 
			String begin_time = DateUtils.getDefinedDateTime("yyyy-MM-dd 21:30:30");
			String end_time = DateUtils.getDefinedDateTime("yyyy-MM-dd 23:59:40");
			if (DateUtils.comString3(currect_time, begin_time)>0&&DateUtils.comString3(currect_time, end_time)<0) {
				z = z+1;
			}
			while (z>0) {
				boolean flag = false;
				//当前日期
				String next_currentDate = DateUtils.getAddDays2(z,DateUtils.getCurrentDate());
				//星期几
				int week_day = DateUtils.WhatDay(next_currentDate);
				//下一天
				String hours = course.getStr(course.hours);
				if (!hours.equals("")) {
					String[] hours_week_day_array = hours.split("#");
					for(int w=0;w<hours_week_day_array.length;w++){
						String hours_week_arrays = hours_week_day_array[w];
						String[] hours_week_array = hours_week_arrays.split("@");
						//星期几
						int hours_week = Integer.parseInt(hours_week_array[1]);
						if (hours_week==week_day) {
							List<Schedule> schedules = Schedule.dao.find("select schedule_id from schedule where user_id="+user_id+" and choice_currentdate='"+next_currentDate+"' and status=1 and (type=0 or type=1 or type=2)");
							if (schedules.size()==32) {
								//没有时间选择
							}else {
								choice_day = next_currentDate;
								//flag =true;
								break;
							}
						}else {
							//这项没有时间选择
						}
					}
				}
				//////////////////////////////////////////////////////////
				List<ScheduleBean> sBeans = new ArrayList<ScheduleBean>();
				String choice_currentdate = next_currentDate;
				String [][]schedule_time_array22= ScheduleBean2.schedule_time_array2;
				{
					//选择课程时间,距离现在不可选择的时间（小时）
					int booking_period_of_validity=1;
					String current_date = DateUtils.getCurrentDate();
					ArrayList<Integer> als = new ArrayList<Integer>(); //不能选择的
					if (DateUtils.comString2(current_date, choice_currentdate)<= 0) {
						//未来时间可以选择
						if (DateUtils.comString2(current_date, choice_currentdate)==0) {
							Constant courseInfo = Constant.dao.findFirst("select constant_id,booking_period_of_validity from constant");
							if (courseInfo!=null) {
								booking_period_of_validity = courseInfo.get(courseInfo.booking_period_of_validity);
							}
							String current_time = DateUtils.getAddHourString(booking_period_of_validity,DateUtils.getCurrentDateTime());
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
							LocalDateTime ldt = LocalDateTime.parse(current_time, formatter);
							String hour = "", minute = "";
							hour = ldt.getHour() + "";
							minute = ldt.getMinute() + "";
							if (ldt.getHour() < 10) {
								hour = "0" + ldt.getHour();
							}
							if (ldt.getMinute() < 10) {
								minute = "0" + ldt.getMinute();
							}
							String scedule_hour_minute = hour+":"+minute;
							for (int i = 0; i < schedule_time_array22.length; i++) {
								if (scedule_hour_minute.compareTo(schedule_time_array22[i][2])> 0) {
									// 表示小于
									if ((i+1)<33) {
										als.add((i+1));
									}
								}
							}
						}
						/*end*/
						ArrayList<Integer> als2 = new ArrayList<Integer>(); //不能选择的
						if (!hours.equals("")) {
							String[] hours_week_array = hours.split("#");
							week_day = DateUtils.WhatDay(choice_currentdate);
							//设置上课时间
							for(int i=0;i<hours_week_array.length;i++){
								String[] hours_index_array = hours_week_array[i].split("@");
								if (week_day==Integer.parseInt(hours_index_array[1])) {
									als2.add(Integer.parseInt(hours_index_array[0]));
									als2.add(Integer.parseInt(hours_index_array[0])+1);
								}
							}
						}
						////课程时间段end///
						// 时间表默认全天空闲
						List<Schedule> schedules = Schedule.dao.find("select schedule_id,time_slot,remarks,course_id,course_name,course_time,course_location,type from schedule where type!=3 and status=1 and choice_currentdate='"+choice_currentdate+"' and user_id="+user_id);
						if (schedules.size()>0) {
							String strb = "#";
							for(Schedule schedule:schedules){
								int time_slot = schedule.get(schedule.time_slot);
								int type = schedule.get(schedule.type);
								////////////
								if (type==1) {
									/*ScheduleBean sBean = new ScheduleBean();
									sBean.setHour(schedule_time_array22[time_slot][1]+"-"+schedule_time_array22[time_slot][2]);
									sBean.setHour_index(time_slot);
									sBean.setSchedule_state(1);
									sBean.setSchedule_state_message("unavaliable");
									sBeans.add(sBean);*/
								}
								if (type==2) {
									/*ScheduleBean sBean = new ScheduleBean();
									sBean.setHour(schedule_time_array22[time_slot][1]+"-"+schedule_time_array22[time_slot][2]);
									sBean.setHour_index(time_slot);
									sBean.setSchedule_state(1);
									//
									sBean.setSchedule_state_message("unavaliable");
									sBeans.add(sBean);*/
								}
								strb = strb+time_slot+"#";
							}
							//去掉
							String times = com.quark.utils.StringUtils.getContains(strb);
							if (times!=null&&!times.equals("")) {
								String[] times_array = times.split("#");
								for(int i=0;i<times_array.length;i++){
									String time_slot = times_array[i];
									if (time_slot!=null&&!time_slot.equals("")) {
										if (als.contains(Integer.parseInt(time_slot))) {
											//包含了
											/*ScheduleBean sBean = new ScheduleBean();
											sBean.setHour(schedule_time_array22[Integer.parseInt(time_slot)][1]+"-"+schedule_time_array22[Integer.parseInt(time_slot)][2]);
											sBean.setHour_index(Integer.parseInt(time_slot));
											sBean.setSchedule_state(1);
											sBean.setSchedule_state_message("unavaliable");
											sBeans.add(sBean);*/
										}else {
											if (als2.contains(Integer.parseInt(time_slot))) {
												ScheduleBean sBean = new ScheduleBean();
												sBean.setHour(schedule_time_array22[Integer.parseInt(time_slot)][1]+"-"+schedule_time_array22[Integer.parseInt(time_slot)][2]);
												sBean.setHour_index(Integer.parseInt(time_slot));
												sBean.setSchedule_state(3);
												sBean.setSchedule_state_message("avaliable");
												sBeans.add(sBean);
											}else{
												/*ScheduleBean sBean = new ScheduleBean();
												sBean.setHour(schedule_time_array22[Integer.parseInt(time_slot)][1]+"-"+schedule_time_array22[Integer.parseInt(time_slot)][2]);
												sBean.setHour_index(Integer.parseInt(time_slot));
												sBean.setSchedule_state(1);
												sBean.setSchedule_state_message("unavaliable");
												sBeans.add(sBean);*/
											}
										}
									}
								}
							}
						}else {
							for(int i=1;i<33;i++){
								if (als.contains(i)) {
									//包含了//选择课程时间,距离现在不可选择的时间（小时）
									/*ScheduleBean sBean = new ScheduleBean();
									sBean.setHour(schedule_time_array22[i][1]+"-"+schedule_time_array22[i][2]);
									sBean.setHour_index(i);
									sBean.setSchedule_state(1);
									sBean.setSchedule_state_message("unavaliable");
									sBeans.add(sBean);*/
								}else {
									if (als2.contains(i)) {
										ScheduleBean sBean = new ScheduleBean();
										sBean.setHour(schedule_time_array22[i][1]+"-"+schedule_time_array22[i][2]);
										sBean.setHour_index(i);
										sBean.setSchedule_state(3);
										sBean.setSchedule_state_message("avaliable");
										sBeans.add(sBean);
									}else{
										/*ScheduleBean sBean = new ScheduleBean();
										sBean.setHour(schedule_time_array22[i][1]+"-"+schedule_time_array22[i][2]);
										sBean.setHour_index(i);
										sBean.setSchedule_state(1);
										sBean.setSchedule_state_message("unavaliable");
										sBeans.add(sBean);*/
									}
								}
							}
						}
					}else{
						//历史时间不能选择
						for(int i=1;i<33;i++){
							/*ScheduleBean sBean = new ScheduleBean();
							sBean.setHour(schedule_time_array22[i][1]+"-"+schedule_time_array22[i][2]);
							sBean.setHour_index(i);
							sBean.setSchedule_state(1);
							sBean.setSchedule_state_message("unavaliable");
							sBeans.add(sBean);*/
						}
					}
				}
				//////////////////////////////////////////////////////////
				if (sBeans.size()>0) {
					choice_day = next_currentDate;
					//flag =true;
					break;
				}
				if (flag) {
					break;
				}
				z = z+1;
			}
			System.out.println(course_id+"===========choice_day=="+choice_day+"==is_stop_course="+is_stop_course);
		}
		return choice_day;
	}
}
