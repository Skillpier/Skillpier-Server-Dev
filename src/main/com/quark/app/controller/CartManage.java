/**
 * 
 */
package com.quark.app.controller;

import java.text.DecimalFormat;
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
import com.quark.app.bean.CourseVedioBean;
import com.quark.app.bean.FileNameBean;
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
 * 课程管理
 */
@Before(Tx.class)
public class CartManage extends Controller {

	@Author("cluo")
	@Rp("购物车")
	@Explaination(info = "购物车列表[status=1已加入购物车，2-购物车的产品失效]")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "1", explain = Value.Infer, type = Type.String, name = "pn")
	@URLParam(defaultValue = "5", explain = Value.Infer, type = Type.String, name = "page_size")
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String_NotRequired, name = Course.latitude)
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String_NotRequired, name = Course.longitude)
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	// 返回信息
	// page property
	@ReturnOutlet(name = "MyCourseListResponse{myCourseListResult:Courses:pageNumber}", remarks = "page number", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "MyCourseListResponse{myCourseListResult:Courses:pageSize}", remarks = "result amount of this page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "MyCourseListResponse{myCourseListResult:Courses:totalPage}", remarks = "total page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "MyCourseListResponse{myCourseListResult:Courses:totalRow}", remarks = "total row", dataType = DataType.Int, defaultValue = "")
	//
	@ReturnDBParam(name = "MyCartListResponse{myCartListResult:Carts:list[Orders:$]}", column = Orders.orders_id)
	@ReturnDBParam(name = "MyCartListResponse{myCartListResult:Carts:list[Orders:$]}", column = Orders.course_id)
	@ReturnDBParam(name = "MyCartListResponse{myCartListResult:Carts:list[Orders:$]}", column = Orders.course_title)
	@ReturnDBParam(name = "MyCartListResponse{myCartListResult:Carts:list[Orders:$]}", column = Orders.course_user_id)
	@ReturnDBParam(name = "MyCartListResponse{myCartListResult:Carts:list[Orders:$]}", column = Orders.buy_amount)
	@ReturnDBParam(name = "MyCartListResponse{myCartListResult:Carts:list[Orders:$]}", column = Orders.session_rate)
	@ReturnDBParam(name = "MyCartListResponse{myCartListResult:Carts:list[Orders:$]}", column = Orders.total_session_rate)
	@ReturnDBParam(name = "MyCartListResponse{myCartListResult:Carts:list[Orders:$]}", column = Orders.original_total_session_rate)
	@ReturnDBParam(name = "MyCartListResponse{myCartListResult:Carts:list[Orders:$]}", column = Orders.first_joint_fee)
	@ReturnDBParam(name = "MyCartListResponse{myCartListResult:Carts:list[Orders:$]}", column = Orders.status)
	
	@ReturnDBParam(name = "MyCartListResponse{myCartListResult:Carts:list[Orders:Course:$]}", column = Course.course_id) 
	@ReturnDBParam(name = "MyCartListResponse{myCartListResult:Carts:list[Orders:Course:$]}", column = Course.user_id)   
	@ReturnDBParam(name = "MyCartListResponse{myCartListResult:Carts:list[Orders:Course:$]}", column = Course.title)
	@ReturnDBParam(name = "MyCartListResponse{myCartListResult:Carts:list[Orders:Course:$]}", column = Course.category_01_name)
	@ReturnDBParam(name = "MyCartListResponse{myCartListResult:Carts:list[Orders:Course:$]}", column = Course.category_02_name)
	@ReturnDBParam(name = "MyCartListResponse{myCartListResult:Carts:list[Orders:Course:$]}", column = Course.catetory_name)
	@ReturnDBParam(name = "MyCartListResponse{myCartListResult:Carts:list[Orders:Course:$]}", column = Course.session_rate)
	@ReturnDBParam(name = "MyCartListResponse{myCartListResult:Carts:list[Orders:Course:$]}", column = Course.total_score)
	@ReturnDBParam(name = "MyCartListResponse{myCartListResult:Carts:list[Orders:Course:$]}", column = Course.city)
	@ReturnDBParam(name = "MyCartListResponse{myCartListResult:Carts:list[Orders:Course:$]}", column = Course.area)
	@ReturnDBParam(name = "MyCartListResponse{myCartListResult:Carts:list[Orders:Course:$]}", column = Course.street)
	@ReturnDBParam(name = "MyCartListResponse{myCartListResult:Carts:list[Orders:Course:$]}", column = Course.address)
	@ReturnOutlet(name = "MyCartListResponse{myCartListResult:Carts:list[Orders:Course:distance]}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "MyCartListResponse{myCartListResult:Carts:list[Orders:is_official]}", remarks = "是否官方认证：0-否，1-是", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "MyCartListResponse{myCartListResult:Carts:list[Orders:coach_name]}", remarks = "教练名称", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "MyCartListResponse{myCartListResult:Carts:list[Orders:coach_image]}", remarks = "教练图片", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "MyCartListResponse{has_invalid_car_num}", remarks = "购物车失效产品数量,大于0出现一键删除", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "MyCartListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "MyCartListResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "MyCartListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void myCartList() {
		try {
			String token = getPara("token");
			int pn = getParaToInt("pn", 1);
			int page_size = getParaToInt("page_size", 5);
			String latitude = getPara("latitude","");
			String longitude = getPara("longitude","");
			String invoke = getPara("invoke","app");
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("MyCartListResponse", response2);
				renderMultiJson("MyCartListResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="";
			String filter_sql =" (status=1 or status=2) ";
			
			//SQL 中where条件不能用别名，只能 ("+distance_sql+")<500 这样用。
			String distance_sql= "round(6378.138*2*asin(sqrt(pow(sin( ("
					+ latitude
					+ "*pi()/180-latitude*pi()/180)/2),2)+cos("
					+ latitude
					+ "*pi()/180)*cos(latitude*pi()/180)* pow(sin( ("
					+ longitude
					+ "*pi()/180-longitude*pi()/180)/2),2)))*1000)";
			String select = "select orders_id,course_id,course_title,course_user_id,buy_amount,session_rate,first_joint_fee,total_session_rate,original_total_session_rate,status,post_time ";
			String select2 = "select course_id,user_id,user_images_01,title,category_01_name,category_02_name,catetory_name,session_rate,total_score,city,area,street,address ";
			if (!latitude.equals("")) {
				select2 = "select course_id,user_id,user_images_01,title,category_01_name,category_02_name,catetory_name,session_rate,total_score,city,area,street,address,latitude,longitude,"+distance_sql+" as distance ";
			}
			final Page<Orders> ordersPage = Orders.dao
					.paginate(pn,
							page_size,
							select," from orders where "+filter_sql+" and user_id=? order by post_time desc",user_id);
			int has_invalid_car_num = 0;
			for(Orders orders:ordersPage.getList()){
				//教练信息
				int course_user_id = orders.get(orders.course_user_id);
				User course_user = User.dao.findById(course_user_id);
				String coach_name="",coach_image="";
				int is_official=0;
				if (course_user!=null) {
					is_official = course_user.get(course_user.is_official);
					coach_name=course_user.getStr(course_user.nickname);
				}
				orders.put("is_official", is_official);
				orders.put("coach_name", coach_name);
				//课程id
				int course_id = orders.get(orders.course_id);
				Course course = Course.dao.findFirst(select2+" from course where course_id="+course_id);
				coach_image = course.getStr(course.user_images_01);
				orders.put("Course", course);
				orders.put("coach_image", coach_image);
				int orders_status = orders.get(orders.status);
				if (orders_status==2) {
					has_invalid_car_num = has_invalid_car_num+1;
				}
				if (!latitude.equals("")) {
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
				}else {
					course.put("distance", "Distance cannot be calculated.");
				}
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("has_invalid_car_num", has_invalid_car_num);
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("Result", new HashMap<String, Object>() {
				{
					put("Carts", ordersPage);
				}
			});
			responseValues.put("message", "Data request successful.");
			setAttr("MyCartListResponse", responseValues);
			renderMultiJson("MyCartListResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("CartManage/myCartList", "我的購物車列表", this);
		}
	}
	@Author("cluo")
	@Rp("购物车、我的订单")
	@Explaination(info = "一建删除失效產品")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "{1、2}", explain = "1-购物车失效，2-订单失效", type = Type.String, name = "delete_type")
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "AllDeleteOrderResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "AllDeleteOrderResponse{status}", remarks = "0-失败，1-操作成功，2-不存在", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "AllDeleteOrderResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void allDeleteOrder() {
		try {
			String token = getPara("token");
			int delete_type = getParaToInt("delete_type",1);
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("delete_type", delete_type+"");
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
				setAttr("AllDeleteOrderResponse", response2);
				renderMultiJson("AllDeleteOrderResponse");
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
				setAttr("AllDeleteOrderResponse", response2);
				renderMultiJson("AllDeleteOrderResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="请选择删除订单";
			String fiter_sql ="status=2";
			boolean delete = true;
			if (delete_type==2) {
				fiter_sql ="status=10";
			}
			List<Orders> ordersList = Orders.dao.find("select orders_id from orders where user_id=? and "+fiter_sql,user_id);
			for(Orders orders:ordersList){
				delete = orders.set(orders.status, 9).update();
				if (delete) {
					status = 1;
					message="Deletion successful.";
				}else {
					status = 0;
					message="Deletion failed. Please check for errors.";
					break;
				}
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("AllDeleteOrderResponse", responseValues);
			renderMultiJson("AllDeleteOrderResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("OrdersManage/allDeleteOrder", "一建删除失效產品", this);
		}
	}
}
