/**
 * 
 */
package com.quark.app.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import cn.jpush.api.examples.PushExample;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinal.upload.UploadFile;
import com.quark.api.annotation.Author;
import com.quark.api.annotation.DataType;
import com.quark.api.annotation.Explaination;
import com.quark.api.annotation.ReturnDBParam;
import com.quark.api.annotation.ReturnOutlet;
import com.quark.api.annotation.Rp;
import com.quark.api.annotation.Type;
import com.quark.api.annotation.URLParam;
import com.quark.api.annotation.UpdateLog;
import com.quark.api.annotation.Value;
import com.quark.api.auto.bean.ResponseValues;
import com.quark.app.bean.CategoryBean;
import com.quark.app.bean.EmailUntil;
import com.quark.app.bean.ScheduleBean;
import com.quark.app.bean.ScheduleBean2;
import com.quark.app.bean.SortClass;
import com.quark.app.logs.AppLog;
import com.quark.common.AppData;
import com.quark.common.RongToken;
import com.quark.common.Storage;
import com.quark.common.config;
import com.quark.interceptor.AppToken;
import com.quark.mail.SendMail;
import com.quark.model.extend.Category01;
import com.quark.model.extend.Category02;
import com.quark.model.extend.Catetory;
import com.quark.model.extend.Collection;
import com.quark.model.extend.Comment;
import com.quark.model.extend.CommentReply;
import com.quark.model.extend.Constant;
import com.quark.model.extend.Course;
import com.quark.model.extend.CourseCertification;
import com.quark.model.extend.Experience;
import com.quark.model.extend.IndexBanner;
import com.quark.model.extend.MyCoupon;
import com.quark.model.extend.Orders;
import com.quark.model.extend.OrdersSchedule;
import com.quark.model.extend.Schedule;
import com.quark.model.extend.Tokens;
import com.quark.model.extend.User;
import com.quark.rsa.RSAsecurity;
import com.quark.sign.RequestHandler;
import com.quark.utils.DateUtils;
import com.quark.utils.EmojiFilter;
import com.quark.utils.FileUtils;
import com.quark.utils.MD5Util;
import com.quark.utils.MessageUtils;
import com.quark.utils.RandomUtils;
import com.quarkso.utils.ImgSavePathUtils;
import com.quarkso.utils.StringUtils;

/**
 * @author C罗
 * 预约时间管理
 */
@Before(Tx.class)
public class TimeOrdersManage extends Controller {

	@Author("cluo")
	@Rp("预约时间、课程（教练）详情")
	@Explaination(info = "预约时间[预约时间需要orders_id，课程（教练）详情orders_id=0]")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.user_id)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.course_id)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String_NotRequired, name = Orders.orders_id)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Schedule.choice_currentdate)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "BookingScheduleListResponse{ScheduleBeans:list[ScheduleBean:hour]}", remarks = "时间段", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "BookingScheduleListResponse{ScheduleBeans:list[ScheduleBean:hour_index]}", remarks = "下标：1至24", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "BookingScheduleListResponse{ScheduleBeans:list[ScheduleBean:schedule_state]}", remarks = " 0-被预约，1-unavaliable，2-busy,3-空闲时间添加备注", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "BookingScheduleListResponse{ScheduleBeans:list[ScheduleBean:schedule_state_message]}", remarks = "状态文字", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "BookingScheduleListResponse{under_select_course_num}", remarks = "未选课程数量:刚下单未选课数=购买量", dataType = DataType.String, defaultValue = "")
	
	@ReturnOutlet(name = "BookingScheduleListResponse{is_stop_course}", remarks = "今后暂停课程 0-否，1-是", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "BookingScheduleListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "BookingScheduleListResponse{status}", remarks = "0-失败，1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "BookingScheduleListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void bookingScheduleList() {
		try {
			String user_id = getPara("user_id");
			String course_id = getPara("course_id");
			String choice_currentdate = getPara("choice_currentdate");
			String orders_id = getPara("orders_id");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("choice_currentdate", choice_currentdate);
			packageParams.put("course_id", course_id);
			packageParams.put("orders_id", orders_id);
			packageParams.put("user_id", user_id);
			RequestHandler reqHandler = new RequestHandler(null, null);
			reqHandler.init(config.app_quark_id, config.app_quark_secret, config.app_quark_key);
			String sign = reqHandler.createSign(packageParams);
			String invoke = getPara("invoke","app");
			if (invoke.equals("h5")) {
				sign = "123456";
			}
			if (!app_sign.equals(sign)) {
				// Signature verification failed.
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Signature verification failed.");
				response2.put("code", 403);
				setAttr("BookingScheduleListResponse", response2);
				renderMultiJson("BookingScheduleListResponse");
				return;
			}
			/**
			 * 接口签名end
			 */
			Course course = Course.dao.findById(course_id);
			//当前日期 // 1-unavaliable，2-busy,3-有空
			int is_stop_course = 0;
			List<ScheduleBean> sBeans = new ArrayList<ScheduleBean>();
			if (course!=null) {
				String course_user_id= course.getStr(course.user_id);
				User course_user = User.dao.findById(course_user_id);
				is_stop_course = course_user.get(course_user.is_stop_course);
				String [][]schedule_time_array22= ScheduleBean2.schedule_time_array2;
				if (is_stop_course==1) {
					//停课
					for(int i=1;i<33;i++){
						ScheduleBean sBean = new ScheduleBean();
						sBean.setHour(schedule_time_array22[i][1]+"-"+schedule_time_array22[i][2]);
						sBean.setHour_index(i);
						sBean.setSchedule_state(1);
						sBean.setSchedule_state_message("unavaliable");
						sBeans.add(sBean);
					}
				}else {
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
						String hours = course.getStr(course.hours);
						ArrayList<Integer> als2 = new ArrayList<Integer>(); //不能选择的
						if (!hours.equals("")) {
							String[] hours_week_array = hours.split("#");
							int week_day = DateUtils.WhatDay(choice_currentdate);
							//设置上课时间
							for(int i=0;i<hours_week_array.length;i++){
								String[] hours_index_array = hours_week_array[i].split("@");
								if (week_day==Integer.parseInt(hours_index_array[1])) {
									als2.add(Integer.parseInt(hours_index_array[0]));
									als2.add(Integer.parseInt(hours_index_array[0])+1);
								}
							}
						}
						////课程时间段end///0-被预约，1-unavaliable，2-busy,3-空闲时间添加备注
						// 时间表默认全天空闲
						List<Schedule> schedules = Schedule.dao.find("select schedule_id,time_slot,remarks,course_id,course_name,course_time,course_location,type from schedule where type!=3 and status=1 and choice_currentdate='"+choice_currentdate+"' and user_id="+course_user_id);
						if (schedules.size()>0) {
							String strb = "#";
							for(Schedule schedule:schedules){
								int time_slot = schedule.get(schedule.time_slot);
								int type = schedule.get(schedule.type);
								////////////
								if (type==0) {
									ScheduleBean sBean = new ScheduleBean();
									sBean.setHour(schedule_time_array22[time_slot][1]+"-"+schedule_time_array22[time_slot][2]);
									sBean.setHour_index(time_slot);
									sBean.setSchedule_state(0);
									//
									sBean.setSchedule_state_message("booked");
									sBeans.add(sBean);
								}
								if (type==1) {
									ScheduleBean sBean = new ScheduleBean();
									sBean.setHour(schedule_time_array22[time_slot][1]+"-"+schedule_time_array22[time_slot][2]);
									sBean.setHour_index(time_slot);
									sBean.setSchedule_state(1);
									//
									sBean.setSchedule_state_message("unavaliable");
									sBeans.add(sBean);
								}
								if (type==2) {
									ScheduleBean sBean = new ScheduleBean();
									sBean.setHour(schedule_time_array22[time_slot][1]+"-"+schedule_time_array22[time_slot][2]);
									sBean.setHour_index(time_slot);
									sBean.setSchedule_state(1);
									//
									sBean.setSchedule_state_message("unavaliable");
									sBeans.add(sBean);
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
											ScheduleBean sBean = new ScheduleBean();
											sBean.setHour(schedule_time_array22[Integer.parseInt(time_slot)][1]+"-"+schedule_time_array22[Integer.parseInt(time_slot)][2]);
											sBean.setHour_index(Integer.parseInt(time_slot));
											sBean.setSchedule_state(1);
											sBean.setSchedule_state_message("unavaliable");
											sBeans.add(sBean);
										}else {
											if (als2.contains(Integer.parseInt(time_slot))) {
												ScheduleBean sBean = new ScheduleBean();
												sBean.setHour(schedule_time_array22[Integer.parseInt(time_slot)][1]+"-"+schedule_time_array22[Integer.parseInt(time_slot)][2]);
												sBean.setHour_index(Integer.parseInt(time_slot));
												sBean.setSchedule_state(3);
												sBean.setSchedule_state_message("avaliable");
												sBeans.add(sBean);
											}else{
												ScheduleBean sBean = new ScheduleBean();
												sBean.setHour(schedule_time_array22[Integer.parseInt(time_slot)][1]+"-"+schedule_time_array22[Integer.parseInt(time_slot)][2]);
												sBean.setHour_index(Integer.parseInt(time_slot));
												sBean.setSchedule_state(1);
												sBean.setSchedule_state_message("unavaliable");
												sBeans.add(sBean);
											}
										}
									}
								}
							}
						}else {
							for(int i=1;i<33;i++){
								if (als.contains(i)) {
									//包含了//选择课程时间,距离现在不可选择的时间（小时）
									ScheduleBean sBean = new ScheduleBean();
									sBean.setHour(schedule_time_array22[i][1]+"-"+schedule_time_array22[i][2]);
									sBean.setHour_index(i);
									sBean.setSchedule_state(1);
									sBean.setSchedule_state_message("unavaliable");
									sBeans.add(sBean);
								}else {
									if (als2.contains(i)) {
										ScheduleBean sBean = new ScheduleBean();
										sBean.setHour(schedule_time_array22[i][1]+"-"+schedule_time_array22[i][2]);
										sBean.setHour_index(i);
										sBean.setSchedule_state(3);
										sBean.setSchedule_state_message("avaliable");
										sBeans.add(sBean);
									}else{
										ScheduleBean sBean = new ScheduleBean();
										sBean.setHour(schedule_time_array22[i][1]+"-"+schedule_time_array22[i][2]);
										sBean.setHour_index(i);
										sBean.setSchedule_state(1);
										sBean.setSchedule_state_message("unavaliable");
										sBeans.add(sBean);
									}
								}
							}
						}
					}else{
						//历史时间不能选择
						for(int i=1;i<33;i++){
							ScheduleBean sBean = new ScheduleBean();
							sBean.setHour(schedule_time_array22[i][1]+"-"+schedule_time_array22[i][2]);
							sBean.setHour_index(i);
							sBean.setSchedule_state(1);
							sBean.setSchedule_state_message("unavaliable");
							sBeans.add(sBean);
						}
					}
				}
				SortClass.sortList(sBeans, "hour_index", false);
			}
			//未选课程数量
			long under_select_course_num = 0;
			if (orders_id!=null&&!orders_id.equals("0")&&!orders_id.equals("")) {
				OrdersSchedule ordersSchedules = OrdersSchedule.dao.findFirst("select count(orders_schedule_id) as under_select_course_num from orders_schedule where ((refund_status=10 and status=21) or (refund_status=10 and status=4)) and orders_id=? ",orders_id);
				if (ordersSchedules!=null) {
					under_select_course_num = ordersSchedules.getLong("under_select_course_num");
				}
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("is_stop_course", is_stop_course);
			responseValues.put("under_select_course_num", under_select_course_num);
			responseValues.put("ScheduleBeans", sBeans);
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "Data request successful.");
			setAttr("BookingScheduleListResponse", responseValues);
			renderMultiJson("BookingScheduleListResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("TimeOrdersManage/bookingScheduleList", "时间表", this);
		}
	}
	@Author("cluo")
	@Rp("预约时间")
	@Explaination(info = "booking预定时间")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = OrdersSchedule.orders_id)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Schedule.course_id)
	@URLParam(defaultValue = "", explain = "课程日期如：2015-06-11A1A2#2015-06-11A22A23#2015-06-14A12A13", type = Type.String, name = "choice_currentdates")
	@URLParam(defaultValue = "", explain = "未选课程数量", type = Type.String, name = "under_select_course_num")
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "BookingResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "BookingResponse{status}", remarks = "0-失败，1-操作成功，2-请选择课程时间，3-选择课程时间数必须小于", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "BookingResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void booking() {
		try {
			String token = getPara("token");
			String orders_id = getPara("orders_id");
			String course_id = getPara("course_id");
			String choice_currentdates = getPara("choice_currentdates");
			int under_select_course_num = getParaToInt("under_select_course_num",0);
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("choice_currentdates", choice_currentdates);
			packageParams.put("course_id", course_id);
			packageParams.put("orders_id", orders_id);
			packageParams.put("token", token);
			packageParams.put("under_select_course_num", under_select_course_num+"");
			RequestHandler reqHandler = new RequestHandler(null, null);
			reqHandler.init(config.app_quark_id, config.app_quark_secret, config.app_quark_key);
			String sign = reqHandler.createSign(packageParams);
			String invoke = getPara("invoke","app");
			if (invoke.equals("h5")) {
				sign = "123456";
			}
			if (!app_sign.equals(sign)) {
				// Signature verification failed.
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Signature verification failed.");
				response2.put("code", 403);
				setAttr("BookingResponse", response2);
				renderMultiJson("BookingResponse");
				return;
			}
			/**
			 * 接口签名end
			 */
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("BookingResponse", response2);
				renderMultiJson("BookingResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="Booking failed. Please review all inputted data and check for errors.";
			if (choice_currentdates==null) {
				status = 2;
				message="Please select your course time.";
			}
			String [][]schedule_time_array22= ScheduleBean2.schedule_time_array2;
			String[] choice_currentdate_array = choice_currentdates.split("#");
			if (choice_currentdate_array.length>under_select_course_num) {
				status = 3;
				message="The number of courses you select cannot exceed the number of courses you have purchased.";
			}
			if (status==0) {
				Course course = Course.dao.findById(course_id);
				boolean save = false;
				if (status==0&&course!=null) {
					String course_user_id = course.getStr(course.user_id);
					int refund_priod_of_validity = 3,order_period_of_validity=1;
					Constant constant = Constant.dao.findFirst("select constant_id,first_joint_fee,order_period_of_validity,refund_priod_of_validity from constant ");
					if (constant!=null) {
						refund_priod_of_validity = constant.get(constant.refund_priod_of_validity);
						order_period_of_validity = constant.get(constant.order_period_of_validity);
					}
					User user = User.dao.findById(user_id);
					String nickname = user.getStr(user.nickname);
					String course_title = course.getStr(course.title);
					String city = course.getStr(course.city);
					String area = course.getStr(course.area);
					String street = course.getStr(course.street);
					String address = course.getStr(course.address);
					StringBuffer buffer = new StringBuffer();  
					for(int i=0;i<choice_currentdate_array.length;i++){
						String schedule_data_indexs = choice_currentdate_array[i];
						String[] schedule_data_index = schedule_data_indexs.split("A");
						boolean save2 = false;
						String schedule_ids = "";
						for(int j=1;j<3;j++){
							int schedule_id = 0;
							Schedule schedule = new Schedule();
							save2 = schedule.set(schedule.user_id, course_user_id)
									.set(schedule.orders_id, orders_id)
									.set(schedule.choice_currentdate, schedule_data_index[0])
									.set(schedule.time_slot, schedule_data_index[j])
									.set(schedule.username, nickname)
									.set(schedule.course_id, course_id)
									.set(schedule.course_name, course_title)
									.set(schedule.course_time, schedule_data_index[0]+" "+schedule_time_array22[Integer.parseInt(schedule_data_index[j])][1]+"~"+schedule_time_array22[Integer.parseInt(schedule_data_index[j])][2])
									.set(schedule.course_location, address)
									.set(schedule.type, 1)
									.set(schedule.status, 1)
									.save();
							if (save2) {
								schedule_id = schedule.get("schedule_id");
								schedule_ids+=schedule_id+"#";
							}
						}
						if (save2) {
							OrdersSchedule ordersSchedule = OrdersSchedule.dao.findFirst("select orders_schedule_id from orders_schedule where orders_id=? and user_id=? and ((refund_status=10 and status=21) or (refund_status=10 and status=4)) ",orders_id,user_id);
							if (ordersSchedule!=null) {
								save = ordersSchedule.set(ordersSchedule.orders_id, orders_id)
										.set(ordersSchedule.user_id, user_id)
										.set(ordersSchedule.schedule_ids, schedule_ids)
										.set(ordersSchedule.schedule_data, schedule_data_index[0])
										.set(ordersSchedule.schedule_hours, schedule_time_array22[Integer.parseInt(schedule_data_index[1])][1]+"~"+schedule_time_array22[Integer.parseInt(schedule_data_index[2])][2])
										.set(ordersSchedule.schedule_time_slots, schedule_data_index[1]+"A"+schedule_data_index[2])
										.set(ordersSchedule.status, 3)
										.set(ordersSchedule.refund_status, 1)
										.set(ordersSchedule.post_time, DateUtils.getCurrentDateTime())
										.set(ordersSchedule.order_period_of_validity, order_period_of_validity)
										.set(ordersSchedule.order_period_start_time, DateUtils.getCurrentDateTime())
										.set(ordersSchedule.refund_priod_of_validity, refund_priod_of_validity)
										.set(ordersSchedule.refund_priod_start_time, DateUtils.getCurrentDateTime())
										.update();
							}
						}
						  buffer.append(schedule_time_array22[Integer.parseInt(schedule_data_index[1])][1]+"~"+schedule_time_array22[Integer.parseInt(schedule_data_index[2])][2]+"  "+DateUtils.WhatDayStr(schedule_data_index[0])+" "+schedule_data_index[0]).append("<br>"); 
					}
					if (save) {
						status = 1;
						message="booking submitted";
						Orders orders = Orders.dao.findById(orders_id);
						String order_number = orders.getStr(orders.order_number);
						//设置不能booking
						OrdersSchedule oSchedules = OrdersSchedule.dao.findFirst("select count(orders_schedule_id) as no_booking_num from orders_schedule where ((status=21 and refund_status=10) or (status=4 and refund_status=10))  and orders_id=? and user_id=?",orders_id,user_id);
						if (oSchedules!=null) {
							long no_booking_num = oSchedules.getLong("no_booking_num");
							if (no_booking_num==0) {
								orders.set(orders.booking_status, 2).update();
							}
						}
						//邮件通知系统-教练预定时间
						EmailUntil.sendEmailCoachBooking(orders,nickname,course_title,buffer.toString());
						String note = nickname + " booked  “"+course_title+"”  . Order number:"+order_number;
						if (save) {
							PushExample.pushToUser(course_user_id+"", note);
						}
					}
				}
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("BookingResponse", responseValues);
			renderMultiJson("BookingResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("TimeOrdersManage/booking", "booking预定时间", this);
		}
	}
}
