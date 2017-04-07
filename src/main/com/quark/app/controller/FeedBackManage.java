/**
 * 
 */
package com.quark.app.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
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
import com.quark.app.logs.AppLog;
import com.quark.common.AppData;
import com.quark.common.RongToken;
import com.quark.common.Storage;
import com.quark.common.config;
import com.quark.interceptor.AppToken;
import com.quark.model.extend.Feedback;
import com.quark.model.extend.Tokens;
import com.quark.model.extend.User;
import com.quark.utils.DateUtils;
import com.quark.utils.FileUtils;
import com.quark.utils.MD5Util;
import com.quark.utils.MessageUtils;
import com.quark.utils.StringUtils;

/**
 * @author C罗
 * 意见反馈
 *
 */
@Before(Tx.class)
public class FeedBackManage extends Controller {

	@Author("cluo")
	@Rp("contact_us")
	@Explaination(info = "意见反馈")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String_NotRequired, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = "first_name")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = "last_name")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Feedback.content)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Feedback.email)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Feedback.phone)
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "FeedbackResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "FeedbackResponse{status}", remarks = "1-操作成功,2-填写邮箱", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "FeedbackResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void feedback() {
		try {
			String token = getPara("token");
			String first_name = getPara("first_name","");
			String last_name = getPara("last_name","");
			String content = getPara("content");
			String phone = getPara("phone");
			String email = getPara("email","");
			String invoke = getPara("invoke","app");
			String user_id = "0";
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				 user_id = "0";
			}else {
				user_id = AppToken.getUserId(token, this,invoke);
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			int status=0;String message = "Submission failed. Please check for errors.";
			if (first_name.equals("")) {
				status=2;
				message = "please enter your first name";
			}
			if (status==0) {
				if (last_name.equals("")) {
					status=2;
					message = "please enter your last name";
				}
			}
			if (status==0) {
				if (email.equals("")) {
					status=2;
					message = "please enter your email";
				}else {
					if (StringUtils.isEmail(email)==false) {
						status=2;
						message = "please enter your right email";
					}
				}
			}
			if (status==0) {
				if (content.equals("")) {
					status=2;
					message = "please say something";
				}
			}
			if (status==0) {
				Feedback feedback = new Feedback();
				boolean save = feedback.set(feedback.user_id, user_id).set(feedback.content, content)
						.set(feedback.name, (first_name+last_name)).set(feedback.email, email)
						.set(feedback.phone, phone)
						.set(feedback.post_time, DateUtils.getCurrentDateTime())
						.save();
				if (save) {
					status=1;
					message = "Thank you for your valuable advice.";
				}
			}
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("FeedbackResponse", responseValues);
			renderMultiJson("FeedbackResponse");
			AppLog.info("", getRequest());
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("FeedBackManage/Feedback", "意见反馈", this);
		}
	}
}
