package com.quark.admin.controller;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import com.quark.app.bean.ScheduleBean2;
import com.quark.common.config;
import com.quark.interceptor.Login;
import com.quark.interceptor.Privilege;
import com.quark.model.extend.Category01;
import com.quark.model.extend.Category02;
import com.quark.model.extend.Catetory;
import com.quark.model.extend.Collection;
import com.quark.model.extend.Course;
import com.quark.model.extend.CourseCertification;
import com.quark.model.extend.Orders;
import com.quark.model.extend.User;
import com.quark.utils.DateUtils;
import com.quark.utils.FileUtils;
import com.quarkso.utils.DateUitls;

/**
 * 普通课程管理  
 * @author Administrator
 *
 */
@Before(Login.class)
public class ConmmonCourse extends Controller {

	public void list()throws ParseException {
		int currentPage = getParaToInt("pn", 1);
		int status = getParaToInt("status", 1);
		String shi = getPara("city", "请选择"); // 市
		String qu = getPara("area", "请选择"); // 区
		String title = getPara("title", "");
		String message="list";
		String filter_sql= " status ="+status;
		if (!title.equals("")) {
			title = title.trim();
			filter_sql = filter_sql + " and (title like '%" + title + "%' or category_02_name like'"+title+"')";
			message="search";
		}
		setAttr("city", shi);
		setAttr("area", qu);
		setAttr("title", title);	
		setAttr("action", message);	
		Page<Course> coursePage = Course.dao.paginate(
				currentPage, 
				PAGE_SIZE, 
				"select * ", 
				"from course where "+ filter_sql + " order by sort asc, post_time desc");
		for(Course course:coursePage.getList()){
			String user_id=course.getStr(course.user_id);
			User user = User.dao.findFirst("select is_stop_course,email from user where user_id=?",user_id);
			if(user!=null){
				course.put("is_stop_course", user.get(user.is_stop_course));
				course.put("emial", user.getStr(user.email));
			}
		}
		Course normol_count = Course.dao
				.findFirst("select count(*) as normol_count from course where status=1");
		Course nodo_count = Course.dao
				.findFirst("select count(*) as nodo_count from course where status=0");
		Course nodo_status = Course.dao
				.findFirst("select count(*) as nodo_status from course where status=2");

		//传给更新到推荐列表的下拉框
		List<Catetory> catetory=Catetory.dao.find("select * from catetory");
		setAttr("s",catetory);
		setAttr("normol_count", normol_count.get("normol_count"));
		setAttr("nodo_count", nodo_count.get("nodo_count"));
		setAttr("nodo_status", nodo_status.get("nodo_status"));
		setAttr("list", coursePage);
		setAttr("status", status);
		setAttr("pn", currentPage);
		render("/admin/CommonCourse.html");
	}
	/**
	 * 课程详情
	 */
	public void courseInfo(){	
		int course_id=getParaToInt("course_id");
		Course course=Course.dao.findById(course_id);
		if(course!=null){
			List<CourseCertification> courseCertification=CourseCertification.dao.find("select * from course_certification where course_id=?", course_id);	
			course.put("courseCertification", courseCertification);
		}
		setAttr("r", course);
		//查询一级分类
		List<Category01> category=Category01.dao.find("select category_01_id,category_01_name from category_01 where status=1");
		setAttr("category",category);
		//查询二级分类
		List<Category02> category2=Category02.dao.find("select category_02_id,category_02_name from category_02 where status=1");
		setAttr("category2",category2);
		render("/admin/courseInfo.html");
	}
	/**
	 * 编辑课程
	 */
	@Before(Privilege.class)
	public void modifyConmmonCourse(){	
		UploadFile uploadFile=getFile("user_images_01", config.save_path);
		int course_id=getParaToInt("course_id");
		Course courses=Course.dao.findById(course_id);
		if(uploadFile!=null){
			courses.set(courses.user_images_01, FileUtils.renameToFile(uploadFile, 100, 100));
		}
		double old_session_rate = courses.getDouble(courses.session_rate);
		String category_01_names=getPara("category_01_name","");
		String category_02_names=getPara("category_02_name","");
		String session_length=getPara("session_length");
		String session_rate=getPara("session_rate");
		String city=getPara("city","");
		String area=getPara("area","");
		String street=getPara("street","");
		String address=getPara("address","");
		String zipcode=getPara("zipcode","");
		String teaching_since=getPara("teaching_since");
		String teaching_age=getPara("teaching_age");		
		String travel_to_session=getPara("travel_to_session");
		int travel_to_session_distance=getParaToInt("travel_to_session_distance",0);
		String travel_to_session_trafic_surcharge=getPara("travel_to_session_trafic_surcharge");
		String additional_partner=getPara("additional_partner");
		String surcharge_for_each=getPara("surcharge_for_each");
		String title=getPara("title","");
		String achievements=getPara("achievements","");
		String specialist=getPara("specialist","");
		String overview=getPara("overview");
		double new_session_rate = Double.parseDouble(session_rate);
		int status=getParaToInt("status");
		boolean update = courses.set(courses.status, status)
						.set(courses.category_01_name, category_01_names.split("#")[1])
						.set(courses.category_01_id, category_01_names.split("#")[0])
						.set(courses.category_02_name, category_02_names.split("#")[1])
						.set(courses.category_02_id, category_02_names.split("#")[0])
						.set(courses.session_rate, session_rate)
						.set(courses.city, city)
						.set(courses.area, area)
						.set(courses.street, street)
						.set(courses.zipcode, zipcode)
						.set(courses.address, address)
						.set(courses.teaching_since, teaching_since)
						.set(courses.travel_to_session, travel_to_session)
						.set(courses.travel_to_session_distance, travel_to_session_distance)
						.set(courses.travel_to_session_distance_double, travel_to_session_distance*1609.344)
						.set(courses.travel_to_session_trafic_surcharge, travel_to_session_trafic_surcharge)
						.set(courses.additional_partner, additional_partner)
						.set(courses.surcharge_for_each, surcharge_for_each)
						.set(courses.title, title)
						.set(courses.achievements, achievements)
						.set(courses.specialist, specialist)
						.set(courses.overview, overview)
						.set(courses.teaching_age, teaching_age)
						.set(courses.post_time, DateUtils.getCurrentDateTime())
						.update();
		if (update) {
			if (status==0) {
				//收藏表
				List<Collection> coList = Collection.dao.find("select collection_id from collection where course_id=?",course_id);
				for(Collection collection:coList){
					collection.delete();
				}
			}
			//修改未付款订单为失效订单
			List<Orders> ordersList = Orders.dao.find("select * from orders where (status=11 or status=1) and course_id=?",course_id);
			for(Orders orders2 :ordersList){
				int orders_status = orders2.get(orders2.status);
				int orders_id = orders2.get(orders2.orders_id);
				System.out.println(orders_id+"="+old_session_rate+""+"new_session_rate"+new_session_rate+"orders_status="+orders_status);
				if (status==0||(old_session_rate!=new_session_rate)) {
					if (orders_status==1) {
						orders2.set(orders2.status, 2)
							   .set(orders2.booking_status, 2);
					}
					if (orders_status==11) {
						orders2.set(orders2.status, 10)
							   .set(orders2.booking_status, 2);
					}
				}
				orders2.update();
			}
		}
		redirect("/admin/ConmmonCourse/list");
	}
	/**
	 * 下线
	 */
	@Before(Privilege.class)
	public void freeze() {
		int course_id = getParaToInt("course_id");
		Course course = Course.dao.findById(course_id);
		boolean update = course.set("status", 0)
				.set(course.post_time, DateUtils.getCurrentDateTime())
				.update();
		if (update) {
			//收藏表
			List<Collection> coList = Collection.dao.find("select collection_id from collection where course_id=?",course_id);
			for(Collection collection:coList){
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
		}
		redirect("/admin/ConmmonCourse/list?status=0");
	}
	
	/**
	 * 上线
	 */
	@Before(Privilege.class)
	public void unFreeze() {
		int course_id = getParaToInt("course_id");
		Course course = Course.dao.findById(course_id);
		boolean update = course.set(course.status, 1)
						.set(course.post_time, DateUtils.getCurrentDateTime())
						.update();
		redirect("/admin/ConmmonCourse/list?status=1");
	}
	/**
	 * 一键上下线
	 */
	@Before(Privilege.class)
	public void allUnFreeze() {
		String ids = getPara("ids");
		int type = getParaToInt("type");
		String[] idtemp = ids.split(",");
		int status=0;
		for (int i = 0; i < idtemp.length; i++){
			Course course = Course.dao.findById(idtemp[i]);
			if(course!=null){
				if (type==2) {
					course.set(course.status, 1);
					status=1;
				}
				if (type==3) {//下线
					course.set(course.status, 0);
					status=0;
					//删除收藏表
					List<Collection> coList = Collection.dao.find("select collection_id from collection where course_id=?",idtemp[i]);
					for(Collection collection:coList){
						collection.delete();
					}
					//修改未付款订单为失效订单
					List<Orders> orders = Orders.dao.find("select orders_id,status from orders where (status=11 or status=1) and course_id=?",idtemp[i]);
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
				if (type==4) {
					course.set(course.status, 1);
					status=1;
				}
				course.set(course.post_time, DateUitls.getCurrentDateTime()).update();	
			}
		}
		redirect("/admin/ConmmonCourse/list?status=1");
	}
	/**
	 * 置顶发布任务：....3、2、1倒排,0按时间排序
	 * by:cluo
	 */
	public void updateOrder(){
		int course_id = getParaToInt("course_id");
		try {
			int sort = getParaToInt("sort");
			if(sort>-1){
				Course course = Course.dao.findById(course_id);
				int status = course.get("status");
				List<Course> newList = Course.dao.find("select * from course where status="+status+" and sort>="+sort+" and sort<11011111 and course_id!="+course_id+"  order by sort asc,post_time desc");
				for(Course product2:newList){
					int sort_2 = product2.get("sort");
					int product_id_2 =  product2.get("course_id");
					Course product3 = Course.dao.findById(product_id_2);
					product3.set("sort", sort_2+1).update();
				}
				boolean save = course.set("sort", sort).update();
				renderJson("message",save);
			}else{
				renderJson("message",false);
			}
		} catch (Exception e) {
			renderJson("message",false);
		}
	}
	/**
	 * 设置为推荐分类
	 */
	@Before(Privilege.class)
	public void addModifyCatetory(){
		String catetory_name=getPara("catetory_name");
		int course_id=getParaToInt("course_id");
		String []temp = catetory_name.split(":");
		int catetory_id = Integer.parseInt(temp[0]);
		catetory_name = temp[1];
		Course course=Course.dao.findById(course_id);
		if(course!=null){
			course.set("catetory_id", catetory_id)
			.set("catetory_name", catetory_name)
			.update();
		}
		redirect("/admin/ConmmonCourse/list");
	}
	
	
	//异步查询二级分类
	public void selectGory02(){
		String categoryid01=getPara("categoryid01");
		List<Category02> category=null;
		if(categoryid01!=null){
			category=Category02.dao.find("select category_02_id,category_02_name from category_02 where status=1 and category_01_id="+categoryid01);
		}
		renderJson("category",category);
	}
	
	//修改证书
	@Before(Privilege.class)
	public void modifyCertification(){
		int course_certification_id=getParaToInt("course_certification_id");
		if(course_certification_id!=0){
			CourseCertification certification =CourseCertification.dao.findById(course_certification_id);
			setAttr("r",certification);	
		}
		int message=getParaToInt("message",0);
		if(message!=0){
			if(message==1){
				setAttr("message",message);
			}
		}
		render("/admin/CertificationModify.html");
	}
	@Before(Privilege.class)
	public void certificationModify(){
		UploadFile uploadFile=getFile("image_01", config.save_path);
		
		String name=getPara("name");
		String institue=getPara("institue");
		String get_time=getPara("get_time");
		int course_id=getParaToInt("course_id");
		int course_certification_id=getParaToInt("course_certification_id");
		CourseCertification certification =CourseCertification.dao.findById(course_certification_id);
		if(uploadFile!=null){	
			certification.set(certification.image_01, FileUtils.renameToFile(uploadFile, 100, 100));
		}
			boolean update=certification.set(certification.name, name)		
															.set(certification.institue, institue)
															.set(certification.get_time, get_time)
															.set(certification.post_time, DateUtils.getCurrentDateTime())													
															.update();		
			redirect("/admin/ConmmonCourse/courseInfo?course_id="+course_id);
			
	}
	//增加证书
	@Before(Privilege.class)
	public void addCertification(){
		int message=getParaToInt("message",0);
		if(message!=0){
			if(message==1){
				setAttr("message","请上传图片");
			}
		}
		int course_id=getParaToInt("course_id",0);
		setAttr("course_id",course_id);
		render("/admin/CertificationAdd.html");

	}
	@Before(Privilege.class)
	public void certificationAdd(){
		UploadFile uploadFile=getFile("image_01", config.save_path);
		String name=getPara("name");
		String institue=getPara("institue");
		String get_time=getPara("get_time");
		int course_id=getParaToInt("course_id");
		CourseCertification certification=new CourseCertification();
		if(uploadFile!=null){
			String fileName=FileUtils.renameToFile(uploadFile, 100, 100);
			certification.set(certification.name,name)
							.set(certification.institue, institue)
							.set(certification.get_time, get_time)
							.set(certification.course_id, course_id)
							.set(certification.post_time, DateUtils.getCurrentDateTime())
							.set(certification.image_01, fileName)
							.save();
			redirect("/admin/ConmmonCourse/courseInfo?course_id="+course_id);
		}else{
			redirect("/admin/ConmmonCourse/addCertification?message=1&course_id="+course_id);
		}
	}
	
	//增加图片
	@Before(Privilege.class)
	public void addPic(){
		int course_id=getParaToInt("course_id");
		setAttr("course_id",course_id);
		int message=getParaToInt("message",0);
		if(message!=0){
			if(message==1){
				setAttr("message","请上传图片");
			}
		}
		render("/admin/PicAdd.html");
	}
	@Before(Privilege.class)
	public void picAdd(){
		UploadFile uploadFile=getFile("banners", config.save_path);
		String banner="";
		String pic="";
	
		int course_id=getParaToInt("course_id");	
		if(uploadFile!=null){
			String fileName=FileUtils.renameToFile(uploadFile, 100, 100);
			Course courses=Course.dao.findById(course_id);
			if(courses!=null){
				pic=courses.get(courses.banners);
				if(pic.length()==0){
					courses.set(courses.banners , fileName).update();
				}else{
					banner=pic+"#"+fileName;
					courses.set(courses.banners, banner).update();
				}
				redirect("/admin/ConmmonCourse/courseInfo?course_id="+course_id);	
			}
		}else{
			redirect("/admin/ConmmonCourse/addPic?message=1&course_id="+course_id);	
		}

	}
	//删除图片
	@Before(Privilege.class)
	public void deletePic(){
		String xy_index=getPara("xy","");	
		int course_id = getParaToInt("course_id");
		Course courses = Course.dao.findById(course_id);
		if (courses!=null) {
			String banners = courses.get(courses.banners);
			String bannersaves="";
			if (!banners.equals("")) {
				String[] banners_array = banners.split("#");
				for(int i=0;i<banners_array.length;i++){
					String banner_img = banners_array[i];
					if (!xy_index.equals(banner_img)) {
						bannersaves = bannersaves+"#";
					}
				}
				courses.set(courses.banners , bannersaves).update();
			}
		}
		redirect("/admin/ConmmonCourse/courseInfo?course_id="+course_id);	
	}

	//增加url
	@Before(Privilege.class)
	public void addUrl(){
		String shipin="";
		String vido="";
		String videos=getPara("videos");
		int course_id=getParaToInt("course_id");
		Course courses=Course.dao.findById(course_id);
		if(courses!=null){
			vido=courses.get(courses.videos);
			if(vido.length()==0){
				courses.set(courses.videos, videos).update();
			}else{
				shipin=vido+"#"+videos;
				courses.set(courses.videos, 	shipin).update();
			}
			redirect("/admin/ConmmonCourse/courseInfo?course_id="+course_id);	
		}	
	}
	
	//删除url
	@Before(Privilege.class)
	public void deleteUrl(){
		String xr_index=getPara("xr");	
		int course_id=getParaToInt("course_id");
		Course courses=Course.dao.findById(course_id);
		String urls = courses.get(courses.videos);
		int index = urls.indexOf(xr_index);
		int length = xr_index.length()-1;

		if(index != 0)
		{
			index = index -1;
			length = length + 1;
		}else if(index==0){
			length = length + 1;
		}
		int size = index+length;
		urls = urls.substring(0,index)+urls.substring(size+1);
	     courses.set(courses.videos, urls).update();
		redirect("/admin/ConmmonCourse/courseInfo?course_id="+course_id);	
	}	
	

	
}
	
	


