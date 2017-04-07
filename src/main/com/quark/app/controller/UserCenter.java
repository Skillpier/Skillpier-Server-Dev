/**
 * 
 */
package com.quark.app.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

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
import com.quark.app.bean.EmailUntil;
import com.quark.app.logs.AppLog;
import com.quark.common.AppData;
import com.quark.common.RongToken;
import com.quark.common.Storage;
import com.quark.common.config;
import com.quark.interceptor.AppToken;
import com.quark.mail.SendMail;
import com.quark.model.extend.CityBean;
import com.quark.model.extend.Comment;
import com.quark.model.extend.CommentReply;
import com.quark.model.extend.Course;
import com.quark.model.extend.OrdersSchedule;
import com.quark.model.extend.Tokens;
import com.quark.model.extend.User;
import com.quark.rsa.RSAsecurity;
import com.quark.sign.RequestHandler;
import com.quark.utils.BaiduAPI;
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
 * 用户登陆注册
 */
@Before(Tx.class)
public class UserCenter extends Controller {

	@Author("cluo")
	@Rp("注册")
	@Explaination(info = "验证未注册邮箱是否正确")
	@UpdateLog(date = "2015-03-24 11:12", log = "初次添加")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = User.email)
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "checkEmailResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "checkEmailResponse{status}", remarks = "1-成功(无需提示):2-邮箱已经被注册,3-邮箱不正确", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "checkEmailResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "checkEmailResponse{tel_code}", remarks = "验证码", dataType = DataType.String, defaultValue = "")
	public void checkRegisterEmail() {
		try {
			String email = getPara(User.email);
			User user = null;
			String tel_code = "",message = "Email not found in system.";
			int status = 1;
			if(com.quark.utils.StringUtils.isEmail(email)==false){
				status = 3;
				message = "Please enter the correct email address.";
			}else {
				user = User.dao.findFirst("select * from user where email=?", email);
				if (user == null) {
					// 用户不存在
					status = 1;
				} else {
					message = "This email address is already in use.";
					status = 2;
				}
			}
			
			ResponseValues response = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			response.put("tel_code", tel_code);
			response.put("status", status);
			response.put("code", 200);
			response.put("message", message);
			setAttr("checkEmailResponse", response);
			renderMultiJson("checkEmailResponse");
			AppLog.info(null, getRequest());
		} catch (Exception e) {
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("UserCenter/checkRegisterEmail", "校验email", this);
		}
	}
	@Author("cluo")
	@Rp("注册")
	@Explaination(info = "注册用户")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = User.nickname)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = User.email)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = User.password)
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "RegistTelResponse{message}", remarks = "message", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "RegistTelResponse{user:token}", remarks = "token", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "RegistTelResponse{status}", remarks = "2-操作成功,3-,4-请输入昵称，5-请选择性别,6-请上传头像,7-手机号码已注册,8-登陆密码长度不合格，必须要大于6位以上，9-邮箱已注册，请重新输入", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "RegistTelResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void RegistTel() {
		try {
			String nickname = getPara("nickname","");
			String email = getPara("email","");
			String login_password = getPara("password");
			int sex = getParaToInt("sex",2);
			String birthday = getPara("birthday","1900-01-01");
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			String message="";int status=0;
			/**
			 * 解密密文
			 */ 
			String invoke = getPara("invoke","h5");
			if (!invoke.equals("h5")) {
				try {
					login_password = RSAsecurity.DESAndRSADecrypt(login_password);
				} catch (Exception e) {
					// TODO: handle exception
					ResponseValues response2 = new ResponseValues(this, Thread
							.currentThread().getStackTrace()[1].getMethodName());
					response2.put("message", "Password is incorrect.");
					response2.put("code", 414);
					setAttr("RegistTelResponse", response2);
					renderMultiJson("RegistTelResponse");
					return;
				}
			}
			/**
			 * end解密密文
			 */
			User user2 = User.dao.findFirst("select * from user where email=?", email);
			if (user2 != null) {
				int user_status = user2.get(user2.status);
				if (user_status==2) {
					status=3;
					message = "Your account has not yet been activated. Please go to your email to activate your account.";
					String token2 = AppToken.sign(user2.get("user_id") + "",invoke);
					EmailUntil.sendEmailActivateUser(email,token2,nickname);
				}
				if (user_status==0) {
					status=6;
					message = "Due to unusual activity, your account has been locked for security purposes. If you have any questions, please contact us.";
				}
				if (user_status==1) {
					status=5;
					message = "This email address is already in use. Please use another email address.";
				}
			}else{
				if(login_password.equals("")){
					status=4;
					message = "Please enter password.";
				} 
				if(login_password.length()<6||login_password.length()>16){
					status=4;
					message = "Length of password must be between 6-16 characters.";
				} 
				if(nickname.equals("")){
					status=5;
					message = "Please enter your username.";
				} 
			}
			if (status==0) {
				User user = new User();
				if (!birthday.equals("")&&birthday!=null) {
					int age_int = 0;
					if (!"1900-01-01".equals(birthday)) {
						age_int = DateUtils.getCurrentAgeByBirthdate(birthday);
					}
					user.set(User.age, age_int).set(User.birthday, "1900-01-01");
				}
				boolean save = user.set(User.email, email)
						.set(User.password, MD5Util.string2MD5(login_password))
						.set(User.post_time, DateUtils.getCurrentDateTime())
						.set(User.last_login_time, DateUtils.getCurrentDateTime())
						.set(User.nickname, nickname)
						.set(User.image_01, "example_7.png")
						.set(User.sex, sex).set(User.last_read_time, DateUtils.getCurrentDateTime())
						.set(User.birthday, birthday)
						.set(User.agent_level, 1)
						.set(User.coupon_change_max_num, 3)
						.set(User.authen_num, 3)
						.set(User.status, 2)
						.set(User.is_third, 0)
						.set(User.authen_status, 0)
						.save();
				status=2;
				int user_id = user.get("user_id");
				User user_info = User.dao.findFirst("select user_id,telephone,image_01 from user where user_id =?",user_id);
				// 设置token
				String token = "";
				if (user_info != null) {
					token = AppToken.sign(user_info.get("user_id") + "",invoke);
					EmailUntil.sendEmailRegistUser(email);
				}
				message = "Congratulations! You have almost finished your registration. Please go to your email to activate your account.";
				EmailUntil.sendEmailActivateUser(email,token,nickname);
			}
			responseValues.put("status", status);
			responseValues.put("message", message);
			responseValues.put("code", 200);
			setAttr("RegistTelResponse", responseValues);
			renderMultiJson("RegistTelResponse");
			AppLog.info("", getRequest());
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("UserCenter/RegistTel", "注册用户", this);
		}
	}
	@Author("cluo")
	@Rp("登录")
	@Explaination(info = "重新发送激活账号邮件")
	@URLParam(defaultValue = "", explain = "邮箱", type = Type.String, name = User.email)
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	//   
	@ReturnOutlet(name = "ResetSendEmailActivateResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "ResetSendEmailActivateResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "ResetSendEmailActivateResponse{status}", remarks = "1-发送成功,2-Invalid Email", dataType = DataType.Int, defaultValue = "")
	public void resetSendEmailActivate() {
		try {
			String email = getPara("email","");
			String invoke = getPara("invoke","app");
			ResponseValues responseValues = new ResponseValues(this, 
					Thread.currentThread().getStackTrace()[1].getMethodName());
			if (!email.equals("")) {
				email = email.trim();
			}
			String token = "";
			User user = User.dao.findFirst("select * from user where email='"+email+"'");
			if (user == null) {
				//若账号没有注册，提示框显示 Invalid Email or password
				responseValues.put("message", "Invalid Email");
				responseValues.put("status", 2);
			} else {
				int status = user.get("status");
				if(status==2){
					if (user != null) {
						token = AppToken.sign(user.get("user_id") + "",invoke);
					}
					String nickname = user.getStr(user.nickname);
					responseValues.put("message", "Congratulations! You have almost finished your registration. Please go to your email to activate your account.");
					EmailUntil.sendEmailActivateUser(email,token,nickname);
				}
				responseValues.put("message", "Send Success");
				responseValues.put("status", 1);
			}
			// end  
			responseValues.put("code", 200);
			setAttr("ResetSendEmailActivateResponse", responseValues);
			renderMultiJson("ResetSendEmailActivateResponse");
			AppLog.info("", getRequest());
			System.out.println("token:" + token);
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		}finally{
			AppData.analyze("UserCenter/resetSendEmailActivate", "重新发送激活账号邮件", this);
		}
	}
	@Author("cluo")
	@Rp("登录")
	@Explaination(info = "登陆【密码传来的是RSA密文】")
	@URLParam(defaultValue = "", explain = "手机号码", type = Type.String, name = User.email)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = User.password)
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	//   
	@ReturnOutlet(name = "LoginResponse{image_01}", remarks = "頭像", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "LoginResponse{token}", remarks = "用户token", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "LoginResponse{agent_level}", remarks = "1-用户，2-教练", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "LoginResponse{user_id}", remarks = "user_id", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "LoginResponse{email}", remarks = "email", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "LoginResponse{nickname}", remarks = "昵称", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "LoginResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "LoginResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "LoginResponse{status}", remarks = "1-登陆成功,2-手机没注册,3-登陆失败，密码不对,4-账号被冻结,5-未激活", dataType = DataType.Int, defaultValue = "")
	public void Login() {
		try {
			String email = getPara("email");
			String login_password = getPara("password");
			String invoke = getPara("invoke","app");
			System.out.println("email: " + email);
		
			
			
			/**
			 * 解密密文
			 */ 
			try {
				login_password = RSAsecurity.DESAndRSADecrypt(login_password);
				System.out.println("pwd1: " + login_password);
				System.out.println("pwd md51:"+ MD5Util.string2MD5(login_password));
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("login e: " + e.toString());
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Password is incorrect.");
				response2.put("code", 414);
				setAttr("LoginResponse", response2);
				renderMultiJson("LoginResponse");
				return;
			}
			/**
			 * end解密密文
			 */
			ResponseValues responseValues = new ResponseValues(this, 
					Thread.currentThread().getStackTrace()[1].getMethodName());
			String token = "",image_01="",nickname="";int user_id= 0,agent_level = 1,is_third = 1;
			User user = User.dao.findFirst("select * from user where email=?",email);
			if (user == null) {
				//若账号没有注册，提示框显示 Invalid Email or password
				responseValues.put("message", "Invalid Email or password");
				responseValues.put("status", 2);
			} else {
				int status = user.get("status");
				if(status==2){
					responseValues.put("message", "Your email has not been activated. Please go to your email address to activate your account. If you have any questions, please contact us.");
					responseValues.put("status", 5);
					setAttr("token", "");
				}else {
					user = User.dao.findFirst("select * from user where email=? and password=?",email,MD5Util.string2MD5(login_password));
					if (user != null) {
						if(status==1){
							// 登陆成功
							is_third = user.get(user.is_third);
							agent_level = user.get(user.agent_level);
							user_id = user.get(user.user_id);
							user.set(User.last_login_time, DateUtils.getCurrentDateTime()).update();
							responseValues.put("message", "Congratulations! Sign-in successful.");
							responseValues.put("status", 1);
							nickname = user.getStr(user.nickname);
							image_01 = user.getStr(user.image_01);
							token = AppToken.sign(user.get("user_id") + "",invoke);
						}
						if(status==0){
							responseValues.put("message", "Due to unusual activity, your account has been locked for security purposes. If you have any questions, please contact us.");
							responseValues.put("status", 4);
							setAttr("token", "");
						}
						if(status==2){
							responseValues.put("message", "Your email has not been activated. Please go to your email address to activate your account. If you have any questions, please contact us.");
							responseValues.put("status", 5);
							setAttr("token", "");
						}
					} else {
						// 登陆失败，密码不对
						responseValues.put("message", "Password is incorrect.");
						responseValues.put("status", 3);
						setAttr("token", "");
					}
				}
			}
			// 设置cokie 
			setCookie("token", token, Integer.MAX_VALUE);
			// end  
			responseValues.put("is_third", is_third);
			responseValues.put("agent_level", agent_level);
			responseValues.put("user_id", user_id);
			responseValues.put("email", email);
			responseValues.put("nickname", nickname);
			responseValues.put("image_01", image_01);
			responseValues.put("token", token);
			responseValues.put("code", 200);
			setAttr("LoginResponse", responseValues);
			renderMultiJson("LoginResponse");
			AppLog.info("", getRequest());
			System.out.println("token:" + token);
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		}finally{
			AppData.analyze("UserCenter/Login", "登陆用户", this);
		}
	}
	@Author("cluo")
	@Rp("忘记密码")
	@Explaination(info = "发送邮件前往邮箱重置密码[忘记]")
	@UpdateLog(date = "2015-08-11 11:12", log = "初次添加")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = User.email)
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "SendForgetEmailResponse{status}", remarks = "1-获取成功，0-失败，2-没注册", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "SendForgetEmailResponse{message}", remarks = "邮箱验证码", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "SendForgetEmailResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.String, defaultValue = "")
	public void sendForgetEmail() {
		try {
			String email = getPara("email");
			String invoke = getPara("invoke","app");
			String code = "",message="";int status = 0;
			if (AppData.checkEmail(email)) {
				User user = User.dao.findFirst("select user_id,nickname from user where email=?", email);
				if (user != null) {
					// 用户存在
					int user_id = user.get(user.user_id);
					String nickname = user.getStr(user.nickname);
					String token = AppToken.sign(user_id + "",invoke);
					status = 1;
					message = "Please go to your email to reset your password.";
					EmailUntil.sendEmailResetPwd(email,nickname,token);
				} else {
					message = "This user does not exist.";
					status = 2;
				}
			}else {
				status = 0;
				message = "Please enter a valid email address.";
			}
			ResponseValues response = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			response.put("status", status);
			response.put("code", 200);
			response.put("email_code", code);
			response.put("message", message);
			setAttr("SendForgetEmailResponse", response);
			renderMultiJson("SendForgetEmailResponse");
			AppLog.info(null, getRequest());
		} catch (Exception e) {
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("UserCenter/sendForgetEmail", "发送邮件前往邮箱重置密码", this);
		}
	}
	@Author("cluo")
	@Rp("忘记密码")
	@Explaination(info = "发送邮件前往邮箱修改密码[修改]")
	@UpdateLog(date = "2015-08-11 11:12", log = "初次添加")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "SendModifyEmailResponse{status}", remarks = "1-获取成功，0-失败，2-没注册", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "SendModifyEmailResponse{message}", remarks = "邮箱验证码", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "SendModifyEmailResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.String, defaultValue = "")
	public void sendModifyEmail() {
		try {
			String token = getPara("token");
			String invoke = getPara("invoke","app");
			String code = "",message="";int status = 0;
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this,
						Thread.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "URL expired.");
				response2.put("code", 405);
				setAttr("SendModifyEmailResponse", response2);
				renderMultiJson("SendModifyEmailResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			User user = User.dao.findById(user_id);
			if (user != null) {
				// 用户存在
				String email = user.getStr(user.email);
				EmailUntil.sendEmailModifyPwd(email,token);
				status = 1;
				message = "Please sign-in to your email to change your password.";
			} else {
				message = "This user does not exist.";
				status = 2;
			}
			ResponseValues response = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			response.put("status", status);
			response.put("code", 200);
			response.put("email_code", code);
			response.put("message", message);
			setAttr("SendModifyEmailResponse", response);
			renderMultiJson("SendModifyEmailResponse");
			AppLog.info(null, getRequest());
		} catch (Exception e) {
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("UserCenter/sendModifyEmail", "发送邮件前往邮箱修改密码", this);
		}
	}
	@Author("cluo")
	@Rp("pc-重置密码")
	@Explaination(info = "忘记密码-设置新密码[忘记，修改]")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = User.password)
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "ResetPasswordResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "ResetPasswordResponse{status}", remarks = "1-操作成功,2-验证码不正确,3-密码长度不合格,请输入不少于6位", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "ResetPasswordResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void resetPassword() {
		try {
			String token = getPara("token");
			String invoke = getPara("invoke","app");
			String message = "";
			String login_password = getPara("password");
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this,
						Thread.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "URL expired.");
				response2.put("code", 405);
				setAttr("ResetPasswordResponse", response2);
				renderMultiJson("ResetPasswordResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			int status = 1;
			// 检测验证码
			if (login_password.length() < 6 || login_password.length()>16) {// 检测密码长度是否合格
				status = 3;
				message = "Length of password must be between 6-16 characters.";
			}else {
				User user = User.dao.findById(user_id);
				boolean update = user.set(User.password, MD5Util.string2MD5(login_password)).update();
				if (update) {
					status = 1;
					message = "Password has been changed.";
				}else {
					status = 0;
					message = "Password change failed.";
				}
			}
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("ResetPasswordResponse", responseValues);
			renderMultiJson("ResetPasswordResponse");
			AppLog.info("", getRequest());
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("UserCenter/resetPassword", "重置密码", this);
		}
	}
	@Author("cluo")
	@Rp("账户中心、个人主页")
	@Explaination(info = "修改头像网页")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = "文件名", type = Type.String, name = "filename")
	@ReturnOutlet(name = "UpdateAvatarH5Response{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "UpdateAvatarH5Response{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "UpdateAvatarH5Response{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void UpdateAvatarH5() {
		try {
			String token = getPara("token");
			String filename = getPara("filename","");
			String invoke = getPara("invoke","app");
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this,
						Thread.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("UpdateAvatarH5Response", response2);
				renderMultiJson("UpdateAvatarH5Response");
				return;
			}
			String message="";
			String user_id = AppToken.getUserId(token, this,invoke);
			User user = User.dao.findById(user_id);
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			if (!filename.equals("")){
				boolean update = user.set(user.image_01, filename)
								.set(user.last_login_time, DateUtils.getCurrentDateTime())
								.update();
				if (update) {
					message = "Update successful.";
					responseValues.put("status", 1);
				} else {
					message = "Update failed.";
					responseValues.put("status", 0);
				}
			}else{
				message = "Please upload your pictures. ";
				responseValues.put("status", 2);
			}
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("UpdateAvatarH5Response", responseValues);
			renderMultiJson("UpdateAvatarH5Response");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("UserCenter/UpdateAvatar", "修改头像", this);
		}
	}
	@Author("cluo")
	@Rp("账户中心、个人主页")
	@Explaination(info = "修改头像")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.File, name = User.image_01)
	@ReturnOutlet(name = "UpdateAvatarResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "UpdateAvatarResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "UpdateAvatarResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void UpdateAvatar() {
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
				setAttr("UpdateAvatarResponse", response2);
				renderMultiJson("UpdateAvatarResponse");
				return;
			}
			String message="";
			String user_id = AppToken.getUserId(token, this,invoke);
			User user = User.dao.findById(user_id);
			String image_01String = user.getStr(user.image_01);
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			if (image_01 != null){
				String fileName = FileUtils.renameToFile(image_01,378,360);
				user.set(User.image_01, fileName);
				boolean update = user.set(user.last_login_time, DateUtils.getCurrentDateTime()).update();
				if (update) {
					message = "Update successful.";
					FileUtils.deleteFile(config.save_path+image_01String);
					responseValues.put("status", 1);
				} else {
					message = "Update failed.";
					responseValues.put("status", 0);
				}
			}else{
				message = "Please upload your pictures. ";
				responseValues.put("status", 2);
			}
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("UpdateAvatarResponse", responseValues);
			renderMultiJson("UpdateAvatarResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("UserCenter/UpdateAvatar", "修改头像", this);
		}
	}
	@Author("cluo")
	@Rp("账户中心、个人主页")
	@Explaination(info = "修改昵稱")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = User.nickname)
	@URLParam(defaultValue = "", explain = "RSA加密原来的密码", type = Type.String, name = "old_password")
	@URLParam(defaultValue = "", explain = "RSA加密新密码", type = Type.String, name = User.password)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = User.setting_notify_start_course)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "UpdateNicknameResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "UpdateNicknameResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "UpdateNicknameResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void UpdateNickname() {
		try {
			String token = getPara("token");
			String nickname = getPara("nickname","");
			String password = getPara("password","");
			String old_password = getPara("old_password","");
			int setting_notify_start_course = getParaToInt("setting_notify_start_course",1);
			String invoke = getPara("invoke","app");
			if (!password.equals("")) {
				/**
				 * 解密密文
				 */ 
				try {
					password = RSAsecurity.DESAndRSADecrypt(password);
				} catch (Exception e) {
					// TODO: handle exception
					ResponseValues response2 = new ResponseValues(this, Thread
							.currentThread().getStackTrace()[1].getMethodName());
					response2.put("message", "Password is incorrect.");
					response2.put("code", 414);
					setAttr("UpdateNicknameResponse", response2);
					renderMultiJson("UpdateNicknameResponse");
					return;
				}
				try {
					old_password = RSAsecurity.DESAndRSADecrypt(old_password);
				} catch (Exception e) {
					// TODO: handle exception
					ResponseValues response2 = new ResponseValues(this, Thread
							.currentThread().getStackTrace()[1].getMethodName());
					response2.put("message", "Password is incorrect.");
					response2.put("code", 414);
					setAttr("UpdateNicknameResponse", response2);
					renderMultiJson("UpdateNicknameResponse");
					return;
				}
				/**
				 * end解密密文
				 */
			}
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this,
						Thread.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("UpdateNicknameResponse", response2);
				renderMultiJson("UpdateNicknameResponse");
				return;
			}
			String message="Update successful.";int status = 1;
			boolean update = false;
			String user_id = AppToken.getUserId(token, this,invoke);
			User user = User.dao.findById(user_id);
			if (user!=null) {
				if (!password.equals("")) {
					String sql_password = user.getStr(user.password);
					if (sql_password.equals(MD5Util.string2MD5(old_password))) {
						if(password.length()<6||password.length()>16){
							status=2;
							message = "Length of password must be between 6-16 characters.";
						}else{
							user.set(User.password, MD5Util.string2MD5(password));
						}
					}else {
						status=2;
						message = "Please enter the correct password.";
					}
				}
				if (!nickname.equals("")) {
					user.set(User.nickname, nickname);
				}
				update = user.set(user.last_login_time, DateUtils.getCurrentDateTime())
						.set(user.setting_notify_start_course, setting_notify_start_course)
						.update();
				if (update==false) {
					status = 0;
					message = "Update failed.";
				}
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("message", message);
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("UpdateNicknameResponse", responseValues);
			renderMultiJson("UpdateNicknameResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("UserCenter/UpdateNickname", "修改昵稱", this);
		}
	}
	@Author("cluo")
	@Rp("账户中心")
	@Explaination(info = "基本信息")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnDBParam(name = "InfoResponse{baseInfoResult:UserInfo:$}", column = User.user_id)
	@ReturnDBParam(name = "InfoResponse{baseInfoResult:UserInfo:$}", column = User.image_01)
	@ReturnDBParam(name = "InfoResponse{baseInfoResult:UserInfo:$}", column = User.nickname)
	@ReturnDBParam(name = "InfoResponse{baseInfoResult:UserInfo:$}", column = User.email)
	@ReturnDBParam(name = "InfoResponse{baseInfoResult:UserInfo:$}", column = User.agent_level)
	@ReturnDBParam(name = "InfoResponse{baseInfoResult:UserInfo:$}", column = User.authen_status)
	@ReturnDBParam(name = "InfoResponse{baseInfoResult:UserInfo:$}", column = User.authen_num)
	@ReturnDBParam(name = "InfoResponse{baseInfoResult:UserInfo:$}", column = User.authen_time)
	@ReturnDBParam(name = "InfoResponse{baseInfoResult:UserInfo:$}", column = User.setting_notify_start_course)
	@ReturnDBParam(name = "InfoResponse{baseInfoResult:UserInfo:$}", column = User.is_third)
	
	@ReturnOutlet(name = "InfoResponse{read_number}", remarks = "未读消息数", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "InfoResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "InfoResponse{status}", remarks = "1-操作成功，0-失败", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "InfoResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void baseInfo() {
		try {
			String token = getPara("token");
			ResponseValues response = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
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
				setAttr("InfoResponse", response2);
				renderMultiJson("InfoResponse");
				return;
			}
			/**
			 * 接口签名end
			 */
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this,
						Thread.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("InfoResponse", response2);
				renderMultiJson("InfoResponse");
				return;
			}
			int status = 1;String message="";
			String user_id = AppToken.getUserId(token, this,invoke);
			String curr_time = DateUtils.getCurrentDate();
			final User user2 = User.dao.findFirst("select user_id,is_third,telephone,image_01,nickname,email,agent_level,authen_status,authen_num,authen_time,setting_notify_start_course from user where user_id=?",user_id);
			int agent_level = user2.get(user2.agent_level);
			int read_number = 0;
			String filter_sql ="";
			if (agent_level==1) {
				//1-用户，2-教练
				filter_sql =" (status=1 or status=3) and user_id="+user_id;
			}
			if (agent_level==2) {
				filter_sql =" (status=1 or status=2) and public_course_user_id="+user_id;
			}
			int course_id=0;
			Course course = Course.dao.findFirst("select course_id from course where user_id="+user_id+" and status=2 and is_auth_public=2");
			if (course!=null) {
				course_id = course.get(course.course_id);
			}
			user2.put("course_id", course_id);
			List<Comment> comments = Comment.dao.find("select comment_id,is_reply,comment_reply_id from comment where "+filter_sql+" and is_read=0 ");
			List<CommentReply> commentReplies = CommentReply.dao.find("select comment_reply_id from comment_reply where user_id=? and is_read=0 ",user_id);
			user2.put("read_number", (comments.size()+commentReplies.size()));
			List<OrdersSchedule> ordersSchedules = OrdersSchedule.dao.find("select orders_schedule_id from orders_schedule where schedule_ids>0 and is_read=0 and orders_id in(select orders_id from orders where course_user_id="+user_id+")");
			user2.put("booking_number", ordersSchedules.size());
			response.put("message", message);
			response.put("status", status);
			response.put("code", 200);
			response.put("Result", new HashMap<String, Object>() {
				{
					put("UserInfo", user2);
				}
			});
			setAttr("InfoResponse", response);
			renderMultiJson("InfoResponse");
			
			AppLog.info("", getRequest());
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("UserCenter/baseInfo", "基本信息", this);
		}
	}
	/**
	 * 邮件激活账户
	 */
	public void activate() {
		// TODO Auto-generated method stub
		String token = getPara("token");
		String invoke = getPara("invoke","h5");
		if (!AppToken.check(token, this,invoke)) {
			// 登陆失败
			render("/third/email_oauth.html");
		}else{
			String user_id = AppToken.getUserId(token, this,invoke);
			User user = User.dao.findById(user_id);
			if (user!=null) {
				int status = user.get(user.status);
				if (status==2) {
					user.set(user.status, 1).update();
				}
			}
			render("/third/email_oauth.html");
		}
	}
	 /**
		 * 跳转界面
		 */
	public void resetPasswordHtml() {
		// TODO Auto-generated method stub
		String token = getPara("token");
		setAttr("token",token);
		render("/admin/addRecommend.html");
	}
	@Author("cluo")
	@Rp("我是教练")
	@Explaination(info = "教练认证")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = User.nickname)
	@URLParam(defaultValue = "{0、1}", explain = Value.Infer, type = Type.String, name = User.sex)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = User.birthday)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = User.telephone)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = User.cover_ID_01)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = User.cover_ID_02)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = User.category_name)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = User.experiences)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "AuthenResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "AuthenResponse{status}", remarks = "1-操作成功，0-失败", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "AuthenResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void authen() {
		try {
			String token = getPara("token");
			String nickname = getPara("nickname","");
			int sex = getParaToInt("sex",0);
			String birthday = getPara("birthday","");
			String telephone = getPara("telephone","");
			String cover_ID_01 = getPara("cover_ID_01","");
			String cover_ID_02 = getPara("cover_ID_02","");
			String category_name = getPara("category_name","");
			String experiences = getPara("experiences","");
			ResponseValues response = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("birthday", birthday);
			packageParams.put("category_name", category_name);
			packageParams.put("cover_ID_01", cover_ID_01);
			packageParams.put("cover_ID_02", cover_ID_02);
			packageParams.put("experiences", experiences);
			packageParams.put("nickname", nickname);
			packageParams.put("sex", sex+"");
			packageParams.put("telephone", telephone);
			packageParams.put("token", token);
			RequestHandler reqHandler = new RequestHandler(null, null);
			reqHandler.init(config.app_quark_id, config.app_quark_secret, config.app_quark_key);
			String sign = reqHandler.createSign(packageParams);
			String invoke = getPara("invoke","app");
			if (invoke.equals("h5")) {
				sign = "123456";
			}
			System.out.println(sign);
			if (!app_sign.equals(sign)) {
				// Signature verification failed.
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Signature verification failed.");
				response2.put("code", 403);
				setAttr("AuthenResponse", response2);
				renderMultiJson("AuthenResponse");
				return;
			}
			/**
			 * 接口签名end
			 */
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this,
						Thread.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("AuthenResponse", response2);
				renderMultiJson("AuthenResponse");
				return;
			}
			int status = 0;String message="Submission failed.";
			if (nickname.equals("")||birthday.equals("")||telephone.equals("")) {
				status = 2;
				message="Please fill out the relevant information.";
			}
			if (status==0) {
				if (cover_ID_01.equals("")||cover_ID_02.equals("")) {
					status = 2;
					message="Please upload your ID.";
				}
			}
			if (status==0) {
				if (category_name.equals("")) {
					status = 2;
					message="Please select your category.";
				}
			}
			if (status==0) {
				if (experiences.equals("")) {
					status = 2;
					message="Please select your teaching experience.";
				}
			}
			if (status==0) {
				String user_id = AppToken.getUserId(token, this,invoke);
				User user = User.dao.findById(user_id);
				String email = user.getStr(user.email);
				int authen_num = user.get(user.authen_num);
				if (authen_num<1) {
					status = 3;
					message="There are no more certification opportunities. Please contact customer service.";
				}else {
					boolean update = user.set(user.authen_nickname, nickname)
							.set(user.birthday, birthday)
							.set(user.sex, sex)
							.set(user.telephone, telephone)
							.set(user.cover_ID_01, cover_ID_01)
							.set(user.cover_ID_02, cover_ID_02)
							.set(user.authen_num, (authen_num-1))
							.set(user.authen_status, 1)
							.set(user.category_name, category_name)
							.set(user.experiences, experiences)
							.set(user.authen_time, DateUtils.getCurrentDateTime())
							.update();
					if (update) {
						status = 1;
						message="Submission successful. Our administrator will review your submission shortly and contact you as soon as possible.";
						EmailUntil.sendEmailApplyCourse(user_id,email);
					}
				}
			}
			response.put("message", message);
			response.put("status", status);
			response.put("code", 200);
			setAttr("AuthenResponse", response);
			renderMultiJson("AuthenResponse");
			AppLog.info("", getRequest());
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("UserCenter/authen", "教练认证", this);
		}
	}
	////////////////////第三方关联登陆
	@Author("cluo")
	@Rp("登录")
	@Explaination(info = "判断是否关联【先注册后关联，第三方登陆第一步】")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = "email")
	@URLParam(defaultValue = "", explain = "第三方头像", type = Type.String, name = "icon_url")
	@URLParam(defaultValue = "", explain = "", type = Type.String, name = User.nickname)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "IsThirdLoginRelatedResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "IsThirdLoginRelatedResponse{status}", remarks = "1-已关联，并登陆,2-mailbox unavailable,3-您的账号因异常，4-未关联", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "IsThirdLoginRelatedResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void isThirdLoginRelated() {
		try {
			String email = getPara("email","");
			String nickname = getPara("nickname","");
			String icon_url = getPara("icon_url","");
			/**
			* 接口签名
			*/ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("email", email);
			packageParams.put("icon_url", icon_url);
			packageParams.put("nickname", nickname);
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
				setAttr("IsThirdLoginRelatedResponse", response2);
				renderMultiJson("IsThirdLoginRelatedResponse");
				return;
			}
			/**
			* 接口签名end
			*/
			int status = 1;String message = "已关联";
			ResponseValues responseValues = new ResponseValues(this, 
					Thread.currentThread().getStackTrace()[1].getMethodName());
			String token = "",image_01="";int user_id= 0,agent_level = 1,is_third = 1;
			if (email.equals("")) {
				status = 2;
				message = "mailbox unavailable";
			}else {
				image_01 = System.currentTimeMillis()+"";
				if (!icon_url.equals("")) {
					ImgSavePathUtils.getImages(icon_url, image_01, config.images_path);
					image_01 = image_01+".jpg";
				}
				email = email.trim();
				String sql = "select user_id,is_third,status,agent_level,image_01,is_third from user where email='"+email+"'";
				User user = User.dao.findFirst(sql); 
				if (user!=null) {
					is_third = user.get(user.is_third);
					if (is_third==1||is_third==2) {
						// 已经关联-登录  0-封号，1-正常,2-待审核
						int user_status = user.get(user.status);
						if (user_status==1) {
							// 登陆成功
							status = 1;
							message =  "Congratulations! Sign-in successful.";
							agent_level = user.get(user.agent_level);
							user_id = user.get(user.user_id);
							token = AppToken.sign(user_id + "",invoke);
							user.set(User.image_01, image_01).set(User.last_login_time, DateUtils.getCurrentDateTime()).update();
						}
						if(user_status==0){
							status = 3;
							setAttr("token", "");
							message = "Due to unusual activity, your account has been locked for security purposes. If you have any questions, please contact us.";
						}
						if(user_status==2){
							status = 3;
							setAttr("token", "");
							message = "Your email has not been activated. Please go to your email address to activate your account. If you have any questions, please contact us.";
						}
						// 设置cokie 
						setCookie("token", token, Integer.MAX_VALUE);
						// end
					}
					if (is_third==0) {
						status = 4;
						setAttr("token", "");
						message = "未关联";
					}
				} else {
					//立即关联
					User user2 = new User();
					boolean save = user2.set(user2.email, email).set(user2.password, "")
							.set(user2.post_time, DateUtils.getCurrentDateTime())
							.set(user2.last_login_time, DateUtils.getCurrentDateTime())
							.set(user2.nickname, nickname)
							.set(user2.image_01, image_01)
							.set(user2.last_read_time, DateUtils.getCurrentDateTime())
							.set(user2.birthday, "1900-01-01")
							.set(user2.agent_level, 1)
							.set(user2.coupon_change_max_num, 3)
							.set(user2.authen_num, 3)
							.set(user2.status, 1)
							.set(user2.is_third, 1)
							.set(user2.authen_status, 0)
							.save();
					if (save) {
						status = 1;
						message =  "Congratulations! Sign-in successful.";
						is_third = 1;
						image_01 = user2.getStr(user2.image_01);
						agent_level = user2.get(user2.agent_level);
						user_id = user2.get("user_id");
						token = AppToken.sign(user_id + "",invoke);
					}
				}
			}
			responseValues.put("is_third", is_third);
			responseValues.put("agent_level", agent_level);
			responseValues.put("user_id", user_id);
			responseValues.put("status", status);
			responseValues.put("image_01", image_01);
			responseValues.put("server_telephone", "");
			responseValues.put("message", message);
			responseValues.put("token", token);
			responseValues.put("code", 200);
			setAttr("IsThirdLoginRelatedResponse", responseValues);
			renderMultiJson("IsThirdLoginRelatedResponse");
			AppLog.info("", getRequest());
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("UserCenter/isThirdLoginRelated", "是否第三方登录", this);
		}
	}
	@Author("cluo")
	@Rp("登录")
	@Explaination(info = "第三方登录（立即关联时调用）【第三方登陆第二步】【密码传来的是RSA密文】")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = User.email)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = User.password)
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "ThirdLoginResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "ThirdLoginResponse{status}", remarks = "1-已关联，并登陆,2-uid获取失败{用户账号不存在或者密码错误},3-您的账号因异常", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "ThirdLoginResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void thirdLogin() {
		try {
			String email = getPara("email");
			String password = getPara("password");
			String invoke = getPara("invoke","app");
			/**
			 * 解密密文
			 */ 
			try {
				System.out.println(password+"===");
				password = RSAsecurity.DESAndRSADecrypt(password);
			} catch (Exception e) {
				// TODO: handle exception
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Password is incorrect.");
				response2.put("code", 414);
				setAttr("ThirdLoginResponse", response2);
				renderMultiJson("ThirdLoginResponse");
				return;
			}
			/**
			 * end解密密文
			 */
			ResponseValues responseValues = new ResponseValues(this, 
					Thread.currentThread().getStackTrace()[1].getMethodName());
			int status = 1;String message = "已关联";
			if (email.equals("")) {
				status = 2;
				message = "mailbox unavailable";
			}
			String token = "",image_01="";int user_id= 0,agent_level = 1,is_third = 1;
			if (status==1) {
				//立即关联
				password = MD5Util.string2MD5(password);
				User user = User.dao.findFirst("select user_id,is_third,status,agent_level,image_01 from user where email=? and password=?",email, password);
				if (user == null) {
					status = 2 ;
					message= "The account does not exist or password is incorrect.";
				}else {
					// 已经关联-登录  0-封号，1-正常,2-待激活
					int user_status = user.get("status");
					if (user_status==1) {
						// 登陆成功
						message =  "Congratulations! Sign-in successful.";
						status = 1;
						is_third = user.get(user.is_third);
						agent_level = user.get(user.agent_level);
						user_id = user.get(user.user_id);
						image_01 = user.getStr(user.image_01);
						token = AppToken.sign(user_id + "",invoke);
						// 设置cokie 
						setCookie("token", token, Integer.MAX_VALUE);
						user.set(user.last_login_time, DateUtils.getCurrentDateTime())
							.set(user.is_third, 2)
							.update();
					}
					if(status==0){
						status = 3;
						message = "Due to unusual activity, your account has been locked for security purposes. If you have any questions, please contact us.";
					}
					if(status==2){
						status = 3;
						message = "Your email has not been activated. Please go to your email address to activate your account. If you have any questions, please contact us.";
					}
				}
			}
			responseValues.put("is_third", is_third);
			responseValues.put("agent_level", agent_level);
			responseValues.put("user_id", user_id);
			responseValues.put("status", status);
			responseValues.put("image_01", image_01);
			responseValues.put("server_telephone", "");
			responseValues.put("message", message);
			responseValues.put("token", token);
			responseValues.put("code", 200);
			setAttr("ThirdLoginResponse", responseValues);
			renderMultiJson("ThirdLoginResponse");
			AppLog.info("", getRequest());
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("UserCenter/thirdLogin", "第三方登录", this);
		}
	}
}
