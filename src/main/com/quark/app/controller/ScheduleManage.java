/**
 * 
 */
package com.quark.app.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

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
import com.quark.app.bean.CourseVedioBean;
import com.quark.app.bean.FileNameBean;
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
import com.quark.model.extend.Course;
import com.quark.model.extend.CourseCertification;
import com.quark.model.extend.Experience;
import com.quark.model.extend.Orders;
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
import com.sun.org.apache.xpath.internal.operations.And;

/**
 * @author C罗
 * 教练时间管理
 * 历史、有课程的不能选择。默认时间有空。
 * 今后停课 ScheduleBeans
 */
@Before(Tx.class)
public class ScheduleManage extends Controller {

	@Author("cluo")
	@Rp("时间管理")
	@Explaination(info = "时间表")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = User.user_id)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Schedule.choice_currentdate)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "ScheduleListResponse{ScheduleBeans:list[ScheduleBean:course_id]}", remarks = "课程id", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "ScheduleListResponse{ScheduleBeans:list[ScheduleBean:course_name]}", remarks = "课程名称", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "ScheduleListResponse{ScheduleBeans:list[ScheduleBean:course_location]}", remarks = "课程地址", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "ScheduleListResponse{ScheduleBeans:list[ScheduleBean:course_time]}", remarks = "课程时间", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "ScheduleListResponse{ScheduleBeans:list[ScheduleBean:hour]}", remarks = "时间段", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "ScheduleListResponse{ScheduleBeans:list[ScheduleBean:schedule_state]}", remarks = " 0-被预约，1-unavaliable，2-busy,3-有空", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "ScheduleListResponse{ScheduleBeans:list[ScheduleBean:hour_index]}", remarks = "下标：1至24", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "ScheduleListResponse{ScheduleBeans:list[ScheduleBean:remarks]}", remarks = "备注", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "ScheduleListResponse{ScheduleBeans:list[ScheduleBean:username]}", remarks = "用户姓名", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "ScheduleListResponse{is_stop_course}", remarks = "今后暂停课程 0-否，1-是", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "ScheduleListResponse{is_busy_24}", remarks = "全天没有空  0-否，1-是", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "ScheduleListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "ScheduleListResponse{status}", remarks = "0-失败，1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "ScheduleListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void scheduleList() {
		try {
			String user_id = getPara("user_id");
			String choice_currentdate = getPara("choice_currentdate");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("choice_currentdate", choice_currentdate);
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
				setAttr("ScheduleListResponse", response2);
				renderMultiJson("ScheduleListResponse");
				return;
			}
			/**
			 * 接口签名end
			 */
			if (user_id==null||user_id.equals("")) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("ScheduleListResponse", response2);
				renderMultiJson("ScheduleListResponse");
				return;
			}
			//是否今后停课
			User user = User.dao.findById(user_id);
		    int is_stop_course = user.get(user.is_stop_course);
		    //当前日期 // 1-unavaliable，2-busy,3-有空
		    String [][]schedule_time_array22= ScheduleBean2.schedule_time_array2;
		    List<ScheduleBean> sBeans = new ArrayList<ScheduleBean>();
			if (is_stop_course==0) {
				List<Schedule> schedules = Schedule.dao.find("select schedule_id,time_slot,remarks,course_id,username,course_name,course_time,course_location,type from schedule where status=1 and choice_currentdate='"+choice_currentdate+"' and user_id="+user_id);
				if (schedules.size()>0) {
					String strb = "#";
					for(Schedule schedule:schedules){
						int time_slot = schedule.get(schedule.time_slot);
						int type = schedule.get(schedule.type);
						String remarks = schedule.getStr(schedule.remarks);
						int course_id = schedule.get(schedule.course_id);
						String course_name = schedule.getStr(schedule.course_name);
						String course_time = schedule.getStr(schedule.course_time);
						String course_location = schedule.getStr(schedule.course_location);
						String username = schedule.getStr(schedule.username);
						////////////
						if (type==1||type==0) {
							ScheduleBean sBean = new ScheduleBean();
							sBean.setHour(schedule_time_array22[time_slot][1]+"-"+schedule_time_array22[time_slot][2]);
							sBean.setHour_index(time_slot);
							if (type==1) {
								sBean.setSchedule_state(1);
								sBean.setSchedule_state_message("unavaliable");
							}
							if (type==0) {
								sBean.setSchedule_state(0);
								sBean.setSchedule_state_message("This time is reserved.");//被预约
							}
							//
							sBean.setCourse_id(course_id);
							sBean.setCourse_location(course_location);
							sBean.setCourse_name(course_name);
							sBean.setCourse_time(course_time);
							sBean.setUsername(username);
							sBean.setRemarks(remarks);
							sBeans.add(sBean);
						}
						if (type==2) {
							ScheduleBean sBean = new ScheduleBean();
							sBean.setHour(schedule_time_array22[time_slot][1]+"-"+schedule_time_array22[time_slot][2]);
							sBean.setHour_index(time_slot);
							sBean.setSchedule_state(2);
							//
							sBean.setCourse_id(course_id);
							sBean.setCourse_location(course_location);
							sBean.setCourse_name(course_name);
							sBean.setCourse_time(course_time);
							sBean.setUsername(username);
							sBean.setRemarks(remarks);
							sBean.setSchedule_state_message("busy");
							sBeans.add(sBean);
						}
						if (type==3) {
							ScheduleBean sBean = new ScheduleBean();
							sBean.setHour(schedule_time_array22[time_slot][1]+"-"+schedule_time_array22[time_slot][2]);
							sBean.setHour_index(time_slot);
							//
							sBean.setCourse_id(course_id);
							sBean.setCourse_location(course_location);
							sBean.setCourse_name(course_name);
							sBean.setCourse_time(course_time);
							sBean.setUsername(username);
							sBean.setRemarks(remarks);
							sBean.setSchedule_state(3);
							sBean.setSchedule_state_message("avaliable");
							sBeans.add(sBean);
						}
						strb = strb+time_slot+"#";
					}
					//去掉strb这部分
					String times = com.quark.utils.StringUtils.getContains(strb);
					if (times!=null&&!times.equals("")) {
						String[] times_array = times.split("#");
						for(int i=0;i<times_array.length;i++){
							String time_slot = times_array[i];
							if (time_slot!=null&&!time_slot.equals("")) {
								String current_date = DateUtils.getCurrentDateTime();
								String choice_currentdate2 = choice_currentdate+" "+schedule_time_array22[Integer.parseInt(time_slot)][1]+":00";
								ScheduleBean sBean = new ScheduleBean();
								sBean.setHour(schedule_time_array22[Integer.parseInt(time_slot)][1]+"-"+schedule_time_array22[Integer.parseInt(time_slot)][2]);
								sBean.setHour_index(Integer.parseInt(time_slot));
								if (DateUtils.comString3(current_date, choice_currentdate2)> 0) {
									//历史时间 
									sBean.setSchedule_state(1);
									sBean.setSchedule_state_message("unavaliable");
								}else{
									sBean.setSchedule_state(3);
									sBean.setSchedule_state_message("avaliable");
								}
								sBeans.add(sBean);
							}
						}
					}
				}else {
					for(int i=1;i<33;i++){
						ScheduleBean sBean = new ScheduleBean();
						sBean.setHour(schedule_time_array22[i][1]+"-"+schedule_time_array22[i][2]);
						sBean.setHour_index(i);
						String current_date = DateUtils.getCurrentDateTime();
						String choice_currentdate2 = choice_currentdate+" "+schedule_time_array22[i][1]+":00";
						if (DateUtils.comString3(current_date, choice_currentdate2)> 0) {
							//历史时间 
							sBean.setSchedule_state(1);
							sBean.setSchedule_state_message("unavaliable");
						}else{
							sBean.setSchedule_state(3);
							sBean.setSchedule_state_message("avaliable");
						}
						sBeans.add(sBean);
					}
				}
			}else {
				//停课
				for(int i=1;i<33;i++){
					ScheduleBean sBean = new ScheduleBean();
					sBean.setHour(schedule_time_array22[i][1]+"-"+schedule_time_array22[i][2]);
					sBean.setHour_index(i);
					sBean.setSchedule_state(1);
					sBean.setSchedule_state_message("unavaliable");
					sBeans.add(sBean);
				}
			}
			SortClass.sortList(sBeans, "hour_index", false);
		    int is_busy_24 = 0;
		    List<Schedule> schedules2 = Schedule.dao.find("select schedule_id,type from schedule where status=1 and choice_currentdate='"+choice_currentdate+"' and user_id="+user_id);
			int is_24_num= 0;
		    for(Schedule cSchedule :schedules2){
				int type= cSchedule.get(cSchedule.type);
				if (type==1) {
					is_24_num=is_24_num+1;
				}
				if (type==2) {
					is_24_num=is_24_num+1;	
				}
			}
		    if (is_24_num==32) {
				is_busy_24 = 1;
			}
		    ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("ScheduleBeans", sBeans);
			responseValues.put("is_stop_course", is_stop_course);
			responseValues.put("is_busy_24", is_busy_24);
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "Data request successful.");
			setAttr("ScheduleListResponse", responseValues);
			renderMultiJson("ScheduleListResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("ScheduleManage/scheduleList", "时间表", this);
		}
	}
	@Author("cluo")
	@Rp("时间管理")
	@Explaination(info = "Mark as free time【一天24小格【1表09:00-09:30,2表09:30-10:00,3表10:00-10:30......依次类推】】")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Schedule.choice_currentdate)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String_NotRequired, name = Schedule.remarks)
	@URLParam(defaultValue = "1#3#4", explain = "设置多个时间段有空1#3#4 【1至24代表】{1、2、3、4、5、6、7、8、9、10、11、12、13、14、15、16、17、18、19、20、21、22、23、24}", type = Type.String, name = "time_slots")
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "SetFreeTimeResponse{schedule_state}", remarks = "1-unavaliable，2-busy,3-有空", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "SetFreeTimeResponse{schedule_state_message}", remarks = "状态文字", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "SetFreeTimeResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "SetFreeTimeResponse{status}", remarks = "0-失败，1-操作成功，2-请选择设置的时间段,3-不能设置历史时间", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "SetFreeTimeResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void setFreeTime() {
		try {
			String token = getPara("token");
			String choice_currentdate = getPara("choice_currentdate");
			String time_slots = getPara("time_slots");
			String remarks = getPara("remarks");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("choice_currentdate", choice_currentdate);
			packageParams.put("remarks", remarks);
			packageParams.put("time_slots", time_slots);
			packageParams.put("token", token);
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
				setAttr("SetFreeTimeResponse", response2);
				renderMultiJson("SetFreeTimeResponse");
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
				setAttr("SetFreeTimeResponse", response2);
				renderMultiJson("SetFreeTimeResponse");
				return;
			}
			//当前日期
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="Time setting failed, please check errors.";
			String currentDate = DateUtils.getCurrentDate();
			if (time_slots==null||time_slots.equals("")) {
				status = 2;
				message="Please select your course time";
			}
			if (DateUtils.comString2(choice_currentdate,currentDate)==-1) {
				status = 3;
				message="You can not select passed time";
			}
			if (status==0) {
				if (time_slots!=null&&!time_slots.equals("")) {
					String[] times_array = time_slots.split("#");
					for(int i=0;i<times_array.length;i++){
						String time_slot = times_array[i];
						Schedule schedule = Schedule.dao.findFirst("select schedule_id,type from schedule where status=1 and type!=1 and  choice_currentdate='"+choice_currentdate+"' and user_id="+user_id+" and time_slot="+time_slot);
						if (schedule!=null) {
							int type = schedule.get(schedule.type);
							if (type==2) {
								schedule.set(schedule.type, 3)
									.set(schedule.remarks, remarks)	
									.set(schedule.status, 1)
									.update();
							}
						}
					}
				}
				status = 1;
				message="Set success";
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("schedule_state", 3);
			responseValues.put("schedule_state_message", "avaliable");
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("SetFreeTimeResponse", responseValues);
			renderMultiJson("SetFreeTimeResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("ScheduleManage/setFreeTime", "Mark as free time", this);
		}
	}
	@Author("cluo")
	@Rp("时间管理")
	@Explaination(info = "Mark as busy time【一天24小格【1表09:00-09:30,2表09:30-10:00,3表10:00-10:30......依次类推】】")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Schedule.choice_currentdate)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String_NotRequired, name = Schedule.remarks)
	@URLParam(defaultValue = "1#3#4", explain = "设置多个时间段有空1#3#4 【1至24代表】{1、2、3、4、5、6、7、8、9、10、11、12、13、14、15、16、17、18、19、20、21、22、23、24}", type = Type.String, name = "time_slots")
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "SetBusyTimeResponse{schedule_state}", remarks = "1-unavaliable，2-busy,3-有空", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "SetBusyTimeResponse{schedule_state_message}", remarks = "状态文字", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "SetBusyTimeResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "SetBusyTimeResponse{status}", remarks = "0-失败，1-操作成功，2-请选择设置的时间段，3-不能设置历史时间", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "SetBusyTimeResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void setBusyTime() {
		try {
			String token = getPara("token");
			String choice_currentdate = getPara("choice_currentdate");
			String time_slots = getPara("time_slots");
			String remarks = getPara("remarks");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("choice_currentdate", choice_currentdate);
			packageParams.put("remarks", remarks);
			packageParams.put("time_slots", time_slots);
			packageParams.put("token", token);
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
				setAttr("SetBusyTimeResponse", response2);
				renderMultiJson("SetBusyTimeResponse");
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
				setAttr("SetBusyTimeResponse", response2);
				renderMultiJson("SetBusyTimeResponse");
				return;
			}
			//当前日期
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="Time setting failed, please check errors.";
			String currentDate = DateUtils.getCurrentDate();
			if (time_slots==null||time_slots.equals("")) {
				status = 2;
				message="Please select your course time";
			}
			if (DateUtils.comString2(choice_currentdate,currentDate)==-1) {
				status = 3;
				message="You can not select passed time";
			}
			if (status==0) {
				boolean update = true;
				String[] time_slot_array = time_slots.split("#");
				for(int i=0;i<time_slot_array.length;i++){
					String time_slot = time_slot_array[i];
					Schedule schedule = Schedule.dao.findFirst("select schedule_id,type from schedule where status=1 and type!=1 and choice_currentdate='"+choice_currentdate+"' and user_id="+user_id+" and time_slot="+time_slot);
					if (schedule!=null) {
						int type = schedule.get(schedule.type);
						if (type==3) {
							update = schedule.set(schedule.type, 2)
								.set(schedule.remarks, remarks)
								.set(schedule.status, 1)
								.update();
						}
						if (type==2) {
							update = schedule.set(schedule.remarks, remarks).update();
						}
					}else {
						Schedule schedule2 = new Schedule();
						update = schedule2.set(schedule2.user_id, user_id)
							.set(schedule2.choice_currentdate, choice_currentdate)
							.set(schedule2.time_slot, time_slot)
							.set(schedule2.remarks, remarks)
							.set(schedule2.type, 2)
							.set(schedule2.status, 1)
							.save();
					}
				}
				if (update) {
					status = 1;
					message="Set success";
				}
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("schedule_state", 2);
			responseValues.put("schedule_state_message", "busy");
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("SetBusyTimeResponse", responseValues);
			renderMultiJson("SetBusyTimeResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("ScheduleManage/setBusyTime", "Mark as busy time", this);
		}
	}
	
	@Author("cluo")
	@Rp("时间管理")
	@Explaination(info = "設置全天沒有空")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "{0、1}", explain = "0-全天有空 ，1-全天没有空 ", type = Type.String, name = "is_busy_24")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Schedule.choice_currentdate)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "SetAllBusyTimeResponse{schedule_state}", remarks = "1-unavaliable，2-busy,3-有空", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "SetAllBusyTimeResponse{schedule_state_message}", remarks = "状态文字", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "SetAllBusyTimeResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "SetAllBusyTimeResponse{status}", remarks = "0-失败，1-操作成功，2-不能设置历史时间", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "SetAllBusyTimeResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void setAllBusyTime() {
		try {
			String token = getPara("token");
			String choice_currentdate = getPara("choice_currentdate");
			int is_busy_24 = getParaToInt("is_busy_24",0);
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("choice_currentdate", choice_currentdate);
			packageParams.put("is_busy_24", is_busy_24+"");
			packageParams.put("token", token);
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
				setAttr("SetAllBusyTimeResponse", response2);
				renderMultiJson("SetAllBusyTimeResponse");
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
				setAttr("SetAllBusyTimeResponse", response2);
				renderMultiJson("SetAllBusyTimeResponse");
				return;
			}
			//当前日期
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="Time setting failed, please check errors.";
			String currentDate = DateUtils.getCurrentDate();
			if (DateUtils.comString2(choice_currentdate,currentDate)==-1) {
				status = 2;
				message="You can not select passed time";
			}
			if (status==0) {
				boolean update = true;
				if (is_busy_24==1) {
					List<Schedule> schedules = Schedule.dao.find("select schedule_id,time_slot,type from schedule where status=1 and choice_currentdate='"+choice_currentdate+"' and user_id="+user_id);
					String strb = "#";
					for(Schedule schedule:schedules){
						int time_slot = schedule.get(schedule.time_slot);
						strb = strb+time_slot+"#";
						int type = schedule.get(schedule.type);
						if (type==3) {
							schedule.set(schedule.type, 2).update();
						}
					}
					//去掉
					String times = com.quark.utils.StringUtils.getContains(strb);
					if (times!=null&&!times.equals("")) {
						String[] times_array = times.split("#");
						for(int i=0;i<times_array.length;i++){
							String time_slot = times_array[i];
							Schedule schedule2 = new  Schedule();
							schedule2.set(schedule2.user_id, user_id)
							.set(schedule2.choice_currentdate, choice_currentdate)
							.set(schedule2.time_slot, time_slot)
							.set(schedule2.type, 2)
							.set(schedule2.status, 1)
							.save();
						}
					}
				}
				if (is_busy_24==0) {
					//设置全天有空
					List<Schedule> schedules = Schedule.dao.find("select schedule_id,time_slot,type,remarks from schedule where status=1 and type=2 and choice_currentdate='"+choice_currentdate+"' and user_id="+user_id);
					for(Schedule schedule:schedules){
						int type = schedule.get(schedule.type);
						String remarks = schedule.getStr(schedule.remarks);
						if (type==2) {
							if (remarks==null||remarks.equals("")) {
								schedule.delete();
							}else {
								schedule.set(schedule.type, 3).update();
							}
						}
					}
				}
				if (update) {
					status = 1;
					message="Setting success";
				}else {
					status = 0;
					message="Setting failed";
				}
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("schedule_state", 2);
			responseValues.put("schedule_state_message", "busy");
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("SetAllBusyTimeResponse", responseValues);
			renderMultiJson("SetAllBusyTimeResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("ScheduleManage/setAllBusyTime", "設置全天沒有空", this);
		}
	}
	@Author("cluo")
	@Rp("时间管理")
	@Explaination(info = "設置停止课程")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "{0、1}", explain = Value.Infer, type = Type.String, name = User.is_stop_course)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "SetStopCourseResponse{schedule_state}", remarks = "1-unavaliable，2-busy,3-有空", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "SetStopCourseResponse{schedule_state_message}", remarks = "状态文字", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "SetStopCourseResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "SetStopCourseResponse{status}", remarks = "0-失败，1-操作成功，2-不能设置历史时间", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "SetStopCourseResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void setStopCourse() {
		try {
			String token = getPara("token");
			int is_stop_course = getParaToInt("is_stop_course",0);
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("is_stop_course", is_stop_course+"");
			packageParams.put("token", token);
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
				setAttr("SetStopCourseResponse", response2);
				renderMultiJson("SetStopCourseResponse");
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
				setAttr("SetStopCourseResponse", response2);
				renderMultiJson("SetStopCourseResponse");
				return;
			}
			//当前日期
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="Time setting failed, please check errors.";
			User user = User.dao.findById(user_id);
			if (user!=null) {
				boolean update = user.set(user.is_stop_course, is_stop_course)
					.set(user.stop_course_time, DateUtils.getCurrentDateTime())
					.update();
				if (update) {
					status = 1;
					message="Set success";
				}
			}else {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("SetStopCourseResponse", response2);
				renderMultiJson("SetStopCourseResponse");
				return;
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("schedule_state", 1);
			responseValues.put("schedule_state_message", "unavaliable");
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("SetStopCourseResponse", responseValues);
			renderMultiJson("SetStopCourseResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("ScheduleManage/setStopCourse", "", this);
		}
	}
}
