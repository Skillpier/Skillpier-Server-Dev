package com.quark.admin.controller;

import java.io.File;

import com.jfinal.core.Controller;
import com.quark.common.config;
import com.quark.interceptor.AdminLogin;
import com.quark.interceptor.SuperAdminLogin;
import com.quark.mail.SendMail;
import com.quark.model.extend.AdminUser;
import com.quark.utils.Base64Util;
import com.quark.utils.MD5Util;
import com.quark.utils.RandomUtils;

public class Login extends Controller {

	public void forgetPost() {
		String email = getPara("email");
		AdminUser user = AdminUser.dao.findFirst(
				"select id from admin_user where email=?", email);
		if (user != null) {
			String new_password = RandomUtils.getRandomString(6);
			user.set("password", MD5Util.string2MD5(new_password)).update();
			SendMail.send("【skill】重置密码通知！",email, new_password);
			redirect("/admin/Login/index");
			return;
		} else {
			setAttr("errorEmail", "电子邮箱不存在");
			render("/common/forget.html");
		}
	}
	public void forget() {
		setAttr("errorEmail", "电子邮箱");
		render("/common/forget.html");
	}

	public void index() {
		setAttr("error", "用戶名");
		render("/common/Login.html");
	}

	public void in() {
		String username = getPara("username");
		String password = getPara("password");
		password = MD5Util.string2MD5(password);
		
		AdminUser user = AdminUser.dao.findFirst(
				"select * from admin_user where username=? and password=?",
				username, password);
		setAttr("inOrforget", 1);
		
		if (user != null) {
			int id = user.get("id");
			String role = user.getStr("role");
			String username_encode = Base64Util.encode(username);
			if (role.equals("admin")) {
				SuperAdminLogin.hash.put(username_encode, String.valueOf(id));
			} else {
				AdminLogin.hash.put(username_encode, String.valueOf(id));
			}
			setCookie("usession", username_encode, Integer.MAX_VALUE);
			redirect("/admin/UsersGeneral/list");
		} else {
			setAttr("error", "用戶名或者密碼錯誤");
			render("/common/Login.html");
		}
	}

	public void out() {
		String u = getCookie("usession");
		if (AdminLogin.hash.containsKey(u)) {
			AdminLogin.hash.remove(u);
		}
		if (SuperAdminLogin.hash.containsKey(u)) {
			SuperAdminLogin.hash.remove(u);
		}
		render("/common/Login.html");
	}
	
	public void getImg() {
		String imgPath = getPara("imgpath");
		String path = config.images_path + imgPath;
		renderFile(new File(path));
	}

	public void testvisa() {
		render("/third/testvisa.html");
	}
}
