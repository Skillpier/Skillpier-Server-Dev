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
import com.quark.app.logs.AppLog;
import com.quark.common.AppData;
import com.quark.common.RongToken;
import com.quark.common.Storage;
import com.quark.common.config;
import com.quark.interceptor.AppToken;
import com.quark.model.extend.Category01;
import com.quark.model.extend.Category02;
import com.quark.model.extend.Catetory;
import com.quark.model.extend.CityBean;
import com.quark.model.extend.Comment;
import com.quark.model.extend.Course;
import com.quark.model.extend.IndexBanner;
import com.quark.model.extend.Tokens;
import com.quark.model.extend.User;
import com.quark.model.extend.ZipCode;
import com.quark.sign.RequestHandler;
import com.quark.utils.DateUtils;
import com.quark.utils.FileUtils;
import com.quark.utils.MD5Util;
import com.quark.utils.MessageUtils;

/**
 * @author C罗
 * 首页
 *
 */
@Before(Tx.class)
public class Home extends Controller {

	@Author("cluo")
	@Rp("主页")
	@Explaination(info = "两大类")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnDBParam(name = "Category01ListResponse{category01ListResult:Category01s:list[Category01:$]}", column = Category01.category_01_id)
	@ReturnDBParam(name = "Category01ListResponse{category01ListResult:Category01s:list[Category01:$]}", column = Category01.image_01)
	@ReturnDBParam(name = "Category01ListResponse{category01ListResult:Category01s:list[Category01:$]}", column = Category01.big_image_01)
	@ReturnDBParam(name = "Category01ListResponse{category01ListResult:Category01s:list[Category01:$]}", column = Category01.category_describe)
	@ReturnDBParam(name = "Category01ListResponse{category01ListResult:Category01s:list[Category01:$]}", column = Category01.category_01_name)
	@ReturnOutlet(name = "Category01ListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "Category01ListResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "Category01ListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	@Before(CacheInterceptor.class)
	@CacheName("sweetinfo")
	public void category01List() {
		try {
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			final List<Category01> category01s = Category01.dao.find("select category_01_id,category_01_name,category_describe,image_01,big_image_01 from category_01 where status=1 order by sort asc");
			responseValues.put("Result", new HashMap<String, Object>() {
				{
					put("Category01s", category01s);
				}
			});
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "成功");
			setAttr("Category01ListResponse", responseValues);
			renderMultiJson("Category01ListResponse");
			AppLog.info("", getRequest());
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("Home/category01List", "两大类", this);
		}
	}
	@Author("cluo")
	@Rp("主页")
	@Explaination(info = "热门推荐新列表")
	@URLParam(defaultValue = "1", explain = Value.Infer, type = Type.String, name = "pn")
	@URLParam(defaultValue = "5", explain = Value.Infer, type = Type.String, name = "page_size")
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	//检索
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	// 返回信息
	// page property
	@ReturnOutlet(name = "HotRedcommandListNewResponse{hotRedcommandListNewResult:HotRedcommandListNew:pageNumber}", remarks = "page number", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "HotRedcommandListNewResponse{hotRedcommandListNewResult:HotRedcommandListNew:pageSize}", remarks = "result amount of this page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "HotRedcommandListNewResponse{hotRedcommandListNewResult:HotRedcommandListNew:totalPage}", remarks = "total page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "HotRedcommandListNewResponse{hotRedcommandListNewResult:HotRedcommandListNew:totalRow}", remarks = "total row", dataType = DataType.Int, defaultValue = "")
	//
	@ReturnDBParam(name = "HotRedcommandListNewResponse{hotRedcommandListNewResult:HotRedcommandListNew:list[Course:$]}", column = Course.course_id)
	@ReturnDBParam(name = "HotRedcommandListNewResponse{hotRedcommandListNewResult:HotRedcommandListNew:list[Course:$]}", column = Course.user_id)   
	@ReturnDBParam(name = "HotRedcommandListNewResponse{hotRedcommandListNewResult:HotRedcommandListNew:list[Course:$]}", column = Course.title)
	@ReturnDBParam(name = "HotRedcommandListNewResponse{hotRedcommandListNewResult:HotRedcommandListNew:list[Course:$]}", column = Course.user_images_01)
	@ReturnDBParam(name = "HotRedcommandListNewResponse{hotRedcommandListNewResult:HotRedcommandListNew:list[Course:$]}", column = Course.session_rate)
	@ReturnOutlet(name = "HotRedcommandListNewResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "HotRedcommandListNewResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "HotRedcommandListNewResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void hotRedcommandListNew() {
		try {
			int pn = getParaToInt("pn", 1);
			int page_size = getParaToInt("page_size", 5);
			/** 
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
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
				setAttr("HotRedcommandListNewResponse", response2);
				renderMultiJson("HotRedcommandListNewResponse");
				return;
			}
			/**
			 * 接口签名end
			 */
			int status = 0;String message="";
			String filter_sql =" status=1 and is_hot_recommand=1 and is_frozen=1 ";
			String order_by_sql = " order by sort asc,is_hot_recommand desc,total_score desc,hot desc ,post_time desc";
			String select = "select course_id,user_id,user_images_01,title,session_rate ";
			final Page<Course> coursePage = Course.dao
					.paginate(
							pn,
							page_size,
							select," from course where "+filter_sql+order_by_sql);
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("Result", new HashMap<String, Object>() {
				{
					put("HotRedcommandListNew", coursePage);
				}
			});
			responseValues.put("message", "Data request successful.");
			setAttr("HotRedcommandListNewResponse", responseValues);
			renderMultiJson("HotRedcommandListNewResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("Home/hotRedcommandListNew", "", this);
		}
	}
	/**
	 * 主页仅两个主分类，后台可以配置图标，名称，目前定sports，arts
	 * 推荐分类跟主类没有关系
	 */
	@Author("cluo")
	@Rp("主页")
	@Explaination(info = "热门分类列表")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnDBParam(name = "HotRecommandListResponse{hotRecommandListResult:Catetorys:list[Catetory:$]}", column = Catetory.catetory_id)
	@ReturnDBParam(name = "HotRecommandListResponse{hotRecommandListResult:Catetorys:list[Catetory:$]}", column = Catetory.sub_title)
	@ReturnDBParam(name = "HotRecommandListResponse{hotRecommandListResult:Catetorys:list[Catetory:$]}", column = Catetory.image_01)
	@ReturnDBParam(name = "HotRecommandListResponse{hotRecommandListResult:Catetorys:list[Catetory:$]}", column = Catetory.big_image_01)
	@ReturnDBParam(name = "HotRecommandListResponse{hotRecommandListResult:Catetorys:list[Catetory:$]}", column = Catetory.name)
	@ReturnDBParam(name = "HotRecommandListResponse{hotRecommandListResult:Catetorys:list[Catetory:Courses:list[Course:$]]}", column = Course.course_id)
	@ReturnDBParam(name = "HotRecommandListResponse{hotRecommandListResult:Catetorys:list[Catetory:Courses:list[Course:$]]}", column = Course.title)
	@ReturnDBParam(name = "HotRecommandListResponse{hotRecommandListResult:Catetorys:list[Catetory:Courses:list[Course:$]]}", column = Course.user_id)
	@ReturnDBParam(name = "HotRecommandListResponse{hotRecommandListResult:Catetorys:list[Catetory:Courses:list[Course:$]]}", column = Course.session_rate)
	@ReturnDBParam(name = "HotRecommandListResponse{hotRecommandListResult:Catetorys:list[Catetory:Courses:list[Course:$]]}", column = Course.user_images_01)
	@ReturnOutlet(name = "HotRecommandListResponse{hotRecommandListResult:Catetorys:list[Catetory:Courses:list[Course:coach_name]]}", remarks = "教练名称", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "HotRecommandListResponse{hotRecommandListResult:Catetorys:list[Catetory:Courses:list[Course:coach_image]]}", remarks = "教练头像", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "HotRecommandListResponse{hotRecommandListResult:Catetorys:list[Catetory:Courses:list[Course:is_official]]}", remarks = "是否官方认证：0-否，1-是", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "HotRecommandListResponse{hotRecommandListResult:Catetorys:list[Catetory:Courses:list[Course:is_stop_course]]}", remarks = "今后暂停课程 0-否，1-是", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "HotRecommandListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "HotRecommandListResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "HotRecommandListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void hotRecommandList() {
		try {
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			final List<Catetory> CatetoryList = new ArrayList<Catetory>();
			final List<Catetory> catetorys = Catetory.dao.find("select catetory_id,name,sub_title,image_01,big_image_01 from catetory where status=1 and is_hot_recommand=1 order by sort asc , post_time desc");
			for(Catetory catetory:catetorys){
				int catetory_id = catetory.get(catetory.catetory_id);
				String invoke = getPara("invoke","app");
				String fiter_sql = " limit 0, 4 ";
				if (invoke.equals("h5")) {
					fiter_sql = " limit 0, 6 ";
				}
				List<Course> courses = Course.dao.find("select course_id,title,session_rate,user_id,user_images_01 from course where status=1 and catetory_id=? and is_hot_recommand=1 and is_frozen=1 order by sort asc , post_time desc "+fiter_sql,catetory_id);
				if (courses.size()!=0) {
					for(Course course : courses){
						String user_id = course.getStr(course.user_id);
						User user = User.dao.findById(user_id);
						String coach_name="",coach_image=course.getStr(course.user_images_01);
						int is_official=0,is_stop_course=0;
						if (user!=null) {
							is_official = user.get(user.is_official);
							coach_name=user.getStr(user.nickname);
							is_stop_course = user.get(user.is_stop_course);
						}
						course.put("is_stop_course", is_stop_course);
						course.put("is_official", is_official);
						course.put("coach_name", coach_name);
						course.put("coach_image", coach_image);
						course.put("big_coach_image", coach_image);
					}
					catetory.put("Courses", courses);
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
			setAttr("HotRecommandListResponse", responseValues);
			renderMultiJson("HotRecommandListResponse");
			AppLog.info("", getRequest());
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("Home/hotRecommandList", "两大类", this);
		}
	}
	@Author("cluo")
	@Rp("搜索")
	@Explaination(info = "搜索列表")
	@URLParam(defaultValue = "", explain = "教练或者课程名", type = Type.String_NotRequired, name = "kw")
	@URLParam(defaultValue = "", explain = "地区", type = Type.String_NotRequired, name = Course.zipcode)
	@URLParam(defaultValue = "1", explain = Value.Infer, type = Type.String, name = "pn")
	@URLParam(defaultValue = "5", explain = Value.Infer, type = Type.String, name = "page_size")
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String, name = Course.latitude)
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String, name = Course.longitude)
	//检索
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String_NotRequired, name = Course.category_02_name)
	@URLParam(defaultValue = "", explain = "距离", type = Type.String_NotRequired, name = "distances")
	@URLParam(defaultValue = "", explain = "价格刷选", type = Type.String_NotRequired, name = "session_rate")
	@URLParam(defaultValue = "{0、1、2}", explain = "价格排序：0-不需要， 1-高,2-低切换", type = Type.String_NotRequired, name = "price_type")
	@URLParam(defaultValue = "{0、1、2}", explain = "评价排序：0-不需要， 1-高,2-低切换", type = Type.String_NotRequired, name = "review_type")
	@URLParam(defaultValue = "{0、1、2}", explain = "距离排序：0-不需要， 1-远,2-近切换", type = Type.String_NotRequired, name = "distance_type")
	@URLParam(defaultValue = "{0、1、2}", explain = "热度排序：0-不需要， 1-热,2-冷切换", type = Type.String_NotRequired, name = "hot_type")
	@URLParam(defaultValue = "{0、1}", explain = "是否上门服务:1-是，0-否", type = Type.String_NotRequired, name = "travel_to_session")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	// 返回信息
	// page property
	@ReturnOutlet(name = "SearchListResponse{SearchListResult:Courses:pageNumber}", remarks = "page number", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "SearchListResponse{SearchListResult:Courses:pageSize}", remarks = "result amount of this page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "SearchListResponse{SearchListResult:Courses:totalPage}", remarks = "total page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "SearchListResponse{SearchListResult:Courses:totalRow}", remarks = "total row", dataType = DataType.Int, defaultValue = "")
	//
	@ReturnDBParam(name = "SearchListResponse{SearchListResult:Courses:list[Course:$]}", column = Course.course_id)
	@ReturnDBParam(name = "SearchListResponse{SearchListResult:Courses:list[Course:$]}", column = Course.user_id)   
	@ReturnDBParam(name = "SearchListResponse{SearchListResult:Courses:list[Course:$]}", column = Course.title)
	@ReturnDBParam(name = "SearchListResponse{SearchListResult:Courses:list[Course:$]}", column = Course.catetory_name)
	@ReturnDBParam(name = "SearchListResponse{SearchListResult:Courses:list[Course:$]}", column = Course.session_rate)
	@ReturnDBParam(name = "SearchListResponse{SearchListResult:Courses:list[Course:$]}", column = Course.total_score)
	@ReturnDBParam(name = "SearchListResponse{SearchListResult:Courses:list[Course:$]}", column = Course.latitude)
	@ReturnDBParam(name = "SearchListResponse{SearchListResult:Courses:list[Course:$]}", column = Course.longitude)
	@ReturnDBParam(name = "SearchListResponse{SearchListResult:Courses:list[Course:$]}", column = Course.city)
	@ReturnDBParam(name = "SearchListResponse{SearchListResult:Courses:list[Course:$]}", column = Course.area)
	@ReturnDBParam(name = "SearchListResponse{SearchListResult:Courses:list[Course:$]}", column = Course.street)
	@ReturnDBParam(name = "SearchListResponse{SearchListResult:Courses:list[Course:$]}", column = Course.address)
	@ReturnOutlet(name = "SearchListResponse{SearchListResult:Courses:list[Course:total_coment_num]}", remarks = "评论总数", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "SearchListResponse{SearchListResult:Courses:list[Course:is_official]}", remarks = "是否官方认证：0-否，1-是", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "SearchListResponse{SearchListResult:Courses:list[Course:is_stop_course]}", remarks = "今后暂停课程 0-否，1-是", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "SearchListResponse{SearchListResult:Courses:list[Course:coach_name]}", remarks = "教练名称", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "SearchListResponse{SearchListResult:Courses:list[Course:coach_image]}", remarks = "教练图片", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "SearchListResponse{SearchListResult:Courses:list[Course:distance]}", remarks = "距离", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "SearchListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "SearchListResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "SearchListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void searchList() {
		try {
			String kw = getPara("kw","");
			String zipcode = getPara("zipcode");
			int pn = getParaToInt("pn", 1);
			int page_size = getParaToInt("page_size", 5);
			String latitude = getPara("latitude");
			String longitude = getPara("longitude");
			String category_02_name = getPara("category_02_name","");
			String distances = getPara("distances");
			
			int price_type = getParaToInt("price_type", 0);
			int review_type = getParaToInt("review_type", 0);
			int distance_type = getParaToInt("distance_type", 0);
			int hot_type = getParaToInt("hot_type", 0);
			
			int travel_to_session = getParaToInt("travel_to_session", 2);
			String session_rate_str = getPara("session_rate","");
			/** 
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("category_02_name", category_02_name);
			packageParams.put("distances", distances);
			packageParams.put("distance_type", distance_type+"");
			packageParams.put("hot_type", hot_type+"");
			packageParams.put("kw", kw);
			packageParams.put("latitude", latitude);
			packageParams.put("longitude", longitude);
			packageParams.put("page_size", page_size+"");
			packageParams.put("pn", pn+"");
			packageParams.put("price_type", price_type+"");
			packageParams.put("review_type", review_type+"");
			packageParams.put("session_rate", session_rate_str);
			packageParams.put("zipcode", zipcode);
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
				setAttr("SearchListResponse", response2);
				renderMultiJson("SearchListResponse");
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
			//教练名称或者课程名称
			if (!kw.equals("")) {
				kw = kw.trim();
				if (kw.equals("Tennis")||kw.equals("tennis")) {
					filter_sql = filter_sql + " and category_02_name!='Table tennis' and (category_02_name like '%"+kw+"%' or title like'%" + kw + "%' or user_id in(select user_id from user where nickname like'%" + kw + "%'))";
				}else {
					filter_sql = filter_sql + " and (category_02_name like '%"+kw+"%' or title like'%" + kw + "%' or user_id in(select user_id from user where nickname like'%" + kw + "%'))";
				}
			}
			//CATEGORY
			if (!category_02_name.equals("")) {
				category_02_name = category_02_name.trim();
				if (category_02_name.equals("Tennis")||category_02_name.equals("tennis")) {
					filter_sql = filter_sql + " and category_02_name like'%" + category_02_name + "%' and category_02_name!='Table tennis'";
				}else {
					filter_sql = filter_sql + " and category_02_name like'%" + category_02_name + "%'";
				}
			}
			//DISTANCE 
			if (distances!=null) {
				int distance_double = Integer.parseInt(distances);
				double distance = distance_double*1609.344;
				filter_sql = filter_sql + " and (("+distance_sql+")<"+distance+" or ("+distance_sql+")< travel_to_session_distance_double "+" )";
			}
			if (travel_to_session!=2) {
				filter_sql = filter_sql + " and travel_to_session =" + travel_to_session;
			}
			if (!session_rate_str.equals("")) {
				double session_rate = Double.parseDouble(session_rate_str);
				filter_sql = filter_sql + " and session_rate <=" + session_rate ;
			}
			// prioritys
			//price_type:按价格由低到高排列review_type:按评价由高到低排序distance_type:按距离由近到远排序hot_type:按热度由热到冷排序
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
			System.out.println(select+" from course where "+filter_sql+order_by_sql);
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
					course.put("distance", distance_miles+"miles");
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
			setAttr("SearchListResponse", responseValues);
			renderMultiJson("SearchListResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("Home/searchList", "搜索", this);
		}
	}
	@Author("cluo")
	@Rp("二级分类")
	@Explaination(info = "二级分类分类列表")
	@URLParam(defaultValue = "{select category_01_id from category_02 group by category_01_id}", explain = Value.Infer, type = Type.String, name = Category02.category_01_id)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnDBParam(name = "Category02ListResponse{category02ListResult:Category02s:list[Category02:$]}", column = Category02.image_01)
	@ReturnDBParam(name = "Category02ListResponse{category02ListResult:Category02s:list[Category02:$]}", column = Category02.category_02_name)
	@ReturnDBParam(name = "Category02ListResponse{category02ListResult:Category02s:list[Category02:$]}", column = Category02.category_describe)
	@ReturnDBParam(name = "Category02ListResponse{category02ListResult:Category02s:list[Category02:$]}", column = Category02.category_01_id)
	@ReturnOutlet(name = "Category02ListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "Category02ListResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "Category02ListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	//缓存
	@Before(CacheInterceptor.class)
	@CacheName("sweets_list")
	public void category02List() {
		try {
			String category_01_id = getPara("category_01_id");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("category_01_id", category_01_id);
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
				setAttr("Category02ListResponse", response2);
				renderMultiJson("Category02ListResponse");
				return;
			}
			/**
			 * 接口签名end
			 */
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			final List<Category02> category02s = new ArrayList<Category02>();
			List<Category02> category02List = Category02.dao.find("select category_02_id,category_02_name,category_describe,image_01,big_image_01 from category_02 where category_01_id=? and status=1 order by sort asc ,post_time desc",category_01_id);
			for(Category02 category02:category02List){
				/*int category_02_id = category02.get(category02.category_02_id);
				Course course = Course.dao.findFirst("select category_02_name from course where status=1 and category_02_id=?",category_02_id);
				if (course!=null) {
				}*/
				category02s.add(category02);
			}
			responseValues.put("Result", new HashMap<String, Object>() {
				{
					put("Category02s", category02s);
				}
			});
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "成功");
			setAttr("Category02ListResponse", responseValues);
			renderMultiJson("Category02ListResponse");
			AppLog.info("", getRequest());
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("Home/category02List", "二级分类分类列表", this);
		}
	}
	@Author("cluo")
	@Rp("主页")
	@Explaination(info = "城市列表")
	@URLParam(defaultValue = "", explain = "", type = Type.String_NotRequired, name = "kw")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnDBParam(name = "CityListResponse{cityListResult:CityBeans:list[CityBean:$]}", column = ZipCode.zipcode)
	@ReturnDBParam(name = "CityListResponse{cityListResult:CityBeans:list[CityBean:$]}", column = ZipCode.county)
	@ReturnDBParam(name = "CityListResponse{cityListResult:CityBeans:list[CityBean:$]}", column = ZipCode.city)
	@ReturnOutlet(name = "CityListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CityListResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CityListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	@Before(CacheInterceptor.class)
	@CacheName("sweetme")
	public void cityList() {
		try {
			String kw = getPara("kw","");
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			String fitler_sql = "1=1";
			fitler_sql = "zipcode like'"+kw+"__' or zipcode like'"+kw+"_' or zipcode like'"+kw+"'";
			if (!kw.equals("")) {
			}
			final List<ZipCode> cityBeans = ZipCode.dao.find("select county,city,zipcode from zip_code where "+fitler_sql);
			responseValues.put("Result", new HashMap<String, Object>() {
				{
					put("CityBeans", cityBeans);
				}
			});
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "成功");
			setAttr("CityListResponse", responseValues);
			renderMultiJson("CityListResponse");
			AppLog.info("", getRequest());
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("Home/category01List", "两大类", this);
		}
	}
	@Author("cluo")
	@Rp("主页")
	@Explaination(info = "城市列表")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnDBParam(name = "CityListResponse{cityListResult:CityBeans:list[CityBean:$]}", column = CityBean.zipcode)
	@ReturnDBParam(name = "CityListResponse{cityListResult:CityBeans:list[CityBean:$]}", column = CityBean.latitude)
	@ReturnDBParam(name = "CityListResponse{cityListResult:CityBeans:list[CityBean:$]}", column = CityBean.county)
	@ReturnDBParam(name = "CityListResponse{cityListResult:CityBeans:list[CityBean:$]}", column = CityBean.longitude)
	@ReturnDBParam(name = "CityListResponse{cityListResult:CityBeans:list[CityBean:$]}", column = CityBean.city)
	@ReturnOutlet(name = "CityListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CityListResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CityListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void cityListOld() {
		try {
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			final List<CityBean> cityBeans = CityBean.dao.find("select county,city,zipcode,longitude,latitude from city_bean");
			responseValues.put("Result", new HashMap<String, Object>() {
				{
					put("CityBeans", cityBeans);
				}
			});
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "成功");
			setAttr("CityListResponse", responseValues);
			renderMultiJson("CityListResponse");
			AppLog.info("", getRequest());
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("Home/category01List", "两大类", this);
		}
	}
}
