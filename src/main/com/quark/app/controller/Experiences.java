/**
 * 
 */
package com.quark.app.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinal.upload.UploadFile;
import com.quark.api.annotation.Author;
import com.quark.api.annotation.DataType;
import com.quark.api.annotation.Explaination;
import com.quark.api.annotation.ReturnDBParam;
import com.quark.api.annotation.ReturnOutlet;
import com.quark.api.annotation.Rp;
import com.quark.api.annotation.Type;
import com.quark.api.annotation.URLParam;
import com.quark.api.annotation.UpdateLog;
import com.quark.api.annotation.Value;
import com.quark.api.auto.bean.ResponseValues;
import com.quark.app.bean.CategoryBean;
import com.quark.app.logs.AppLog;
import com.quark.common.AppData;
import com.quark.common.RongToken;
import com.quark.common.Storage;
import com.quark.common.config;
import com.quark.interceptor.AppToken;
import com.quark.mail.SendMail;
import com.quark.model.extend.Category02;
import com.quark.model.extend.Comment;
import com.quark.model.extend.Course;
import com.quark.model.extend.Experience;
import com.quark.model.extend.Tokens;
import com.quark.model.extend.User;
import com.quark.rsa.RSAsecurity;
import com.quark.sign.RequestHandler;
import com.quark.utils.DateUtils;
import com.quark.utils.EmojiFilter;
import com.quark.utils.FileUtils;
import com.quark.utils.MD5Util;
import com.quark.utils.MessageUtils;
import com.quark.utils.RandomUtils;
import com.quarkso.utils.ImgSavePathUtils;
import com.quarkso.utils.StringUtils;

/**
 * @author C罗
 * 课程管理
 */
@Before(Tx.class)
public class Experiences extends Controller {
	
	@Author("cluo")
	@Rp("新建课程、撰写经历、新增证书、我是教练")
	@Explaination(info = "上传图片网页")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.File, name = "image_01")
	@URLParam(defaultValue = "", explain = "app无需传，H5必须传，已协商好", type = Type.String_NotRequired, name = "filename")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "UpdatePicResponse{fileName}", remarks = "图片名称", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "UpdatePicResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "UpdatePicResponse{status}", remarks = "1-操作成功，2-上傳無null", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "UpdatePicResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void updatePic2() {
		try {
			UploadFile image_01 = getFile("image_01", config.save_path);
			String token = getPara("token");
			String invoke = getPara("invoke","app");
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this,
						Thread.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("UpdatePicResponse", response2);
				renderMultiJson("UpdatePicResponse");
				return;
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			String fileName = "",message="";int status = 0;
			if (image_01 != null){
				status = 1;
				if (invoke.equals("h5")) {
					fileName = FileUtils.renameToFileH52(image_01);
				}else {
					fileName = FileUtils.renameToFile(image_01,750,750);
				}
				message = "Upload successful";
			}else {
				status = 2;
				message = "Please choose a picture to upload";
			}
			responseValues.put("fileName", fileName);
			responseValues.put("message", message);
			responseValues.put("status", status);
			responseValues.put("code", 200);
			setAttr("UpdatePicResponse", responseValues);
			renderMultiJson("UpdatePicResponse");
			
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("Experiences/updatePic", "上传图片", this);
		}
	}

	@Author("cluo")
	@Rp("新建课程、撰写经历、新增证书、我是教练、账户中心、个人主页")
	@Explaination(info = "上传图片")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.File, name = "image_01")
	@URLParam(defaultValue = "", explain = "app无需传，H5必须传，已协商好", type = Type.String_NotRequired, name = "filename")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "UpdatePicResponse{fileName}", remarks = "图片名称", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "UpdatePicResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "UpdatePicResponse{status}", remarks = "1-操作成功，2-上傳無null", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "UpdatePicResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void updatePic() {
		try {
			UploadFile image_01 = getFile("image_01", config.save_path);
			String token = getPara("token");
			String invoke = getPara("invoke","app");
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this,
						Thread.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("UpdatePicResponse", response2);
				renderMultiJson("UpdatePicResponse");
				return;
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			String fileName = "",message="";int status = 0;
			if (image_01 != null){
				status = 1;
				if (invoke.equals("h5")) {
					fileName = FileUtils.renameToFileH52(image_01);
					if(fileName.equals("example_7.png")){
						status = 2;
						message = "Upload error,Please choose a picture to upload.";
					}else{
						message = "Upload successful";
					}
				}else {
					fileName = FileUtils.renameToFile(image_01,750,750);
					message = "Upload successful";
				}
			}else {
				status = 2;
				message = "Please choose a picture to upload";
			}
			responseValues.put("fileName", fileName);
			responseValues.put("message", message);
			responseValues.put("status", status);
			responseValues.put("code", 200);
			setAttr("UpdatePicResponse", responseValues);
			renderMultiJson("UpdatePicResponse");
			
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("Experiences/updatePic", "上传图片", this);
		}
	}
	@Author("cluo")
	@Rp("撰写经历")
	@Explaination(info = "新增经历")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Experience.title)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Experience.content)
	@URLParam(defaultValue = "{1、2}", explain = Value.Infer, type = Type.String, name = Experience.status)
	@URLParam(defaultValue = "", explain = "拼接如：11.jpg#22.jpg#11.jpg#22.jpg【图片名称#图片名称】", type = Type.String, name = "fileName")
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "AddExperienceResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "AddExperienceResponse{status}", remarks = "1-操作成功，2-请上传图片", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "AddExperienceResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void addExperience() {
		try {
			String token = getPara("token");
			String title = getPara("title");
			String content = getPara("content");
			int ex_status = getParaToInt("status", 1);
			String fileNames = getPara("fileName","");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("content", content);
			packageParams.put("fileName", fileNames);
			packageParams.put("status", ex_status+"");
			packageParams.put("title", title);
			packageParams.put("token", token);
			RequestHandler reqHandler = new RequestHandler(null, null);
			reqHandler.init(config.app_quark_id, config.app_quark_secret, config.app_quark_key);
			String sign = reqHandler.createSign(packageParams);
			String invoke = getPara("invoke","app");
			if (invoke.equals("h5")) {
				sign = "123456";
			}
			if (!app_sign.equals(sign)) {
				// Signature verification failed.
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Signature verification failed.");
				response2.put("code", 403);
				setAttr("AddExperienceResponse", response2);
				renderMultiJson("AddExperienceResponse");
				return;
			}
			/**
			 * 接口签名end
			 */
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("AddExperienceResponse", response2);
				renderMultiJson("AddExperienceResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="Experience created failed";
			/*if (ex_status==1) {
				if (fileNames.equals("")) {
					status = 2;
					message="Please upload the experience pictures.";
				}
			}*/
			if (status==0) {
				Experience experience = new Experience();
				boolean save = experience.set(experience.user_id, user_id)
						.set(experience.title, title)
						.set(experience.content, content)
						.set(experience.images, fileNames)
						.set(experience.post_time, DateUtils.getCurrentDateTime())
						.set(experience.status, ex_status)
						.save();
				if (save) {
					status = 1;
					message="Experience created";
					if (ex_status==2) {
						message="Save successful.";
					}
				}
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("AddExperienceResponse", responseValues);
			renderMultiJson("AddExperienceResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("Experiences/addExperience", "新增", this);
		}
	}
	@Author("cluo")
	@Rp("撰写经历")
	@Explaination(info = "编辑经历【注意看字段说明】")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Experience.experience_id)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Experience.title)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Experience.content)
	@URLParam(defaultValue = "{1、2}", explain = Value.Infer, type = Type.String, name = Experience.status)
	@URLParam(defaultValue = "", explain = "编辑不编辑都拼接如：11.jpg#22.jpg#11.jpg#22.jpg【图片名称#图片名称】", type = Type.String, name = "fileName")
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "EditExperienceResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "EditExperienceResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "EditExperienceResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void editExperience() {
		try {
			String experience_id = getPara("experience_id");
			String token = getPara("token");
			String title = getPara("title","");
			String content = getPara("content","");
			int ex_status = getParaToInt("status", 1);
			String fileNames = getPara("fileName","");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("content", content);
			packageParams.put("experience_id", experience_id);
			packageParams.put("fileName", fileNames);
			packageParams.put("status", ex_status+"");
			packageParams.put("title", title);
			packageParams.put("token", token);
			RequestHandler reqHandler = new RequestHandler(null, null);
			reqHandler.init(config.app_quark_id, config.app_quark_secret, config.app_quark_key);
			String sign = reqHandler.createSign(packageParams);
			String invoke = getPara("invoke","app");
			if (invoke.equals("h5")) {
				sign = "123456";
			}
			if (!app_sign.equals(sign)) {
				// Signature verification failed.
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Signature verification failed.");
				response2.put("code", 403);
				setAttr("EditExperienceResponse", response2);
				renderMultiJson("EditExperienceResponse");
				return;
			}
			/**
			 * 接口签名end
			 */
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("EditExperienceResponse", response2);
				renderMultiJson("EditExperienceResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="Edit failed. Please check for errors.";
			/*if (ex_status==1) {
				if (fileNames.equals("")) {
					status = 2;
					message="Please upload the experience pictures.";
				}
			}*/
			if (status==0) {
				Experience experience = Experience.dao.findById(experience_id);
				boolean update = experience.set(experience.title, title)
						.set(experience.content, content)
						.set(experience.images, fileNames)
						.set(experience.post_time, DateUtils.getCurrentDateTime())
						.set(experience.status, ex_status)
						.update();
				if (update) {
					status = 1;message="Edit successful.";
				}
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("EditExperienceResponse", responseValues);
			renderMultiJson("EditExperienceResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("Experiences/editExperience", "编辑", this);
		}
	}
	@Author("cluo")
	@Rp("我的经历")
	@Explaination(info = "删除经历")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Experience.experience_id)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "DeleteExperienceResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "DeleteExperienceResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "DeleteExperienceResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void deleteExperience() {
		try {
			String experience_id = getPara("experience_id");
			String token = getPara("token");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("experience_id", experience_id);
			packageParams.put("token", token);
			RequestHandler reqHandler = new RequestHandler(null, null);
			reqHandler.init(config.app_quark_id, config.app_quark_secret, config.app_quark_key);
			String sign = reqHandler.createSign(packageParams);
			String invoke = getPara("invoke","app");
			if (invoke.equals("h5")) {
				sign = "123456";
			}
			if (!app_sign.equals(sign)) {
				// Signature verification failed.
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Signature verification failed.");
				response2.put("code", 403);
				setAttr("DeleteExperienceResponse", response2);
				renderMultiJson("DeleteExperienceResponse");
				return;
			}
			/**
			 * 接口签名end
			 */
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("DeleteExperienceResponse", response2);
				renderMultiJson("DeleteExperienceResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="Deletion failed. Please check for errors.";
			Experience experience = Experience.dao.findById(experience_id);
			if (experience!=null) {
				String oldImages = experience.getStr(experience.images);
				boolean delete = experience.delete();
				if (delete) {
					if (oldImages!=null&&!oldImages.equals("")) {
						String[] image_name_array = oldImages.split("#");
						for(int i=0;i<image_name_array.length;i++){
							String image_name = image_name_array[i];
							FileUtils.deleteFile(config.save_path+image_name);
						}
					}
					status = 1;message="Deletion successful.";
				}
			}else {
				status = 2;
				message="Content does not exist. Please check for errors.";
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("DeleteExperienceResponse", responseValues);
			renderMultiJson("DeleteExperienceResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("Experiences/deleteExperience", "删除", this);
		}
	}
	@Author("cluo")
	@Rp("我的经历")
	@Explaination(info = "我的经历列表")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnDBParam(name = "ExperienceListResponse{experiencesResult:Experiences:list[Experience:$]}", column = Experience.experience_id)
	@ReturnDBParam(name = "ExperienceListResponse{experiencesResult:Experiences:list[Experience:$]}", column = Experience.title)
	@ReturnDBParam(name = "ExperienceListResponse{experiencesResult:Experiences:list[Experience:$]}", column = Experience.content)
	@ReturnDBParam(name = "ExperienceListResponse{experiencesResult:Experiences:list[Experience:$]}", column = Experience.post_time)
	@ReturnDBParam(name = "ExperienceListResponse{experiencesResult:Experiences:list[Experience:$]}", column = Experience.status)
	@ReturnOutlet(name = "ExperienceListResponse{experiencesResult:Experiences:list[Experience:image_01]}", remarks = "经验图片封面", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "ExperienceListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "ExperienceListResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "ExperienceListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void experienceList() {
		try {
			String token = getPara("token");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("token", token);
			RequestHandler reqHandler = new RequestHandler(null, null);
			reqHandler.init(config.app_quark_id, config.app_quark_secret, config.app_quark_key);
			String sign = reqHandler.createSign(packageParams);
			String invoke = getPara("invoke","app");
			if (invoke.equals("h5")) {
				sign = "123456";
			}
			if (!app_sign.equals(sign)) {
				// Signature verification failed.
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Signature verification failed.");
				response2.put("code", 403);
				setAttr("ExperienceListResponse", response2);
				renderMultiJson("ExperienceListResponse");
				return;
			}
			/**
			 * 接口签名end
			 */
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("ExperienceListResponse", response2);
				renderMultiJson("ExperienceListResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			final List<Experience> experiences = Experience.dao.find("select experience_id,title,content,images,post_time,status from experience where user_id=? order by post_time desc",user_id);
			for(Experience experience:experiences){
				String oldImages = experience.getStr(experience.images);
				String image_01="";
				if (oldImages!=null&&!oldImages.equals("")) {
					String[] image_name_array = oldImages.split("#");
					image_01 = image_name_array[0];
				}
				experience.put("image_01", image_01);
			}
			responseValues.put("Result", new HashMap<String, Object>() {
				{
					put("Experiences", experiences);
				}
			});
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "成功");
			setAttr("ExperienceListResponse", responseValues);
			renderMultiJson("ExperienceListResponse");
			AppLog.info("", getRequest());
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("Experiences/experienceList", "列表", this);
		}
	}
}
