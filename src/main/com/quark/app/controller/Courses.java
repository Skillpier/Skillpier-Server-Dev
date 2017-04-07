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
import com.quark.model.extend.CommentReply;
import com.quark.model.extend.Course;
import com.quark.model.extend.CourseCertification;
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
 * 课程信息
 */
@Before(Tx.class)
public class Courses extends Controller {

	@Author("cluo")
	@Rp("课程（教练）详情")
	@Explaination(info = "课程详情")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String_NotRequired, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.course_id)
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String, name = Course.latitude)
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String, name = Course.longitude)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")

	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.course_id)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.user_id)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.title)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.category_02_name)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.overview)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.session_length)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.session_rate)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.teaching_age)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.teaching_since)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.city)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.area)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.street)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.address)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.latitude)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.longitude)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.additional_partner)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.teaching_since)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.travel_to_session)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.travel_to_session_distance)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.travel_to_session_trafic_surcharge)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.discount_type)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.discount_onetion_pur_money_01)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.discount_onetion_pur_money_02)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.discount_onetion_pur_money_03)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.discount_price_01)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.discount_price_02)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.discount_price_03)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.achievements)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.specialist)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:$}", column = Course.status)
	@ReturnOutlet(name = "CourseInfo2Response{courseInfo2Result:Course:is_official}", remarks = "是否官方认证：0-否，1-是", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CourseInfo2Response{courseInfo2Result:Course:user_image_01}", remarks = "用户头像", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CourseInfo2Response{courseInfo2Result:Course:is_stop_course}", remarks = "今后暂停课程 0-否，1-是", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CourseInfo2Response{courseInfo2Result:Course:nickname}", remarks = "昵称", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CourseInfo2Response{courseInfo2Result:Course:is_collection}", remarks = "是否收藏：0-否，1-是", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CourseInfo2Response{courseInfo2Result:Course:collection_id}", remarks = "收藏id", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CourseInfo2Response{courseInfo2Result:Course:total_coment_num}", remarks = "评论总数", dataType = DataType.String, defaultValue = "")
	//banner video    
	@ReturnOutlet(name = "CourseInfo2Response{courseInfo2Result:Course:CourseBanners:list[CourseBanner:image_01]}", remarks = "banner图片", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CourseInfo2Response{courseInfo2Result:Course:CourseBanners:list[CourseVedio:vedio_url]}", remarks = "vedio_url", dataType = DataType.String, defaultValue = "")
	//证书
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:CourseCertifications:list[CourseCertification:$]}", column = CourseCertification.course_certification_id)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:CourseCertifications:list[CourseCertification:$]}", column = CourseCertification.course_id)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:CourseCertifications:list[CourseCertification:$]}", column = CourseCertification.name)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:CourseCertifications:list[CourseCertification:$]}", column = CourseCertification.get_time)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:CourseCertifications:list[CourseCertification:$]}", column = CourseCertification.institue)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:CourseCertifications:list[CourseCertification:$]}", column = CourseCertification.image_01)
	@ReturnDBParam(name = "CourseInfo2Response{courseInfo2Result:Course:CourseCertifications:list[CourseCertification:$]}", column = CourseCertification.post_time)
	@ReturnOutlet(name = "CourseInfo2Response{courseInfo2Result:Course:CourseCertifications:list[CourseCertification:freeCourseDay]}", remarks = "有空日期", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CourseInfo2Response{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CourseInfo2Response{status}", remarks = "0-失败，1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CourseInfo2Response{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void courseInfo2() {
		try {
			String token = getPara("token");
			String course_id = getPara("course_id");
			String latitude = getPara("latitude");
			String longitude = getPara("longitude");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("course_id", course_id);
			packageParams.put("latitude", latitude);
			packageParams.put("longitude", longitude);
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
				setAttr("CourseInfo2Response", response2);
				renderMultiJson("CourseInfo2Response");
				return;
			}
			/**
			 * 接口签名end
			 */
			String user_id = "0";
			if (token!=null&&AppToken.check(token, this,invoke)) {
				// 登陆失败
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
			String sqlString = "select course_id,user_id,user_images_01,title,total_score,achievements,specialist,"+distance_sql+" as distance,category_02_name,teaching_since,travel_to_session,travel_to_session_distance,travel_to_session_trafic_surcharge,session_rate,teaching_since,session_length,additional_partner,teaching_age,city,area,street,address,latitude,longitude,overview,discount_type,discount_onetion_pur_money_01,discount_onetion_pur_money_02,discount_onetion_pur_money_03,discount_price_01,discount_price_02,discount_price_03,banners,videos,hot,status  ";
			final Course course = Course.dao.findFirst(sqlString+" from course where course_id=?",course_id);
			if (course!=null) {
				int hot = course.get(course.hot);
				course.set(course.hot, (hot+1)).update();
				String course_user_id = course.getStr(course.user_id);
				String user_image_01=course.getStr(course.user_images_01),nickname = "";
				int is_official= 0,is_stop_course= 0;
				User courseUser = User.dao.findById(course_user_id);
				if (courseUser!=null) {
					is_official = courseUser.get(courseUser.is_official);
					nickname = courseUser.getStr(courseUser.nickname);
					is_stop_course = courseUser.get(courseUser.is_stop_course);
				}
				course.put("is_official", is_official);
				course.put("user_image_01", user_image_01);
				course.put("nickname", nickname);
				course.put("is_stop_course", is_stop_course);
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
				//是否收藏
				int is_collection=0,collection_id=0;
				Collection collection = Collection.dao.findFirst("select collection_id from collection where course_id=? and user_id=?",course_id,user_id);
				if (collection!=null) {
					collection_id = collection.get(collection.collection_id);
					is_collection = 1;
				}
				course.put("is_collection", is_collection);
				course.put("collection_id", collection_id);
				
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
				List<CourseCertification> certifications = CourseCertification.dao.find("select * from course_certification where status=1 and course_id=? order by post_time desc",course_id);
				course.put("CourseCertifications", certifications);
				//评论总数
				Comment comment = Comment.dao.findFirst("select count(course_id) as total_coment_num from comment where type=1 and course_id=? and status!=0",course_id);
				long total_coment_num = 0;
				if (comment!=null) {
					total_coment_num = comment.getLong("total_coment_num");
				}
				course.put("total_coment_num", total_coment_num);
				///课程有空时间
				String freeCourseDay = ScheduleBean2.freeCourseDay(Integer.parseInt(course_id));
				course.put("freeCourseDay", freeCourseDay);
			}
			responseValues.put("Result", new HashMap<String, Object>() {
				{
					put("Course", course);
				}
			});
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "Data request successful.");
			setAttr("CourseInfo2Response", responseValues);
			renderMultiJson("CourseInfo2Response");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("Courses/courseInfo2", "courseInfo2", this);
		}
	}
	@Author("cluo")
	@Rp("课程（教练）详情")
	@Explaination(info = "添加收藏")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.course_id)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "AddCollectionCourseResponse{collection_id}", remarks = "收藏ID", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "AddCollectionCourseResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "AddCollectionCourseResponse{status}", remarks = "0-失败，1-操作成功，2-不存在", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "AddCollectionCourseResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void addCollectionCourse() {
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
				setAttr("AddCollectionCourseResponse", response2);
				renderMultiJson("AddCollectionCourseResponse");
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
				setAttr("AddCollectionCourseResponse", response2);
				renderMultiJson("AddCollectionCourseResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 1,collection_id=0;String message="Add to Favorites.";
			Collection collection = Collection.dao.findFirst("select collection_id from collection where course_id=? and user_id=?",course_id,user_id);
			if (collection!=null) {
				collection_id = collection.get(collection.collection_id);
				status = 2;
				message="Favorites";
			}else {
				Collection collection2 = new Collection();
				boolean save = collection2.set(collection2.course_id, course_id)
						.set(collection2.user_id, user_id)
						.set(collection2.post_time, DateUtils.getCurrentDateTime())
						.save();
				if (save) {
					collection_id = collection2.get("collection_id");
				}else {
					status = 0;
					message="Add to Favorites failed. Please check for errors.";
				}
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("collection_id", collection_id);
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("AddCollectionCourseResponse", responseValues);
			renderMultiJson("AddCollectionCourseResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("Courses/addCollectionCourse", "收藏", this);
		}
	}
	@Author("cluo")
	@Rp("课程（教练）详情、我的收藏")
	@Explaination(info = "删除收藏")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Collection.collection_id)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "DeleteCollectionCourseResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "DeleteCollectionCourseResponse{status}", remarks = "0-失败，1-操作成功，2-不存在", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "DeleteCollectionCourseResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void deleteCollectionCourse() {
		try {
			String token = getPara("token");
			String collection_id = getPara("collection_id");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("collection_id", collection_id);
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
				setAttr("DeleteCollectionCourseResponse", response2);
				renderMultiJson("DeleteCollectionCourseResponse");
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
				setAttr("DeleteCollectionCourseResponse", response2);
				renderMultiJson("DeleteCollectionCourseResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="Remove from Favorites failed.";
			Collection collection = Collection.dao.findById(collection_id);
			if (collection!=null) {
				boolean delete = collection.delete();
				if (delete) {
					status = 1;
					message="Remove from Favorites successful.";
				}
			}else {
				status = 2;
				message="Favorite course does not exist.";
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("DeleteCollectionCourseResponse", responseValues);
			renderMultiJson("DeleteCollectionCourseResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("Courses/deleteCollectionCourse", "收藏", this);
		}
	}
	@Author("cluo")
	@Rp("我的收藏")
	@Explaination(info = "我的收藏列表")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "1", explain = Value.Infer, type = Type.String, name = "pn")
	@URLParam(defaultValue = "5", explain = Value.Infer, type = Type.String, name = "page_size")
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String, name = Course.latitude)
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String, name = Course.longitude)
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	// 返回信息
	// page property
	@ReturnOutlet(name = "MyCollectionCourseListResponse{myCollectionCourseListResult:Collections:pageNumber}", remarks = "page number", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "MyCollectionCourseListResponse{myCollectionCourseListResult:Collections:pageSize}", remarks = "result amount of this page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "MyCollectionCourseListResponse{myCollectionCourseListResult:Collections:totalPage}", remarks = "total page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "MyCollectionCourseListResponse{myCollectionCourseListResult:Collections:totalRow}", remarks = "total row", dataType = DataType.Int, defaultValue = "")
	//
	@ReturnDBParam(name = "MyCollectionCourseListResponse{myCollectionCourseListResult:Collections:list[Collection:$]}", column = Collection.collection_id)
	@ReturnDBParam(name = "MyCollectionCourseListResponse{myCollectionCourseListResult:Collections:list[Collection:$]}", column = Collection.course_id)
	@ReturnDBParam(name = "MyCollectionCourseListResponse{myCollectionCourseListResult:Collections:list[Collection:Course:$]}", column = Course.course_id)
	@ReturnDBParam(name = "MyCollectionCourseListResponse{myCollectionCourseListResult:Collections:list[Collection:Course:$]}", column = Course.user_id)   
	@ReturnDBParam(name = "MyCollectionCourseListResponse{myCollectionCourseListResult:Collections:list[Collection:Course:$]}", column = Course.title)
	@ReturnDBParam(name = "MyCollectionCourseListResponse{myCollectionCourseListResult:Collections:list[Collection:Course:$]}", column = Course.category_02_name)
	@ReturnDBParam(name = "MyCollectionCourseListResponse{myCollectionCourseListResult:Collections:list[Collection:Course:$]}", column = Course.session_rate)
	@ReturnDBParam(name = "MyCollectionCourseListResponse{myCollectionCourseListResult:Collections:list[Collection:Course:$]}", column = Course.total_score)
	@ReturnDBParam(name = "MyCollectionCourseListResponse{myCollectionCourseListResult:Collections:list[Collection:Course:$]}", column = Course.latitude)
	@ReturnDBParam(name = "MyCollectionCourseListResponse{myCollectionCourseListResult:Collections:list[Collection:Course:$]}", column = Course.longitude)
	@ReturnDBParam(name = "MyCollectionCourseListResponse{myCollectionCourseListResult:Collections:list[Collection:Course:$]}", column = Course.city)
	@ReturnDBParam(name = "MyCollectionCourseListResponse{myCollectionCourseListResult:Collections:list[Collection:Course:$]}", column = Course.area)
	@ReturnDBParam(name = "MyCollectionCourseListResponse{myCollectionCourseListResult:Collections:list[Collection:Course:$]}", column = Course.street)
	@ReturnDBParam(name = "MyCollectionCourseListResponse{myCollectionCourseListResult:Collections:list[Collection:Course:$]}", column = Course.address)
	@ReturnOutlet(name = "MyCollectionCourseListResponse{myCollectionCourseListResult:Collections:list[Collection:Course:is_official]}", remarks = "是否官方认证：0-否，1-是", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "MyCollectionCourseListResponse{myCollectionCourseListResult:Collections:list[Collection:Course:coach_name]}", remarks = "教练名称", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "MyCollectionCourseListResponse{myCollectionCourseListResult:Collections:list[Collection:Course:coach_image]}", remarks = "教练图片", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "MyCollectionCourseListResponse{myCollectionCourseListResult:Collections:list[Collection:Course:distance]}", remarks = "距离", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "MyCollectionCourseListResponse{myCollectionCourseListResult:Collections:list[Collection:total_coment_num]}", remarks = "评论总数", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "MyCollectionCourseListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "MyCollectionCourseListResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "MyCollectionCourseListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void myCollectionCourseList() {
		try {
			int pn = getParaToInt("pn", 1);
			int page_size = getParaToInt("page_size", 5);
			String latitude = getPara("latitude");
			String longitude = getPara("longitude");
			String token = getPara("token");
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
				setAttr("MyCollectionCourseListResponse", response2);
				renderMultiJson("MyCollectionCourseListResponse");
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
				setAttr("MyCollectionCourseListResponse", response2);
				renderMultiJson("MyCollectionCourseListResponse");
				return;
			}
			int status = 0;String message="";
			String user_id = AppToken.getUserId(token, this,invoke);
			//SQL 中where条件不能用别名，只能 ("+distance_sql+")<500 这样用。
			String distance_sql= "round(6378.138*2*asin(sqrt(pow(sin( ("
					+ latitude
					+ "*pi()/180-latitude*pi()/180)/2),2)+cos("
					+ latitude
					+ "*pi()/180)*cos(latitude*pi()/180)* pow(sin( ("
					+ longitude
					+ "*pi()/180-longitude*pi()/180)/2),2)))*1000)";
			String select = "select course_id,user_id,user_images_01,title,category_02_name,session_rate,total_score,latitude,longitude,"+distance_sql+" as distance,city,area,street,address ";
			final Page<Collection> collectionPage = Collection.dao
					.paginate(
							pn,
							page_size,
							"select collection_id,course_id "," from collection where user_id="+user_id+" order by post_time desc");
			for(Collection collection:collectionPage.getList()){
				int course_id = collection.get(collection.course_id); 
				Course course = Course.dao.findFirst(select+" from course where status=1 and course_id=? order by post_time desc",course_id);
				if (course!=null) {
					//教练信息
					String course_user_id = course.getStr(course.user_id);
					User user = User.dao.findById(course_user_id);
					String coach_name="",coach_image=course.getStr(course.user_images_01);
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
				collection.put("Course", course);
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("Result", new HashMap<String, Object>() {
				{
					put("Collections", collectionPage);
				}
			});
			responseValues.put("message", "Data request successful.");
			setAttr("MyCollectionCourseListResponse", responseValues);
			renderMultiJson("MyCollectionCourseListResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("Courses/myCollectionCourseList", "收藏列表", this);
		}
	}
	@Author("cluo")
	@Rp("课程详情评论")
	@Explaination(info = "课程详情评论列表")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Course.course_id)
	@URLParam(defaultValue = "1", explain = Value.Infer, type = Type.String, name = "pn")
	@URLParam(defaultValue = "5", explain = Value.Infer, type = Type.String, name = "page_size")
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	// 返回信息
	// page property
	@ReturnOutlet(name = "CourseCommentListResponse{courseCommentListResult:Comments:pageNumber}", remarks = "page number", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CourseCommentListResponse{courseCommentListResult:Comments:pageSize}", remarks = "result amount of this page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CourseCommentListResponse{courseCommentListResult:Comments:totalPage}", remarks = "total page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CourseCommentListResponse{courseCommentListResult:Comments:totalRow}", remarks = "total row", dataType = DataType.Int, defaultValue = "")
	//,,,,,,,,,, 
	@ReturnDBParam(name = "CourseCommentListResponse{courseCommentListResult:Comments:list[Comment:$]}", column = Comment.comment_id)
	@ReturnDBParam(name = "CourseCommentListResponse{courseCommentListResult:Comments:list[Comment:$]}", column = Comment.comment_name)
	@ReturnDBParam(name = "CourseCommentListResponse{courseCommentListResult:Comments:list[Comment:$]}", column = Comment.comment_image)
	@ReturnDBParam(name = "CourseCommentListResponse{courseCommentListResult:Comments:list[Comment:$]}", column = Comment.note)
	@ReturnDBParam(name = "CourseCommentListResponse{courseCommentListResult:Comments:list[Comment:$]}", column = Comment.pro_skill)
	@ReturnDBParam(name = "CourseCommentListResponse{courseCommentListResult:Comments:list[Comment:$]}", column = Comment.teaching_environment)
	@ReturnDBParam(name = "CourseCommentListResponse{courseCommentListResult:Comments:list[Comment:$]}", column = Comment.teaching_attitude)
	@ReturnDBParam(name = "CourseCommentListResponse{courseCommentListResult:Comments:list[Comment:$]}", column = Comment.is_reply)
	@ReturnDBParam(name = "CourseCommentListResponse{courseCommentListResult:Comments:list[Comment:$]}", column = Comment.comment_reply_id)
	@ReturnDBParam(name = "CourseCommentListResponse{courseCommentListResult:Comments:list[Comment:$]}", column = Comment.post_time)
	@ReturnDBParam(name = "CourseCommentListResponse{courseCommentListResult:Comments:list[Comment:$]}", column = Comment.post_date)
	// 
	@ReturnOutlet(name = "CourseCommentListResponse{courseCommentListResult:Comments:list[Comment:total_score]}", remarks = "评价总分", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CourseCommentListResponse{courseCommentListResult:Comments:list[Comment:commentReply_content]}", remarks = "回复语", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CourseCommentListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CourseCommentListResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CourseCommentListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void courseCommentList() {
		try {
			int pn = getParaToInt("pn", 1);
			int page_size = getParaToInt("page_size", 5);
			String course_id = getPara("course_id");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("course_id", course_id);
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
				setAttr("CourseCommentListResponse", response2);
				renderMultiJson("CourseCommentListResponse");
				return;
			}
			/**
			 * 接口签名end
			 */
			int status = 0;String message="";
			String sqlString="select comment_id,comment_name,comment_image,note,pro_skill,teaching_environment,teaching_attitude,is_reply,comment_reply_id,post_time,post_date ";
			final Page<Comment> commentPage = Comment.dao
					.paginate(
							pn,
							page_size,
							sqlString," from comment where type=1 and status=1 and course_id="+course_id+" order by post_time desc");
			for(Comment comment:commentPage.getList()){
				String comment_post_time = comment.getTimestamp("post_time").toString();
				String current_time = DateUtils.getCurrentDateTime();
				String active_time = DateUtils.getActiveTime(comment_post_time, current_time);
				comment.put("format_post_time", active_time);
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
			setAttr("CourseCommentListResponse", responseValues);
			renderMultiJson("CourseCommentListResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("Courses/courseCommentList", "评论列表", this);
		}
	}
}
