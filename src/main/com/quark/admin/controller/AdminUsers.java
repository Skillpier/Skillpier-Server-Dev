
package com.quark.admin.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.quark.interceptor.Login;
import com.quark.interceptor.Privilege;
import com.quark.model.extend.AdminUser;
import com.quark.utils.MD5Util;

@Before(Login.class)
public class AdminUsers extends Controller {

	@Before(Privilege.class)
	public void add() {
		render("/admin/AdminUserAdd.html");
	}

	public void modify(){
		int id = getParaToInt("id");
		AdminUser user = AdminUser.dao.findById(id);
		setAttr("user", user);
		render("/admin/AdminUserModify.html");
	}
	public void modifyCommit(){
		String username = getPara("username");
		String password = getPara("password");
		password = MD5Util.string2MD5(password);
		String email = getPara("email");
		AdminUser user = new AdminUser().findFirst("select * from admin_user where username=?",username);
		user.set("password", password)
				.set("email", email).update();
		redirect("/admin/AdminUsers/list"); 
	}
	@Before(Privilege.class)
	public void delete(){
	  	int id = getParaToInt("id");
	  	AdminUser user = AdminUser.dao.findById(id);
	  	if(user != null)
	  		user.delete();
	  	redirect("/admin/AdminUsers/list");
	}
	public void addCommit() {
		String username = getPara("username");
		String password = getPara("password");
		password = MD5Util.string2MD5(password);
		String email = getPara("email");
		AdminUser check_username = new AdminUser();
		AdminUser check_email = new AdminUser();
		check_username = check_username.findFirst(
				"select id from admin_user where username=?", username);
		check_email = check_email.findFirst(
				"select id from admin_user where email=?", email);
		if (check_username != null) {
			setAttr("username", username);
		}
		if (check_email != null) {
			setAttr("email", email);
		}
		if (check_email != null || check_username != null) {
			setAttr("error", "");
			render("/admin/AdminUserAdd.html");
			return;
		}
		AdminUser user = new AdminUser();
		user.set("username", username).set("password", password)
				.set("email", email).set("role", "poster").save();
		redirect("/admin/AdminUsers/list");
	}
	@Before(Privilege.class)
	public void list() {
		int pn = getParaToInt("pn",1);
		Page<AdminUser> list = AdminUser.dao.paginate(pn, PAGE_SIZE,
				"select * ", "from admin_user where role ='poster'");
		setAttr("list", list);
		render("/admin/AdminUserList.html");
	}
}
