/**
 * 
 */
package com.quark.app.controller;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
 * 教练订单管理
 */
@Before(Tx.class)
public class CoachOrdersManage extends Controller {

	@Author("cluo")
	@Rp("订单管理")
	@Explaination(info = "教练订单列表")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "{1、2、3}", explain = "1-已支付，2-已完成,3-退款", type = Type.String, name = "order_type")
	@URLParam(defaultValue = "1", explain = Value.Infer, type = Type.String, name = "pn")
	@URLParam(defaultValue = "5", explain = Value.Infer, type = Type.String, name = "page_size")
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String, name = Course.latitude)
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String, name = Course.longitude)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	// page property
	@ReturnOutlet(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:pageNumber}", remarks = "page number", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:pageSize}", remarks = "result amount of this page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:totalPage}", remarks = "total page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:totalRow}", remarks = "total row", dataType = DataType.Int, defaultValue = "")
	// 
	@ReturnDBParam(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:$]}", column = Orders.orders_id)
	@ReturnDBParam(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:$]}", column = Orders.course_id)
	@ReturnDBParam(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:$]}", column = Orders.buy_amount)
	@ReturnDBParam(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:$]}", column = Orders.total_session_rate)
	@ReturnDBParam(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:$]}", column = Orders.original_total_session_rate)
	@ReturnDBParam(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:$]}", column = Orders.status)
	
	@ReturnDBParam(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:Course:$]}", column = Course.course_id) 
	@ReturnDBParam(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:Course:$]}", column = Course.user_id)   
	@ReturnDBParam(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:Course:$]}", column = Course.title)
	@ReturnDBParam(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:Course:$]}", column = Course.category_01_name)
	@ReturnDBParam(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:Course:$]}", column = Course.category_02_name)
	@ReturnDBParam(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:Course:$]}", column = Course.catetory_name)
	@ReturnDBParam(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:Course:$]}", column = Course.session_rate)
	@ReturnDBParam(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:Course:$]}", column = Course.total_score)
	@ReturnDBParam(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:Course:$]}", column = Course.latitude)
	@ReturnDBParam(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:Course:$]}", column = Course.longitude)
	@ReturnDBParam(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:Course:$]}", column = Course.city)
	@ReturnDBParam(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:Course:$]}", column = Course.area)
	@ReturnDBParam(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:Course:$]}", column = Course.street)
	@ReturnDBParam(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:Course:$]}", column = Course.address)
	
	@ReturnOutlet(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:Course:is_official]}", remarks = "是否官方认证：0-否，1-是", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:Course:coach_name]}", remarks = "教练名称", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:Course:coach_image]}", remarks = "教练图片", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:Course:distance]}", remarks = "距离", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:Course:total_coment_num]}", remarks = "评论总数", dataType = DataType.String, defaultValue = "")
	
	@ReturnOutlet(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:button_status1]}", remarks = "0-不显示，1-显示-cancel取消课程(退款)", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:button_status2]}", remarks = "0-不显示，1-显示-confirm确认课程时间  ", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:button_status3]}", remarks = "0-不显示，1-显示-finish确认课程已经完成", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:button_status4]}", remarks = "0-不显示，1-显示-delete删除已完成订单", dataType = DataType.String, defaultValue = "")
	
	@ReturnOutlet(name = "CoachOrdersListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersListResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void coachOrdersList() {
		try {
			String token = getPara("token");
			int order_type = getParaToInt("order_type", 1);
			int pn = getParaToInt("pn", 1);
			int page_size = getParaToInt("page_size", 5);
			String latitude = getPara("latitude");
			String longitude = getPara("longitude");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("latitude", latitude);
			packageParams.put("longitude", longitude);
			packageParams.put("order_type", order_type+"");
			packageParams.put("page_size", page_size+"");
			packageParams.put("pn", pn+"");
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
				setAttr("CoachOrdersListResponse", response2);
				renderMultiJson("CoachOrdersListResponse");
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
				setAttr("CoachOrdersListResponse", response2);
				renderMultiJson("CoachOrdersListResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="Please wait.";
			//SQL 中where条件不能用别名，只能 ("+distance_sql+")<500 这样用。
			String distance_sql= "round(6378.138*2*asin(sqrt(pow(sin( ("
					+ latitude
					+ "*pi()/180-latitude*pi()/180)/2),2)+cos("
					+ latitude
					+ "*pi()/180)*cos(latitude*pi()/180)* pow(sin( ("
					+ longitude
					+ "*pi()/180-longitude*pi()/180)/2),2)))*1000)";
			String select = "select course_id,user_id,user_images_01,title,category_01_name,category_02_name,catetory_name,session_rate,total_score,latitude,longitude,"+distance_sql+" as distance,city,area,street,address ";
			String filer_sql = " (status=21 or status=22)  ";
			if (order_type==2) {
				//1-已支付，2-已完成,3-退款
				filer_sql = " (status=30 or status=40) ";
			}
			if (order_type==3) {
				filer_sql = " (status=30 or status=40 or status=21 or status=22) and has_refund_status=2 ";
			}
			List<OrdersSchedule> ordersSchedules = OrdersSchedule.dao.find("select orders_schedule_id,orders_id,is_read from orders_schedule where is_read=0 and orders_id in(select orders_id from orders where course_user_id="+user_id+")");
			for(OrdersSchedule oSchedule:ordersSchedules){
				oSchedule.set(oSchedule.is_read, 1).update();
			}
			final Page<Orders> ordersPage = Orders.dao
					.paginate(
							pn,
							page_size,
							"select orders_id,course_id,buy_amount,total_session_rate,original_total_session_rate,status,post_time,buy_nickname "," from orders where "+filer_sql+" and course_user_id="+user_id+" order by post_time desc");
			for(Orders orders:ordersPage.getList()){
				//无按钮
				int button_status1 = 0,button_status2 = 0,button_status3 = 0,button_status4 = 0;
				int orders_id = orders.get(orders.orders_id);
				//课程id
				int course_id = orders.get(orders.course_id);
				Course course = Course.dao.findFirst(select+" from course where course_id="+course_id);
				int is_official=0;
				String coach_name="",coach_image= course.getStr(course.user_images_01);
				if (course!=null) {
					//教练信息
					String course_user_id = course.getStr(course.user_id);
					User course_user = User.dao.findById(course_user_id);
					if (course_user!=null) {
						is_official = course_user.get(course_user.is_official);
						coach_name=course_user.getStr(course_user.nickname);
					}
				}
				course.put("is_official", is_official);
				course.put("coach_name", coach_name);
				course.put("coach_image", coach_image);
				//距离
				String course_latitude = course.getStr(course.latitude);
				if (course_latitude.equals("")||course_latitude.equals("0")) {
					course.put("distance", "Distance cannot be calculated.");
				}else {
					double distance = course.getDouble("distance");
					double distance_miles = distance/1609.344;
					DecimalFormat df=new DecimalFormat(".##");
					distance_miles=Double.parseDouble(df.format(distance_miles));
					course.put("distance", distance_miles+" miles");
				}
				//评论总数
				Comment comment = Comment.dao.findFirst("select count(course_id) as total_coment_num from comment where type=1 and course_id=? and status!=0",course_id);
				long total_coment_num = 0;
				if (comment!=null) {
					total_coment_num = comment.getLong("total_coment_num");
				}
				course.put("total_coment_num", total_coment_num);
				
				orders.put("Course", course);
				if (order_type!=3) {
					/////按钮状态
					int orders_status = orders.get(orders.status);
					int cancel_num = 0,confirm=0,can_finish_num=0;
					if (orders_status==21||orders_status==22) {
						List<OrdersSchedule> oSchedules = OrdersSchedule.dao.find("select orders_schedule_id,schedule_ids,refund_status,status,finish_status from orders_schedule where orders_id=?",orders_id);
						for(OrdersSchedule oSchedule:oSchedules){
							int oSchedule_status = oSchedule.get(oSchedule.status);
							int oSchedule_refund_status = oSchedule.get(oSchedule.refund_status);
							int finish_status = oSchedule.get(oSchedule.finish_status);
							if (oSchedule_status==20&&oSchedule_refund_status==10) {
								cancel_num = cancel_num+1;
							}
							if ((oSchedule_status==3&&oSchedule_refund_status==10)||(oSchedule_status==3&&oSchedule_refund_status==1)) {
								confirm = confirm+1;
							}
							//if (finish_status==2) {
							if (oSchedule_status==20&&oSchedule_refund_status==4) {
								can_finish_num=can_finish_num+1;
							}
						}
						if (cancel_num>0) {
							//教练端订单列表只有教练确认了课程后才出现取消课程(退款)按钮
							button_status1 = 1;
						}
						if (confirm>0) {
							//确认课程时间
							button_status2 = 1;
						}
						if (can_finish_num>0) {
							//确认课程已经完成
							button_status3 = 1;
						}
					}
					if (orders_status==30||orders_status==40) {
						//delete，删除已完成订单
						button_status4 = 1;
					}
				}
				orders.put("button_status1", button_status1);
				orders.put("button_status2", button_status2);
				orders.put("button_status3", button_status3);
				orders.put("button_status4", button_status4);
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("Result", new HashMap<String, Object>() {
				{
					put("CoachOrderList", ordersPage);
				}
			});
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "Data request successful.");
			setAttr("CoachOrdersListResponse", responseValues);
			renderMultiJson("CoachOrdersListResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("CoachOrdersManage/coachOrdersList", "教练订单列表", this);
		}
	}
	@Author("cluo")
	@Rp("查看时间表详情-取消订单、查看时间表详情-完成、查看时间表详细-确认时间")
	@Explaination(info = "教练订单日期列表A")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "{1、2、3}", explain = "1-cancel，2-confirm，3-finish", type = Type.String, name = "schedule_type")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = OrdersSchedule.orders_id)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	// 
	@ReturnDBParam(name = "CoachOrdersScheduleListAResponse{scheduleDatas:list[OrdersSchedule:$]}", column = OrdersSchedule.schedule_data)
	@ReturnOutlet(name = "CoachOrdersScheduleListAResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersScheduleListAResponse{status}", remarks = "1-操作成功，2-客户未选择时间课程", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersScheduleListAResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void coachOrdersScheduleListA() {
		try {
			String token = getPara("token");
			String orders_id = getPara("orders_id");
			int schedule_type = getParaToInt("schedule_type",1);
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("orders_id", orders_id);
			packageParams.put("schedule_type", schedule_type+"");
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
				setAttr("CoachOrdersScheduleListAResponse", response2);
				renderMultiJson("CoachOrdersScheduleListAResponse");
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
				setAttr("CoachOrdersScheduleListAResponse", response2);
				renderMultiJson("CoachOrdersScheduleListAResponse");
				return;
			}
			//1-cancel，2-confirm，3-finish
			String fiter_sql = " status=20 and refund_status=10 ";
			if (schedule_type==2) {
				fiter_sql = " ((status=3 and refund_status=10) or (status=3 and refund_status=4) or (status=3 and refund_status=1)) ";
			}
			if (schedule_type==3) {
				fiter_sql = " status=20 ";
			}
			Orders orders = Orders.dao.findById(orders_id);
			int user_id = orders.get(orders.user_id);
			//当前日期 // 1-unavaliable，2-busy,3-有空
			List<OrdersSchedule> scheduleDatas = OrdersSchedule.dao.find("select schedule_data from orders_schedule where "+fiter_sql+" and orders_id="+orders_id+" and user_id="+user_id+" group by schedule_data order by schedule_data asc");
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("scheduleDatas", scheduleDatas);
			if (scheduleDatas.size()>0) {
				responseValues.put("status", 1);
				responseValues.put("message", "Data request successful.");
			}else {
				responseValues.put("status", 2);
				responseValues.put("message", "Client has not made course schedule.");
			}
			responseValues.put("code", 200);
			setAttr("CoachOrdersScheduleListAResponse", responseValues);
			renderMultiJson("CoachOrdersScheduleListAResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("CoachOrdersManage/coachOrdersScheduleListA", "教练订单时间列表", this);
		}
	}
	@Author("cluo")
	@Rp("查看时间表详情-取消订单、查看时间表详情-完成、查看时间表详细-确认时间")
	@Explaination(info = "教练订单时间列表B-先调用A")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "{1、2、3}", explain = "1-cancel，2-confirm，3-finish", type = Type.String, name = "schedule_type")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = OrdersSchedule.schedule_data)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = OrdersSchedule.orders_id)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")

	@ReturnOutlet(name = "CoachOrdersScheduleListBResponse{ScheduleBeans:list:[ScheduleBean:course_id]}", remarks = "课程id", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersScheduleListBResponse{ScheduleBeans:list:[ScheduleBean:course_name]}", remarks = "课程名称", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersScheduleListBResponse{ScheduleBeans:list:[ScheduleBean:course_location]}", remarks = "课程地址", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersScheduleListBResponse{ScheduleBeans:list:[ScheduleBean:course_time]}", remarks = "课程时间", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersScheduleListBResponse{ScheduleBeans:list:[ScheduleBean:hour]}", remarks = "时间段", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersScheduleListBResponse{ScheduleBeans:list:[ScheduleBean:schedule_state]}", remarks = " 1-unavaliable，2-busy,3-有空", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersScheduleListBResponse{ScheduleBeans:list:[ScheduleBean:hour_index]}", remarks = "下标：1至24", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersScheduleListBResponse{ScheduleBeans:list:[ScheduleBean:remarks]}", remarks = "备注", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersScheduleListBResponse{schedule_data}", remarks = "日期", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersScheduleListBResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersScheduleListBResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersScheduleListBResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void coachOrdersScheduleListB() {
		try {
			String token = getPara("token");
			String orders_id = getPara("orders_id");
			String schedule_data = getPara("schedule_data");
			int schedule_type = getParaToInt("schedule_type",1);
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("orders_id", orders_id);
			packageParams.put("schedule_data", schedule_data);
			packageParams.put("schedule_type", schedule_type+"");
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
				setAttr("CoachOrdersScheduleListBResponse", response2);
				renderMultiJson("CoachOrdersScheduleListBResponse");
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
				setAttr("CoachOrdersScheduleListBResponse", response2);
				renderMultiJson("CoachOrdersScheduleListBResponse");
				return;
			}
			//1-cancel，2-confirm，3-finish
			String fiter_sql = "  status=20 and refund_status=10 ";
			if (schedule_type==2) {
				fiter_sql = " ((status=3 and refund_status=10) or (status=3 and refund_status=4) or (status=3 and refund_status=1)) ";
			}
			if (schedule_type==3) {
				fiter_sql = " status=20 ";
			}
			Orders orders = Orders.dao.findById(orders_id);
			int user_id = orders.get(orders.user_id);
			//当前日期 // 1-unavaliable，2-busy,3-有空
			String [][]schedule_time_array22= ScheduleBean2.schedule_time_array2;
			List<OrdersSchedule> oScheduleList = OrdersSchedule.dao.find("select orders_schedule_id,schedule_data,schedule_time_slots from orders_schedule where "+fiter_sql+" and orders_id="+orders_id+" and user_id="+user_id+" and schedule_data='"+schedule_data+"'");
			String schedule_time_slots = "",orders_schedule_ids="";
			for(OrdersSchedule oSchedule :oScheduleList){
				int orders_schedule_id = oSchedule.get(oSchedule.orders_schedule_id);
				String schedule_time_slot = oSchedule.getStr(oSchedule.schedule_time_slots);
				schedule_time_slots+=schedule_time_slot+"A";
				orders_schedule_ids+=orders_schedule_id+"A"+orders_schedule_id+"A";
			}
			if (schedule_time_slots.length()>0) {
				schedule_time_slots = schedule_time_slots.substring(0,schedule_time_slots.length()-1);	
				orders_schedule_ids = orders_schedule_ids.substring(0,orders_schedule_ids.length()-1);	
			}
			if (oScheduleList.size()==0||schedule_time_slots.equals("")) {
				schedule_time_slots="0";
				orders_schedule_ids="0";
			}
			List<ScheduleBean> sBeans = new ArrayList<ScheduleBean>();
			if (!schedule_time_slots.equals("0")) {
				//去掉
				String times = com.quark.utils.StringUtils.getContainsA(schedule_time_slots);
				if (times!=null&&!times.equals("")) {
					String[] times_array = times.split("A");
					for(int i=0;i<times_array.length;i++){
						ScheduleBean sBean = new ScheduleBean();
						sBean.setHour(schedule_time_array22[Integer.parseInt(times_array[i])][1]+"-"+schedule_time_array22[Integer.parseInt(times_array[i])][2]);
						sBean.setSchedule_state(1);
						sBean.setHour_index(Integer.parseInt(times_array[i]));
						sBean.setSchedule_state_message("unavaliable");
						sBeans.add(sBean);
					}
				}
				//21@22
				String[]schedule_time_slot = schedule_time_slots.split("A"); 
				String[]orders_schedule_id_array = orders_schedule_ids.split("A"); 
				for(int j=0;j<schedule_time_slot.length;j++){
					ScheduleBean sBean = new ScheduleBean();
					sBean.setHour(schedule_time_array22[Integer.parseInt(schedule_time_slot[j])][1]+"-"+schedule_time_array22[Integer.parseInt(schedule_time_slot[j])][2]);
					sBean.setSchedule_state(3);
					sBean.setHour_index(Integer.parseInt(schedule_time_slot[j]));
					sBean.setSchedule_state_message("avaliable");
					sBean.setOrders_schedule_id(orders_schedule_id_array[j]);
					sBeans.add(sBean);
				}
			}else {
				for(int i=1;i<33;i++){
					ScheduleBean sBean = new ScheduleBean();
					sBean.setHour(schedule_time_array22[i][1]+"-"+schedule_time_array22[i][2]);
					sBean.setSchedule_state(1);
					sBean.setHour_index(i);
					sBean.setSchedule_state_message("unavaliable");
					sBeans.add(sBean);
				}	
			}
			SortClass.sortList(sBeans, "hour_index", false);
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("schedule_data", schedule_data);
			responseValues.put("ScheduleBeans", sBeans);
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "Data request successful.");
			setAttr("CoachOrdersScheduleListBResponse", responseValues);
			renderMultiJson("CoachOrdersScheduleListBResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("CoachOrdersManage/coachOrdersScheduleListB", "教练订单时间列表", this);
		}
	}
	/**
	 *  金额计算：已付款金额 * 退款课程数/总课程数 + 首次对接费用 。
	 */
	@Author("cluo")
	@Rp("查看时间表详情-取消订单")
	@Explaination(info = "教练cancel订单-退款[orders_schedule_id一个课程只传一个值]")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = "orders_schedule_id：多订单同时取消【11#22#1#23】，如果取消一个就传一个值", type = Type.String, name = "orders_schedule_ids")
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "CancelOrdersResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CancelOrdersResponse{status}", remarks = "0-失败，1-操作成功，2-不存在", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CancelOrdersResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void cancelOrders() {
		try {
			String token = getPara("token");
			String orders_schedule_ids = getPara("orders_schedule_ids");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("orders_schedule_ids", orders_schedule_ids);
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
				setAttr("CancelOrdersResponse", response2);
				renderMultiJson("CancelOrdersResponse");
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
				setAttr("CancelOrdersResponse", response2);
				renderMultiJson("CancelOrdersResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="Cancel failed.";
			if (orders_schedule_ids==null) {
				status = 2;
				message="请选择待Cancel的订单";
			}
			if (status==0) {
				String orders_id = "0";
				boolean delete = true;
				orders_schedule_ids = orders_schedule_ids.trim();
				String[] orders_schedule_id_array = orders_schedule_ids.split("#");
				for(int i=0;i<orders_schedule_id_array.length;i++){
					String orders_schedule_id = orders_schedule_id_array[i];
					OrdersSchedule ordersSchedule = OrdersSchedule.dao.findById(orders_schedule_id);
					if (ordersSchedule!=null) {
						orders_id = ordersSchedule.getStr(ordersSchedule.orders_id);
						delete = ordersSchedule.set(ordersSchedule.status, 22)
								.set(ordersSchedule.refund_status, 30)
								.update();
						if (delete) {
							status = 1;
							message="Cancel successful.";
						}else {
							status = 0;
							message="Cancel failed.";
							break;
						}
					}
				}
				if (delete&&status==1) {
					//教练同意退款  金额计算：已付款金额 * 退款课程数/总课程数 + 首次对接费用 。
					Orders orders = Orders.dao.findById(orders_id);
					if (orders!=null) {
						String order_number = orders.getStr(orders.order_number);
						//邮件通知系统
						SendMail.send("教练申请退款通知","2318680679@qq.com", "【Skillpier】订单编号："+order_number+",教练申请退款,请查看");
					}
				}
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("CancelOrdersResponse", responseValues);
			renderMultiJson("CancelOrdersResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("CoachOrdersManage/cancelOrders", "取消订单", this);
		}
	}
	@Author("cluo")
	@Rp("查看时间表详细-确认时间")
	@Explaination(info = "教练reject-confirm订单[orders_schedule_id一个课程只传一个值]")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "{1、2}", explain = "1-reject，2-confirm", type = Type.String, name = "schedule_type")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String_NotRequired, name = OrdersSchedule.refund_reason)
	@URLParam(defaultValue = "", explain = "orders_schedule_id:多订单同时reject-confirm【11#22#1#23】，如果reject-confirm一个就传一个值", type = Type.String, name = "orders_schedule_ids")
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "RejectConfirmOrdersResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "RejectConfirmOrdersResponse{status}", remarks = "0-失败，1-操作成功，2-不存在", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "RejectConfirmOrdersResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void rejectConfirmOrders() {
		try {
			String token = getPara("token");
			int schedule_type = getParaToInt("schedule_type",1);
			String orders_schedule_ids = getPara("orders_schedule_ids");
			String refund_reason = getPara("refund_reason","");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("orders_schedule_ids", orders_schedule_ids);
			packageParams.put("refund_reason", refund_reason);
			packageParams.put("schedule_type", schedule_type+"");
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
				setAttr("RejectConfirmOrdersResponse", response2);
				renderMultiJson("RejectConfirmOrdersResponse");
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
				setAttr("RejectConfirmOrdersResponse", response2);
				renderMultiJson("RejectConfirmOrdersResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="Please wait.";
			if (orders_schedule_ids==null) {
				status = 2;
				message="Please select your orders";
			}
			String orders_id = "0";
			boolean update = true;
			if (status==0) {
				orders_schedule_ids = orders_schedule_ids.trim();
				String[] orders_schedule_id_array = orders_schedule_ids.split("#");
				for(int i=0;i<orders_schedule_id_array.length;i++){
					String orders_schedule_id = orders_schedule_id_array[i];
					OrdersSchedule ordersSchedule = OrdersSchedule.dao.findById(orders_schedule_id);
					if (ordersSchedule!=null) {
						orders_id = ordersSchedule.getStr(ordersSchedule.orders_id);
						if (schedule_type==1) {
							String schedule_ids = ordersSchedule.getStr(ordersSchedule.schedule_ids);
							String[] schedule_id_array = schedule_ids.split("#");
							for(int j=0;j<schedule_id_array.length;j++){
								Schedule schedule = Schedule.dao.findById(schedule_id_array[j]);
								if (schedule!=null) {
									update = schedule.set(schedule.status, 0).update();
								}
							}
							if (update) {
								//1-reject，2-confirm 确认时间的课程状态为已确认（confirmed），拒绝时间的课程状态回归未选择时间
								update = ordersSchedule.set(ordersSchedule.status, 21)
										.set(ordersSchedule.refund_status, 10)
										.set(ordersSchedule.schedule_ids, 0)
										.set(ordersSchedule.schedule_time_slots, 0)
										.set(ordersSchedule.refund_reason, refund_reason)
										.set(ordersSchedule.finish_status, 1)
										.update();
								if (update) {
									status = 1;
									message="Decline Success";
								}else {
									status = 0;
									message="Decline Failure";
									break;
								}
							}
						}
						if (schedule_type==2) {
							update = ordersSchedule.set(ordersSchedule.status, 20)
									.set(ordersSchedule.refund_status, 10)
									.set(ordersSchedule.order_period_start_time, DateUtils.getCurrentDateTime())
									.update();
							if (update) {
								status = 1;
								message="Schedule Comfirmed.";
							}else {
								status = 0;
								message="Confirm failed. Please check for errors.";
								break;
							}
						}
					}
				}
			}
			if (update&&status==1) {
				Orders orders = Orders.dao.findById(orders_id);
				if (orders!=null) {
					int buy_amount = orders.get(orders.buy_amount);
					int no_booking_num = 0;//没有booking时间的数量
					int can_refund_num = 0;//可退款的数量
					int booking_status=1,can_refund_status=21;
					List<OrdersSchedule> oSchedules = OrdersSchedule.dao.find("select orders_schedule_id,schedule_ids,status,refund_status from orders_schedule where orders_id=?",orders_id);
					for(OrdersSchedule oSchedule:oSchedules){
						String schedule_ids = oSchedule.getStr(oSchedule.schedule_ids);
						int refund_status = oSchedule.get(oSchedule.refund_status);
						if (schedule_ids.equals("0")) {
							no_booking_num = no_booking_num+1;
						}
						if ((refund_status==10)||refund_status==1) {
							can_refund_num = can_refund_num+1;
						}
					}
					if (no_booking_num==0) {
						booking_status = 2;//不可booking
					}
					if (can_refund_num==0) {
						can_refund_status = 22;
					}
					orders.set(orders.status, can_refund_status)
						  .set(orders.booking_status, booking_status)
						  .update();
				}
				//邮件通知系统-客户端，教练确定时间或者拒绝时间
				EmailUntil.sendEmailCoachRejectConfirm(orders,schedule_type);
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("RejectConfirmOrdersResponse", responseValues);
			renderMultiJson("RejectConfirmOrdersResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("CoachOrdersManage/confirmOrders", "教练confirm订单", this);
		}
	}
	@Author("cluo")
	@Rp("查看时间表详情-完成")
	@Explaination(info = "教练finish订单[orders_schedule_id一个课程只传一个值]")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = "orders_schedule_id:多订单同时取消【11#22#1#23】，如果取消一个就传一个值", type = Type.String, name = "orders_schedule_ids")
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "FinishOrdersResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "FinishOrdersResponse{status}", remarks = "0-失败，1-操作成功，2-不存在", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "FinishOrdersResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void finishOrders() {
		try {
			String token = getPara("token");
			String orders_schedule_ids = getPara("orders_schedule_ids");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("orders_schedule_ids", orders_schedule_ids);
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
				setAttr("FinishOrdersResponse", response2);
				renderMultiJson("FinishOrdersResponse");
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
				setAttr("FinishOrdersResponse", response2);
				renderMultiJson("FinishOrdersResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="Finish failed. Please check for errors.";
			if (orders_schedule_ids==null) {
				status = 2;
				message="Please check for errors. Order is pending.";
			}
			if (status==0) {
				boolean delete = true;
				orders_schedule_ids = orders_schedule_ids.trim();
				String[] orders_schedule_id_array = orders_schedule_ids.split("#");
				for(int i=0;i<orders_schedule_id_array.length;i++){
					String orders_schedule_id = orders_schedule_id_array[i];
					OrdersSchedule ordersSchedule = OrdersSchedule.dao.findById(orders_schedule_id);
					if (ordersSchedule!=null) {
						delete = ordersSchedule.set(ordersSchedule.status, 1)
								.set(ordersSchedule.refund_status, 4)
								.set(ordersSchedule.finish_status, 1)
								.update();
						if (delete) {
							status = 1;
							message="Finish successful.";
						}else {
							status = 0;
							message="Finish failed. Please check for errors.";
							break;
						}
					}
				}
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("FinishOrdersResponse", responseValues);
			renderMultiJson("FinishOrdersResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("CoachOrdersManage/finishOrders", "教练finish订单", this);
		}
	}
	
	@Author("cluo")
	@Rp("管理订单详情")
	@Explaination(info = "教练的订单详情[status=1 课程完成,(status=20 and refund_status=10)教练已确认时间,(status=3 and refund_status=10)教练未确认时间，(status=4 and refund_status=10)用户未选择时间，(refund_status=2 or refund_status=30)退款中，(refund_status=31)退款完成]")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.orders_id)
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String, name = Course.latitude)
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String, name = Course.longitude)
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	// 
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:$}", column = Orders.orders_id)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:$}", column = Orders.course_id)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:$}", column = Orders.user_id)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:$}", column = Orders.course_user_id)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:$}", column = Orders.session_rate)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:$}", column = Orders.take_partner_num)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:$}", column = Orders.surcharge_for_each_cash)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:$}", column = Orders.go_door_traffic_cost)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:$}", column = Orders.buy_amount)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:$}", column = Orders.first_joint_fee)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:$}", column = Orders.discount_price)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:$}", column = Orders.my_coupon_money)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:$}", column = Orders.total_session_rate)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:$}", column = Orders.original_total_session_rate)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:$}", column = Orders.order_number)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:$}", column = Orders.post_time)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:$}", column = Orders.booking_status)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:$}", column = Orders.status)
	//
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.course_id)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.user_id)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.title)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.total_score)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.session_rate)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.category_01_id)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.category_02_id)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.category_02_name)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.travel_to_session)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.travel_to_session_distance)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.travel_to_session_trafic_surcharge)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.city)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.area)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.street)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.address)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.latitude)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.longitude)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.additional_partner)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.surcharge_for_each)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.discount_type)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.discount_onetion_pur_money_01)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.discount_price_01)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.discount_onetion_pur_money_02)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.discount_price_02)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.discount_onetion_pur_money_03)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.discount_price_03)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.achievements)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:Course:$}", column = Course.specialist)
	@ReturnOutlet(name = "CoachOrderInfoResponse{Orders:Course:course_is_official}", remarks = "教练是否官方认证：0-否，1-是", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrderInfoResponse{Orders:Course:course_image_01}", remarks = "教练用户头像", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrderInfoResponse{Orders:Course:course_nickname}", remarks = "教练昵称", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrderInfoResponse{Orders:Course:course_telephone}", remarks = "教练电话", dataType = DataType.String, defaultValue = "")
	
	@ReturnOutlet(name = "CoachOrderInfoResponse{Orders:Course:buy_is_official}", remarks = "购买者是否官方认证：0-否，1-是", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrderInfoResponse{Orders:Course:buy_image_01}", remarks = "购买者用户头像", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrderInfoResponse{Orders:Course:buy_nickname}", remarks = "购买者昵称", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrderInfoResponse{Orders:Course:buy_telephone}", remarks = "购买者电话", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrderInfoResponse{Orders:Course:buy_email}", remarks = "购买者email", dataType = DataType.String, defaultValue = "")
	
	@ReturnOutlet(name = "CoachOrderInfoResponse{Orders:Course:total_coment_num}", remarks = "评论总数", dataType = DataType.String, defaultValue = "")
	
	@ReturnOutlet(name = "CoachOrderInfoResponse{Orders:hasnone_booking_course}", remarks = "未选择时间的课程数", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:button_status]}", remarks = "弃用", dataType = DataType.String, defaultValue = "")
	
	@ReturnOutlet(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:button_status1]}", remarks = "0-不显示，1-显示-cancel取消课程(退款)", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:button_status2]}", remarks = "0-不显示，1-显示-confirm确认课程时间  ", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:button_status3]}", remarks = "0-不显示，1-显示-finish确认课程已经完成", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrdersListResponse{coachOrdersListResult:CoachOrderList:list[Orders:button_status4]}", remarks = "0-不显示，1-显示-delete删除已完成订单", dataType = DataType.String, defaultValue = "")
	
	//时间表 
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:OrdersSchedules:list[OrdersSchedule:$]}", column = OrdersSchedule.orders_schedule_id)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:OrdersSchedules:list[OrdersSchedule:$]}", column = OrdersSchedule.schedule_data)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:OrdersSchedules:list[OrdersSchedule:$]}", column = OrdersSchedule.schedule_hours)
	@ReturnDBParam(name = "CoachOrderInfoResponse{Orders:OrdersSchedules:list[OrdersSchedule:$]}", column = OrdersSchedule.status)
	@ReturnOutlet(name = "CoachOrderInfoResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CoachOrderInfoResponse{status}", remarks = "0-失败，1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CoachOrderInfoResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void coachOrderInfo() {
		try {
			String token = getPara("token");
			String orders_id = getPara("orders_id");
			String latitude = getPara("latitude");
			String longitude = getPara("longitude");
			String invoke = getPara("invoke","app");
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("CoachOrderInfoResponse", response2);
				renderMultiJson("CoachOrderInfoResponse");
				return;
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			String distance_sql = "round(6378.138*2*asin(sqrt(pow(sin( ("
					+ latitude
					+ "*pi()/180-latitude*pi()/180)/2),2)+cos("
					+ latitude
					+ "*pi()/180)*cos(latitude*pi()/180)* pow(sin( ("
					+ longitude
					+ "*pi()/180-longitude*pi()/180)/2),2)))*1000)";
			String sqlString = "select course_id,user_id,title,user_images_01,total_score,achievements,specialist,travel_to_session,travel_to_session_distance,travel_to_session_trafic_surcharge,"+distance_sql+" as distance,session_rate,category_01_id,category_02_id,category_02_name,additional_partner,surcharge_for_each,"
					+ "city,area,street,address,latitude,longitude,discount_type,discount_onetion_pur_money_01,discount_price_01,discount_onetion_pur_money_02,discount_price_02,discount_onetion_pur_money_03,discount_price_03 ";
			final Orders orders = Orders.dao.findFirst("select orders_id,course_id,user_id,course_user_id,session_rate,take_partner_num,surcharge_for_each_cash,go_door_traffic_cost,buy_amount,first_joint_fee,discount_price,my_coupon_money,total_session_rate,original_total_session_rate,order_number,leave_message,booking_status,status,post_time,buy_nickname,phone_number from orders where orders_id=?",orders_id);
			if (orders!=null) {
				int hasnone_booking_course=0;
				int buy_amount = orders.get(orders.buy_amount);
				int course_id = orders.get(orders.course_id);
				Course course = Course.dao.findFirst(sqlString+" from course where course_id=?",course_id);
				String course_nickname ="",course_telephone="",user_images_01="",phone_number="";int course_is_official= 0;
				phone_number = orders.getStr(orders.phone_number);
				if (course!=null) {
					user_images_01 = course.getStr(course.user_images_01);
					//教练
					int course_user_id = orders.get(orders.course_user_id);
					User courseUser = User.dao.findById(course_user_id);
					if (courseUser!=null) {
						course_is_official = courseUser.get(courseUser.is_official);
						course_nickname = courseUser.getStr(courseUser.nickname);
						course_telephone = courseUser.getStr(courseUser.telephone);
					}
					course.put("course_is_official", course_is_official);
					course.put("course_image_01", user_images_01);
					course.put("course_nickname", course_nickname);
					course.put("course_telephone", course_telephone);
					//距离
					String course_latitude = course.getStr(course.latitude);
					if (course_latitude.equals("")||course_latitude.equals("0")) {
						course.put("distance", "Distance cannot be calculated.");
					}else {
						double distance = course.getDouble("distance");
						double distance_miles = distance/1609.344;
						DecimalFormat df=new DecimalFormat(".##");
						distance_miles=Double.parseDouble(df.format(distance_miles));
						course.put("distance", distance_miles+" miles");
					}
					//购买者
					String buy_image_01="",buy_nickname ="",buy_telephone="",buy_email="";int buy_is_official= 0;
					int buy_user_id = orders.get(orders.user_id);
					User buyUser = User.dao.findById(buy_user_id);
					if (buyUser!=null) {
						buy_is_official = buyUser.get(buyUser.is_official);
						buy_image_01 = buyUser.getStr(buyUser.image_01);
						buy_nickname = buyUser.getStr(buyUser.nickname);
						buy_telephone = buyUser.getStr(buyUser.telephone);
						buy_email = buyUser.getStr(buyUser.email);
					}
					course.put("buy_is_official", buy_is_official);
					course.put("buy_image_01", buy_image_01);
					course.put("buy_nickname", buy_nickname);
					course.put("buy_telephone", phone_number);
					course.put("buy_email", buy_email);
					//评论总数
					Comment comment = Comment.dao.findFirst("select count(course_id) as total_coment_num from comment where type=1 and course_id=? and status!=0",course_id);
					long total_coment_num = 0;
					if (comment!=null) {
						total_coment_num = comment.getLong("total_coment_num");
					}
					course.put("total_coment_num", total_coment_num);
				}
				orders.put("Course", course);
				//时间表
				List<OrdersSchedule> oSchedules = OrdersSchedule.dao.find("select orders_schedule_id,schedule_data,schedule_hours,refund_status,status,finish_status from orders_schedule where orders_id=? order by schedule_data desc",orders_id);
				for(OrdersSchedule oSchedule:oSchedules){
					int refund_status = oSchedule.get(oSchedule.refund_status);
					int oSchedule_status = oSchedule.get(oSchedule.status);
					if (((refund_status==10)&&(oSchedule_status==4))||(refund_status==10)&&(oSchedule_status==21)) {
						hasnone_booking_course = hasnone_booking_course+1;
					}
				}
				orders.put("OrdersSchedules", oSchedules);
				course.put("hasnone_booking_course", hasnone_booking_course);
				/////按钮状态
				int orders_status = orders.get(orders.status);
				int cancel_num = 0,confirm=0,can_finish_num=0;
				//无按钮
				int button_status1 = 0,button_status2 = 0,button_status3 = 0,button_status4 = 0;
				if (orders_status==21||orders_status==22) {
					for(OrdersSchedule oSchedule:oSchedules){
						int oSchedule_status = oSchedule.get(oSchedule.status);
						int oSchedule_refund_status = oSchedule.get(oSchedule.refund_status);
						int finish_status = oSchedule.get(oSchedule.finish_status);
						if (oSchedule_status==20&&oSchedule_refund_status==10) {
							cancel_num = cancel_num+1;
						}
						if ((oSchedule_status==3&&oSchedule_refund_status==10)||(oSchedule_status==3&&oSchedule_refund_status==1)) {
							confirm = confirm+1;
						}
						//if (finish_status==2) {
						if (oSchedule_status==20&&oSchedule_refund_status==4) {
							can_finish_num=can_finish_num+1;
						}
					}
					if (cancel_num>0) {
						//取消课程(退款)
						button_status1 = 1;
					}
					if (confirm>0) {
						//确认课程时间
						button_status2 =1;
					}
					if (can_finish_num>0) {
						//确认课程已经完成
						button_status3 =1;
					}
				}
				if (orders_status==30||orders_status==40) {
					//delete，删除已完成订单
					button_status4 = 1;
				}
				orders.put("button_status1", button_status1);
				orders.put("button_status2", button_status2);
				orders.put("button_status3", button_status3);
				orders.put("button_status4", button_status4);
			}
			responseValues.put("Orders", orders);
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "Data request successful.");
			setAttr("CoachOrderInfoResponse", responseValues);
			renderMultiJson("CoachOrderInfoResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("CoachOrdersManage/coachOrderInfo", "coachOrderInfo", this);
		}
	}
}
