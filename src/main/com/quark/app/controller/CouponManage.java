/**
 * 
 */
package com.quark.app.controller;

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
import com.quark.model.extend.Comment;
import com.quark.model.extend.Coupon;
import com.quark.model.extend.Course;
import com.quark.model.extend.CourseCertification;
import com.quark.model.extend.Experience;
import com.quark.model.extend.MyCoupon;
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
 * 优惠券管理
 */
@Before(Tx.class)
public class CouponManage extends Controller {

	@Author("cluo")
	@Rp("我的优惠券")
	@Explaination(info = "我的优惠券列表")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "{-1、1}", explain = "-1過期，1-正常", type = Type.String, name = "type")
	@URLParam(defaultValue = "1", explain = Value.Infer, type = Type.String, name = "pn")
	@URLParam(defaultValue = "5", explain = Value.Infer, type = Type.String, name = "page_size")
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	// 返回信息
	// page property
	@ReturnOutlet(name = "MyCouponListResponse{myCouponListResult:MyCoupons:pageNumber}", remarks = "page number", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "MyCouponListResponse{myCouponListResult:MyCoupons:pageSize}", remarks = "result amount of this page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "MyCouponListResponse{myCouponListResult:MyCoupons:totalPage}", remarks = "total page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "MyCouponListResponse{myCouponListResult:MyCoupons:totalRow}", remarks = "total row", dataType = DataType.Int, defaultValue = "")
	//
	@ReturnDBParam(name = "MyCouponListResponse{myCouponListResult:MyCoupons:list[MyCoupon:$]}", column = MyCoupon.my_coupon_id) 
	@ReturnDBParam(name = "MyCouponListResponse{myCouponListResult:MyCoupons:list[MyCoupon:$]}", column = MyCoupon.provider)
	@ReturnDBParam(name = "MyCouponListResponse{myCouponListResult:MyCoupons:list[MyCoupon:$]}", column = MyCoupon.coupon_name)
	@ReturnDBParam(name = "MyCouponListResponse{myCouponListResult:MyCoupons:list[MyCoupon:$]}", column = MyCoupon.coupon_money)
	@ReturnDBParam(name = "MyCouponListResponse{myCouponListResult:MyCoupons:list[MyCoupon:$]}", column = MyCoupon.coupon_num)
	@ReturnDBParam(name = "MyCouponListResponse{myCouponListResult:MyCoupons:list[MyCoupon:$]}", column = MyCoupon.begin_time)
	@ReturnDBParam(name = "MyCouponListResponse{myCouponListResult:MyCoupons:list[MyCoupon:$]}", column = MyCoupon.end_time)
	@ReturnDBParam(name = "MyCouponListResponse{myCouponListResult:MyCoupons:list[MyCoupon:$]}", column = MyCoupon.coupon_rule)
	@ReturnDBParam(name = "MyCouponListResponse{myCouponListResult:MyCoupons:list[MyCoupon:$]}", column = MyCoupon.post_time)
	@ReturnOutlet(name = "MyCouponListResponse{myCouponListResult:MyCoupons:list[MyCoupon:total_coupon_money]}", remarks = "优惠券价格", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "MyCouponListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "MyCouponListResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "MyCouponListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void myCouponList() {
		try {
			String token = getPara("token");
			int type = getParaToInt("type", 1);
			int pn = getParaToInt("pn", 1);
			int page_size = getParaToInt("page_size", 5);
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("page_size", page_size+"");
			packageParams.put("pn", pn+"");
			packageParams.put("token", token);
			packageParams.put("type", type+"");
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
				setAttr("MyCouponListResponse", response2);
				renderMultiJson("MyCouponListResponse");
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
				setAttr("MyCouponListResponse", response2);
				renderMultiJson("MyCouponListResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			String filter_sql =" status=1 ";
			if (type==-1) {
				filter_sql =" status=-1 ";
			}
			String select = "select my_coupon_id,provider,coupon_name,coupon_money,count(coupon_number) as my_coupon_num,coupon_rule,begin_time,end_time,post_time  ";
			final Page<MyCoupon> mycouponPage = MyCoupon.dao
					.paginate(//coupon_num
							pn,
							page_size,
							select," from my_coupon where "+filter_sql+" and user_id=?  group by coupon_number order by post_time desc",user_id);
			for(MyCoupon myCoupon:mycouponPage.getList()){
				double coupon_money = myCoupon.getDouble(myCoupon.coupon_money);
				long my_coupon_num = myCoupon.get("my_coupon_num");
				double total_coupon_money=coupon_money*my_coupon_num;
				myCoupon.put("coupon_num", my_coupon_num);
				myCoupon.put("total_coupon_money", total_coupon_money);
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("Result", new HashMap<String, Object>() {
				{
					put("MyCoupons", mycouponPage);
				}
			});
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "Data request successful.");
			setAttr("MyCouponListResponse", responseValues);
			renderMultiJson("MyCouponListResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("CouponManage/myCouponList", "我的优惠券列表", this);
		}
	}
	@Author("cluo")
	@Rp("我的优惠券-详情")
	@Explaination(info = "我的优惠券-详情")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "{select my_coupon_id from my_coupon}", explain = Value.Infer, type = Type.String, name = MyCoupon.my_coupon_id)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnDBParam(name = "MyCouponInfoResponse{MyCoupon:$}", column = MyCoupon.my_coupon_id)
	@ReturnDBParam(name = "MyCouponInfoResponse{MyCoupon:$}", column = MyCoupon.provider)
	@ReturnDBParam(name = "MyCouponInfoResponse{MyCoupon:$}", column = MyCoupon.coupon_money)
	@ReturnDBParam(name = "MyCouponInfoResponse{MyCoupon:$}", column = MyCoupon.coupon_num)
	@ReturnDBParam(name = "MyCouponInfoResponse{MyCoupon:$}", column = MyCoupon.begin_time)
	@ReturnDBParam(name = "MyCouponInfoResponse{MyCoupon:$}", column = MyCoupon.end_time)
	@ReturnDBParam(name = "MyCouponInfoResponse{MyCoupon:$}", column = MyCoupon.coupon_rule)
	@ReturnOutlet(name = "MyCouponInfoResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "MyCouponInfoResponse{status}", remarks = "0-失败，1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "MyCouponInfoResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void myCouponInfo() {
		try {
			String token = getPara("token");
			String my_coupon_id = getPara("my_coupon_id");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("my_coupon_id", my_coupon_id);
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
				setAttr("MyCouponInfoResponse", response2);
				renderMultiJson("MyCouponInfoResponse");
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
				setAttr("MyCouponInfoResponse", response2);
				renderMultiJson("MyCouponInfoResponse");
				return;
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			MyCoupon myCoupon = MyCoupon.dao.findFirst("select my_coupon_id,provider,coupon_money,coupon_num,begin_time,end_time,coupon_rule from my_coupon where my_coupon_id=?",my_coupon_id);
			responseValues.put("MyCoupon", myCoupon);
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "Data request successful.");
			setAttr("MyCouponInfoResponse", responseValues);
			renderMultiJson("MyCouponInfoResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("CouponManage/myCouponInfo", "myCouponInfo", this);
		}
	}
	@Author("cluo")
	@Rp("我的优惠券")
	@Explaination(info = "删除")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = MyCoupon.my_coupon_id)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "DeteleMyCouponResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "DeteleMyCouponResponse{status}", remarks = "0-失败，1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "DeteleMyCouponResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void deteleMyCoupon() {
		try {
			String token = getPara("token");
			String my_coupon_id = getPara("my_coupon_id");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("my_coupon_id", my_coupon_id);
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
				setAttr("DeteleMyCouponResponse", response2);
				renderMultiJson("DeteleMyCouponResponse");
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
				setAttr("DeteleMyCouponResponse", response2);
				renderMultiJson("DeteleMyCouponResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			MyCoupon myCoupon = MyCoupon.dao.findById(my_coupon_id);
			int status=0;
			String message ="Deletion failed. Please check for errors.";
			if (myCoupon!=null) {
				boolean update = myCoupon.set(myCoupon.status, 0).update();
				if (update) {
					status=1;
					message ="Deletion successful.";
				}
			}else {
				status=2;
				message ="Invalid coupon code.";
			}
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("DeteleMyCouponResponse", responseValues);
			renderMultiJson("DeteleMyCouponResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("CouponManage/deleteMyCoupon", "myCouponInfo", this);
		}
	}
	@Author("cluo")
	@Rp("我的优惠券")
	@Explaination(info = "获取优惠券")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Coupon.number)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "GetCouponResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "GetCouponResponse{status}", remarks = "0-领取失败，1-操作成功,2-please Enter your coupon number,3-优惠券不存在，请检查,4-已被领取完,5-你已领取完，每个人最多能领取", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "GetCouponResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void getCoupon() {
		try {
			String token = getPara("token");
			String number = getPara("number");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("number", number);
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
				setAttr("GetCouponResponse", response2);
				renderMultiJson("GetCouponResponse");
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
				setAttr("GetCouponResponse", response2);
				renderMultiJson("GetCouponResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			int status=0;
			String message ="领取失败";
			if (number==null||number.equals("")) {
				status=2;
				message ="please Enter your coupon number ";
			}
			if (status==0) {
				number = number.trim();
				Coupon coupon = Coupon.dao.findFirst("select * from coupon where status=1 and coupon_type=2 and number='"+number+"'");
				if (coupon!=null) {
					String coupon_number = coupon.getStr(coupon.coupon_number);
					int num_coupon_amount = coupon.get(coupon.num_coupon_amount);
					int limit_num = coupon.get(coupon.limit_num);
					if (num_coupon_amount<1) {
						status=4;
						message ="This coupon is not available. Please contact the administrator.";
					}else {
						boolean update = false;
						List<MyCoupon> myCoupons = MyCoupon.dao.find("select my_coupon_id from my_coupon where user_id="+user_id+" and coupon_number='"+coupon_number+"'");
						if (myCoupons.size()<limit_num) {
							String provider = coupon.getStr(coupon.provider);
							String coupon_name = coupon.getStr(coupon.coupon_name);
							double coupon_money = coupon.getDouble(coupon.coupon_money);
							int usable_day = coupon.get(coupon.usable_day);
							String coupon_rule = coupon.getStr(coupon.coupon_rule);
							double consume_money = coupon.getDouble(coupon.consume_money);
							int category_01_id = coupon.get(coupon.category_01_id);
							int category_02_id = coupon.get(coupon.category_02_id);
							int is_seller = coupon.get(coupon.is_seller);
							int is_course = coupon.get(coupon.is_course);
							MyCoupon myCoupon = new MyCoupon();
							update = myCoupon.set(myCoupon.user_id, user_id)
								.set(myCoupon.user_id, user_id)
								.set(myCoupon.provider, provider)
								.set(myCoupon.coupon_name, coupon_name)
								.set(myCoupon.coupon_money, coupon_money)
								.set(myCoupon.coupon_num, 1)
								.set(myCoupon.coupon_number, coupon_number)
								.set(myCoupon.begin_time, DateUtils.getCurrentDateTime())
								.set(myCoupon.end_time, DateUtils.getAddDaysString(usable_day,DateUtils.getCurrentDateTime()))
								.set(myCoupon.coupon_rule, coupon_rule)
								.set(myCoupon.post_time, DateUtils.getCurrentDateTime())
								.set(myCoupon.consume_money, consume_money)
								.set(myCoupon.category_01_id, category_01_id)
								.set(myCoupon.category_02_id, category_02_id)
								.set(myCoupon.is_seller, is_seller)
								.set(myCoupon.is_course, is_course)
								.set(myCoupon.status, 1)
								.save();
							if (update) {
								status=1;
								message ="Coupon acquired.";
								coupon.set(coupon.num_coupon_amount, (num_coupon_amount-1)).update();
							}
						}else {
							status=5;
							message = "You already got this coupon.";
							//message ="你已领取完，每个人最多能领取"+myCoupons.size()+"张";
						}
					}
				}else {
					status=3;
					message ="Invalid coupon code.";
				}
			}
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("GetCouponResponse", responseValues);
			renderMultiJson("GetCouponResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("CouponManage/getCoupon", "myCouponInfo", this);
		}
	}
}
