package com.quark.admin.controller;

import java.util.List;

import org.jsoup.helper.DataUtil;

import cn.jpush.api.examples.PushExample;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import com.quark.common.config;
import com.quark.interceptor.Login;
import com.quark.interceptor.Privilege;
import com.quark.model.extend.AndroidAutoUpdate;
import com.quark.utils.DateUtils;
import com.quark.utils.FileUtils;

/**
 * 自动更新
 * 
 * @author c罗
 *
 */
@Before(Login.class)
public class AndroidAutoUpdates extends Controller {

	/**
	 * 列表
	 * type：1-系统消息，2-站内消息
	 */
	@Before(Privilege.class)
	public void list() {
		String message = getPara("message");
		AndroidAutoUpdate autoUpdate = AndroidAutoUpdate.dao.findFirst("select * from android_auto_update");
		setAttr("r", autoUpdate);
		if (message!=null) {
			setAttr("ok", "更新成功");
		}
		render("/admin/AndroidAutoUpdateList.html");
	}

	/**
	 * 增加
	 * type：1-系统消息，2-站内消息
	 */
	public void add() {
		UploadFile upload_apk_name = getFile("apk_name", config.images_path);
		String new_versionCode = getPara("new_versionCode","1");
		String out_versionCode = getPara("out_versionCode","1.0.0");
		String update_text = getPara("update_text");
		String apk_size = getPara("apk_size");
		int status = getParaToInt("status",2);
		AndroidAutoUpdate autoUpdate = AndroidAutoUpdate.dao.findFirst("select android_auto_update_id,apk_name from android_auto_update");
		if (autoUpdate!=null) {
			String old_apk_name = autoUpdate.getStr(autoUpdate.apk_name);
			FileUtils.deleteFile(config.images_path+old_apk_name);
			if (upload_apk_name != null) {
				autoUpdate.set(autoUpdate.apk_name, upload_apk_name.getFileName());
			}
			autoUpdate.set(autoUpdate.new_versionCode, new_versionCode)
				.set(autoUpdate.out_versionCode, out_versionCode)
				.set(autoUpdate.update_text, update_text)
				.set(autoUpdate.apk_size, apk_size)
				.set(autoUpdate.post_time, DateUtils.getCurrentDateTime())
				.set(autoUpdate.status, status)
				.update();
		}else {
			AndroidAutoUpdate autoUpdate2 = new AndroidAutoUpdate();
			if (upload_apk_name != null) {
				autoUpdate2.set(autoUpdate2.apk_name, upload_apk_name.getFileName());
			}
			autoUpdate2.set(autoUpdate2.new_versionCode, new_versionCode)
					.set(autoUpdate2.out_versionCode, out_versionCode)
					.set(autoUpdate2.update_text, update_text)
					.set(autoUpdate.apk_size, apk_size)
					.set(autoUpdate2.post_time, DateUtils.getCurrentDateTime())
					.set(autoUpdate.status, status)
					.save();
		}
		redirect("/admin/AndroidAutoUpdates/list?message=1");//添加成功
	}
}