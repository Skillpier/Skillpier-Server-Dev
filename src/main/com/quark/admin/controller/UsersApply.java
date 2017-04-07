package com.quark.admin.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.quark.app.bean.EmailUntil;
import com.quark.interceptor.Login;
import com.quark.interceptor.Privilege;
import com.quark.model.extend.Course;
import com.quark.model.extend.User;
import com.quark.utils.DateUtils;
/*
 * 
 */
@Before(Login.class)
public class UsersApply extends Controller {
	
	public void apply() throws ParseException{
		int currentPage = getParaToInt("pn", 1);
		String kw = getPara("kw","");
		String message="list";
		Page<User> users = null;
		String filter_sql=" status=1 ";//正常用户
		String start_time = getPara("start_time", "");
		String end_time = getPara("end_time", "");
		if (!kw.equals("")) {
			kw = kw.trim();
			filter_sql = filter_sql +"  and (telephone like '%" + kw + "%' or nickname like '%" + kw+ "%') ";
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
				"from user where agent_level=1 and "+filter_sql+"  and (authen_status=1 or authen_status=3)  order by post_time desc");
		
		setAttr("list", users);
		setAttr("pn", currentPage);
		render("/admin/UserApplyList.html");
	}
	
	public void userInfo(){
		int currentPage = getParaToInt("pn", 1);
		String province = getPara("province", "全部"); // 省
		String city = getPara("city", "全部"); // 市
		String kw = getPara("kw","");
		int user_id = getParaToInt("user_id");
		User user = User.dao.findById(user_id);
		setAttr("r", user);
		setAttr("pn", currentPage);
		setAttr("kw", kw);
		setAttr("province", province);
		setAttr("city", city);
		render("/admin/UserApplyStatus.html");
	}
	@Before(Privilege.class)
	public void unfreeze() throws UnsupportedEncodingException{
		int currentPage = getParaToInt("pn", 1);
		String province = getPara("province", "全部"); // 省
		String city = getPara("city", "全部"); // 市
		String kw = getPara("kw","");
		int user_id = getParaToInt("user_id");
		int authen_status = getParaToInt("authen_status");
		User user = User.dao.findById(user_id);
		String nickname = user.getStr(user.nickname);
		String email = user.getStr(user.email);
		user.set(user.authen_status, authen_status);
		if (authen_status==2) {
			String authen_nickname = user.getStr(user.authen_nickname);
			user.set(user.agent_level, 2).set(user.nickname, authen_nickname);
		}
		boolean update = user.update();
		if (update) {
			if (authen_status==2) {
				EmailUntil.sendEmailApplyStatus(email,2,nickname);
				Course course = Course.dao.findFirst("select course_id from course where user_id="+user_id+" and status=2");
				if (course!=null) {
					course.set(course.status, 1).set(course.is_auth_public, 1).update();
				}
			}
			if (authen_status==3) {
				EmailUntil.sendEmailApplyStatus(email,3,nickname);
			}
		}
		redirect("/admin/UsersApply/apply?pn="+currentPage+"&province="+URLEncoder.encode(province, "UTF-8")+"&city="+URLEncoder.encode(city, "UTF-8")+"&kw="+URLEncoder.encode(kw, "UTF-8"));
	}
}
