package com.quark.admin.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import com.quark.common.config;
import com.quark.interceptor.Login;
import com.quark.interceptor.Privilege;
import com.quark.model.extend.Category01;
import com.quark.model.extend.Comment;
import com.quark.model.extend.CommentReply;
import com.quark.model.extend.Course;
import com.quark.model.extend.User;
import com.quark.utils.DateUtils;
import com.quark.utils.FileUtils;

@Before(Login.class)
public class Comments extends Controller {

	public void list() {
		int currentPage = getParaToInt("pn", 1);
		String kw = getPara("kw","");
		String message="list";
		String filter_sql=" status!=0 and type=1 ";//正常用户
		if (!kw.equals("")) {
			kw = kw.trim();
			filter_sql = filter_sql +" and (note like'%"+kw+"%' or (user_id in (select user_id from user where (email like '%" + kw + "%' or nickname like '%" + kw+ "%')))) ";
			message="search";
		}
		Page<Comment> commentPage = Comment.dao.paginate(currentPage, PAGE_SIZE,
				" select * ", "from comment where "+filter_sql+" order by post_time desc");
		for(Comment comment:commentPage.getList()){
			int user_id = comment.get(comment.user_id);
			User user = User.dao.findById(user_id);
			String user_comment_nickname="",user_comment_email="";
			if (user!=null) {
				user_comment_nickname = user.getStr(user.nickname);
				user_comment_email = user.getStr(user.email);
			}
			comment.put("user_comment_nickname", user_comment_nickname);
			comment.put("user_comment_email", user_comment_email);
			int course_id = comment.get(comment.course_id);
			Course course = Course.dao.findById(course_id);
			String course_name = "";
			if (course!=null) {
				course_name = course.getStr(course.title);
			}
			comment.put("course_name", course_name);
		}
		setAttr("kw", kw);
		setAttr("action", message);
		setAttr("list", commentPage);
		setAttr("pn", currentPage);
		render("/admin/CommentsList.html");
	}
	/**
	 * 删除评论
	 */
	@Before(Privilege.class)
	public void delete(){
		int type = getParaToInt("type");
		int pn = getParaToInt("pn");
		int comment_id=getParaToInt("comment_id");
		Comment comment = Comment.dao.findById(comment_id);
		if (comment!=null) {
			int course_id = comment.get(comment.course_id);
			int comment_reply_id = comment.get(comment.comment_reply_id);
			boolean delete = comment.delete();
			if (delete) {
				int pro_skill2 = comment.get(comment.pro_skill); 
				int teaching_environment2 = comment.get(comment.teaching_environment); 
				int teaching_attitude2 = comment.get(comment.teaching_attitude); 
				double total_score = (pro_skill2+teaching_environment2+teaching_attitude2)/3;
				Course course = Course.dao.findById(course_id);
				if (course!=null) {
					double c_total_score = course.getDouble(course.total_score);
					if ((c_total_score-total_score)<0.5) {
						course.set(course.total_score, 0).update();
					}else {
						course.set(course.total_score, (c_total_score-total_score)).update();
					}
				}
				CommentReply.dao.deleteById(comment_reply_id);
			}
		}
		if (type==1) {
			redirect("/admin/Comments/list?pn="+pn);
		}else {
			redirect("/admin/Comments/systemList?pn="+pn);
		}
	}
	/**
	 * 系统消息
	 */
	@Before(Privilege.class)
	public void systemList(){
		int currentPage = getParaToInt("pn", 1);
		String kw = getPara("kw","");
		String message="list";
		String filter_sql=" status!=0 and type=2 ";//正常用户
		if (!kw.equals("")) {
			kw = kw.trim();
			filter_sql = filter_sql +" and (note like'%"+kw+"%' or (user_id in (select user_id from user where (email like '%" + kw + "%' or nickname like '%" + kw+ "%')))  or (public_course_user_id in (select user_id from user where (email like '%" + kw + "%' or nickname like '%" + kw+ "%')))) ";
			message="search";
		}
		Page<Comment> commentPage = Comment.dao.paginate(currentPage, PAGE_SIZE,
				" select * ", "from comment where "+filter_sql+" order by post_time desc");
		for(Comment comment:commentPage.getList()){
			int user_id = comment.get(comment.user_id);
			int public_course_user_id = comment.get(comment.public_course_user_id);
			String user_comment_nickname="",user_comment_email="";
			if (user_id!=0) {
				User user = User.dao.findById(user_id);
				if (user!=null) {
					user_comment_nickname = user.getStr(user.nickname);
					user_comment_email = user.getStr(user.email);
				}
			}
			if (public_course_user_id!=0) {
				User user = User.dao.findById(public_course_user_id);
				if (user!=null) {
					user_comment_nickname = user.getStr(user.nickname);
					user_comment_email = user.getStr(user.email);
				}
			}
			comment.put("user_comment_nickname", user_comment_nickname);
			comment.put("user_comment_email", user_comment_email);
		}
		setAttr("kw", kw);
		setAttr("action", message);
		setAttr("list", commentPage);
		setAttr("pn", currentPage);
		render("/admin/SystemMsgList.html");
	}
}
