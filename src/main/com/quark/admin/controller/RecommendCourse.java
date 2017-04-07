package com.quark.admin.controller;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.quark.interceptor.Login;
import com.quark.interceptor.Privilege;
import com.quark.model.extend.Category02;
import com.quark.model.extend.Catetory;
import com.quark.model.extend.Collection;
import com.quark.model.extend.Course;
import com.quark.model.extend.CourseCertification;
import com.quark.model.extend.Orders;
import com.quark.model.extend.User;
import com.quark.utils.DateUtils;
import com.quarkso.utils.DateUitls;

//推荐课程管理  xxy
@Before(Login.class)
public class RecommendCourse extends Controller {
	
	public void list(){
		String message1 = getPara("message",null);
		int currentPage=getParaToInt("pn",1);
		String city=getPara("city","请选择");
		String area=getPara("area","请选择");
		String catetory_name=getPara("catetory_name","");
		String message="list";
		int status=getParaToInt("status",1);
		String filter_sql=" 1=1 ";
		
		if(!city.equals("请选择")){
			filter_sql = filter_sql + " and city like '%" + city + "%'";
			message="search";
		}
		if(!area.equals("请选择")){
			filter_sql = filter_sql + " and area like '%" + area + "%'";
			message="search";
		}
		if(!catetory_name.equals("")){
			filter_sql = filter_sql + " and catetory_name like '%" + catetory_name + "%'";
			message="search";
		}
		setAttr("city",city);
		setAttr("area",area);
		setAttr("catetory_name",catetory_name);
		setAttr("action",message);
	
		Page<Course> course=Course.dao.paginate(currentPage, 
				PAGE_SIZE, 
				"select * ", "from course where "+filter_sql+" and catetory_id!=0 and status="+status+" order by sort asc,post_time desc");	
		for(Course courses:course.getList()){
			String user_id=courses.getStr(courses.user_id);
			User user = User.dao.findFirst("select is_stop_course,email from user where user_id=?",user_id);
			if(user!=null){
				courses.put("is_stop_course", user.get(user.is_stop_course));
				courses.put("emial", user.getStr(user.email));
			}
		}
		setAttr("list",course);
		setAttr("status",status);
		setAttr("pn",currentPage);
		//查询上下线的条数
		Course course1= Course.dao.findFirst("select count(course_id) as normol_count from course  where status=1 and catetory_id!=0 and "+filter_sql);
		Course course2=Course.dao.findFirst("select count(course_id) as nodo_count from course  where status=0 and catetory_id!=0 and "+filter_sql);
		setAttr("normol_count",course1.get("normol_count"));
		setAttr("nodo_count",course2.get("nodo_count"));
		//传参数给推荐列表下拉框
		List<Catetory> catetory=Catetory.dao.find("select * from catetory");
		setAttr("s",catetory);

		if (message1!=null) {
			if (message1.equals("1")) {
				setAttr("ok", "添加成功");
			}
			if (message1.equals("2")) {
				setAttr("ok", "添加失败，已经有同类型");
			}
			if (message1.equals("3")) {
				setAttr("ok", "修改成功");
			}
			if (message1.equals("4")) {
				setAttr("ok", "修改失败");
			}
			if (message1.equals("5")) {
				setAttr("ok", "修改失败，已经有同类型");
			}
		}
		render("/admin/RecommendCourseList.html");
	}
	
	/**
	 * 取消热门推荐
	 */
	@Before(Privilege.class)
	public void freeze(){
		int course_id=getParaToInt("course_id");
		Course course=Course.dao.findById(course_id);
		boolean update = course.set(course.is_hot_recommand,0)
			  .set(course.post_time, DateUtils.getCurrentDateTime())
			  .update();
		redirect("/admin/RecommendCourse/list?status=1");
	}
	/**
	 * 设定热门推荐
	 */
	@Before(Privilege.class)
	public void unFreeze(){
		int course_id=getParaToInt("course_id");
		Course course=Course.dao.findById(course_id);
		boolean update=course.set(course.is_hot_recommand, 1)
				.set(course.post_time, DateUtils.getCurrentDateTime())
				.update();
		redirect("/admin/RecommendCourse/list?status=1");
	}
	/**
	 * 一键上下线
	 */
	@Before(Privilege.class)
	public void allUnFreeze() {
		String ids = getPara("ids");
		int type = getParaToInt("type");
		String catetory_id = null ;
		String catetory_name = null;
		String is_hot_recommand = null;
		if(type==4){
			String selects = getPara("selects");
			String[] tem = selects.split(":");
			catetory_id = tem[0];
			catetory_name = tem[1];
			is_hot_recommand = tem[2];
		}
		String[] idtemp = ids.split(",");	
		int status=0;
		for (int i = 0; i < idtemp.length; i++) {
			Course course = Course.dao.findById(idtemp[i]);
			if(course!=null){
				if (type==2) {
					course.set("status", 1);
					status=1;
				}
				if (type==3){
					//下线
					course.set("status", 0);
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
				if(type == 4){
					status=1;
					course.set(course.catetory_name, catetory_name)
						  .set(course.catetory_id, catetory_id);		
				}
				course.set(course.post_time, DateUitls.getCurrentDateTime()).update();	
			}
		}
		redirect("/admin/RecommendCourse/list?status="+status);
	}
	//置顶
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
	//编辑
	@Before(Privilege.class)
	public void modify(){
		int course_id=getParaToInt("course_id");
		Course course=Course.dao.findById(course_id);
		if(course!=null){
			List<CourseCertification> cc=CourseCertification.dao.find("select * from course_certification where course_id=?",course_id);
			course.put("courseCertification", cc);
		}
		String course_name=course.getStr(course.catetory_name);
		List<Catetory> catetory=Catetory.dao.find("select * from catetory");
		setAttr("s",catetory);
		setAttr("r",course);
		render("/admin/RecommendCourseModify.html");	
	}

	public void addModify(){
		String catetory_name=getPara("catetory_name");
		int course_id=getParaToInt("course_id");
		Course course=Course.dao.findById(course_id);
		String [] tem=catetory_name.split(":");
		String catetory_id=tem[0];
		catetory_name=tem[1];
		String is_hot_recommand=tem[2];
		if(course!=null){
			course.set("catetory_name", catetory_name)
				  .set("catetory_id", catetory_id)
				  .update();
		}
		redirect("/admin/RecommendCourse/list");
	}
		
	//移出操作
	@Before(Privilege.class)
	public void delete(){
		int course_id=getParaToInt("course_id");
		Course course=Course.dao.findById(course_id);
		if(course!=null){
			course.set("catetory_id", 0).update();
		}
		redirect("/admin/RecommendCourse/list");		
	}
			
}


