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
import com.jfinal.plugin.ehcache.CacheInterceptor;
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
import com.quark.app.bean.ScheduleBean2;
import com.quark.app.logs.AppLog;
import com.quark.common.AppData;
import com.quark.common.RongToken;
import com.quark.common.Storage;
import com.quark.common.config;
import com.quark.interceptor.AppToken;
import com.quark.model.extend.Category01;
import com.quark.model.extend.Category02;
import com.quark.model.extend.Catetory;
import com.quark.model.extend.Comment;
import com.quark.model.extend.Course;
import com.quark.model.extend.IndexBanner;
import com.quark.model.extend.Tokens;
import com.quark.model.extend.User;
import com.quark.sign.RequestHandler;
import com.quark.utils.DateUtils;
import com.quark.utils.FileUtils;
import com.quark.utils.MD5Util;
import com.quark.utils.MessageUtils;

/**
 * @author C罗
 * 分类管理
 *
 */
@Before(Tx.class)
public class CatetoryManage extends Controller {

	@Author("cluo")
	@Rp("推荐列表、搜索、教练（课程）列表")
	@Explaination(info = "CATEGORY列表")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "CategoryListResponse{categoryListResult:Categorys:list[CategoryBean:category_02_name]}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CategoryListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CategoryListResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CategoryListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void categoryList() {
		try {
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			final List<CategoryBean> categoryBeans = new ArrayList<CategoryBean>();
			List<Category02> category02s = Category02.dao.find("select category_02_id,category_02_name from category_02 where status=1 group by category_02_name order by post_time desc");
			for(Category02 category02:category02s){
				int category_02_id = category02.get(category02.category_02_id);
				String category_02_name = category02.getStr(category02.category_02_name);
				CategoryBean cBean = new CategoryBean();
				cBean.setCategory_02_id(category_02_id);
				cBean.setCategory_02_name(category_02_name);
				categoryBeans.add(cBean);
			}
			responseValues.put("Result", new HashMap<String, Object>() {
				{
					put("Categorys", categoryBeans);
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
			AppData.analyze("CatetoryManage/categoryList", "二级类别", this);
		}
	}
	@Author("cluo")
	@Rp("教练（课程）列表、推荐列表")
	@Explaination(info = "教练（课程）列表")
	@URLParam(defaultValue = "", explain = "0表示全部，其他按分类--分类ID", type = Type.String, name = "category_02_id")
	@URLParam(defaultValue = "", explain = "地区", type = Type.String_NotRequired, name = Course.zipcode)
	@URLParam(defaultValue = "{1、2}", explain = "1-直接从首页推荐分类进入，2-下拉选择category/二级分类进入", type = Type.String, name = "type")
	@URLParam(defaultValue = "", explain = "教练或者课程名", type = Type.String_NotRequired, name = "kw")
	@URLParam(defaultValue = "1", explain = Value.Infer, type = Type.String, name = "pn")
	@URLParam(defaultValue = "5", explain = Value.Infer, type = Type.String, name = "page_size")
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String, name = Course.latitude)
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String, name = Course.longitude)
	//检索
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String_NotRequired, name = Course.category_02_name)
	@URLParam(defaultValue = "", explain = "距离开始", type = Type.String_NotRequired, name = "distances_begin")
	@URLParam(defaultValue = "", explain = "距离结束", type = Type.String_NotRequired, name = "distances")
	@URLParam(defaultValue = "", explain = "价格刷选开始", type = Type.String_NotRequired, name = "session_rate_begin")
	@URLParam(defaultValue = "", explain = "价格刷选结束", type = Type.String_NotRequired, name = "session_rate")
	@URLParam(defaultValue = "{0、1、2}", explain = "价格排序：0-不需要， 1-高,2-低切换", type = Type.String_NotRequired, name = "price_type")
	@URLParam(defaultValue = "{0、1、2}", explain = "评价排序：0-不需要， 1-高,2-低切换", type = Type.String_NotRequired, name = "review_type")
	@URLParam(defaultValue = "{0、1、2}", explain = "距离排序：0-不需要， 1-远,2-近切换", type = Type.String_NotRequired, name = "distance_type")
	@URLParam(defaultValue = "{0、1、2}", explain = "热度排序：0-不需要， 1-热,2-冷切换", type = Type.String_NotRequired, name = "hot_type")
	@URLParam(defaultValue = "{0、1}", explain = "是否上门服务:1-是，0-否", type = Type.String_NotRequired, name = "travel_to_session")
	
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	// 返回信息
	// page property
	@ReturnOutlet(name = "CourseListResponse{courseListResult:Courses:pageNumber}", remarks = "page number", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CourseListResponse{courseListResult:Courses:pageSize}", remarks = "result amount of this page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CourseListResponse{courseListResult:Courses:totalPage}", remarks = "total page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CourseListResponse{courseListResult:Courses:totalRow}", remarks = "total row", dataType = DataType.Int, defaultValue = "")
	//
	@ReturnDBParam(name = "CourseListResponse{courseListResult:Courses:list[Course:$]}", column = Course.course_id)
	@ReturnDBParam(name = "CourseListResponse{courseListResult:Courses:list[Course:$]}", column = Course.user_id)   
	@ReturnDBParam(name = "CourseListResponse{courseListResult:Courses:list[Course:$]}", column = Course.title)
	@ReturnDBParam(name = "CourseListResponse{courseListResult:Courses:list[Course:$]}", column = Course.category_01_name)
	@ReturnDBParam(name = "CourseListResponse{courseListResult:Courses:list[Course:$]}", column = Course.category_02_name)
	@ReturnDBParam(name = "CourseListResponse{courseListResult:Courses:list[Course:$]}", column = Course.catetory_name)
	@ReturnDBParam(name = "CourseListResponse{courseListResult:Courses:list[Course:$]}", column = Course.session_rate)
	@ReturnDBParam(name = "CourseListResponse{courseListResult:Courses:list[Course:$]}", column = Course.total_score)
	@ReturnDBParam(name = "CourseListResponse{courseListResult:Courses:list[Course:$]}", column = Course.latitude)
	@ReturnDBParam(name = "CourseListResponse{courseListResult:Courses:list[Course:$]}", column = Course.longitude)
	@ReturnDBParam(name = "CourseListResponse{courseListResult:Courses:list[Course:$]}", column = Course.city)
	@ReturnDBParam(name = "CourseListResponse{courseListResult:Courses:list[Course:$]}", column = Course.area)
	@ReturnDBParam(name = "CourseListResponse{courseListResult:Courses:list[Course:$]}", column = Course.street)
	@ReturnDBParam(name = "CourseListResponse{courseListResult:Courses:list[Course:$]}", column = Course.address)
	@ReturnOutlet(name = "CourseListResponse{courseListResult:Courses:list[Course:total_coment_num]}", remarks = "评论总数", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CourseListResponse{courseListResult:Courses:list[Course:is_official]}", remarks = "是否官方认证：0-否，1-是", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CourseListResponse{courseListResult:Courses:list[Course:coach_name]}", remarks = "教练名称", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CourseListResponse{courseListResult:Courses:list[Course:coach_image]}", remarks = "教练图片", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CourseListResponse{courseListResult:Courses:list[Course:distance]}", remarks = "距离", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CourseListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CourseListResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CourseListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void courseList() {
		try {
			String category_02_id = getPara("category_02_id","0");
			String zipcode = getPara("zipcode","");
			String kw = getPara("kw");
			int pn = getParaToInt("pn", 1);
			int page_size = getParaToInt("page_size", 5);
			int type = getParaToInt("type", 1);
			String latitude = getPara("latitude");
			String longitude = getPara("longitude");
			String category_02_name = getPara("category_02_name","");
			String distances = getPara("distances");
			String distances_begin = getPara("distances_begin","0");
			int price_type = getParaToInt("price_type", 0);
			int review_type = getParaToInt("review_type", 0);
			int distance_type = getParaToInt("distance_type", 0);
			int hot_type = getParaToInt("hot_type", 0);
			int travel_to_session = getParaToInt("travel_to_session", 2);
			String session_rate_str = getPara("session_rate","");
			String session_rate_begin_str = getPara("session_rate_begin","");
			int status = 0;String message="";
			String filter_sql =" ";
			//1-直接从首页推荐分类进入，2-下拉选择category/二级分类进入
			if (type==1) {
				filter_sql = " status=1 and is_frozen=1 and catetory_id= "+category_02_id;
			}
			if (type==2) {
				filter_sql = " status=1 and is_frozen=1 and category_02_id= "+category_02_id;
			}
			//全部分类
			if (category_02_id.equals("0")) {
				filter_sql =" status=1 and is_frozen=1 ";
			}
			String order_by_sql = " order by sort asc,distance asc,is_hot_recommand desc,total_score desc,hot desc ,post_time desc";
			//SQL 中where条件不能用别名，只能 ("+distance_sql+")<500 这样用。
			String distance_sql= "round(6378.138*2*asin(sqrt(pow(sin( ("
					+ latitude
					+ "*pi()/180-latitude*pi()/180)/2),2)+cos("
					+ latitude
					+ "*pi()/180)*cos(latitude*pi()/180)* pow(sin( ("
					+ longitude
					+ "*pi()/180-longitude*pi()/180)/2),2)))*1000)";
			//教练名称或者课程名称
			if (kw!=null) {
				if (!kw.equals("")) {
					filter_sql = filter_sql + " and (category_02_name like '%"+kw+"%' or title like'%" + kw + "%' or user_id in(select user_id from user where nickname like'%" + kw + "%'))";
				}
			}
			//CATEGORY
			if (!category_02_name.equals("")) {
				filter_sql = filter_sql + " and category_02_name like'%" + category_02_name + "%'";
			}
			//DISTANCE 
			if (distances!=null) {
				int distance_double = Integer.parseInt(distances);
				double distance = distance_double*1609.344;
				if (!distances_begin.equals("0")) {
					int distances_begin_int = Integer.parseInt(distances_begin);
					double distance_begin = distances_begin_int*1609.344;
					filter_sql = filter_sql + " and ((("+distance_sql+")>"+distance_begin+" and ("+distance_sql+")<"+distance+") or ("+distance_sql+")< travel_to_session_distance_double "+" )";
				}else{
					filter_sql = filter_sql + " and (("+distance_sql+")<"+distance+" or ("+distance_sql+")< travel_to_session_distance_double "+" )";
				}
			}
			//price_type:按价格由低到高排列review_type:按评价由高到低排序distance_type:按距离由近到远排序hot_type:按热度由热到冷排序
			if (!zipcode.equals("")) {
				//filter_sql = filter_sql + " and zipcode like'%" + zipcode + "%'";
			}
			if (travel_to_session!=2) {
				filter_sql = filter_sql + " and travel_to_session =" + travel_to_session;
			}
			if (!session_rate_str.equals("")) {
				double session_rate = Double.parseDouble(session_rate_str);
				if (!session_rate_begin_str.equals("")) {
					double session_rate_begin = Double.parseDouble(session_rate_begin_str);
					filter_sql = filter_sql + " and (session_rate>="+session_rate_begin+" and session_rate <=" + session_rate+")" ;
				}else {
					filter_sql = filter_sql + " and session_rate <=" + session_rate ;
				}
			}
			if (price_type==1) {
				order_by_sql = " order by session_rate desc,sort asc,distance asc,is_hot_recommand desc ,hot desc ,post_time desc";
			}
			if (price_type==2) {
				order_by_sql = " order by session_rate asc,sort asc,distance asc,is_hot_recommand desc ,hot desc ,post_time desc";
			}
			if (review_type==1) {
				order_by_sql = " order by total_score desc,distance asc,sort asc,is_hot_recommand desc ,hot desc ,post_time desc";
			}
			if (review_type==2) {
				order_by_sql = " order by total_score asc,distance asc,sort asc,is_hot_recommand desc ,hot desc ,post_time desc";
			}
			if (distance_type==1) {
				order_by_sql = " order by distance desc,sort asc,is_hot_recommand desc,total_score desc,hot desc ,post_time desc";
			}
			if (distance_type==2) {
				order_by_sql = " order by distance asc,sort asc,is_hot_recommand desc,total_score desc,hot desc ,post_time desc";
			}
			if (hot_type==1) {
				order_by_sql = " order by hot desc,sort asc,distance asc,is_hot_recommand desc ,post_time desc";
			}
			if (hot_type==2) {
				order_by_sql = " order by hot asc,sort asc,distance asc,is_hot_recommand desc ,post_time desc";
			}
			String select = "select course_id,user_id,user_images_01,title,category_01_name,category_02_name,catetory_name,session_rate,total_score,latitude,longitude,"+distance_sql+" as distance,city,area,street,address ";
			final Page<Course> coursePage = Course.dao
					.paginate(
							pn,
							page_size,
							select," from course where "+filter_sql+order_by_sql);
			for(Course course:coursePage.getList()){
				//教练信息
				int course_id = course.get(course.course_id);
				String user_id = course.getStr(course.user_id);
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
			setAttr("CourseListResponse", responseValues);
			renderMultiJson("CourseListResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("CatetoryManage/courseList", "教练（课程）列表", this);
		}
	}
	@Author("cluo")
	@Rp("分类")
	@Explaination(info = "分类列表")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnDBParam(name = "CatetoryListResponse{catetoryListResult:Catetorys:list[Catetory:$]}", column = Catetory.catetory_id)
	@ReturnDBParam(name = "CatetoryListResponse{catetoryListResult:Catetorys:list[Catetory:$]}", column = Catetory.name)
	@ReturnOutlet(name = "CatetoryListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CatetoryListResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CatetoryListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void catetoryList() {
		try {
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			final List<Catetory> CatetoryList = new ArrayList<Catetory>();
			final List<Catetory> catetorys = Catetory.dao.find("select catetory_id,name from catetory where status=1 order by sort asc , post_time desc");
			for(Catetory catetory:catetorys){
				int catetory_id = catetory.get(catetory.catetory_id);
				List<Course> courses = Course.dao.find("select course_id from course where catetory_id=? and status=1 and is_frozen=1 order by sort asc , post_time desc limit 0, 4",catetory_id);
				if (courses.size()!=0) {
					CatetoryList.add(catetory);
				}
			}
			responseValues.put("Result", new HashMap<String, Object>() {
				{
					put("Catetorys", CatetoryList);
				}
			});
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "成功");
			setAttr("CatetoryListResponse", responseValues);
			renderMultiJson("CatetoryListResponse");
			AppLog.info("", getRequest());
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("CatetoryManage/catetoryList", "分类列表", this);
		}
	}
	@Author("cluo")
	@Rp("分类")
	@Explaination(info = "分类下的課程列表列表")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Catetory.catetory_id)
	@URLParam(defaultValue = "1", explain = Value.Infer, type = Type.String, name = "pn")
	@URLParam(defaultValue = "5", explain = Value.Infer, type = Type.String, name = "page_size")
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String, name = Course.latitude)
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String, name = Course.longitude)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	// 返回信息
	// page property
	@ReturnOutlet(name = "CatetoryCourseListResponse{catetoryCourseListResult:Courses:pageNumber}", remarks = "page number", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CatetoryCourseListResponse{catetoryCourseListResult:Courses:pageSize}", remarks = "result amount of this page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CatetoryCourseListResponse{catetoryCourseListResult:Courses:totalPage}", remarks = "total page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CatetoryCourseListResponse{catetoryCourseListResult:Courses:totalRow}", remarks = "total row", dataType = DataType.Int, defaultValue = "")
	//
	@ReturnDBParam(name = "CatetoryCourseListResponse{catetoryCourseListResult:Courses:list[Course:$]}", column = Course.course_id)
	@ReturnDBParam(name = "CatetoryCourseListResponse{catetoryCourseListResult:Courses:list[Course:$]}", column = Course.user_id)   
	@ReturnDBParam(name = "CatetoryCourseListResponse{catetoryCourseListResult:Courses:list[Course:$]}", column = Course.title)
	@ReturnDBParam(name = "CatetoryCourseListResponse{catetoryCourseListResult:Courses:list[Course:$]}", column = Course.catetory_name)
	@ReturnDBParam(name = "CatetoryCourseListResponse{catetoryCourseListResult:Courses:list[Course:$]}", column = Course.session_rate)
	@ReturnDBParam(name = "CatetoryCourseListResponse{catetoryCourseListResult:Courses:list[Course:$]}", column = Course.latitude)
	@ReturnDBParam(name = "CatetoryCourseListResponse{catetoryCourseListResult:Courses:list[Course:$]}", column = Course.longitude)
	@ReturnDBParam(name = "CatetoryCourseListResponse{catetoryCourseListResult:Courses:list[Course:$]}", column = Course.city)
	@ReturnDBParam(name = "CatetoryCourseListResponse{catetoryCourseListResult:Courses:list[Course:$]}", column = Course.area)
	@ReturnDBParam(name = "CatetoryCourseListResponse{catetoryCourseListResult:Courses:list[Course:$]}", column = Course.street)
	@ReturnDBParam(name = "CatetoryCourseListResponse{catetoryCourseListResult:Courses:list[Course:$]}", column = Course.address)
	@ReturnOutlet(name = "CatetoryCourseListResponse{catetoryCourseListResult:Courses:list[Course:is_official]}", remarks = "是否官方认证：0-否，1-是", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CatetoryCourseListResponse{catetoryCourseListResult:Courses:list[Course:coach_name]}", remarks = "教练名称", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CatetoryCourseListResponse{catetoryCourseListResult:Courses:list[Course:coach_image]}", remarks = "教练图片", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CatetoryCourseListResponse{catetoryCourseListResult:Courses:list[Course:distance]}", remarks = "距离", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CatetoryCourseListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CatetoryCourseListResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CatetoryCourseListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void catetoryCourseList() {
		try {
			int pn = getParaToInt("pn", 1);
			int page_size = getParaToInt("page_size", 5);
			String catetory_id = getPara("catetory_id");
			String latitude = getPara("latitude");
			String longitude = getPara("longitude");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("catetory_id", catetory_id);
			packageParams.put("latitude", latitude);
			packageParams.put("longitude", longitude);
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
				setAttr("CatetoryCourseListResponse", response2);
				renderMultiJson("CatetoryCourseListResponse");
				return;
			}
			/**
			 * 接口签名end
			 */
			int status = 0;String message="";
			String filter_sql =" status=1 and is_frozen=1 ";
			String order_by_sql = " order by sort asc,distance asc,is_hot_recommand desc,total_score desc,hot desc ,post_time desc";
			//SQL 中where条件不能用别名，只能 ("+distance_sql+")<500 这样用。
			String distance_sql= "round(6378.138*2*asin(sqrt(pow(sin( ("
					+ latitude
					+ "*pi()/180-latitude*pi()/180)/2),2)+cos("
					+ latitude
					+ "*pi()/180)*cos(latitude*pi()/180)* pow(sin( ("
					+ longitude
					+ "*pi()/180-longitude*pi()/180)/2),2)))*1000)";
			String select = "select course_id,user_id,user_images_01,title,catetory_name,session_rate,latitude,longitude,"+distance_sql+" as distance,city,area,street,address ";
			final Page<Course> coursePage = Course.dao
					.paginate(
							pn,
							page_size,
							select," from course where "+filter_sql+" and catetory_id=? "+order_by_sql,catetory_id);
			for(Course course:coursePage.getList()){
				//教练信息
				String user_id = course.getStr(course.user_id);
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
			setAttr("CatetoryCourseListResponse", responseValues);
			renderMultiJson("CatetoryCourseListResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("CatetoryManage/catetoryCourseList", "分类下的課程列表列表", this);
		}
	}
}
