package com.quark.admin.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.quark.app.bean.FileNameBean;
import com.quark.interceptor.Login;
import com.quark.interceptor.Privilege;
import com.quark.model.extend.Comment;
import com.quark.model.extend.Experience;
import com.quark.model.extend.Feedback;
import com.quark.model.extend.Orders;
import com.quark.model.extend.User;
import com.quark.utils.DateUtils;

/**
 * 
 * @author Administrator
 *
 */
@Before(Login.class)
public class Feedbacks extends Controller {
	
	@Before(Privilege.class)
	public void feedback() {
		int currentPage = getParaToInt("pn", 1);
		Page<Feedback> fdPage = null;
		fdPage = Feedback.dao.paginate(currentPage, PAGE_SIZE,
				"select * ",
				"from feedback order by post_time desc");
		for(Feedback feedback:fdPage.getList()){
			int user_id = feedback.get("user_id");
			User user2 = User.dao.findById(user_id);
			if (user2!=null) {
				String nickname = user2.getStr("telephone");
				feedback.put("login_telephone", nickname);
			}
			
		}
		setAttr("list", fdPage);
		setAttr("pn", currentPage);
		render("/admin/FeedbackList.html");
	}
	public void delete(){
		int currentPage = getParaToInt("pn", 1);
		int feedback_id = getParaToInt("feedback_id");
		Feedback feedback = Feedback.dao.findById(feedback_id);
		feedback.delete();
		redirect("/admin/Feedbacks/feedback?pn="+currentPage);
	}
	
}
















