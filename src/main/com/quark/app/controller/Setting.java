/**
 * 
 */
package com.quark.app.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
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
import com.quark.app.logs.AppLog;
import com.quark.common.AppData;
import com.quark.common.config;
import com.quark.interceptor.AppToken;
import com.quark.model.extend.AndroidAutoUpdate;
import com.quark.model.extend.Tokens;
import com.quark.model.extend.User;
import com.quark.utils.DateUtils;
import com.quark.utils.MD5Util;

/**
 * @author C罗
 *
 */
@Before(Tx.class)
public class Setting extends Controller {

	@Author("cluo")
	@Rp("设置")
	@Explaination(info = "所有状态")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "SettingStatusResponse{website_url}", remarks = "官网ＵＲＬ", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "SettingStatusResponse{apple_url}", remarks = "itunes.apple", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "SettingStatusResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "SettingStatusResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "SettingStatusResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void settingStatus() {
		try {
			String token = getPara("token");
			String message = "操作成功",user_id="0";
			ResponseValues response2 = new ResponseValues(this, Thread
					.currentThread().getStackTrace()[1].getMethodName());
			response2.put("website_url", "https://www.skillpier.com");
			response2.put("apple_url", "http://itunes.apple.com/WebObjects/MZStore.woa/wa/viewContentsUserReviews?id=1105373363&pageNumber=0&sortOrdering=2&type=Purple+Software&mt=8");
			response2.put("message", message);
			response2.put("status", 1);
			response2.put("code", 200);
			setAttr("SettingStatusResponse", response2);
			renderMultiJson("SettingStatusResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("Setting/settingStatus", "所有状态", this);
		}
	}
	@Author("cluo")
	@Rp("设置")
	@Explaination(info = "IOS自动更新")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "AutoUpdateResponse{version}", remarks = "你懂的", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "AutoUpdateResponse{releaseNotes}", remarks = "你懂的", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "AutoUpdateResponse{trackViewUrl}", remarks = "你懂的", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "AutoUpdateResponse{status}", remarks = "1-有更新，0-无更新", dataType = DataType.Int, defaultValue = "")
	public void autoUpdate() {
		try {
			String version="",releaseNotes="",trackViewUrl="";
			ResponseValues response2 = new ResponseValues(this, Thread
					.currentThread().getStackTrace()[1].getMethodName());
			Document doc = Jsoup.connect("https://itunes.apple.com/lookup?id="+config.itunes_apple_id).timeout(9000).post();
			String content = doc.select("body").first().text();
			JSONObject json = new JSONObject(content);
			int resultCount = json.getInt("resultCount");
			org.json.JSONArray jsonArray = json.getJSONArray("results");
			if (jsonArray!=null) {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject infoObject = jsonArray.getJSONObject(i);
					version = infoObject.get("version")+"";
					//releaseNotes = infoObject.get("releaseNotes")+"";
					trackViewUrl = infoObject.get("trackViewUrl")+"";
				}
			}
			String note = "1:修改首页列表icon显示\n2：修改首页轮播器的显示";
			response2.put("status", resultCount);
			response2.put("version", version);
			response2.put("releaseNotes", note);
			response2.put("trackViewUrl", trackViewUrl);
			setAttr("AutoUpdateResponse", response2);
			renderMultiJson("AutoUpdateResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("Setting/autoUpdate", "自动更新", this);
		}
	}
	@Author("cluo")
	@Rp("设置")
	@Explaination(info = "Android自动更新")
	@URLParam(defaultValue = "{1、2}", explain = Type.Int, type = Type.String, name = AndroidAutoUpdate.type)
	@URLParam(defaultValue = "", explain = "版本号", type = Type.Int, name = "versionCode")
	@ReturnOutlet(name = "AndroidAutoUpdateResponse{out_versionCode}", remarks = "版本号", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "AndroidAutoUpdateResponse{update_text}", remarks = "更新内容", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "AndroidAutoUpdateResponse{apk_name}", remarks = "APK名称：http://Ip:端口/files/image?name=apk_name", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "AndroidAutoUpdateResponse{apk_size}", remarks = "大小", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "AndroidAutoUpdateResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "AndroidAutoUpdateResponse{status}", remarks = "1-不需更新，2-有新版本,3-必须更新", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "AndroidAutoUpdateResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void androidAutoUpdate() {
		try {
			int type = getParaToInt("type",1);
			int versionCode = getParaToInt("versionCode");
			ResponseValues response2 = new ResponseValues(this, Thread
					.currentThread().getStackTrace()[1].getMethodName());
			int status=1;String out_versionCode="",update_text="",apk_name="",apk_size="";
			AndroidAutoUpdate autoUpdate = AndroidAutoUpdate.dao.findFirst("select new_versionCode,out_versionCode,update_text,apk_name,apk_size,status from android_auto_update where type=?",type);
			if (autoUpdate!=null) {
				int new_versionCode = autoUpdate.get(autoUpdate.new_versionCode);
				if (new_versionCode>versionCode) {
					 status = autoUpdate.get(autoUpdate.status);
					 out_versionCode = autoUpdate.getStr(autoUpdate.out_versionCode);
					 update_text = autoUpdate.getStr(autoUpdate.update_text);
					 apk_name = autoUpdate.getStr(autoUpdate.apk_name);
					 apk_size = autoUpdate.getStr(autoUpdate.apk_size);
				}
			}
			response2.put("status", status);
			response2.put("message", "Data request successful.");
			response2.put("code", 200);
			response2.put("out_versionCode", out_versionCode);
			response2.put("update_text", update_text);
			response2.put("apk_name", apk_name);
			response2.put("apk_size", apk_size);
			setAttr("AndroidAutoUpdateResponse", response2);
			renderMultiJson("AndroidAutoUpdateResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("Setting/androidAutoUpdate", "自动更新", this);
		}
	}
	@Author("cluo")
	@Rp("设置")
	@Explaination(info = "关于我们--h5")
	public void aboutUsH5() {
		try {
			render("/webview/about_us.html");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("Setting/aboutUsH5", "关于我们", this);
		}
	}
}
