package com.quark.admin.controller;

import java.text.ParseException;
import java.util.List;

import org.jsoup.helper.DataUtil;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import com.quark.common.config;
import com.quark.interceptor.Login;
import com.quark.interceptor.Privilege;
import com.quark.model.extend.Category01;
import com.quark.model.extend.Category02;
import com.quark.model.extend.Coupon;
import com.quark.model.extend.Course;
import com.quark.utils.DateUtils;
import com.quark.utils.FileUtils;

/**
 * 二级分类
 * @author Administrator
 *
 */
@Before(Login.class)
public class twoList extends Controller {

	/**
	 * 分类列表
	 */
	@Before(Privilege.class)
	public void list() throws ParseException{
		int currentPage = getParaToInt("pn", 1);
		String message = getPara("message",null);
		Page<Category02> catalog = null;
		String except_sql = "";
		except_sql = " from category_02 where status=1 order by category_01_id asc";
		setAttr("action", "list");
		catalog = Category02.dao.paginate(currentPage, PAGE_SIZE,
				"select * ", except_sql);
		setAttr("list", catalog);
		setAttr("pn", currentPage);
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
		render("/admin/twoList.html");
	}
	/**
	 * 增加
	 */
	public void add(){
		render("/admin/twoAdd.html");
	}
	public void submitAdd() {
		UploadFile upload_cover = getFile("image_01", config.images_path);
		UploadFile upload_big_cover = getFile("big_image_01", config.images_path);
		Category02 category02=new Category02();
		if (upload_cover != null){
			category02.set(category02.image_01, FileUtils.renameToFile(upload_cover,705,282));	
		}
		if(upload_big_cover!=null){
			category02.set(category02.big_image_01, FileUtils.renameToFile(upload_big_cover,216,216));	
		}
		String category_01_name = getPara("category_01_name");
		String category_02_name = getPara("category_02_name");
		int sort = getParaToInt("sort",0);
		String category_describe=getPara("category_describe");
		category_02_name = category_02_name.trim();
		Category02 category022 = Category02.dao.findFirst("select category_01_id from category_02 where status=1 and category_02_name='"+category_02_name+"'");
		if (category022==null){
			if (!category_describe.equals("")) {
				category_describe = category_describe.trim();
			}
			boolean save = false;
			if(category_01_name!=null && category_01_name.equals("SPORTS")){
				category02.set(category02.category_01_id,1)
					.set(category02.category_01_name,category_01_name);
			}
			if(category_01_name!=null && category_01_name.equals("ARTS")){
				category02.set(category02.category_01_id,2)
					.set(category02.category_01_name,category_01_name);
			}
			save = category02.set(category02.category_02_name, category_02_name)
					.set(category02.sort, sort)
					.set(category02.category_describe, category_describe)
					.set(category02.post_time, DateUtils.getCurrentDateTime())
					.save();
				if (save){
					redirect("/admin/twoList/list?message=1");//添加成功
				}
		}else {
			redirect("/admin/twoList/list?message=2");//添加失败
		}
	}
	public void modify(){
		int category_02_id = getParaToInt("category_02_id");
		Category02 category02 = Category02.dao.findById(category_02_id);
		setAttr("r", category02);
		render("/admin/twoEdit.html");
	}
	public void modifyCommit(){
		UploadFile upload_cover = getFile("image_01", config.images_path);	
		UploadFile upload_big_cover = getFile("big_image_01", config.images_path);;
		int category_02_id=getParaToInt("category_02_id");
		Category02 category02=Category02.dao.findById(category_02_id);
		if (upload_cover != null) {
			category02.set(category02.image_01, FileUtils.renameToFile(upload_cover,705,282));	
		}
		if(upload_big_cover!=null){
			category02.set(category02.big_image_01, FileUtils.renameToFile(upload_big_cover, 216, 216));
		}
		int sort=getParaToInt("sort");
		String category_describe=getPara("category_describe","");
		String category_02_name=getPara("category_02_name");
		Category02 category=Category02.dao.findFirst("select category_02_id from category_02 where status=1 and category_02_name='"+category_02_name+"' and category_02_id !="+category_02_id);
		if(category==null){
			category02.set(category02.category_02_name, category_02_name);
		}
		if (!category_describe.equals("")) {
			category_describe = category_describe.trim();
		}
		boolean update = category02.set(category02.sort, sort)
				.set(category02.category_describe, category_describe)
				.set(category02.post_time, DateUtils.getCurrentDateTime())
				.update();
		if (update) {
			redirect("/admin/twoList/list?message=3");//更新成功
		}else{
			redirect("/admin/twoList/list?message=4");//更新失败
		}
		
	}
	/**
	 * 删除
	 */
	public void delete() {
		int category_02_id = getParaToInt("category_02_id");
		Category02 classifyTwo = Category02.dao.findById(category_02_id);
		boolean delete = classifyTwo.set(classifyTwo.status, 0).update();
		if (delete) {
			//课程
			List<Course>pList = Course.dao.find("select * from course where status=1 and category_02_id=?",category_02_id);
			for(Course course:pList){
				course.set(course.status, 0).update();
			}
		}
		redirect("/admin/twoList/list");
	}
}