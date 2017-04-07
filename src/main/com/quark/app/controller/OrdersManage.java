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
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipayCore;
import com.alipay.util.AlipaySubmit;
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
import com.quark.app.bean.PayTypeUntil;
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
import com.quark.model.extend.ChargeLog;
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
import com.tenpay.util.WXUtil;
import com.unionpay.acp.demo.UnionPayUtils;

/**
 * @author C罗
 * 课程信息
 */
@Before(Tx.class)
public class OrdersManage extends Controller {

	@Author("cluo")
	@Rp("确认下单")
	@Explaination(info = "下单详情")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String_NotRequired, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.course_id)
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String, name = Course.latitude)
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String, name = Course.longitude)
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.course_id)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.user_id)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.title)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.total_score)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.session_rate)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.category_01_id)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.category_02_id)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.travel_to_session)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.travel_to_session_distance)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.travel_to_session_trafic_surcharge)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.category_02_name)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.city)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.area)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.street)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.address)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.latitude)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.longitude)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.zipcode)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.additional_partner)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.surcharge_for_each)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.discount_type)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.discount_onetion_pur_money_01)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.discount_price_01)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.discount_onetion_pur_money_02)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.discount_price_02)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.discount_onetion_pur_money_03)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.discount_price_03)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.achievements)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.specialist)
	@ReturnDBParam(name = "OrderCourseInfoResponse{Course:$}", column = Course.status)
	@ReturnOutlet(name = "OrderCourseInfoResponse{Course:is_stop_course}", remarks = "今后暂停课程 0-否，1-是", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "OrderCourseInfoResponse{Course:is_official}", remarks = "是否官方认证：0-否，1-是", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "OrderCourseInfoResponse{Course:user_image_01}", remarks = "用户头像", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "OrderCourseInfoResponse{Course:nickname}", remarks = "昵称", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "OrderCourseInfoResponse{Course:first_joint_fee}", remarks = "首次跟该教练产生交易 首次对接费用百分比", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "OrderCourseInfoResponse{Course:total_coment_num}", remarks = "评论总数", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "OrderCourseInfoResponse{Course:freeCourseDay}", remarks = "有空日期", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "OrderCourseInfoResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "OrderCourseInfoResponse{status}", remarks = "0-失败，1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "OrderCourseInfoResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void orderCourseInfo() {
		try {
			String token = getPara("token");
			String course_id = getPara("course_id");
			String latitude = getPara("latitude");
			String longitude = getPara("longitude");
			String invoke = getPara("invoke","app");
			String user_id = "0";
			if (token!=null||AppToken.check(token, this,invoke)) {
				// 登陆成功
				user_id = AppToken.getUserId(token, this,invoke);
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			String distance_sql= "round(6378.138*2*asin(sqrt(pow(sin( ("
					+ latitude
					+ "*pi()/180-latitude*pi()/180)/2),2)+cos("
					+ latitude
					+ "*pi()/180)*cos(latitude*pi()/180)* pow(sin( ("
					+ longitude
					+ "*pi()/180-longitude*pi()/180)/2),2)))*1000)";
			String sqlString = "select course_id,user_id,user_images_01,title,total_score,achievements,specialist,travel_to_session,travel_to_session_distance,travel_to_session_trafic_surcharge,"+distance_sql+" as distance,session_rate,category_01_id,category_02_id,additional_partner,surcharge_for_each,"
					+ "city,area,street,address,zipcode,latitude,longitude,discount_type,discount_onetion_pur_money_01,discount_price_01,discount_onetion_pur_money_02,discount_price_02,discount_onetion_pur_money_03,discount_price_03,category_02_name,status ";
			final Course course = Course.dao.findFirst(sqlString+" from course where course_id=?",course_id);
			if (course!=null) {
				String course_user_id = course.getStr(course.user_id);
				String user_image_01=course.getStr(course.user_images_01),nickname ="";
				int is_official= 0,is_stop_course= 0;
				User courseUser = User.dao.findById(course_user_id);
				if (courseUser!=null) {
					is_official = courseUser.get(courseUser.is_official);
					is_stop_course = courseUser.get(courseUser.is_stop_course);
					nickname = courseUser.getStr(courseUser.nickname);
				}
				course.put("is_stop_course", is_stop_course);
				course.put("is_official", is_official);
				course.put("user_image_01", user_image_01);
				course.put("nickname", nickname);
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
				//首次跟该教练产生交易 首次对接费用百分比
				double first_joint_fee=0;
				int refund_priod_of_validity = 3;
				Orders orders2 = Orders.dao.findFirst("select orders_id from orders where user_id=? and course_user_id=? and status>11",user_id,course_user_id);
				Constant constant = Constant.dao.findFirst("select constant_id,first_joint_fee,refund_priod_of_validity from constant ");
				if (constant!=null) {
					first_joint_fee = constant.getDouble(constant.first_joint_fee);
					refund_priod_of_validity = constant.get(constant.refund_priod_of_validity);
				}
				if (orders2!=null) {
					first_joint_fee = 0;
				}
				course.put("first_joint_fee", first_joint_fee);
				//评论总数
				Comment comment = Comment.dao.findFirst("select count(course_id) as total_coment_num from comment where type=1 and course_id=? and status!=0",course_id);
				long total_coment_num = 0;
				if (comment!=null) {
					total_coment_num = comment.getLong("total_coment_num");
				}
				course.put("total_coment_num", total_coment_num);
				int travel_to_session = course.get(course.travel_to_session);
				String course_street = course.getStr(course.street);
				if (travel_to_session==0) {
					if (!course_street.equals("")) {
						String new_course_street = "";
						String[] course_street_array = course_street.split(" ");
						if (course_street_array.length>1) {
							for(int i=1;i<course_street_array.length;i++){
								new_course_street=new_course_street+" "+course_street_array[i];
							}
						}
						course.put("street", new_course_street);
					}
				}
				///课程有空时间
				String freeCourseDay = ScheduleBean2.freeCourseDay(Integer.parseInt(course_id));
				course.put("freeCourseDay", freeCourseDay);
			}
			responseValues.put("Course", course);
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "Data request successful.");
			setAttr("OrderCourseInfoResponse", responseValues);
			renderMultiJson("OrderCourseInfoResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("OrdersManage/orderCourseInfo", "orderCourseInfo", this);
		}
	}
	@Author("cluo")
	@Rp("我的订单")
	@Explaination(info = "购物车数量")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "CardNumResponse{total_card_number}", remarks = "购物车数量", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CardNumResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CardNumResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CardNumResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void cardNum() {
		try {
			String token = getPara("token");
			String invoke = getPara("invoke","app");
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("CardNumResponse", response2);
				renderMultiJson("CardNumResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			long total_card_number = 0;
			Orders orders = Orders.dao.findFirst("select count(orders_id) as total_card_number from orders where status=1 and user_id=? ",user_id);
			if (orders!=null) {
				total_card_number = orders.getLong("total_card_number");
			}
			responseValues.put("total_card_number", total_card_number);
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "成功返回");
			setAttr("CardNumResponse", responseValues);
			renderMultiJson("CardNumResponse");
			AppLog.info("", getRequest());
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("OrdersManage/cardNum", "购物车数量", this);
		}
	}
	@Author("cluo")
	@Rp("选择优惠券")
	@Explaination(info = "优惠券列表【价格从高到低返回】")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.category_01_id)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.category_02_id)
	@URLParam(defaultValue = "", explain = "商家ID：发布者", type = Type.String, name = Course.user_id)
	@URLParam(defaultValue = "", explain = "课程ID：指定的课程", type = Type.String, name = Course.course_id)
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnDBParam(name = "CourseCouponListResponse{courseCouponListResult:CourseCoupons:list[MyCoupon:$]}", column = MyCoupon.my_coupon_id)
	@ReturnDBParam(name = "CourseCouponListResponse{courseCouponListResult:CourseCoupons:list[MyCoupon:$]}", column = MyCoupon.user_id)
	@ReturnDBParam(name = "CourseCouponListResponse{courseCouponListResult:CourseCoupons:list[MyCoupon:$]}", column = MyCoupon.provider)
	@ReturnDBParam(name = "CourseCouponListResponse{courseCouponListResult:CourseCoupons:list[MyCoupon:$]}", column = MyCoupon.coupon_name)
	@ReturnDBParam(name = "CourseCouponListResponse{courseCouponListResult:CourseCoupons:list[MyCoupon:$]}", column = MyCoupon.coupon_money)
	@ReturnDBParam(name = "CourseCouponListResponse{courseCouponListResult:CourseCoupons:list[MyCoupon:$]}", column = MyCoupon.coupon_num)
	@ReturnDBParam(name = "CourseCouponListResponse{courseCouponListResult:CourseCoupons:list[MyCoupon:$]}", column = MyCoupon.coupon_number)
	@ReturnOutlet(name = "CourseCouponListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CourseCouponListResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CourseCouponListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void courseCouponList2() {
		try {
			String token = getPara("token");
			int category_01_id = getParaToInt("category_01_id",0);
			int category_02_id = getParaToInt("category_02_id",0);
			String publish_user_id = getPara("user_id","0");
			int course_id = getParaToInt("course_id",0);
			String invoke = getPara("invoke","app");
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("AddExperienceResponse", response2);
				renderMultiJson("AddExperienceResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			//规则
			final List<MyCoupon> myCoupons = MyCoupon.dao.find("select * from my_coupon where status=1 and user_id=? order by coupon_money desc, end_time desc",user_id);
			final List<MyCoupon> myCoupons2 = new ArrayList<MyCoupon>();
			for(MyCoupon myCoupon:myCoupons){
				int rule_02 = 0,rule_03 = 0,rule_04 = 0,rule_05 = 0;
				//一级分类是否可使用：0-不限，其他-受限
				int c_category_01_id = myCoupon.get(myCoupon.category_01_id);
				if (c_category_01_id==0) {
					rule_02 = 1;
				}
				if (c_category_01_id>0) {
					if (c_category_01_id==category_01_id) {
						rule_02 = 1;
					}
				}
				//二级分类是否可使用：0-不限，其他-受限
				int c_category_02_id = myCoupon.get(myCoupon.category_02_id);
				if (c_category_02_id==0) {
					rule_03 = 1;
				}
				if (c_category_02_id>0) {
					if (c_category_02_id==category_02_id) {
						rule_03 = 1;
					}
				}
				//0-否，1-是【是否指定的商家活动（即指定商家发布的课程才可使用）】
				int c_is_seller = myCoupon.get(myCoupon.is_seller);
				if (c_is_seller==0) {
					rule_04 = 1;
				}
				if (c_is_seller>0) {
					if (c_is_seller==Integer.parseInt(publish_user_id)) {
						rule_04 = 1;
					}
				}
				//是否指定课程：0-否，1-是【课程ID】
				int c_is_course = myCoupon.get(myCoupon.is_course);
				if (c_is_course==0) {
					rule_05 = 1;
				}
				if (c_is_course>0) {
					if (c_is_course==course_id) {
						rule_05 = 1;
					}
				}
				if (rule_02 == 1 && rule_03 == 1 && rule_04 == 1 && rule_05 == 1) {
					myCoupons2.add(myCoupon);
				}
			}
			responseValues.put("Result", new HashMap<String, Object>() {
				{
					put("CourseCoupons", myCoupons2);
				}
			});
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "成功返回");
			setAttr("CourseCouponListResponse", responseValues);
			renderMultiJson("CourseCouponListResponse");
			AppLog.info("", getRequest());
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("OrdersManage/courseCouponList", "优惠券列表", this);
		}
	}
	
	@Author("cluo")
	@Rp("确认下单")
	@Explaination(info = "确认下单")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "{1、11}", explain = "1-加入购物车，11-直接购买", type = Type.String, name = "order_type")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.course_id)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.title)
	@URLParam(defaultValue = "", explain = "city area street address 拼接", type = Type.String, name = "address")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.course_user_id)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.buy_amount)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.session_rate)
	@URLParam(defaultValue = "{0、1}", explain = Value.Infer, type = Type.Int_NotRequired, name = Orders.go_door_status)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String_NotRequired, name = Orders.go_door_city)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String_NotRequired, name = Orders.go_door_area)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String_NotRequired, name = Orders.go_door_street)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String_NotRequired, name = Orders.go_door_address)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String_NotRequired, name = Orders.go_door_latitude)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String_NotRequired, name = Orders.go_door_longitude)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String_NotRequired, name = Orders.go_door_zipcode)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String_NotRequired, name = Orders.go_door_traffic_cost)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.my_coupon_id)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.my_coupon_money)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String_NotRequired, name = Orders.leave_message)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.discount_type)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.discount_price)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.Int, name = Orders.take_partner_num)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.surcharge_for_each_cash)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.total_session_rate)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.original_total_session_rate)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.first_joint_fee)
	@URLParam(defaultValue = "", explain = "课程日期A日期下标A日期下标如：2015-06-11A1A2#2015-06-12A21A22#2015-06-14A23A24{一节课60分钟，一个数字代表半节课}", type = Type.String_NotRequired, name = "schedule_datas")
	@URLParam(defaultValue = "{1、11}", explain = Value.Infer, type = Type.String, name = "order_type")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "AddPayResponse{orders_id}", remarks = "订单ID", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "AddPayResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "AddPayResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "AddPayResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void addPay() {
		try {
			String token = getPara("token");
			int order_type = getParaToInt("order_type", 1);
			int course_id = getParaToInt("course_id");
			String title = getPara("title");
			String address = getPara("address");
			int course_user_id = getParaToInt("course_user_id");
			int buy_amount = getParaToInt("buy_amount", 1);
			double session_rate = Double.parseDouble(getPara("session_rate"));
			int go_door_status = getParaToInt("go_door_status",0);
			String go_door_city = getPara("go_door_city");
			String go_door_area = getPara("go_door_area");
			String go_door_street = getPara("go_door_street");
			String go_door_address = getPara("go_door_address");
			String go_door_latitude = getPara("go_door_latitude");
			String go_door_longitude = getPara("go_door_longitude");
			String go_door_zipcode = getPara("go_door_zipcode");
			double go_door_traffic_cost = Double.parseDouble(getPara("go_door_traffic_cost","0"));
			int my_coupon_id = getParaToInt("my_coupon_id",0);
			double my_coupon_money = Double.parseDouble(getPara("my_coupon_money","0"));
			String leave_message = getPara("leave_message");
			int discount_type = getParaToInt("discount_type",0);
			double discount_price = Double.parseDouble(getPara("discount_price","0"));
			int take_partner_num = getParaToInt("take_partner_num",0);
			double surcharge_for_each_cash = Double.parseDouble(getPara("surcharge_for_each_cash","0"));
			double total_session_rate = Double.parseDouble(getPara("total_session_rate"));
			double original_total_session_rate = Double.parseDouble(getPara("original_total_session_rate"));
			double first_joint_fee = Double.parseDouble(getPara("first_joint_fee"));
			//日期  
			String schedule_datas = getPara("schedule_datas","");
			String coustomer_name = getPara("coustomer_name","");
			String coustomer_cellphone = getPara("coustomer_cellphone","");
			String invoke = getPara("invoke","app");
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("AddPayResponse", response2);
				renderMultiJson("AddPayResponse");
				return;
			}
			int status = 0;String message="Please wait.";
			String user_id = AppToken.getUserId(token, this,invoke);
			User buy_user = User.dao.findById(user_id);
			User course_user = User.dao.findById(course_user_id);
			int is_stop_course = course_user.get(course_user.is_stop_course);
			if (schedule_datas!=null&&!schedule_datas.equals("")) {
				String[] schedule_data_array = schedule_datas.split("#");
				if (schedule_data_array.length>buy_amount) {
					status = 2;
					message="The time you select can not exceed the amount of course purchased";
				}
			}
			if (status==0) {
				if (buy_user==null||course_user==null) {
					status = 2;
					message="Please wait.";
				}
				if (Integer.parseInt(user_id)==course_user_id) {
					status = 2;
					message="You can not order the course you create";
				}
			}
			if (status==0) {
				if (is_stop_course==1) {
					status = 2;
					message="Sorry, this course is suspended.";
				}
			}
			if (status==0) {
				if (invoke.equals("app")) {
					if (coustomer_name.equals("")||coustomer_cellphone.equals("")) {
						status = 2;
						message="Please input Coustomer Name or Coustomer Cellphone!";
					}
				}
			}
			int orders_id = 0;
			String order_number= DateUtils.getTimeStampNo();
			if (status==0) {
				int refund_priod_of_validity = 3;
				Constant constant = Constant.dao.findFirst("select constant_id,refund_priod_of_validity from constant ");
				if (constant!=null) {
					refund_priod_of_validity = constant.get(constant.refund_priod_of_validity);
				}
				String buy_nickname = buy_user.getStr(buy_user.nickname);
				String buy_email = buy_user.getStr(buy_user.email);
				String course_nickname = course_user.getStr(course_user.nickname);
				String course_email = buy_user.getStr(course_user.email);
				String course_telephone = buy_user.getStr(course_user.telephone);
				Orders orders = new Orders();
				if (go_door_status==0) {
					go_door_traffic_cost = 0;
				}
				boolean save = orders.set(orders.user_id, user_id)
						.set(orders.buy_nickname, buy_nickname)
						.set(orders.buy_email, buy_email)
						.set(orders.course_id, course_id)
						.set(orders.course_title, title)
						.set(orders.course_user_id, course_user_id)
						.set(orders.course_nickname, course_nickname)
						.set(orders.course_email, course_email)
						.set(orders.course_telephone, course_telephone)
						.set(orders.first_joint_fee, first_joint_fee)
						.set(orders.order_number, order_number)
						.set(orders.buy_amount, buy_amount)
						.set(orders.session_rate, session_rate)
						.set(orders.go_door_status, go_door_status)
						.set(orders.go_door_city, go_door_city)
						.set(orders.go_door_area, go_door_area)
						.set(orders.go_door_street, go_door_street)
						.set(orders.go_door_address, go_door_address)
						.set(orders.go_door_latitude, go_door_latitude)
						.set(orders.go_door_longitude, go_door_longitude)
						.set(orders.go_door_zipcode, go_door_zipcode)
						.set(orders.go_door_traffic_cost, go_door_traffic_cost)
						.set(orders.my_coupon_id, my_coupon_id)
						.set(orders.my_coupon_money, my_coupon_money)
						.set(orders.leave_message, leave_message)
						.set(orders.status, order_type)
						.set(orders.booking_status, 2)
						.set(orders.post_time, DateUtils.getCurrentDateTime())
						.set(orders.discount_type, discount_type)
						.set(orders.discount_price, discount_price)
						.set(orders.take_partner_num, take_partner_num)
						.set(orders.surcharge_for_each_cash, surcharge_for_each_cash)
						.set(orders.total_session_rate, total_session_rate)
						.set(orders.original_total_session_rate, original_total_session_rate)
						.set(orders.first_name, coustomer_name)
						.set(orders.phone_number, coustomer_cellphone)
						.save();
			if (save) {
				status = 1;
				message="Add to cart successful.";
				if (order_type==11) {
					message="Your order has been placed. Please make your payment.";
				}
				//时间
				orders_id = orders.get("orders_id");
				if ((schedule_datas!=null&&!schedule_datas.equals(""))) {
					//2015-06-11@1@2#2015-06-12@21@22#2015-06-14@23@24
					String [][]schedule_time_array22 = ScheduleBean2.schedule_time_array2;
					String[] schedule_data_array = schedule_datas.split("#");
					if (schedule_data_array.length<=buy_amount) {
						for(int i=0;i<schedule_data_array.length;i++){
							String schedule_data_indexs = schedule_data_array[i];
							String[] schedule_data_index = schedule_data_indexs.split("A");
							boolean save2 = false;
							String schedule_ids = "";
							for(int j=1;j<3;j++){
								int schedule_id = 0;
								Schedule schedule = new Schedule();
								save2 = schedule.set(schedule.user_id, course_user_id)
										.set(schedule.choice_currentdate, schedule_data_index[0])
										.set(schedule.time_slot, schedule_data_index[j])
										.set(schedule.username, buy_nickname)
										.set(schedule.orders_id, orders_id)
										.set(schedule.course_id, course_id)
										.set(schedule.course_name, title)
										.set(schedule.course_time, schedule_data_index[0]+" "+schedule_time_array22[Integer.parseInt(schedule_data_index[j])][1]+"~"+schedule_time_array22[Integer.parseInt(schedule_data_index[j])][2])
										.set(schedule.course_location, address)
										.set(schedule.type, 0)
										.set(schedule.status, 1)
										.save();
								if (save2) {
									schedule_id = schedule.get("schedule_id");
									schedule_ids+=schedule_id+"#";
								}
							}
							if (save2) {
								OrdersSchedule oSchedule = new OrdersSchedule();
								oSchedule.set(oSchedule.orders_id, orders_id)
									.set(oSchedule.user_id, user_id)
									.set(oSchedule.schedule_ids, schedule_ids)
									.set(oSchedule.orders_id, orders_id)
									.set(oSchedule.schedule_data, schedule_data_index[0])
									.set(oSchedule.schedule_hours, schedule_time_array22[Integer.parseInt(schedule_data_index[1])][1]+"~"+schedule_time_array22[Integer.parseInt(schedule_data_index[2])][2])
									.set(oSchedule.schedule_time_slots, schedule_data_index[1]+"A"+schedule_data_index[2])
									.set(oSchedule.status, 3)
									.set(oSchedule.is_pay, 0)
									.set(oSchedule.refund_status, 1)
									.set(oSchedule.post_time, DateUtils.getCurrentDateTime())
									.set(oSchedule.refund_priod_of_validity, refund_priod_of_validity)
									.set(oSchedule.refund_priod_start_time, DateUtils.getCurrentDateTime())
									.save();
								}
							}
						}
						for(int i=0;i<buy_amount-schedule_data_array.length;i++){
							OrdersSchedule oSchedule = new OrdersSchedule();
							oSchedule.set(oSchedule.orders_id, orders_id)
							.set(oSchedule.user_id, user_id)
							.set(oSchedule.schedule_ids, 0)
							.set(oSchedule.orders_id, orders_id)
							.set(oSchedule.schedule_data, "")
							.set(oSchedule.schedule_hours, "")
							.set(oSchedule.schedule_time_slots, 0)
							.set(oSchedule.status, 4)
							.set(oSchedule.is_pay, 0)
							.set(oSchedule.refund_status, 10)
							.save();
						}
					}else {
						for(int i=0;i<buy_amount;i++){
							OrdersSchedule oSchedule = new OrdersSchedule();
							oSchedule.set(oSchedule.orders_id, orders_id)
							.set(oSchedule.user_id, user_id)
							.set(oSchedule.schedule_ids, 0)
							.set(oSchedule.orders_id, orders_id)
							.set(oSchedule.schedule_data, "")
							.set(oSchedule.schedule_hours, "")
							.set(oSchedule.schedule_time_slots, 0)
							.set(oSchedule.status, 4)
							.set(oSchedule.refund_status, 10)
							.set(oSchedule.is_pay, 0)
							.save();
						}
					}
					//优惠券使用
					if (my_coupon_id!=0) {
						MyCoupon myCoupon = MyCoupon.dao.findById(my_coupon_id);
						if (myCoupon!=null) {
							myCoupon.set(myCoupon.status, 2)
									.set(myCoupon.post_time, DateUtils.getCurrentDateTime())
									.update();
						}
					}
				}
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("order_number", order_number);
			responseValues.put("orders_id", orders_id);
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("AddPayResponse", responseValues);
			renderMultiJson("AddPayResponse");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			AppData.analyze("OrdersManage/addPay", "下单", this);
		}
	}
	@Author("cluo")
	@Rp("我的订单")
	@Explaination(info = "用户订单列表--显示按钮状态【status=10{delete订单失效}status=11{delete,pay now}status=21,22{可退款，不可退款}booking_status=1，2{可booking，不可booking}status=30,40{30-已完成，40-已评价}】")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "{1、2、3、4}", explain = "1-全部，2-未付款，3-已付款，4-已完成", type = Type.String, name = "order_type")
	@URLParam(defaultValue = "1", explain = Value.Infer, type = Type.String, name = "pn")
	@URLParam(defaultValue = "5", explain = Value.Infer, type = Type.String, name = "page_size")
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String, name = Course.latitude)
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String, name = Course.longitude)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	// page property
	@ReturnOutlet(name = "OrdersListResponse{ordersListResult:OrderList:pageNumber}", remarks = "page number", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "OrdersListResponse{ordersListResult:OrderList:pageSize}", remarks = "result amount of this page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "OrdersListResponse{ordersListResult:OrderList:totalPage}", remarks = "total page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "OrdersListResponse{ordersListResult:OrderList:totalRow}", remarks = "total row", dataType = DataType.Int, defaultValue = "")
	// 
	@ReturnDBParam(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:$]}", column = Orders.orders_id)
	@ReturnDBParam(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:$]}", column = Orders.course_id)
	@ReturnDBParam(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:$]}", column = Orders.buy_amount)
	@ReturnDBParam(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:$]}", column = Orders.total_session_rate)
	@ReturnDBParam(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:$]}", column = Orders.original_total_session_rate)
	@ReturnDBParam(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:$]}", column = Orders.first_joint_fee)
	@ReturnDBParam(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:$]}", column = Orders.status)
	@ReturnDBParam(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:$]}", column = Orders.booking_status)
	@ReturnDBParam(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:$]}", column = Orders.post_time)
	
	@ReturnDBParam(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:Course:$]}", column = Course.course_id) 
	@ReturnDBParam(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:Course:$]}", column = Course.user_id)   
	@ReturnDBParam(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:Course:$]}", column = Course.title)
	@ReturnDBParam(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:Course:$]}", column = Course.category_01_name)
	@ReturnDBParam(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:Course:$]}", column = Course.category_02_name)
	@ReturnDBParam(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:Course:$]}", column = Course.catetory_name)
	@ReturnDBParam(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:Course:$]}", column = Course.session_rate)
	@ReturnDBParam(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:Course:$]}", column = Course.total_score)
	@ReturnDBParam(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:Course:$]}", column = Course.latitude)
	@ReturnDBParam(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:Course:$]}", column = Course.longitude)
	@ReturnDBParam(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:Course:$]}", column = Course.city)
	@ReturnDBParam(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:Course:$]}", column = Course.area)
	@ReturnDBParam(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:Course:$]}", column = Course.street)
	@ReturnDBParam(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:Course:$]}", column = Course.address)
	
	@ReturnOutlet(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:Course:is_official]}", remarks = "是否官方认证：0-否，1-是", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:Course:coach_name]}", remarks = "教练名称", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:Course:coach_image]}", remarks = "教练图片", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:Course:distance]}", remarks = "距离", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:Course:total_coment_num]}", remarks = "评论总数", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "OrdersListResponse{ordersListResult:OrderList:list[Orders:Course:hasnone_booking_course]}", remarks = "未选择课程数", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "OrdersListResponse{has_invalid_order_num}", remarks = "订单失效产品数量,大于0出现一键删除", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "OrdersListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "OrdersListResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "OrdersListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void ordersList() {
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
				setAttr("OrdersListResponse", response2);
				renderMultiJson("OrdersListResponse");
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
				setAttr("OrdersListResponse", response2);
				renderMultiJson("OrdersListResponse");
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
			String filer_sql = " (status=10 or status=11 or status=21 or status=22 or status=30 or status=40)  ";
			if (order_type==2) {
				//1-全部，2-未付款，3-已付款，4-已完成
				filer_sql = " (status=10 or status=11) ";
			}
			if (order_type==3) {
				filer_sql = " (status=21 or status=22) ";
			}
			if (order_type==4) {
				filer_sql = " (status=30 or status=40) ";
			}
			final Page<Orders> ordersPage = Orders.dao
					.paginate(
							pn,
							page_size,
							"select orders_id,course_id,buy_amount,total_session_rate,first_joint_fee,original_total_session_rate,status,booking_status,post_time "," from orders where "+filer_sql+" and user_id="+user_id+" order by post_time desc");
			int has_invalid_order_num = 0;
			for(Orders orders:ordersPage.getList()){
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
					///课程有空时间
					String freeCourseDay = ScheduleBean2.freeCourseDay(course_id);
					course.put("freeCourseDay", freeCourseDay);
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
				orders.put("Course", course);
				int orders_status = orders.get(orders.status);
				if (orders_status==10) {
					has_invalid_order_num = has_invalid_order_num+1;
				}
				int comment_id=0;
				Comment comment = Comment.dao.findFirst("select comment_id from comment where type=1 and course_id="+course_id+" and user_id="+user_id+" and status!=0");
				if (comment!=null) {
					comment_id = comment.get(comment.comment_id);
				}
				course.put("comment_id", comment_id);
				//评论总数
				Comment comment2 = Comment.dao.findFirst("select count(course_id) as total_coment_num from comment where type=1 and course_id=? and status!=0",course_id);
				long total_coment_num = 0;
				if (comment2!=null) {
					total_coment_num = comment2.getLong("total_coment_num");
				}
				course.put("total_coment_num", total_coment_num);
				//时间表--- 未选择时间的课程数
				OrdersSchedule  ordersSchedule = OrdersSchedule.dao.findFirst("select count(orders_schedule_id) as hasnone_booking_course from orders_schedule where (status=4 or status=21) and refund_status=10 and orders_id=? and user_id=?",orders_id,user_id);
				long hasnone_booking_course = 0;
				if (ordersSchedule!=null) {
					hasnone_booking_course = ordersSchedule.getLong("hasnone_booking_course");
				}
				course.put("hasnone_booking_course", hasnone_booking_course);
				//
				if (orders_status==21||orders_status==22) {
					OrdersSchedule oSchedules = OrdersSchedule.dao.findFirst("select orders_schedule_id,orders_id,schedule_data,schedule_hours,schedule_ids,refund_status,status from orders_schedule where status!=1 and user_id="+user_id+" and orders_id="+orders_id+" and refund_status=10");
					if (oSchedules!=null) {
						orders.put("status", 21);
					}else {
						orders.put("status", 22);
					}
				}
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("Result", new HashMap<String, Object>() {
				{
					put("OrderList", ordersPage);
				}
			});
			responseValues.put("has_invalid_order_num", has_invalid_order_num);
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "Data request successful.");
			setAttr("OrdersListResponse", responseValues);
			renderMultiJson("OrdersListResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("OrdersManage/ordersList", "我的订单列表", this);
		}
	}
	
	/**
	 *  4. 确认时间的课程状态为已确认（confirmed），拒绝时间的课程状态回归未选择时间
	 */
	@Author("cluo")
	@Rp("我的订单详情")
	@Explaination(info = "我的订单详情[(refund_status=10)用户未申请退款，(refund_status=2)退款中，(refund_status=30)教练申请取消订单退款中，(refund_status=31)管理员确认退款完成，(refund_status=4)退款完成]")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.orders_id)
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String, name = Course.latitude)
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String, name = Course.longitude)
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	// 
	@ReturnDBParam(name = "OrderInfoResponse{Orders:$}", column = Orders.orders_id)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:$}", column = Orders.course_id)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:$}", column = Orders.user_id)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:$}", column = Orders.course_user_id)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:$}", column = Orders.session_rate)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:$}", column = Orders.take_partner_num)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:$}", column = Orders.order_number)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:$}", column = Orders.surcharge_for_each_cash)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:$}", column = Orders.go_door_traffic_cost)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:$}", column = Orders.buy_amount)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:$}", column = Orders.first_joint_fee)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:$}", column = Orders.discount_price)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:$}", column = Orders.my_coupon_money)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:$}", column = Orders.total_session_rate)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:$}", column = Orders.original_total_session_rate)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:$}", column = Orders.order_number)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:$}", column = Orders.post_time)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:$}", column = Orders.booking_status)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:$}", column = Orders.status)
	//
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.course_id)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.user_id)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.title)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.total_score)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.session_rate)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.category_01_id)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.category_02_id)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.category_02_name)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.travel_to_session)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.travel_to_session_distance)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.travel_to_session_trafic_surcharge)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.city)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.area)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.street)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.address)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.latitude)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.longitude)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.additional_partner)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.surcharge_for_each)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.discount_type)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.discount_onetion_pur_money_01)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.discount_price_01)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.discount_onetion_pur_money_02)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.discount_price_02)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.discount_onetion_pur_money_03)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.discount_price_03)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.achievements)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:Course:$}", column = Course.specialist)
	
	@ReturnOutlet(name = "OrderInfoResponse{Orders:Course:course_email}", remarks = "教练email", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "OrderInfoResponse{Orders:Course:course_is_official}", remarks = "教练是否官方认证：0-否，1-是", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "OrderInfoResponse{Orders:Course:course_image_01}", remarks = "教练用户头像", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "OrderInfoResponse{Orders:Course:course_nickname}", remarks = "教练昵称", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "OrderInfoResponse{Orders:Course:course_telephone}", remarks = "教练电话", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "OrderInfoResponse{Orders:Course:total_coment_num}", remarks = "评论总数", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "OrderInfoResponse{Orders:hasnone_booking_course}", remarks = "未选择时间的课程数", dataType = DataType.String, defaultValue = "")
	//时间表 
	@ReturnDBParam(name = "OrderInfoResponse{Orders:OrdersSchedules:list[OrdersSchedule:$]}", column = OrdersSchedule.orders_schedule_id)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:OrdersSchedules:list[OrdersSchedule:$]}", column = OrdersSchedule.schedule_data)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:OrdersSchedules:list[OrdersSchedule:$]}", column = OrdersSchedule.schedule_hours)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:OrdersSchedules:list[OrdersSchedule:$]}", column = OrdersSchedule.refund_status)
	@ReturnDBParam(name = "OrderInfoResponse{Orders:OrdersSchedules:list[OrdersSchedule:$]}", column = OrdersSchedule.status)
	@ReturnOutlet(name = "OrderInfoResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "OrderInfoResponse{status}", remarks = "0-失败，1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "OrderInfoResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void orderInfo() {
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
				setAttr("OrderInfoResponse", response2);
				renderMultiJson("OrderInfoResponse");
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
			String sqlString = "select course_id,user_id,user_images_01,title,category_02_name,total_score,achievements,specialist,travel_to_session,travel_to_session_distance,travel_to_session_trafic_surcharge,"+distance_sql+" as distance,session_rate,category_01_id,category_02_id,additional_partner,surcharge_for_each,"
					+ "city,area,street,address,latitude,longitude,discount_type,discount_onetion_pur_money_01,discount_price_01,discount_onetion_pur_money_02,discount_price_02,discount_onetion_pur_money_03,discount_price_03 ";
			final Orders orders = Orders.dao.findFirst("select orders_id,course_id,user_id,course_user_id,session_rate,order_number,take_partner_num,surcharge_for_each_cash,go_door_traffic_cost,buy_amount,first_joint_fee,discount_price,my_coupon_money,total_session_rate,original_total_session_rate,order_number,leave_message,booking_status,status,post_time from orders where orders_id=?",orders_id);
			if (orders!=null) {
				int hasnone_booking_course=0;
				int buy_amount = orders.get(orders.buy_amount);
				int course_id = orders.get(orders.course_id);
				Course course = Course.dao.findFirst(sqlString+" from course where course_id=?",course_id);
				String course_image_01 = course.getStr(course.user_images_01);
				String course_nickname ="",course_telephone="",course_email="";
				int course_is_official= 0;
				if (course!=null) {
					//教练
					int course_user_id = orders.get(orders.course_user_id);
					User courseUser = User.dao.findById(course_user_id);
					if (courseUser!=null) {
						course_is_official = courseUser.get(courseUser.is_official);
						course_nickname = courseUser.getStr(courseUser.nickname);
						course_telephone = courseUser.getStr(courseUser.telephone);
						course_email = courseUser.getStr(courseUser.email);
					}
					course.put("course_email", course_email);
					course.put("course_is_official", course_is_official);
					course.put("coach_image", course_image_01);
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
					//评论总数
					Comment comment = Comment.dao.findFirst("select count(course_id) as total_coment_num from comment where type=1 and course_id=? and status!=0",course_id);
					long total_coment_num = 0;
					if (comment!=null) {
						total_coment_num = comment.getLong("total_coment_num");
					}
					course.put("total_coment_num", total_coment_num);
					///课程有空时间
					String freeCourseDay = ScheduleBean2.freeCourseDay(course_id);
					course.put("freeCourseDay", freeCourseDay);
				}
				orders.put("Course", course);
				//时间表--- 未选择时间的课程数
				int refund_num = 0;
				List<OrdersSchedule> oSs = OrdersSchedule.dao.find("select orders_schedule_id,schedule_data,schedule_hours,refund_status,status from orders_schedule where orders_id=? order by schedule_data desc",orders_id);
				for(OrdersSchedule oSchedule:oSs){
					int oSchedule_status = oSchedule.get(oSchedule.status);
					int oSchedule_refund_status = oSchedule.get(oSchedule.refund_status);
					if ((oSchedule_status==4 && oSchedule_refund_status==10) || (oSchedule_status==21 && oSchedule_refund_status==10)) {
						hasnone_booking_course = hasnone_booking_course+1;
					}
					if (oSchedule_refund_status==10) {
						refund_num = refund_num+1;
					}
				}
				int orders_status = orders.get(orders.status);
				int user_id = orders.get(orders.user_id);
				if (orders_status==21||orders_status==22) {
					if (refund_num>0) {
						orders.put("status", 21);
					}else {
						orders.put("status", 22);
					}
				}
				orders.put("OrdersSchedules", oSs);
				course.put("hasnone_booking_course", hasnone_booking_course);
			}
			responseValues.put("Orders", orders);
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "Data request successful.");
			setAttr("OrderInfoResponse", responseValues);
			renderMultiJson("OrderInfoResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("OrdersManage/orderInfo", "orderInfo", this);
		}
	}
	/**
	 *  实付金额-服务费-（未退款课程数*原单价） 
	*/
	@Author("cluo")
	@Rp("退款申请")
	@Explaination(info = "退款申请列表--显示按钮状态【refund_status=10{可以申请退款}refund_status=2{退款中}refund_status=30{教练申请退款}refund_status=31{退款完成}refund_status=4{不能退款}】[money计算公式：实付金额-服务费-（未退款课程数*原单价）{总课程数-未选课程数} ")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.orders_id)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	//
	@ReturnDBParam(name = "RefundListResponse{refundList:Refunds:list[OrdersSchedule:$]}", column = OrdersSchedule.orders_schedule_id)
	@ReturnDBParam(name = "RefundListResponse{refundList:Refunds:list[OrdersSchedule:$]}", column = OrdersSchedule.orders_id)
	@ReturnDBParam(name = "RefundListResponse{refundList:Refunds:list[OrdersSchedule:$]}", column = OrdersSchedule.schedule_data)
	@ReturnDBParam(name = "RefundListResponse{refundList:Refunds:list[OrdersSchedule:$]}", column = OrdersSchedule.schedule_hours)
	@ReturnDBParam(name = "RefundListResponse{refundList:Refunds:list[OrdersSchedule:$]}", column = OrdersSchedule.refund_status)
	@ReturnDBParam(name = "RefundListResponse{refundList:Refunds:list[OrdersSchedule:$]}", column = OrdersSchedule.status)
	// 
	@ReturnDBParam(name = "RefundListResponse{Orders:$}", column = Orders.orders_id)
	@ReturnDBParam(name = "RefundListResponse{Orders:$}", column = Orders.buy_amount)
	@ReturnDBParam(name = "RefundListResponse{Orders:$}", column = Orders.total_session_rate)
	@ReturnDBParam(name = "RefundListResponse{Orders:$}", column = Orders.session_rate)
	@ReturnDBParam(name = "RefundListResponse{Orders:$}", column = Orders.first_joint_fee)
	@ReturnDBParam(name = "RefundListResponse{Orders:$}", column = Orders.go_door_traffic_cost)
	@ReturnDBParam(name = "RefundListResponse{Orders:$}", column = Orders.my_coupon_money)
	@ReturnDBParam(name = "RefundListResponse{Orders:$}", column = Orders.discount_price)
	@ReturnDBParam(name = "RefundListResponse{Orders:$}", column = Orders.take_partner_num)
	@ReturnDBParam(name = "RefundListResponse{Orders:$}", column = Orders.surcharge_for_each_cash)
	@ReturnDBParam(name = "RefundListResponse{Orders:$}", column = Orders.original_total_session_rate)
	
	@ReturnOutlet(name = "RefundListResponse{hasnone_booking_course}", remarks = "未选择时间的课程数", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "RefundListResponse{can_refund_num}", remarks = "可以退款数量", dataType = DataType.String, defaultValue = "")
	
	@ReturnOutlet(name = "RefundListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "RefundListResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "RefundListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void refundList() {
		try {
			String token = getPara("token");
			String orders_id = getPara("orders_id");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("orders_id", orders_id);
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
				setAttr("RefundListResponse", response2);
				renderMultiJson("RefundListResponse");
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
				setAttr("RefundListResponse", response2);
				renderMultiJson("RefundListResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="Please wait.";
			final List<OrdersSchedule> oSchedules = OrdersSchedule.dao.find("select orders_schedule_id,orders_id,schedule_data,schedule_hours,schedule_ids,refund_status,status from orders_schedule where status!=1 and user_id="+user_id+" and orders_id="+orders_id+" order by schedule_data desc");
			int can_refund_num = 0,hasnone_booking_course=0;
			for(OrdersSchedule oSchedule:oSchedules){
				int refund_status = oSchedule.get(oSchedule.refund_status);
				if (refund_status==10 ) {
					//可以退款数
					can_refund_num = can_refund_num+1;
				}
				int orders_status = oSchedule.get(oSchedule.status);
				if ((orders_status==4 && refund_status==10) || (orders_status==21 && refund_status==10)) {
					//未选择时间的课程数
					hasnone_booking_course = hasnone_booking_course+1;
				}
			}
			final Orders orders = Orders.dao.findFirst("select orders_id,buy_amount,total_session_rate,session_rate,first_joint_fee,go_door_traffic_cost,my_coupon_money,discount_price,take_partner_num,surcharge_for_each_cash,original_total_session_rate from orders where orders_id=?",orders_id);
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("Result", new HashMap<String, Object>() {
				{
					put("Refunds", oSchedules);
					put("Orders", orders);
				}
			});
			responseValues.put("hasnone_booking_course", hasnone_booking_course);
			responseValues.put("can_refund_num", can_refund_num);
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "Data request successful.");
			setAttr("RefundListResponse", responseValues);
			renderMultiJson("RefundListResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("OrdersManage/refundList", "退款申请列表", this);
		}
	}
	@Author("cluo")
	@Rp("退款申请")
	@Explaination(info = "提交退款申请")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.orders_id)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.refund_success_money)
	@URLParam(defaultValue = "", explain = "多退款订单同时申请【11#22#1#23】，申请一个就传一个值", type = Type.String, name = "orders_schedule_ids")
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "RefundOrdersResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "RefundOrdersResponse{status}", remarks = "0-失败，1-操作成功，2-不存在", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "RefundOrdersResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void refundOrders() {
		try {
			String token = getPara("token");
			String orders_id = getPara("orders_id");
			String orders_schedule_ids = getPara("orders_schedule_ids");
			double refund_success_money = Double.parseDouble(getPara("refund_success_money"));
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("orders_id", orders_id);
			packageParams.put("orders_schedule_ids", orders_schedule_ids);
			packageParams.put("refund_success_money", refund_success_money+"");
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
				setAttr("RefundOrdersResponse", response2);
				renderMultiJson("RefundOrdersResponse");
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
				setAttr("RefundOrdersResponse", response2);
				renderMultiJson("RefundOrdersResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="Refund application unsuccessful. Please check for errors.";
			if (orders_schedule_ids==null) {
				status = 2;
				message="Please select the refund order.";
			}
			if (status==0) {
				boolean delete = true;
				orders_schedule_ids = orders_schedule_ids.trim();
				String[] orders_schedule_id_array = orders_schedule_ids.split("#");
				for(int i=0;i<orders_schedule_id_array.length;i++){
					String orders_schedule_id = orders_schedule_id_array[i];
					OrdersSchedule ordersSchedule2 = OrdersSchedule.dao.findById(orders_schedule_id);
					if (ordersSchedule2!=null) {
						delete = ordersSchedule2.set(ordersSchedule2.refund_status, 2)
								.set(ordersSchedule2.finish_status, 2)
								.update();
					}
					if (delete) {
						status = 1;
						message="Your refund has been approved.";
						//时间表管理
						String schedule_ids = ordersSchedule2.getStr(ordersSchedule2.schedule_ids);
						if (!schedule_ids.equals("0")) {
							String[] schedule_ids_array = schedule_ids.split("#");
							for(int j=0;j<schedule_ids_array.length;j++){
								String schedule_id = schedule_ids_array[j];
								Schedule schedule = Schedule.dao.findById(schedule_id);
								if (schedule!=null) {
									schedule.delete();
								}
							}
						}
					}else {
						status = 0;
						message="Refund application unsuccessful. Please check for errors.";
						break;
					}
				}
				if (delete&&status==1) {
					Orders orders = Orders.dao.findById(orders_id);
					if (orders!=null) {
						int course_user_id = orders.get(orders.course_user_id);
						String order_number = orders.getStr(orders.order_number);
						String buy_nickname = orders.getStr(orders.buy_nickname);
						String course_email = orders.getStr(orders.course_email);
						double total_refund_success_money = orders.getDouble(orders.refund_success_money);
						boolean update2 = orders.set(orders.refund_success_money, (total_refund_success_money+refund_success_money))
								.set(orders.has_refund_status,2)
								.update();
						if (update2) {
							//邮件通知系统 -教练
							EmailUntil.sendEmailRefundMoney(order_number,course_user_id,buy_nickname,course_email);
							//判断用户是否还有未退款课程
							OrdersSchedule oSchedules = OrdersSchedule.dao.findFirst("select count(orders_schedule_id) as can_refund_num  from orders_schedule where refund_status=10 and orders_id=? and user_id=?",orders_id,user_id);
							if (oSchedules!=null) {
								long can_refund_num = oSchedules.getLong("can_refund_num");
								if (can_refund_num==0) {
									orders.set(orders.status, 22).set(orders.booking_status, 2).update();
								}
							}
						}
					}
				}
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("RefundOrdersResponse", responseValues);
			renderMultiJson("RefundOrdersResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("OrdersManage/refundOrders", "退款未付款订单", this);
		}
	}
	@Author("cluo")
	@Rp("订单管理、我的订单、购物车")
	@Explaination(info = "删除订单或購物車，失效產品")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "{1、2}", explain = "1-用户操作，2-教练操作", type = Type.String, name = "delete_type")
	@URLParam(defaultValue = "", explain = "多订单同时删除【11#22#1#23】，如果删除一个就传一个值", type = Type.String, name = "orders_ids")
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "DeletePayOrderResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "DeletePayOrderResponse{status}", remarks = "0-失败，1-操作成功，2-不存在", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "DeletePayOrderResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void deletePayOrder() {
		try {
			String token = getPara("token");
			int delete_type = getParaToInt("delete_type",1);
			String orders_ids = getPara("orders_ids");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("delete_type", delete_type+"");
			packageParams.put("orders_ids", orders_ids);
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
				setAttr("DeletePayOrderResponse", response2);
				renderMultiJson("DeletePayOrderResponse");
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
				setAttr("DeletePayOrderResponse", response2);
				renderMultiJson("DeletePayOrderResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="Deletion failed. Please check for errors.";
			if (orders_ids==null) {
				status = 2;
				message="Please select the order you want to delete.";
			}
			if (status==0) {
				boolean delete = true;
				orders_ids = orders_ids.trim();
				String[] orders_id_array = orders_ids.split("#");
				for(int i=0;i<orders_id_array.length;i++){
					String orders_id = orders_id_array[i];
					Orders orders = Orders.dao.findById(orders_id);
					if (orders!=null) {
						int orders_status = orders.get(orders.status);
						int my_coupon_id = orders.get(orders.my_coupon_id);
						if (delete_type==1) {
							if (orders_status==1||orders_status==2||orders_status==10||orders_status==11) {
								delete = delete = orders.set(orders.status, 9).update();
								if (delete) {
									//删除时间表OrdersSchedule
									List<OrdersSchedule> oSchedules = OrdersSchedule.dao.find("select orders_schedule_id from orders_schedule where orders_id=?",orders_id);
									for(OrdersSchedule oSchedule:oSchedules){
										oSchedule.delete();
									}
									List<Schedule> schedules = Schedule.dao.find("select schedule_id from schedule where orders_id=?",orders_id);
									for(Schedule schedule:schedules){
										schedule.delete();
									}
								}
							}
							if (orders_status==30||orders_status==40) {
								delete = orders.set(orders.status, 12).update();
							}
						}
						if (delete_type==2) {
							delete = orders.set(orders.status, 20).update();
						}
						if (delete) {
							status = 1;
							message="Order Deleted.";
							//优惠券使用
							if (my_coupon_id!=0) {
								MyCoupon myCoupon = MyCoupon.dao.findById(my_coupon_id);
								if (myCoupon!=null) {
									myCoupon.set(myCoupon.status, 1)
											.set(myCoupon.post_time, DateUtils.getCurrentDateTime())
											.update();
								}
							}
						}else {
							status = 0;
							message="Deletion failed. Please check for errors.";
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
			setAttr("DeletePayOrderResponse", responseValues);
			renderMultiJson("DeletePayOrderResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("OrdersManage/deletePayOrder", "删除付款订单", this);
		}
	}
	@Author("cluo")
	@Rp("支付")
	@Explaination(info = "支付金額")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = "多订单ID同时支付【11A22A1A23】，支付一个就传一个值", type = Type.String, name = "orders_ids")
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "PayMoneyResponse{course_titles}", remarks = "课程标题", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "PayMoneyResponse{total_total_session_rate}", remarks = "实际支付价格", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "PayMoneyResponse{total_original_total_session_rate}", remarks = "原价", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "PayMoneyResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "PayMoneyResponse{status}", remarks = "0-失败，1-操作成功，2-不存在", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "PayMoneyResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void payMoney() {
		try {
			String token = getPara("token");
			String orders_ids = getPara("orders_ids");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("orders_ids", orders_ids);
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
				setAttr("PayMoneyResponse", response2);
				renderMultiJson("PayMoneyResponse");
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
				setAttr("PayMoneyResponse", response2);
				renderMultiJson("PayMoneyResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 1;String message="Data request successful.";
			if (orders_ids==null||orders_ids.equals("")) {
				status = 2;
				message="Please select the order you want to pay";
			}
			double total_total_session_rate = 0,total_original_total_session_rate = 0;
			String course_titles = "";
			if (status==1) {
				orders_ids = orders_ids.trim();
				String[] orders_id_array = orders_ids.split("A");
				for(int i=0;i<orders_id_array.length;i++){
					String orders_id = orders_id_array[i];
					Orders orders = Orders.dao.findById(orders_id);
					if (orders!=null) {
						String course_title = orders.getStr(orders.course_title);
						double total_session_rate = orders.getDouble(orders.total_session_rate);
						double original_total_session_rate = orders.getDouble(orders.original_total_session_rate);
						total_total_session_rate = total_total_session_rate+total_session_rate;
						total_original_total_session_rate = total_original_total_session_rate+original_total_session_rate;
						course_titles += course_title+"、";
					}
				}
			}
			if (!course_titles.equals("")) {
				course_titles = course_titles.substring(0, course_titles.length()-1);
			}
			DecimalFormat df=new DecimalFormat(".##");
			total_total_session_rate=Double.parseDouble(df.format(total_total_session_rate));
			total_original_total_session_rate=Double.parseDouble(df.format(total_original_total_session_rate));
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("course_titles", course_titles);
			responseValues.put("total_total_session_rate", total_total_session_rate);
			responseValues.put("total_original_total_session_rate", total_original_total_session_rate);
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("PayMoneyResponse", responseValues);
			renderMultiJson("PayMoneyResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("OrdersManage/payMoney", "付款订单", this);
		}
	}
	@Author("cluo")
	@Rp("支付")
	@Explaination(info = "确认支付")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = "付款总额", type = Type.String, name = ChargeLog.total_money)
	@URLParam(defaultValue = "", explain = "多订单ID同时支付【11A22A1A23】A分割，支付一个就传一个值", type = Type.String, name = "orders_ids")
	@URLParam(defaultValue = "{1、2、3}", explain = "1-支付宝，2-visa，3-paypal", type = Type.Int, name = "pay_type")
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "ComfiyPayResponse{charge_log_id}", remarks = "付款Id", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "ComfiyPayResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "ComfiyPayResponse{status}", remarks = "-1-交易密码为空，1-支付成功,2-资金不足,3-账户异常锁定,4-交易密码不正确,5-任务异常锁定", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "ComfiyPayResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void comfiyPay() {
		try {
			String token = getPara("token");
			String orders_ids = getPara("orders_ids");
			String total_money_str = getPara("total_money");
			int pay_type = getParaToInt("pay_type");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("orders_ids", orders_ids);
			packageParams.put("pay_type", pay_type+"");
			packageParams.put("token", token);
			packageParams.put("total_money", total_money_str);
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
				setAttr("ComfiyPayResponse", response2);
				renderMultiJson("ComfiyPayResponse");
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
				setAttr("ComfiyPayResponse", response2);
				renderMultiJson("ComfiyPayResponse");
				return;
			}
			double total_money = Double.parseDouble(total_money_str);
			String user_id = AppToken.getUserId(token, this,invoke);
			ResponseValues response2 = new ResponseValues(this, Thread
					.currentThread().getStackTrace()[1].getMethodName());
			int status = 0;String message="支付失败";
			if (orders_ids==null||orders_ids.equals("")) {
				status = 2;
				message="Please select the order you want to pay";
			}
			if (status==0) {
				/**
				 * log
				 */
				AppLog.info("充值-生成支付订单", getRequest());
				/**
				 * end
				 */
				ChargeLog charge = new ChargeLog();
				int charge_log_id = 0;
				ChargeLog chargeLog = ChargeLog.dao.findFirst("select charge_log_id from charge_log where orders_id=? and user_id=? and is_pay=0",orders_ids,user_id);
				DecimalFormat df=new DecimalFormat(".##");
				double dd_pay_total_money=Double.parseDouble(df.format(total_money));
				if (chargeLog!=null) {
					charge_log_id = chargeLog.get(chargeLog.charge_log_id);
					chargeLog.set(chargeLog.orders_id, orders_ids)
							 .set(chargeLog.user_id, user_id)
							 .set(chargeLog.type, PayTypeUntil.payMap.get(String.valueOf(pay_type)))
							 .set(chargeLog.total_money, dd_pay_total_money)
							 .set(chargeLog.post_time, DateUtils.getCurrentDateTime())
							 .update();
				}else {
					charge.set(charge.orders_id, orders_ids)
						 .set(charge.user_id, user_id)
						 .set(charge.type, PayTypeUntil.payMap.get(String.valueOf(pay_type)))
						 .set(charge.pay_id, "")
						 .set(charge.is_pay, 0)
						 .set(charge.total_money, dd_pay_total_money)
						 .set(charge.post_time, DateUtils.getCurrentDateTime())
						 .save();
					charge_log_id = charge.get("charge_log_id");
				}
				chargeLog = ChargeLog.dao.findById(charge_log_id);
				Map<String, String> sParaTemp = new HashMap<String, String>();
				if (pay_type==1) {
					//支付宝支付 
					if (invoke.equals("app")) {
						response2.put("status", 1);
					}
				}
				if (pay_type==2) {
					response2.put("status", 1);
				}
				if (pay_type==3) {
					response2.put("status", 1);
				}
				response2.put("charge_log_id",  WXUtil.getTimeStamp()+charge_log_id);
			}
			response2.put("orders_ids", orders_ids);
			response2.put("message", "Data request successful.");
			response2.put("code", 200);
			setAttr("ComfiyPayResponse", response2);
			renderMultiJson("ComfiyPayResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("OrdersManage/comfiyPay", "付款", this);
		}
	}
	@Author("cluo")
	@Rp("支付")
	@Explaination(info = "更新支付信息常量")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = "多订单ID同时支付【11A22A1A23】，支付一个就传一个值", type = Type.String, name = "orders_ids")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.first_name)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.last_name)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.street)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.city_town)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.state)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.zip_code)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.phone_number)
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "UpdateOrdersInfoResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "UpdateOrdersInfoResponse{status}", remarks = "0-失败，1-操作成功，2-不存在", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "UpdateOrdersInfoResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void updateOrdersInfo() {
		try {
			String token = getPara("token");
			String orders_ids = getPara("orders_ids","");
			String first_name = getPara("first_name","");
			String last_name = getPara("last_name","");
			String street = getPara("street","");
			String city_town = getPara("city_town","");
			String state = getPara("state","");
			String zip_code = getPara("zip_code","");
			String phone_number = getPara("phone","");
			String invoke = getPara("invoke","app");
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("UpdateOrdersInfoResponse", response2);
				renderMultiJson("UpdateOrdersInfoResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 1;String message="Data request successful.";
			if (orders_ids.equals("")) {
				status = 2;
				message="Please select the order you want to pay";
			}
			if (status==1) {
				orders_ids = orders_ids.trim();
				first_name = first_name.trim();
				last_name = last_name.trim();
				street = street.trim();
				city_town = city_town.trim();
				state = state.trim();
				zip_code = zip_code.trim();
				phone_number = phone_number.trim();
				String[] orders_id_array = orders_ids.split("A");
				for(int i=0;i<orders_id_array.length;i++){
					String orders_id = orders_id_array[i];
					Orders orders = Orders.dao.findById(orders_id);
					if (orders!=null) {
						orders.set(orders.first_name, first_name)
								.set(orders.last_name, last_name)
								.set(orders.street, street)
								.set(orders.city_town, city_town)
								.set(orders.state, state)
								.set(orders.zip_code, zip_code)
								.set(orders.phone_number, phone_number)
								.update();
					}
				}
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("UpdateOrdersInfoResponse", responseValues);
			renderMultiJson("UpdateOrdersInfoResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("OrdersManage/updateOrdersInfo", "", this);
		}
	}
}
