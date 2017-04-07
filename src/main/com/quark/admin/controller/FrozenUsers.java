package com.quark.admin.controller;


import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.eclipse.jetty.util.UrlEncoded;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheInterceptor;
import com.jfinal.upload.UploadFile;
import com.quark.common.config;
import com.quark.interceptor.Login;
import com.quark.interceptor.Privilege;
import com.quark.model.extend.User;
import com.quark.utils.DateUtils;
import com.quark.utils.FileUtils;
import com.quark.utils.MD5Util;
import com.quarkso.utils.DateUitls;


/**
 * 用戶列表
 * @author xxy
 * 用户状态：0-封号，1-正常
 */
@Before(Login.class)
public class FrozenUsers extends Controller {
	
	public void list() throws ParseException{
		int currentPage = getParaToInt("pn", 1);
		String kw = getPara("kw","");
		String message="list";
		Page<User> users = null;
		String filter_sql=" status=0 ";//冻结用户
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
				"from user where "+filter_sql+" order by post_time desc");
		
		setAttr("list", users);
		setAttr("pn", currentPage);
		render("/admin/FrozenUserList.html");
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
		render("/admin/UserInfo.html");
	}
	
	
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
		redirect("/admin/FrozenUsers/list?pn="+currentPage+"&province="+URLEncoder.encode(province, "UTF-8")+"&city="+URLEncoder.encode(city, "UTF-8")+"&kw="+URLEncoder.encode(kw, "UTF-8"));
	}
}
