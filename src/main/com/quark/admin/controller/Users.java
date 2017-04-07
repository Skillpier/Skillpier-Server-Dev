package com.quark.admin.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.baidu.ueditor.define.State;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.quark.app.bean.FileNameBean;
import com.quark.interceptor.Login;
import com.quark.interceptor.Privilege;
import com.quark.model.extend.Comment;
import com.quark.model.extend.Course;
import com.quark.model.extend.Experience;
import com.quark.model.extend.Orders;
import com.quark.model.extend.User;
import com.quark.utils.DateUtils;

/*
 * 查询教练用户管理
 * 
 * 谢晓阳
 */
@Before(Login.class)
public class Users extends Controller {
	public void list() throws ParseException{
		int currentPage = getParaToInt("pn", 1);
		String kw = getPara("kw","");
		String message="list";
		Page<User> users = null;
		String filter_sql=" status=1 ";//教练用户
		String start_time = getPara("start_time", "");
		String end_time = getPara("end_time", "");
		if (!kw.equals("")) {
			kw = kw.trim();
			filter_sql = filter_sql +"  and (telephone like '%" + kw + "%' or nickname like '%" + kw+ "%' or email like '%" + kw+ "%') ";
			message="search";
		}
		if (!start_time.equals("")&&!end_time.equals("")) {
			filter_sql = filter_sql + " and ( post_time between '" + start_time+ "' and '" + end_time + "') ";
			message="search";
		}
		setAttr("start_time", start_time);
		setAttr("end_time", end_time);
		setAttr("kw", kw);
		setAttr("action", message);
		users = User.dao.paginate(currentPage, PAGE_SIZE,
				"select * ",
				"from user where agent_level=2 and authen_status=2 and "+filter_sql+" order by post_time desc");
		for(User user:users.getList()){
			String last_login_time = user.getTimestamp("last_login_time").toString();
			String current_time = DateUtils.getCurrentDateTime();
			String active_time = DateUtils.getActiveTime(last_login_time, current_time);
			user.put("active_time", active_time);
			int user_id=user.get(user.user_id);
			Experience experiences = Experience.dao.findFirst("select count(*) as experiences from experience where user_id=? and status=1",user_id);
			user.put("experiences", experiences.get("experiences"));
		}
		User user_count = User.dao.findFirst("select count(*) as user_count from user where authen_status=2 and agent_level=2 and status=1");
		setAttr("user_count",user_count.get("user_count"));
		setAttr("list", users);
		setAttr("pn", currentPage);
		render("/admin/UserList.html");
	}
	
	public void userInfo(){
		int currentPage = getParaToInt("pn", 1);
		String province = getPara("province", "全部"); // 省
		String city = getPara("city", "全部"); // 市
		String kw = getPara("kw","");
		int user_id = getParaToInt("user_id");
		User user = User.dao.findById(user_id);
		if(user!=null)
		{
			List<Experience> experience = Experience.dao.find("select experience_id,title,content,images from experience where user_id=? and status=1",user_id);
			user.put("experience", experience);
		}
		setAttr("r", user);
		setAttr("pn", currentPage);
		setAttr("kw", kw);
		setAttr("province", province);
		setAttr("city", city);
		render("/admin/UserInfo.html");
	}
	@Before(Privilege.class)
	public void unfreeze() throws UnsupportedEncodingException{
		int currentPage = getParaToInt("pn", 1);
		String province = getPara("province", "全部"); // 省
		String city = getPara("city", "全部"); // 市
		String kw = getPara("kw","");
		
		int user_id = getParaToInt("user_id");
		int user_status = getParaToInt("user_status");
		int is_official = getParaToInt("is_official",2);
		int sex = getParaToInt("sex");
		String nickname = getPara("nickname","");
		String telephone = getPara("telephone","");
		User user = User.dao.findById(user_id);
		if (is_official!=2) {
			user.set(user.is_official, is_official);
		}
		boolean update = user.set(user.sex, sex)
				.set(user.nickname, nickname)
				.set(user.telephone, telephone)
				.set(user.status, user_status)
				.update();
		if (update) {
			if (user_status==0) {
				//冻结
				List<Course> courses = Course.dao.find("select course_id,is_frozen from course where user_id=? and is_frozen=1",user_id);
				for(Course course:courses){
					course.set(course.is_frozen, 2).update();
				}
			}
			if (user_status==1) {
				List<Course> courses = Course.dao.find("select course_id,is_frozen from course where user_id=? and is_frozen=2",user_id);
				for(Course course:courses){
					course.set(course.is_frozen, 1).update();
				}
			}
		}
		redirect("/admin/Users/list?pn="+currentPage+"&kw="+URLEncoder.encode(kw, "UTF-8"));
	}

	public void experiences(){
		int user_id=getParaToInt("user_id");
		setAttr("user_id",user_id);
		int currentPage = getParaToInt("pn", 1);
		Page<Experience> ep=Experience.dao.paginate(currentPage, PAGE_SIZE," select *", "from experience where user_id= "+user_id+" and status=1 order by post_time desc");
		setAttr("list",ep);
		setAttr("pn",currentPage);	
		render("/admin/ExperiencesList.html");
	}
	@Before(Privilege.class)
	public void delete(){
		int user_id=getParaToInt("user_id");
		int experience_id=getParaToInt("experience_id");
		Experience ep=Experience.dao.findById(experience_id);
		if(ep!=null){
			ep.delete();
			redirect("/admin/Users/experiences?user_id="+user_id);
		}
	}
	
}




