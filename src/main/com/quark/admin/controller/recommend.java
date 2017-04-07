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
import com.quark.model.extend.Category02;
import com.quark.model.extend.Catetory;
import com.quark.model.extend.Collection;
import com.quark.model.extend.Course;
import com.quark.model.extend.User;
import com.quark.utils.DateUtils;
import com.quark.utils.FileUtils;

/**
 * 推荐分类
 * @author Administrator
 *
 */
@Before(Login.class)
public class recommend extends Controller {
	
	@Before(Privilege.class)
	public void List() throws ParseException{
		int currentPage = getParaToInt("pn", 1);
		String message = getPara("message",null);
		String except_sql=" from catetory where status=1 order by sort asc";
		setAttr("action", "list");
		Page<Catetory> catetory=Catetory.dao.paginate(currentPage, PAGE_SIZE," select *", except_sql);
		setAttr("list",catetory);
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
		render("/admin/Recommends.html");
	}
	
	
	public void delete(){
		int catetory_id=getParaToInt("catetory_id");
		Catetory catetory=Catetory.dao.findById(catetory_id);
		boolean flag=catetory.delete();
		if(flag){
			List<Course> list=Course.dao.find("select * from course where status=1 and catetory_id="+catetory_id);
			for(Course course :list){
				course.set(course.status,0).update();
			}
		}
		
		redirect("/admin/recommend/List");
	}
	
	public void add(){
		String message = getPara("message",null);
		if (message!=null) {
			setAttr("message", "请上传图片");
		}
		render("/admin/addRecommendPicture.html");
	}
	
	
	
	
	public void addRecommend(){
		UploadFile upload_cover = getFile("image_01", config.images_path);
		UploadFile upload_big_cover = upload_cover;
		Catetory catetory1=new Catetory();
		if (upload_cover != null) {
			try {
				FileUtils.copyFile(config.images_path+upload_cover.getFileName(), config.images_path+System.currentTimeMillis()+".jpg",false);
			} catch (Exception e) {
				// TODO: handle exception
			}
			catetory1.set(catetory1.image_01, FileUtils.renameToFile(upload_cover,168,160));	
		}
		if(upload_cover!=null)
		{
			catetory1.set(catetory1.big_image_01, FileUtils.renameToFile(upload_cover, 452, 420));
		}
		
		String name=getPara("name");
		String sub_title=getPara("sub_title");
		String is_hot_recommand=getPara("is_hot_recommand");
		int sort=getParaToInt("sort");
		List<Catetory> catetory = Catetory.dao.find("select * from catetory where name=?",name);
		if(catetory.size()==0){
			
			boolean save=catetory1.set("name", name).set("sub_title", sub_title).set("is_hot_recommand", is_hot_recommand).set("sort", sort).set(catetory1.post_time, DateUtils.getCurrentDateTime()).save();
			
			if(save){
				
				redirect("/admin/recommend/List?message=1");//添加成功
			}
			
		}else{
			redirect("/admin/recommend/List?message=2");//添加失败
		}
		
		
	}
	
	public void Editor()
	{
		int catetory_id=getParaToInt("catetory_id");
		Catetory catetory=Catetory.dao.findById(catetory_id);
		setAttr("r",catetory);
		render("/admin/addRecommend.html");
		
	}
	
	public void addModify(){
		UploadFile upload_cover = getFile("image_01", config.images_path);	
		UploadFile upload_big_cover = upload_cover;
		int catetory_id=getParaToInt("catetory_id");
		Catetory catetory=Catetory.dao.findById(catetory_id);
		if (upload_cover != null) {
			try {
				FileUtils.copyFile(config.images_path+upload_cover.getFileName(), config.images_path+System.currentTimeMillis()+".jpg",false);
			} catch (Exception e) {
				// TODO: handle exception
			}
			catetory.set(catetory.image_01, FileUtils.renameToFile(upload_cover,168,160));	
		}
		if (upload_cover != null) {
			catetory.set(catetory.big_image_01, FileUtils.renameToFile(upload_cover,452,420));	
		}
		int sort=getParaToInt("sort");
		String name=getPara("name");
		String sub_title=getPara("sub_title");
		String is_hot_recommand=getPara("is_hot_recommand");
		List<Catetory> catetory1 = Catetory.dao.find("select * from catetory where name='"+name+"'and catetory_id!= "+catetory_id);
		if(catetory1.size()==0){
			boolean update=catetory.set(catetory.sort, sort)
					.set(catetory.name, name)
					.set(catetory.sub_title, sub_title)
					.set(catetory.is_hot_recommand, is_hot_recommand)
					.set(catetory.post_time, DateUtils.getCurrentDateTime())
					.update();
			if(update){
				redirect("/admin/recommend/List?message=3");//更新成功
			}
		}else{
			redirect("/admin/recommend/List?message=4");//更新失败
		}	
	}
	
}
