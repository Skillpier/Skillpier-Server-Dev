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
import com.quark.app.bean.EmailUntil;
import com.quark.app.bean.FileNameBean;
import com.quark.app.bean.PayTypeUntil;
import com.quark.app.bean.ScheduleBean2;
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
import com.quark.utils.MapDistance;
import com.quark.utils.MessageUtils;
import com.quark.utils.RandomUtils;
import com.quarkso.utils.ImgSavePathUtils;
import com.quarkso.utils.StringUtils;

/**
 * @author C罗
 * 课程管理
 */
@Before(Tx.class)
public class CourseManage extends Controller {

	@Author("cluo")
	@Rp("新建课程")
	@Explaination(info = "分类分类列表")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnDBParam(name = "CategoryListResponse{categoryListResult:Categorys:list[Category:$]}", column = Category01.category_01_id)
	@ReturnDBParam(name = "CategoryListResponse{categoryListResult:Categorys:list[Category:$]}", column = Category01.category_01_name)
	@ReturnDBParam(name = "CategoryListResponse{categoryListResult:Categorys:list[category02s:list[category02:$]]}", column = Category02.category_02_id)
	@ReturnDBParam(name = "CategoryListResponse{categoryListResult:Categorys:list[category02s:list[category02:$]]}", column = Category02.category_02_name)
	@ReturnOutlet(name = "CategoryListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CategoryListResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CategoryListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void categoryList() {
		try {
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			final List<Category01> category01s = Category01.dao.find("select category_01_id,category_01_name from category_01 where status=1 order by sort asc ,post_time desc");
			for(Category01 category01:category01s){
				int category_01_id = category01.get(category01.category_01_id);
				List<Category02> category02s = Category02.dao.find("select category_02_id,category_02_name from category_02 where category_01_id=? and status=1 order by sort asc ,post_time desc",category_01_id);
				category01.put("category02s", category02s);
			}
			responseValues.put("Result", new HashMap<String, Object>() {
				{
					put("Categorys", category01s);
				}
			});
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "成功");
			setAttr("CategoryListResponse", responseValues);
			renderMultiJson("CategoryListResponse");
			AppLog.info("", getRequest());
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("CourseManage/categoryList", "分类分类列表", this);
		}
	}
	@Author("cluo")
	@Rp("我是教练、课程管理")
	@Explaination(info = "新建课程时进入调用【不需要提示】")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.is_auth_public)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@ReturnOutlet(name = "newCoursePremissResponse{course_id}", remarks = "课程名称", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "newCoursePremissResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "newCoursePremissResponse{status}", remarks = "1-操作成功，2-上傳無null", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "newCoursePremissResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void newCoursePremiss() {
		try {
			String token = getPara("token");
			int is_auth_public = getParaToInt("is_auth_public",1);
			String invoke = getPara("invoke","app");
			if (!AppToken.check(token, this ,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this,
						Thread.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("newCoursePremissResponse", response2);
				renderMultiJson("newCoursePremissResponse");
				return;
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			String message="Please wait.";int status = 0;
			String user_id = AppToken.getUserId(token, this,invoke);
			int course_id = 0;
			if (is_auth_public==1) {
				Course course = Course.dao.findFirst("select course_id,is_auth_public from course where user_id=? and category_01_id=0 and category_02_id=0 and status=-1",user_id);
				if (course!=null) {
					course_id = course.get(course.course_id);
				}else {
					course = new Course();
					boolean save = course.set(course.user_id, user_id).set(course.status, -1)
							.set(course.is_auth_public, is_auth_public)
							.set(course.post_time, DateUtils.getCurrentDateTime())
							.save();
					if (save){
						course_id = course.get(course.course_id);
					}
				}
			}
			if (is_auth_public==2) {
				Course course = Course.dao.findFirst("select course_id,is_auth_public from course where user_id=? and is_auth_public=2 ",user_id);
				if (course!=null) {
					course_id = course.get(course.course_id);
				}else {
					course = new Course();
					boolean save = course.set(course.user_id, user_id).set(course.status, -1)
							.set(course.is_auth_public, is_auth_public)
							.set(course.post_time, DateUtils.getCurrentDateTime())
							.save();
					if (save){
						course_id = course.get(course.course_id);
					}
				}
			}
			if (course_id>0) {
				status = 1;
				message = "Upload successful";
			}
			responseValues.put("course_id", course_id);
			responseValues.put("status", status);
			responseValues.put("message", message);
			responseValues.put("code", 200);
			setAttr("newCoursePremissResponse", responseValues);
			renderMultiJson("newCoursePremissResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("CourseManage/newCoursePremiss", "进入", this);
		}
	}
	@Author("cluo")
	@Rp("新增证书")
	@Explaination(info = "新增证书")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = CourseCertification.course_id)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = CourseCertification.name)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = CourseCertification.get_time)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = CourseCertification.institue)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = "filename")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "AddCourseCertificationResponse{course_certification_id}", remarks = "ID", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "AddCourseCertificationResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "AddCourseCertificationResponse{status}", remarks = "0-失败，1-操作成功，2-请上传证书图片", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "AddCourseCertificationResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void addCourseCertification() {
		try {
			String token = getPara("token");
			String course_id = getPara("course_id");
			String name = getPara("name");
			String get_time = getPara("get_time");
			String institue = getPara("institue");
			String filename = getPara("filename","");
			String invoke = getPara("invoke","app");
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("AddCourseCertificationResponse", response2);
				renderMultiJson("AddCourseCertificationResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="Certificate created failed";
			int course_certification_id = 0;
			CourseCertification cf = new CourseCertification();
			if (!filename.equals("")){
				boolean save = cf.set(cf.course_id, course_id)
						.set(cf.name, name)
						.set(cf.image_01, filename)
						.set(cf.get_time, get_time)
						.set(cf.post_time, DateUtils.getCurrentDateTime())
						.set(cf.institue, institue)
						.save();
				if (save) {
					course_certification_id = cf.get("course_certification_id");
					status = 1;
					message="Certificate created";
				}
			}else {
				status = 2;
				message="Please upload your certification pictures.";
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("course_certification_id", course_certification_id);
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("AddCourseCertificationResponse", responseValues);
			renderMultiJson("AddCourseCertificationResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("CourseManage/addCourseCertification", "新增证书", this);
		}
	}
	@Author("cluo")
	@Rp("新增证书")
	@Explaination(info = "编辑证书")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = CourseCertification.course_certification_id)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = CourseCertification.name)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = CourseCertification.get_time)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = CourseCertification.institue)
	@URLParam(defaultValue = "", explain = "不修改圖片請上傳空的", type = Type.String, name = "filename")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "EditCourseCertificationResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "EditCourseCertificationResponse{status}", remarks = "0-失败，1-操作成功，2-请上传证书图片", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "EditCourseCertificationResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void editCourseCertification() {
		try {
			String token = getPara("token");
			String course_certification_id = getPara("course_certification_id");
			String name = getPara("name");
			String get_time = getPara("get_time");
			String institue = getPara("institue");
			String filename = getPara("filename","");
			String invoke = getPara("invoke","app");
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("EditCourseCertificationResponse", response2);
				renderMultiJson("EditCourseCertificationResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="Edit failed. Please check for errors.";
			CourseCertification cf = CourseCertification.dao.findById(course_certification_id);
			if (cf!=null) {
				if (filename != null&&!filename.equals("")){
					cf.set(cf.image_01, filename);
				}
				boolean update = cf.set(cf.name, name)
						.set(cf.get_time, get_time)
						.set(cf.institue, institue)
						.update();
				if (update) {
					status = 1;
					message="Edit successful.";
				}
			}else {
				status = 2;
				message="Certification does not exist. Please check for errors.";
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("EditCourseCertificationResponse", responseValues);
			renderMultiJson("EditCourseCertificationResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("CourseManage/editCourseCertification", "编辑证书", this);
		}
	}
	@Author("cluo")
	@Rp("新建课程")
	@Explaination(info = "证书详情")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = CourseCertification.course_certification_id)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnDBParam(name = "CourseCertification{courseCertificationInfoResult:CourseCertification:$}", column = CourseCertification.course_certification_id)
	@ReturnDBParam(name = "CourseCertification{courseCertificationInfoResult:CourseCertification:$}", column = CourseCertification.course_id)
	@ReturnDBParam(name = "CourseCertification{courseCertificationInfoResult:CourseCertification:$}", column = CourseCertification.name)
	@ReturnDBParam(name = "CourseCertification{courseCertificationInfoResult:CourseCertification:$}", column = CourseCertification.get_time)
	@ReturnDBParam(name = "CourseCertification{courseCertificationInfoResult:CourseCertification:$}", column = CourseCertification.institue)
	@ReturnDBParam(name = "CourseCertification{courseCertificationInfoResult:CourseCertification:$}", column = CourseCertification.image_01)
	@ReturnDBParam(name = "CourseCertification{courseCertificationInfoResult:CourseCertification:$}", column = CourseCertification.post_time)
	@ReturnOutlet(name = "CourseCertificationInfoResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CourseCertificationInfoResponse{status}", remarks = "0-失败，1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CourseCertificationInfoResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void courseCertificationInfo() {
		try {
			String token = getPara("token");
			String course_certification_id = getPara("course_certification_id");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("course_certification_id", course_certification_id);
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
				setAttr("CourseCertificationInfoResponse", response2);
				renderMultiJson("CourseCertificationInfoResponse");
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
				setAttr("CourseCertificationInfoResponse", response2);
				renderMultiJson("CourseCertificationInfoResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			final CourseCertification cf = CourseCertification.dao.findById(course_certification_id);
			responseValues.put("Result", new HashMap<String, Object>() {
				{
					put("CourseCertification", cf);
				}
			});
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "Data request successful.");
			setAttr("CourseCertificationInfoResponse", responseValues);
			renderMultiJson("CourseCertificationInfoResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("CourseManage/courseCertificationInfo", "证书info", this);
		}
	}
	@Author("cluo")
	@Rp("新建课程")
	@Explaination(info = "删除证书")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = CourseCertification.course_certification_id)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "DeleteCourseCertificationResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "DeleteCourseCertificationResponse{status}", remarks = "0-失败，1-操作成功，2-不存在", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "DeleteCourseCertificationResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void deleteCourseCertification() {
		try {
			String token = getPara("token");
			String course_certification_id = getPara("course_certification_id");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("course_certification_id", course_certification_id);
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
				setAttr("DeleteCourseCertificationResponse", response2);
				renderMultiJson("DeleteCourseCertificationResponse");
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
				setAttr("DeleteCourseCertificationResponse", response2);
				renderMultiJson("DeleteCourseCertificationResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="Deletion failed. Please check for errors.";
			CourseCertification cf = CourseCertification.dao.findById(course_certification_id);
			if (cf!=null) {
				String image_01 = cf.getStr(cf.image_01);
				boolean delete = cf.delete();
				if (delete) {
					status = 1;message="Deletion successful.";
					FileUtils.deleteFile(config.save_path+image_01);
				}
			}else {
				status = 2;
				message="Content does not exist. Please check for errors.";
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("DeleteCourseCertificationResponse", responseValues);
			renderMultiJson("DeleteCourseCertificationResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("CourseManage/deleteCourseCertification", "删除证书", this);
		}
	}
	@Author("cluo")
	@Rp("新建课程")
	@Explaination(info = "新建课程第二步")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.course_id)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.user_images_01)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.title)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.category_01_id)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.category_01_name)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.category_02_id)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.category_02_name)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.overview)
	@URLParam(defaultValue = "", explain = "拼接如：11.jpg#22.jpg#11.jpg#22.jpg【图片名称#图片名称】", type = Type.String, name = "fileName")
	@URLParam(defaultValue = "", explain = "拼接如：http://#http://#http://#http://【url名称#url名称】", type = Type.String, name = "vedioURL")
	@URLParam(defaultValue = "60", explain = Value.Infer, type = Type.Int, name = Course.session_length)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.session_rate)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.teaching_age)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.teaching_since)
	@URLParam(defaultValue = "{0、1}", explain = Value.Infer, type = Type.String, name = Course.travel_to_session)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String_NotRequired, name = Course.travel_to_session_distance)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String_NotRequired, name = Course.travel_to_session_trafic_surcharge)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.city)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.area)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.street)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.address)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.latitude)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.longitude)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.zipcode)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.additional_partner)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.surcharge_for_each)
	@URLParam(defaultValue = "{1、2}", explain = Value.Infer, type = Type.String, name = Course.discount_type)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.discount_onetion_pur_money_01)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.discount_price_01)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.discount_onetion_pur_money_02)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.discount_price_02)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.discount_onetion_pur_money_03)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.discount_price_03)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.achievements)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.specialist)
	@URLParam(defaultValue = "1@1#2@3#3@5", explain = "选择上课时间，拼接：1@1#3@2#5@3，如【@前面1-代表6:00-7:00，@后面代表星期1-星期天】。【@前面3-代表7:00-8:00，@后面3代表星期四】。。。。星期表示：1-星期天，2-星期一，时间段表示：1表示6:00-7:00，3表示7:00-8:00，5表示8:00-9:00。。1、3、5、7、9、11这样表示时间段。。。", type = Type.String_NotRequired, name = Course.hours)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "AddCourseResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "AddCourseResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "AddCourseResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void addCourse() {
		try {
			String token = getPara("token");
			String course_id = getPara("course_id");
			String user_images_01 = getPara("user_images_01","");
			String title = getPara("title","");
			String category_01_id = getPara("category_01_id","0");
			String category_01_name = getPara("category_01_name","");
			String category_02_id = getPara("category_02_id","0");
			String category_02_name = getPara("category_02_name","");
			String overview = getPara("overview","");
			String fileNames = getPara("fileName","");
			String vedioURLs = getPara("vedioURL","");
			String session_length = getPara("session_length");
			String session_rate = getPara("session_rate");
			String teaching_age = getPara("teaching_age","");
			String teaching_since = getPara("teaching_since","");
			int travel_to_session = getParaToInt("travel_to_session",0);
			int travel_to_session_distance = getParaToInt("travel_to_session_distance",0);
			String travel_to_session_trafic_surcharge = getPara("travel_to_session_trafic_surcharge","0");
			String city = getPara("city","");
			String area = getPara("area","");
			String street = getPara("street","");
			String address = getPara("address","");
			String latitude = getPara("latitude","");
			String longitude = getPara("longitude","");
			String zipcode = getPara("zipcode","");
			int additional_partner = getParaToInt("additional_partner",0);
			String surcharge_for_each_str = getPara("surcharge_for_each","0");
			int discount_type = getParaToInt("discount_type",0);
			String discount_onetion_pur_money_01 = getPara("discount_onetion_pur_money_01","0");
			String discount_price_01 = getPara("discount_price_01","0");
			String discount_onetion_pur_money_02 = getPara("discount_onetion_pur_money_02","0");
			String discount_price_02 = getPara("discount_price_02","0");
			String discount_onetion_pur_money_03 = getPara("discount_onetion_pur_money_03","0");
			String discount_price_03 = getPara("discount_price_03","0");
			String hours = getPara("hours","");
			String achievements = getPara("achievements","");
			String specialist = getPara("specialist","");
			String invoke = getPara("invoke","app");
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("AddCourseResponse", response2);
				renderMultiJson("AddCourseResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="Certificate created failed";
			if (user_images_01.equals("")){
				status = 2;
				message="Please upload your course cover.";
			}
			if (status==0) {
				if (hours.equals("")){
					status = 2;
					message="Please select your course time.";
				}
			}
			if (status==0) {
				if (achievements.equals("")){
					status = 2;
					message="Please input your achievements.";
				}
			}
			double surcharge_for_each = 0;
			if (status==0) {
				if (additional_partner>0) {
					surcharge_for_each = Double.parseDouble(surcharge_for_each_str);
				}
			}
			if (status==0) {
				Course course = Course.dao.findById(course_id);
				boolean update = course.set(course.user_id, user_id)
						.set(course.user_images_01, user_images_01)
						.set(course.title, title)
						.set(course.category_01_id, category_01_id)
						.set(course.category_01_name, category_01_name)
						.set(course.category_02_id, category_02_id)
						.set(course.category_02_name, category_02_name)
						.set(course.overview, overview)
						.set(course.session_length, session_length)
						.set(course.session_rate, session_rate)
						.set(course.teaching_age, teaching_age)
						.set(course.teaching_since, teaching_since)
						.set(course.travel_to_session, travel_to_session)
						.set(course.travel_to_session_distance, travel_to_session_distance)
						.set(course.travel_to_session_distance_double, travel_to_session_distance*1609.344)
						.set(course.travel_to_session_trafic_surcharge, travel_to_session_trafic_surcharge)
						.set(course.city, city)
						.set(course.area, area)
						.set(course.street, street)
						.set(course.address, address)
						.set(course.latitude, latitude).set(course.longitude, longitude)
						.set(course.zipcode, zipcode)
						.set(course.additional_partner, additional_partner)
						.set(course.surcharge_for_each, surcharge_for_each)
						.set(course.discount_type, discount_type)
						.set(course.discount_onetion_pur_money_01, discount_onetion_pur_money_01)
						.set(course.discount_price_01, discount_price_01)
						.set(course.discount_onetion_pur_money_02, discount_onetion_pur_money_02)
						.set(course.discount_price_02, discount_price_02)
						.set(course.discount_onetion_pur_money_03, discount_onetion_pur_money_03)
						.set(course.discount_price_03, discount_price_03)
						.set(course.hours, hours)
						.set(course.banners, fileNames)
						.set(course.videos, vedioURLs)
						.set(course.is_hot_recommand, 0)
						.set(course.hot, 0)
						.set(course.status, 2)
						.set(course.sort, 0)
						.set(course.total_score, 0)
						.set(course.specialist, specialist)
						.set(course.achievements, achievements)
						.set(course.post_time, DateUtils.getCurrentDateTime())
						.update();
				if (update) {
					status = 1;
					message="Our administrator is reviewing your application,  you will receive our decision via email within 48 hours, if you have any questions，please contact us simon_hu@skillpier.com";
				}
				//邮件通知系统-教练发布课程成功
				EmailUntil.sendEmailAddCourse(user_id,title,message);
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("AddCourseResponse", responseValues);
			renderMultiJson("AddCourseResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("CourseManage/addCourse", "新增", this);
		}
	}
	@Author("cluo")
	@Rp("课程管理")
	@Explaination(info = "我的课程列表")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "1", explain = Value.Infer, type = Type.String, name = "pn")
	@URLParam(defaultValue = "5", explain = Value.Infer, type = Type.String, name = "page_size")
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String, name = Course.latitude)
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String, name = Course.longitude)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	// 返回信息
	// page property
	@ReturnOutlet(name = "MyCourseListResponse{myCourseListResult:Courses:pageNumber}", remarks = "page number", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "MyCourseListResponse{myCourseListResult:Courses:pageSize}", remarks = "result amount of this page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "MyCourseListResponse{myCourseListResult:Courses:totalPage}", remarks = "total page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "MyCourseListResponse{myCourseListResult:Courses:totalRow}", remarks = "total row", dataType = DataType.Int, defaultValue = "")
	//
	@ReturnDBParam(name = "MyCourseListResponse{myCourseListResult:Courses:list[Course:$]}", column = Course.course_id)
	@ReturnDBParam(name = "MyCourseListResponse{myCourseListResult:Courses:list[Course:$]}", column = Course.user_id)   
	@ReturnDBParam(name = "MyCourseListResponse{myCourseListResult:Courses:list[Course:$]}", column = Course.title)
	@ReturnDBParam(name = "MyCourseListResponse{myCourseListResult:Courses:list[Course:$]}", column = Course.category_02_name)
	@ReturnDBParam(name = "MyCourseListResponse{myCourseListResult:Courses:list[Course:$]}", column = Course.session_rate)
	@ReturnDBParam(name = "MyCourseListResponse{myCourseListResult:Courses:list[Course:$]}", column = Course.total_score)
	@ReturnDBParam(name = "MyCourseListResponse{myCourseListResult:Courses:list[Course:$]}", column = Course.latitude)
	@ReturnDBParam(name = "MyCourseListResponse{myCourseListResult:Courses:list[Course:$]}", column = Course.longitude)
	@ReturnDBParam(name = "MyCourseListResponse{myCourseListResult:Courses:list[Course:$]}", column = Course.city)
	@ReturnDBParam(name = "MyCourseListResponse{myCourseListResult:Courses:list[Course:$]}", column = Course.area)
	@ReturnDBParam(name = "MyCourseListResponse{myCourseListResult:Courses:list[Course:$]}", column = Course.street)
	@ReturnDBParam(name = "MyCourseListResponse{myCourseListResult:Courses:list[Course:$]}", column = Course.address)
	@ReturnDBParam(name = "MyCourseListResponse{myCourseListResult:Courses:list[Course:$]}", column = Course.zipcode)
	@ReturnOutlet(name = "MyCourseListResponse{myCourseListResult:Courses:list[Course:is_stop_course]}", remarks = "今后暂停课程 0-否，1-是", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "MyCourseListResponse{myCourseListResult:Courses:list[Course:is_official]}", remarks = "是否官方认证：0-否，1-是", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "MyCourseListResponse{myCourseListResult:Courses:list[Course:coach_name]}", remarks = "教练名称", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "MyCourseListResponse{myCourseListResult:Courses:list[Course:coach_image]}", remarks = "教练图片", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "MyCourseListResponse{myCourseListResult:Courses:list[Course:distance]}", remarks = "距离", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "MyCourseListResponse{myCourseListResult:Courses:list[Course:total_coment_num]}", remarks = "评论总数", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "MyCourseListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "MyCourseListResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "MyCourseListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void myCourseList() {
		try {
			String token = getPara("token");
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
				setAttr("MyCourseListResponse", response2);
				renderMultiJson("MyCourseListResponse");
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
				setAttr("MyCourseListResponse", response2);
				renderMultiJson("MyCourseListResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="";
			String filter_sql =" (status=1 or status=0) ";
			String order_by_sql = " order by sort asc,distance asc,is_hot_recommand desc,total_score desc,hot desc ,post_time desc";
			//SQL 中where条件不能用别名，只能 ("+distance_sql+")<500 这样用。
			String distance_sql= "round(6378.138*2*asin(sqrt(pow(sin( ("
					+ latitude
					+ "*pi()/180-latitude*pi()/180)/2),2)+cos("
					+ latitude
					+ "*pi()/180)*cos(latitude*pi()/180)* pow(sin( ("
					+ longitude
					+ "*pi()/180-longitude*pi()/180)/2),2)))*1000)";
			String select = "select course_id,status,user_id,user_images_01,title,category_02_name,session_rate,total_score,latitude,longitude,"+distance_sql+" as distance,city,area,street,address,zipcode ";
			final Page<Course> coursePage = Course.dao
					.paginate(
							pn,
							page_size,
							select," from course where "+filter_sql+" and user_id=? "+order_by_sql,user_id);
			for(Course course:coursePage.getList()){
				int course_id = course.get(course.course_id);
				//教练信息
				User user = User.dao.findById(user_id);
				String coach_name="",coach_image= course.getStr(course.user_images_01);
				int is_official=0,is_stop_course=0;
				if (user!=null) {
					is_official = user.get(user.is_official);
					is_stop_course = user.get(user.is_stop_course);
					coach_name=user.getStr(user.nickname);
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
				Comment comment = Comment.dao.findFirst("select count(course_id) as total_coment_num from comment where course_id=? and status!=0",course_id);
				long total_coment_num = 0;
				if (comment!=null) {
					total_coment_num = comment.getLong("total_coment_num");
				}
				course.put("total_coment_num", total_coment_num);
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("Result", new HashMap<String, Object>() {
				{
					put("Courses", coursePage);
				}
			});
			responseValues.put("message", "Data request successful.");
			setAttr("MyCourseListResponse", responseValues);
			renderMultiJson("MyCourseListResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("CourseManage/myCourseList", "我的列表", this);
		}
	}
	@Author("cluo")
	@Rp("课程管理")
	@Explaination(info = "删除课程")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.course_id)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "DeleteCourseResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "DeleteCourseResponse{status}", remarks = "0-失败，1-操作成功，2-不存在", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "DeleteCourseResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void deleteCourse() {
		try {
			String token = getPara("token");
			String course_id = getPara("course_id");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("course_id", course_id);
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
				setAttr("DeleteCourseResponse", response2);
				renderMultiJson("DeleteCourseResponse");
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
				setAttr("DeleteCourseResponse", response2);
				renderMultiJson("DeleteCourseResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="Deletion failed. Please check for errors.";
			Course course = Course.dao.findById(course_id);
			if (course!=null) {
				boolean delete = course.set(course.status, 0).update();
				if (delete) {
					//banner
					String banners = course.getStr(course.banners);
					if (banners!=null&&!banners.equals("")) {
						String[] banner_array = banners.split("#");
						for(int i=0;i<banner_array.length;i++){
							String image_name = banner_array[i];
							FileUtils.deleteFile(config.save_path+image_name);
						}
					}
					//证书
					List<CourseCertification> ccCertifications = CourseCertification.dao.find("select course_certification_id,image_01 from course_certification where course_id=?",course_id);
					for(CourseCertification certification:ccCertifications){
						String image_01 = certification.getStr(certification.image_01);
						boolean delete2 = certification.set(certification.status, 0).update();
						if (delete2) {
							FileUtils.deleteFile(config.save_path+image_01);
						}
					}
					//收藏
					List<Collection> collections = Collection.dao.find("select collection_id from collection where course_id=?",course_id);
					for(Collection collection:collections){
						collection.delete();
					}
					//修改未付款订单为失效订单
					List<Orders> orders = Orders.dao.find("select orders_id,status from orders where (status=11 or status=1) and course_id=?",course_id);
					for(Orders orders2 :orders){
						int orders_status = orders2.get(orders2.status);
						if (orders_status==1) {
							orders2.set(orders2.status, 2).set(orders2.booking_status, 2).update();
						}
						if (orders_status==11) {
							orders2.set(orders2.status, 10).set(orders2.booking_status, 2).update();
						}
					}
					status = 1;
					message="Deletion successful.";
				}
			}else {
				status = 2;
				message="Course does not exist. Please check for errors.";
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("DeleteCourseResponse", responseValues);
			renderMultiJson("DeleteCourseResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("CourseManage/deleteCourse", "删除", this);
		}
	}
	@Author("cluo")
	@Rp("新建课程")
	@Explaination(info = "编辑课程")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.course_id)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.user_images_01)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.title)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.category_01_id)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.category_01_name)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.category_02_id)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.category_02_name)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.overview)
	@URLParam(defaultValue = "", explain = "【【11.jpg#22.jpg#none#22.jpg】-------banner】拼接如：11.jpg#22.jpg#11.jpg#22.jpg【图片名称#图片名称】", type = Type.String, name = "fileName")
	@URLParam(defaultValue = "", explain = "【【http://www.baidu.com#http://www.baidu.com#none#http://www.baidu.com】--------vedio】拼接如：http://www.baidu.com#http://www.baidu.com#http://www.baidu.com#http://www.baidu.com【vedioURl#vedioURl】", type = Type.String, name = "vedioURL")
	@URLParam(defaultValue = "60", explain = Value.Infer, type = Type.String, name = Course.session_length)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.session_rate)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.teaching_age)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.teaching_since)
	@URLParam(defaultValue = "{0、1}", explain = Value.Infer, type = Type.String, name = Course.travel_to_session)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String_NotRequired, name = Course.travel_to_session_distance)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String_NotRequired, name = Course.travel_to_session_trafic_surcharge)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.city)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.area)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.street)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.address)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.zipcode)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.latitude)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.longitude)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.additional_partner)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.surcharge_for_each)
	@URLParam(defaultValue = "{1、2}", explain = Value.Infer, type = Type.String, name = Course.discount_type)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.discount_onetion_pur_money_01)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.discount_price_01)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.discount_onetion_pur_money_02)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.discount_price_02)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.discount_onetion_pur_money_03)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.discount_price_03)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.achievements)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.specialist)
	@URLParam(defaultValue = "1@1#2@3#3@5", explain = "选择上课时间，拼接：1@1#3@2#5@3，如【@前面1-代表6:00-7:00，@后面代表星期1-星期天】。【@前面3-代表7:00-8:00，@后面3代表星期四】。。。。星期表示：1-星期天，2-星期一，时间段表示：1表示6:00-7:00，3表示7:00-8:00，5表示8:00-9:00。。1、3、5、7、9、11这样表示时间段。。。", type = Type.String_NotRequired, name = Course.hours)	
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "EditCourseResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "EditCourseResponse{status}", remarks = "1-操作成功，2-请选择日期", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "EditCourseResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void editCourse() {
		try {
			String token = getPara("token");
			String course_id = getPara("course_id");
			String user_images_01 = getPara("user_images_01","");
			String title = getPara("title","");
			String category_01_id = getPara("category_01_id","0");
			String category_01_name = getPara("category_01_name","");
			String category_02_id = getPara("category_02_id","0");
			String category_02_name = getPara("category_02_name","");
			String overview = getPara("overview","");
			String fileNames = getPara("fileName","");
			String vedioURL = getPara("vedioURL","");
			
			String session_length = getPara("session_length");
			String session_rate = getPara("session_rate");
			String teaching_age = getPara("teaching_age","");
			String teaching_since = getPara("teaching_since","");
			int travel_to_session = getParaToInt("travel_to_session",0);
			int travel_to_session_distance = getParaToInt("travel_to_session_distance",0);
			String travel_to_session_trafic_surcharge = getPara("travel_to_session_trafic_surcharge","0");
			String city = getPara("city","");
			String area = getPara("area","");
			String street = getPara("street","");
			String address = getPara("address","");
			String zipcode = getPara("zipcode","");
			String latitude = getPara("latitude","");
			String longitude = getPara("longitude","");
			int additional_partner = getParaToInt("additional_partner",0);
			String surcharge_for_each_str = getPara("surcharge_for_each","0");
			int discount_type = getParaToInt("discount_type",0);
			String discount_onetion_pur_money_01 = getPara("discount_onetion_pur_money_01","0");
			String discount_price_01 = getPara("discount_price_01","0");
			String discount_onetion_pur_money_02 = getPara("discount_onetion_pur_money_02","0");
			String discount_price_02 = getPara("discount_price_02","0");
			String discount_onetion_pur_money_03 = getPara("discount_onetion_pur_money_03","0");
			String discount_price_03 = getPara("discount_price_03","0");
			String hours = getPara("hours","");
			String achievements = getPara("achievements","");
			String specialist = getPara("specialist","");
			String invoke = getPara("invoke","app");
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("EditCourseResponse", response2);
				renderMultiJson("EditCourseResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="Edit failed. Please check for errors.";
			if (user_images_01.equals("")){
				status = 2;
				message="Please upload your course cover.";
			}
			if (status==0) {
				if (hours.equals("")){
					status = 2;
					message="Please select your course time.";
				}
			}
			if (status==0) {
				if (achievements.equals("")){
					status = 2;
					message="Please input your achievements.";
				}
			}
			if (status==0) {
				if (specialist.equals("")){
					status = 2;
					message="Please input your specialist.";
				}
			}
			double surcharge_for_each = 0;
			if (status==0) {
				if (additional_partner>0) {
					surcharge_for_each = Double.parseDouble(surcharge_for_each_str);
				}
			}
			if (status==0) {
				Course course = Course.dao.findById(course_id);
				int course_status = course.get(course.status);
				if (course_status==0) {
					course.set(course.status, 2);
				}
				boolean update = course.set(course.user_id, user_id)
						.set(course.user_images_01, user_images_01)
						.set(course.title, title)
						.set(course.category_01_id, category_01_id)
						.set(course.category_01_name, category_01_name)
						.set(course.category_02_id, category_02_id)
						.set(course.category_02_name, category_02_name)
						.set(course.overview, overview)
						.set(course.session_length, session_length)
						.set(course.session_rate, session_rate)
						.set(course.teaching_age, teaching_age)
						.set(course.teaching_since, teaching_since)
						.set(course.travel_to_session, travel_to_session)
						.set(course.travel_to_session_distance, travel_to_session_distance)
						.set(course.travel_to_session_distance_double, travel_to_session_distance*1609.344)
						.set(course.travel_to_session_trafic_surcharge, travel_to_session_trafic_surcharge)
						.set(course.city, city)
						.set(course.area, area)
						.set(course.street, street)
						.set(course.address, address)
						.set(course.zipcode, zipcode)
						.set(course.latitude, latitude).set(course.longitude, longitude)
						.set(course.additional_partner, additional_partner)
						.set(course.surcharge_for_each, surcharge_for_each)
						.set(course.discount_type, discount_type)
						.set(course.discount_onetion_pur_money_01, discount_onetion_pur_money_01)
						.set(course.discount_price_01, discount_price_01)
						.set(course.discount_onetion_pur_money_02, discount_onetion_pur_money_02)
						.set(course.discount_price_02, discount_price_02)
						.set(course.discount_onetion_pur_money_03, discount_onetion_pur_money_03)
						.set(course.discount_price_03, discount_price_03)
						.set(course.banners, fileNames)
						.set(course.videos, vedioURL)
						.set(course.hours, hours)
						.set(course.specialist, specialist)
						.set(course.achievements, achievements)
						.set(course.post_time, DateUtils.getCurrentDateTime())
						.update();
				if (update) {
					status = 1;message="Edit successful.";
					//修改未付款订单为失效订单
					List<Orders> orders = Orders.dao.find("select orders_id,status from orders where user_id=? and (status=11 or status=1) and course_id=?",user_id,course_id);
					for(Orders orders2 :orders){
						int orders_status = orders2.get(orders2.status);
						if (orders_status==1) {
							orders2.set(orders2.status, 2).set(orders2.booking_status, 2).update();
						}
						if (orders_status==11) {
							orders2.set(orders2.status, 10).set(orders2.booking_status, 2).update();
						}
					}
				}
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("EditCourseResponse", responseValues);
			renderMultiJson("EditCourseResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("CourseManage/editCourse", "编辑", this);
		}
	}
	@Author("cluo")
	@Rp("新建课程")
	@Explaination(info = "课程详情")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.course_id)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")

	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.course_id)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.user_id)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.title)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.category_01_id)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.category_01_name)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.category_02_id)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.category_02_name)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.catetory_id)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.catetory_name)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.overview)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.session_length)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.session_rate)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.teaching_age)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.teaching_since)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.travel_to_session)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.travel_to_session_distance)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.travel_to_session_trafic_surcharge)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.city)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.area)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.street)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.address)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.latitude)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.longitude)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.additional_partner)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.surcharge_for_each)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.discount_type)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.discount_onetion_pur_money_01)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.discount_onetion_pur_money_02)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.discount_onetion_pur_money_03)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.discount_price_01)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.discount_price_02)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.discount_price_03)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.hours)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.achievements)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.specialist)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:$}", column = Course.status)
	//banner video
	@ReturnOutlet(name = "CourseInfoResponse{courseInfoResult:Course:CourseBannerVedios:list[CourseBanners:image_01]}", remarks = "banner图片", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CourseInfoResponse{courseInfoResult:Course:CourseBannerVedios:list[CourseBanners:vedio_url]}", remarks = "vedio_url", dataType = DataType.String, defaultValue = "")
	//证书
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:CourseCertifications:list[CourseCertification:$]}", column = CourseCertification.course_certification_id)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:CourseCertifications:list[CourseCertification:$]}", column = CourseCertification.course_id)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:CourseCertifications:list[CourseCertification:$]}", column = CourseCertification.name)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:CourseCertifications:list[CourseCertification:$]}", column = CourseCertification.get_time)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:CourseCertifications:list[CourseCertification:$]}", column = CourseCertification.institue)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:CourseCertifications:list[CourseCertification:$]}", column = CourseCertification.image_01)
	@ReturnDBParam(name = "CourseInfoResponse{courseInfoResult:Course:CourseCertifications:list[CourseCertification:$]}", column = CourseCertification.post_time)
	@ReturnOutlet(name = "CourseInfoResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CourseInfoResponse{status}", remarks = "0-失败，1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CourseInfoResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void courseInfo() {
		try {
			String token = getPara("token");
			String course_id = getPara("course_id");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("course_id", course_id);
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
				setAttr("CourseInfoResponse", response2);
				renderMultiJson("CourseInfoResponse");
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
				setAttr("CourseInfoResponse", response2);
				renderMultiJson("CourseInfoResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			String sqlString ="select ";
			final Course course = Course.dao.findById(course_id);
			if (course!=null) {
				//banner
				String banners = course.getStr(course.banners);
				List<FileNameBean> fBeans = new ArrayList<FileNameBean>();
				if (banners!=null&&!banners.equals("")) {
					String[] image_name_array = banners.split("#");
					for(int i=0;i<image_name_array.length;i++){
						FileNameBean fBean = new FileNameBean();
						String image_name = image_name_array[i];
						fBean.setImage_01(image_name);
						fBeans.add(fBean);
					}
				}
				course.put("CourseBanners", fBeans);
				//vedio
				String videos = course.getStr(course.videos);
				List<CourseVedioBean> vBeans = new ArrayList<CourseVedioBean>();
				if (videos!=null&&!videos.equals("")) {
					String[] videos_array = videos.split("#");
					for(int i=0;i<videos_array.length;i++){
						CourseVedioBean cVedioBean = new CourseVedioBean();
						String video_url = videos_array[i];
						cVedioBean.setVedio_url(video_url);
						vBeans.add(cVedioBean);
					}
				}
				course.put("CourseVedios", vBeans);
				//证书
				List<CourseCertification> certifications = CourseCertification.dao.find("select * from course_certification where  status=1 and course_id=? order by post_time desc",course_id);
				course.put("CourseCertifications", certifications);
			}
			responseValues.put("Result", new HashMap<String, Object>() {
				{
					put("Course", course);
				}
			});
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "Data request successful.");
			setAttr("CourseInfoResponse", responseValues);
			renderMultiJson("CourseInfoResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("CourseManage/courseInfo", "courseInfoinfo", this);
		}
	}
	
	@Author("cluo")
	@Rp("确认下单")
	@Explaination(info = "判断用户输入的地址是否在教练提供的travel distance范围内")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.course_id)
	@URLParam(defaultValue = "", explain = "维度【上课地址】", type = Type.String, name = "latitude")
	@URLParam(defaultValue = "", explain = "经度【上课地址】", type = Type.String, name = "longitude")
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "IsFulfilTravelDistanceResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "IsFulfilTravelDistanceResponse{status}", remarks = "0-失败，1-操作成功，2-不存在", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "IsFulfilTravelDistanceResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void isFulfilTravelDistance() {
		try {
			String latitude = getPara("latitude","");
			String longitude = getPara("longitude","");
			String course_id = getPara("course_id");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("course_id", course_id);
			packageParams.put("latitude", latitude);
			packageParams.put("longitude", longitude);
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
				setAttr("IsFulfilTravelDistanceResponse", response2);
				renderMultiJson("IsFulfilTravelDistanceResponse");
				return;
			}
			/**
			 * 接口签名end
			 */
			int status = 0;String message="Please wait.";
			if (latitude.equals("")||longitude.equals("")) {
				status = 2;
				message="Failed to detect location. Please check for errors.";
			}
			if (status==0) {
				Course course = Course.dao.findById(course_id);
				if (course!=null) {
					String course_latitude = course.getStr(course.latitude);
					String course_longitude = course.getStr(course.longitude);
					double course_latitude_double = 0,course_longitude_double = 0;
					try {
						course_latitude_double = Double.parseDouble(course_latitude);
					} catch (Exception e) {
						// TODO: handle exception
					}
					try {
						course_longitude_double = Double.parseDouble(course_longitude);
					} catch (Exception e) {
						// TODO: handle exception
					}
					int travel_to_session_distance = course.get(course.travel_to_session_distance);
					double to_poit_distance = MapDistance.getDistance(course_latitude_double,course_longitude_double,Double.parseDouble(latitude),Double.parseDouble(longitude));
					if ((to_poit_distance*1000/1609.344)>(travel_to_session_distance*1.0)) {
						status = 2;
						message="Coach does not offer travel service to this location. Please enter a new location.";
					}else {
						status = 1;
						message="Input successful.";
					}
				}else {
					status = 2;
					message="Course does not exist. Please check for errors.";
				}
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("IsFulfilTravelDistanceResponse", responseValues);
			renderMultiJson("IsFulfilTravelDistanceResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("CourseManage/isFulfilTravelDistance", "", this);
		}
	}
}
