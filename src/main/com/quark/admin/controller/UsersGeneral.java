package com.quark.admin.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.api.examples.PushExample;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.quark.app.bean.FileNameBean;
import com.quark.interceptor.Login;
import com.quark.interceptor.Privilege;
import com.quark.model.extend.Comment;
import com.quark.model.extend.Experience;
import com.quark.model.extend.Orders;
import com.quark.model.extend.User;
import com.quark.utils.DateUtils;

/**
 * 用户管理
 * 
 *
 */
@Before(Login.class)
public class UsersGeneral extends Controller {

	public void list() throws ParseException{
		int currentPage = getParaToInt("pn", 1);
		String sheng = getPara("province", "全部"); // 省
		String shi = getPara("city", "全部"); // 市
		String kw = getPara("kw","");
		String message="list";
		Page<User> users = null;
		String filter_sql="status=1 and agent_level=1 ";//正常用户
		String start_time = getPara("start_time", "");
		String end_time = getPara("end_time", "");
		if (!kw.equals("")) {
			kw = kw.trim();
			filter_sql = filter_sql +"  and (email like '%" + kw + "%' or nickname like '%" + kw+ "%') ";
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
				"from user where "+filter_sql+" order by post_time desc");
		for(User user:users.getList()){
			String last_login_time = user.getTimestamp("last_login_time").toString();
			String current_time = DateUtils.getCurrentDateTime();
			String active_time = DateUtils.getActiveTime(last_login_time, current_time);
			user.put("active_time", active_time);
			
			int users_id=user.get(user.user_id);
			//交易数
			Orders ods1= Orders.dao.findFirst("select count(*) as os from orders where user_id=?",users_id);
			user.put("ods1", ods1.get("os"));
		}
		//注册用户数
		User usercount=User.dao.findFirst("select count(*) as usercount from user where "+filter_sql);
		setAttr("usercount",usercount.get("usercount"));
		setAttr("list", users);
		setAttr("pn", currentPage);
		render("/admin/UsersGeneralList.html");
	}
	
	public void userInfo(){
		int currentPage = getParaToInt("pn", 1);
		String province = getPara("province", "全部"); // 省
		String city = getPara("city", "全部"); // 市
		String kw = getPara("kw","");
		int user_id = getParaToInt("user_id");
		User user = User.dao.findById(user_id);
		if(user!=null){
			int users_id=user.get(user.user_id);
			//交易数
			Orders ods1= Orders.dao.findFirst("select count(*) as os from orders where user_id=?",users_id);
			user.put("ods1", ods1.get("os"));
			//评论数
			Comment comment= Comment.dao.findFirst("select count(*) as comm from comment where user_id=?",users_id);
			user.put("comment", comment.get("comm"));	
		}
		setAttr("r", user);
		setAttr("pn", currentPage);
		setAttr("kw", kw);
		setAttr("province", province);
		setAttr("city", city);
		render("/admin/UsersGeneralInfo.html");
	}
	public void coachUserInfo(){
		int currentPage = getParaToInt("pn", 1);
		String province = getPara("province", "全部"); // 省
		String city = getPara("city", "全部"); // 市
		String kw = getPara("kw","");
		int user_id = getParaToInt("user_id");
		User user = User.dao.findById(user_id);
		List<Experience> experience = Experience.dao.find("select experience_id,user_id,title,content,images from experience where user_id=? and status=1 ",user_id);
		user.put("experiences", experience);
		setAttr("experience_size", experience.size());
		setAttr("r", user);
		setAttr("pn", currentPage);
		setAttr("kw", kw);
		setAttr("province", province);
		setAttr("city", city);
		render("/admin/coachUserInfo.html");
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
		User user = User.dao.findById(user_id);
		if (is_official!=2) {
			user.set(user.is_official, is_official);
		}
		user.set(user.status, user_status).update();
		redirect("/admin/UsersGeneral/list?pn="+currentPage+"&kw="+URLEncoder.encode(kw, "UTF-8"));
	}
	public void deleteExperience(){
		int experience_id = getParaToInt("experience_id");
		int user_id = getParaToInt("user_id");
		Experience experience = Experience.dao.findById(experience_id);
		if (experience!=null) {
			experience.set(experience.status, -1).update();
		}
		redirect("/admin/UsersGeneral/coachUserInfo?user_id="+user_id);
	}
	
	public void deal(){
		int user_id=getParaToInt("user_id");
		int currentPage = getParaToInt("pn", 1);
		String message = getPara("message",null);
		Page<Orders> orders=Orders.dao.paginate(currentPage, PAGE_SIZE," select *", "from orders where user_id= "+user_id+" order by post_time desc");
		setAttr("list",orders);
		setAttr("pn",currentPage);	
		render("/admin/DealList.html");
	}
	/**
	 * 发送系统消息
	 */
	public void sendMsg(){
		int user_id=getParaToInt("user_id");
		int currentPage = getParaToInt("pn", 1);
		Page<Comment> commentPage = Comment.dao.paginate(currentPage,
				PAGE_SIZE," select *", 
				" from comment where type=2 and (user_id= "+user_id+" or public_course_user_id= "+user_id+") order by post_time desc");
		User user = User.dao.findById(user_id);
		setAttr("u",user);
		setAttr("list",commentPage);
		setAttr("pn",currentPage);	
		render("/admin/SendMsgList.html");
	}
	public void addSendMsg(){
		int user_id=getParaToInt("user_id");
		User users = User.dao.findById(user_id);
		setAttr("r",users);
		render("/admin/SendMsgAdd.html");
	}
	public void deleteSendMsg(){
		int user_id=getParaToInt("user_id");
		int comment_id=getParaToInt("comment_id");
		Comment comment = Comment.dao.findById(comment_id);
		if (comment!=null) {
			comment.delete();
		}
		redirect("/admin/UsersGeneral/sendMsg?user_id="+user_id);
	}
	public void submitSendMsg(){
		int user_id = getParaToInt("user_id");
		int agent_level = getParaToInt("agent_level");
		String note = getPara("note");
		Comment comments = new Comment();
		if (agent_level==1) {
			//用户
			comments.set(comments.user_id, user_id).set(comments.public_course_user_id, 0);
		}
		if (agent_level==2) {
			//教练
			comments.set(comments.public_course_user_id, user_id).set(comments.user_id, 0);
		}
		boolean save = comments.set(comments.note, note)
				.set(comments.post_date, DateUtils.getCurrentDate())
				.set(comments.post_time, DateUtils.getCurrentDateTime())
				.set(comments.type, 2)
				.set(comments.status, 1)
				.set(comments.is_reply, 1)
				.save();
		if (save) {
			PushExample.pushToUser(user_id+"", note);
		}
		redirect("/admin/UsersGeneral/sendMsg?user_id="+user_id);
	}
	
}











