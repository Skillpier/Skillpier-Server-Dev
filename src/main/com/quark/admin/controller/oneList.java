package com.quark.admin.controller;

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
import com.quark.model.extend.User;
import com.quark.utils.DateUtils;
import com.quark.utils.FileUtils;

@Before(Login.class)
public class oneList extends Controller {
	
	@Before(Privilege.class)
	public void List() throws ParseException{
		int currentPage = getParaToInt("pn", 1);
		String message = getPara("message",null);
		String except_sql="from category_01 order by sort asc";
		setAttr("action", "list");
		Page<Category01> category01=Category01.dao.paginate(currentPage, PAGE_SIZE," select *", except_sql);
		setAttr("list",category01);
		setAttr("pn",currentPage);
		if (message!=null) {
			if (message.equals("1")) {
				setAttr("ok", "添加成功");
			}
			if (message.equals("2")) {
				setAttr("ok", "添加失败，已经有同类型");
			}
			if (message.equals("3")) {
				setAttr("ok", "修改成功");
			}
			if (message.equals("4")) {
				setAttr("ok", "修改失败");
			}
			if (message.equals("5")) {
				setAttr("ok", "修改失败，已经有同类型");
			}
		}
		render("/admin/oneList.html");
	}
	public void addEditor(){
		int category_01_id = getParaToInt("category_01_id");
		Category01 category01 = Category01.dao.findById(category_01_id);
		setAttr("r", category01);
		render("/admin/oneEdit.html");
	}
	public void addModify(){
		UploadFile upload_cover = getFile("image_01", config.images_path);
		UploadFile upload_big_cover = getFile("big_image_01", config.images_path);	
		int category_01_id=getParaToInt("category_01_id");
		Category01 category01=Category01.dao.findById(category_01_id);
		if (upload_cover != null){
			category01.set(category01.image_01, FileUtils.renameToFile(upload_cover,228,228));	
		}
		if (upload_big_cover != null) {
			category01.set(category01.big_image_01, FileUtils.renameToFile(upload_big_cover,1024,685));
		}
		int sort=getParaToInt("sort");
		String category_describe = getPara("category_describe");
		boolean update=category01.set(category01.sort, sort)
					.set(category01.category_describe, category_describe)
					.set(category01.post_time, DateUtils.getCurrentDateTime())
					.update();
		if(update){
			redirect("/admin/oneList/List?message=3");
		}else{
			redirect("/admin/oneList/List?message=4");
		}
	}
}

