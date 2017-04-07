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
import com.quark.app.bean.FileNameBean;
import com.quark.app.logs.AppLog;
import com.quark.common.AppData;
import com.quark.common.RongToken;
import com.quark.common.Storage;
import com.quark.common.config;
import com.quark.interceptor.AppToken;
import com.quark.mail.SendMail;
import com.quark.model.extend.Comment;
import com.quark.model.extend.CommentReply;
import com.quark.model.extend.Course;
import com.quark.model.extend.Experience;
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
 * 個人主頁
 */
@Before(Tx.class)
public class PersonalHomepage extends Controller {

	@Author("cluo")
	@Rp("个人主页")
	@Explaination(info = "基本信息 and MyCourse")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = User.user_id)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnDBParam(name = "PersonalInfoResponse{personalInfoResult:UserInfo:$}", column = User.user_id)
	@ReturnDBParam(name = "PersonalInfoResponse{personalInfoResult:UserInfo:$}", column = User.image_01)
	@ReturnDBParam(name = "PersonalInfoResponse{personalInfoResult:UserInfo:$}", column = User.nickname)
	@ReturnDBParam(name = "PersonalInfoResponse{personalInfoResult:UserInfo:$}", column = User.email)
	@ReturnDBParam(name = "PersonalInfoResponse{personalInfoResult:UserInfo:$}", column = User.is_official)
	@ReturnDBParam(name = "PersonalInfoResponse{personalInfoResult:UserInfo:$}", column = User.telephone)
	@ReturnOutlet(name = "PersonalInfoResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "PersonalInfoResponse{status}", remarks = "1-操作成功，0-失败", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "PersonalInfoResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void personalInfo() {
		try {
			String user_id = getPara("user_id");
			ResponseValues response = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
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
				setAttr("PersonalInfoResponse", response2);
				renderMultiJson("PersonalInfoResponse");
				return;
			}
			/**
			 * 接口签名end
			 */
			int status = 1;String message="";
			String curr_time = DateUtils.getCurrentDate();
			final User user2 = User.dao.findFirst("select user_id,is_official,telephone,image_01,nickname,email from user where user_id=?",user_id);
			response.put("message", message);
			response.put("status", status);
			response.put("code", 200);
			response.put("Result", new HashMap<String, Object>() {
				{
					put("UserInfo", user2);
				}
			});
			setAttr("PersonalInfoResponse", response);
			renderMultiJson("PersonalInfoResponse");
			
			AppLog.info("", getRequest());
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("UserCenter/personalInfo", "基本信息", this);
		}
	}
	@Author("cluo")
	@Rp("个人主页")
	@Explaination(info = "Ta的课程列表")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = User.user_id)
	@URLParam(defaultValue = "1", explain = Value.Infer, type = Type.String, name = "pn")
	@URLParam(defaultValue = "5", explain = Value.Infer, type = Type.String, name = "page_size")
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String, name = Course.latitude)
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String, name = Course.longitude)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	// 返回信息
	// page property
	@ReturnOutlet(name = "TaCourseListResponse{taCourseListResult:Courses:pageNumber}", remarks = "page number", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "TaCourseListResponse{taCourseListResult:Courses:pageSize}", remarks = "result amount of this page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "TaCourseListResponse{taCourseListResult:Courses:totalPage}", remarks = "total page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "TaCourseListResponse{taCourseListResult:Courses:totalRow}", remarks = "total row", dataType = DataType.Int, defaultValue = "")
	//
	@ReturnDBParam(name = "TaCourseListResponse{taCourseListResult:Courses:list[Course:$]}", column = Course.course_id)
	@ReturnDBParam(name = "TaCourseListResponse{taCourseListResult:Courses:list[Course:$]}", column = Course.user_id)   
	@ReturnDBParam(name = "TaCourseListResponse{taCourseListResult:Courses:list[Course:$]}", column = Course.title)
	@ReturnDBParam(name = "TaCourseListResponse{taCourseListResult:Courses:list[Course:$]}", column = Course.category_02_name)
	@ReturnDBParam(name = "TaCourseListResponse{taCourseListResult:Courses:list[Course:$]}", column = Course.session_rate)
	@ReturnDBParam(name = "TaCourseListResponse{taCourseListResult:Courses:list[Course:$]}", column = Course.total_score)
	@ReturnDBParam(name = "TaCourseListResponse{taCourseListResult:Courses:list[Course:$]}", column = Course.latitude)
	@ReturnDBParam(name = "TaCourseListResponse{taCourseListResult:Courses:list[Course:$]}", column = Course.longitude)
	@ReturnDBParam(name = "TaCourseListResponse{taCourseListResult:Courses:list[Course:$]}", column = Course.city)
	@ReturnDBParam(name = "TaCourseListResponse{taCourseListResult:Courses:list[Course:$]}", column = Course.area)
	@ReturnDBParam(name = "TaCourseListResponse{taCourseListResult:Courses:list[Course:$]}", column = Course.street)
	@ReturnDBParam(name = "TaCourseListResponse{taCourseListResult:Courses:list[Course:$]}", column = Course.address)
	@ReturnOutlet(name = "TaCourseListResponse{taCourseListResult:Courses:list[Course:is_official]}", remarks = "是否官方认证：0-否，1-是", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "TaCourseListResponse{taCourseListResult:Courses:list[Course:coach_name]}", remarks = "教练名称", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "TaCourseListResponse{taCourseListResult:Courses:list[Course:coach_image]}", remarks = "教练图片", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "TaCourseListResponse{taCourseListResult:Courses:list[Course:distance]}", remarks = "距离", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "TaCourseListResponse{taCourseListResult:Courses:list[total_coment_num]}", remarks = "评论总数", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "TaCourseListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "TaCourseListResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "TaCourseListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void taCourseList() {
		try {
			String user_id = getPara("user_id");
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
				setAttr("TaCourseListResponse", response2);
				renderMultiJson("TaCourseListResponse");
				return;
			}
			/**
			 * 接口签名end
			 */
			int status = 0;String message="";
			String filter_sql =" status=1 ";
			String order_by_sql = " order by sort asc,distance asc,is_hot_recommand desc,total_score desc,hot desc ,post_time desc";
			//SQL 中where条件不能用别名，只能 ("+distance_sql+")<500 这样用。
			String distance_sql= "round(6378.138*2*asin(sqrt(pow(sin( ("
					+ latitude
					+ "*pi()/180-latitude*pi()/180)/2),2)+cos("
					+ latitude
					+ "*pi()/180)*cos(latitude*pi()/180)* pow(sin( ("
					+ longitude
					+ "*pi()/180-longitude*pi()/180)/2),2)))*1000)";
			String select = "select course_id,user_id,user_images_01,title,category_02_name,session_rate,total_score,latitude,longitude,"+distance_sql+" as distance,city,area,street,address ";
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
				int is_official=0;
				if (user!=null) {
					is_official = user.get(user.is_official);
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
				Comment comment = Comment.dao.findFirst("select count(course_id) as total_coment_num from comment where type=1 and course_id=? and status!=0",course_id);
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
			setAttr("TaCourseListResponse", responseValues);
			renderMultiJson("TaCourseListResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("PersonalHomepage/taCourseList", "ta的列表", this);
		}
	}
	@Author("cluo")
	@Rp("个人主页-我的评论")
	@Explaination(info = "别人对自己的评价")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = User.user_id)
	@URLParam(defaultValue = "1", explain = Value.Infer, type = Type.String, name = "pn")
	@URLParam(defaultValue = "5", explain = Value.Infer, type = Type.String, name = "page_size")
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	// 返回信息
	// page property
	@ReturnOutlet(name = "TaCommentList{taCommentListResult:Comments:pageNumber}", remarks = "page number", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "TaCommentList{taCommentListResult:Comments:pageSize}", remarks = "result amount of this page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "TaCommentList{taCommentListResult:Comments:totalPage}", remarks = "total page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "TaCommentList{taCommentListResult:Comments:totalRow}", remarks = "total row", dataType = DataType.Int, defaultValue = "")
	//,,,,,,,,,, 
	@ReturnDBParam(name = "TaCommentListResponse{taCommentListResult:Comments:list[Comment:$]}", column = Comment.comment_id)
	@ReturnDBParam(name = "TaCommentListResponse{taCommentListResult:Comments:list[Comment:$]}", column = Comment.comment_name)
	@ReturnDBParam(name = "TaCommentListResponse{taCommentListResult:Comments:list[Comment:$]}", column = Comment.comment_image)
	@ReturnDBParam(name = "TaCommentListResponse{taCommentListResult:Comments:list[Comment:$]}", column = Comment.note)
	@ReturnDBParam(name = "TaCommentListResponse{taCommentListResult:Comments:list[Comment:$]}", column = Comment.pro_skill)
	@ReturnDBParam(name = "TaCommentListResponse{taCommentListResult:Comments:list[Comment:$]}", column = Comment.teaching_environment)
	@ReturnDBParam(name = "TaCommentListResponse{taCommentListResult:Comments:list[Comment:$]}", column = Comment.teaching_attitude)
	@ReturnDBParam(name = "TaCommentListResponse{taCommentListResult:Comments:list[Comment:$]}", column = Comment.is_reply)
	@ReturnDBParam(name = "TaCommentListResponse{taCommentListResult:Comments:list[Comment:$]}", column = Comment.comment_reply_id)
	@ReturnDBParam(name = "TaCommentListResponse{taCommentListResult:Comments:list[Comment:$]}", column = Comment.post_time)
	@ReturnDBParam(name = "TaCommentListResponse{taCommentListResult:Comments:list[Comment:$]}", column = Comment.post_date)
	@ReturnDBParam(name = "TaCommentListResponse{taCommentListResult:Comments:list[Comment:$]}", column = Comment.public_course_user_name)
	@ReturnDBParam(name = "TaCommentListResponse{taCommentListResult:Comments:list[Comment:$]}", column = Comment.category_01_name)
	// 
	@ReturnOutlet(name = "TaCommentListResponse{taCommentListResult:Comments:list[Comment:user_name]}", remarks = "用户昵称", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "TaCommentListResponse{taCommentListResult:Comments:list[Comment:user_image]}", remarks = "用户头像", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "TaCommentListResponse{taCommentListResult:Comments:list[Comment:total_score]}", remarks = "评价总分", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "TaCommentListResponse{taCommentListResult:Comments:list[Comment:commentReply_content]}", remarks = "回复语", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "TaCommentListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "TaCommentListResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "TaCommentListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void taCommentList() {
		try {
			int pn = getParaToInt("pn", 1);
			int page_size = getParaToInt("page_size", 5);
			String user_id = getPara("user_id");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("user_id", user_id);
			packageParams.put("page_size", page_size+"");
			packageParams.put("pn", pn+"");
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
				setAttr("TaCommentListResponse", response2);
				renderMultiJson("TaCommentListResponse");
				return;
			}
			/**
			 * 接口签名end
			 */
			int status = 0;String message="";
			String sqlString="select comment_id,user_id,course_id,comment_name,comment_image,public_course_user_name,category_01_name,note,pro_skill,teaching_environment,teaching_attitude,is_reply,comment_reply_id,post_time,post_date ";
			final Page<Comment> commentPage = Comment.dao
					.paginate(
							pn,
							page_size,
							sqlString," from comment where status=1 and type=1 and public_course_user_id=?",user_id);
			for(Comment comment:commentPage.getList()){
				//评价用户信息
				int comment_user_id = comment.get(comment.user_id);
				User user = User.dao.findById(comment_user_id);
				String user_name="",user_image="";
				if (user!=null) {
					user_name=user.getStr(user.nickname);
					user_image=user.getStr(user.image_01);
				}
				comment.put("user_name", user_name);
				comment.put("user_image", user_image);
				//评价总分 
				int total_score = 0;
				int pro_skill = comment.get(comment.pro_skill); 
				int teaching_environment = comment.get(comment.teaching_environment); 
				int teaching_attitude = comment.get(comment.teaching_attitude); 
				total_score = (pro_skill+teaching_environment+teaching_attitude)/3;
				comment.put("total_score", total_score);
				//回复语
				int comment_reply_id = comment.get(comment.comment_reply_id); 
				int is_reply = comment.get(comment.is_reply); 
				String commentReply_content = "";
				if (is_reply==1) {
					CommentReply commentReply = CommentReply.dao.findById(comment_reply_id);
					if (commentReply!=null) {
						commentReply_content = commentReply.getStr(commentReply.content);
					}
				}
				comment.put("commentReply_content", commentReply_content);
				
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("Result", new HashMap<String, Object>() {
				{
					put("Comments", commentPage);
				}
			});
			responseValues.put("message", "Data request successful.");
			setAttr("TaCommentListResponse", responseValues);
			renderMultiJson("TaCommentListResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("Courses/taCommentList", "Ta评论列表", this);
		}
	}
	@Author("cluo")
	@Rp("个人主页-我的经历")
	@Explaination(info = "Ta的经历列表")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = User.user_id)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnDBParam(name = "TaExperienceListResponse{taExperienceListResult:TaExperiences:list[Experience:$]}", column = Experience.experience_id)
	@ReturnDBParam(name = "TaExperienceListResponse{taExperiencesResult:TaExperiences:list[Experience:$]}", column = Experience.title)
	@ReturnDBParam(name = "TaExperienceListResponse{taExperiencesResult:TaExperiences:list[Experience:$]}", column = Experience.content)
	@ReturnDBParam(name = "TaExperienceListResponse{taExperiencesResult:TaExperiences:list[Experience:$]}", column = Experience.post_time)
	@ReturnOutlet(name = "TaExperienceListResponse{taExperiencesResult:TaExperiences:list[Experience:image_01]}", remarks = "经验图片封面", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "TaExperienceListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "TaExperienceListResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "TaExperienceListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void taExperienceList() {
		try {
			String user_id = getPara("user_id");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
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
				setAttr("TaExperienceListResponse", response2);
				renderMultiJson("TaExperienceListResponse");
				return;
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			final List<Experience> experiences = Experience.dao.find("select experience_id,title,content,images,post_time from experience where user_id=? and status=1 order by post_time desc",user_id);
			for(Experience experience:experiences){
				String oldImages = experience.getStr(experience.images);
				String image_01="";
				if (oldImages!=null&&!oldImages.equals("")) {
					String[] image_name_array = oldImages.split("#");
					image_01 = image_name_array[0];
				}
				experience.put("image_01", image_01);
			}
			responseValues.put("Result", new HashMap<String, Object>() {
				{
					put("TaExperiences", experiences);
				}
			});
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "成功返回");
			setAttr("TaExperienceListResponse", responseValues);
			renderMultiJson("TaExperienceListResponse");
			AppLog.info("", getRequest());
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("PersonalHomepage/taExperienceList", "列表", this);
		}
	}
	@Author("cluo")
	@Rp("个人主页-我的经历详细、撰写经历")
	@Explaination(info = "经历详情")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Experience.experience_id)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnDBParam(name = "ExperienceInfoResponse{ExperienceInfoResult:ExperienceInfo:$}", column = Experience.experience_id)
	@ReturnDBParam(name = "ExperienceInfoResponse{ExperienceInfoResult:ExperienceInfo:$}", column = Experience.title)
	@ReturnDBParam(name = "ExperienceInfoResponse{ExperienceInfoResult:ExperienceInfo:$}", column = Experience.content)
	@ReturnDBParam(name = "ExperienceInfoResponse{ExperienceInfoResult:ExperienceInfo:$}", column = Experience.post_time)
	@ReturnOutlet(name = "ExperienceInfoResponse{ExperienceInfoResult:ExperienceInfo:exBanners:list[FileNameBean:image_01]}", remarks = "证书图片", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "ExperienceInfoResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "ExperienceInfoResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "ExperienceInfoResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void experienceInfo() {
		try {
			String experience_id = getPara("experience_id");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("experience_id", experience_id);
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
				setAttr("ExperienceInfoResponse", response2);
				renderMultiJson("ExperienceInfoResponse");
				return;
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			final Experience experience = Experience.dao.findFirst("select experience_id,title,content,images,post_time from experience where experience_id=?",experience_id);
			if (experience!=null) {
				String oldImages = experience.getStr(experience.images);
				List<FileNameBean> fBeans = new ArrayList<FileNameBean>();
				if (oldImages!=null&&!oldImages.equals("")) {
					String[] image_name_array = oldImages.split("#");
					for(int i=0;i<image_name_array.length;i++){
						FileNameBean fBean = new FileNameBean();
						String image_name = image_name_array[i];
						fBean.setImage_01(image_name);
						fBeans.add(fBean);
					}
				}
				experience.put("exBanners", fBeans);
			}
			responseValues.put("Result", new HashMap<String, Object>() {
				{
					put("ExperienceInfo", experience);
				}
			});
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "成功返回");
			setAttr("ExperienceInfoResponse", responseValues);
			renderMultiJson("ExperienceInfoResponse");
			AppLog.info("", getRequest());
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("PersonalHomepage/experienceInfo", "列表", this);
		}
	}
}
