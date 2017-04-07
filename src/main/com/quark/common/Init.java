package com.quark.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jsoup.Jsoup;
import org.xml.sax.InputSource;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.jfinal.plugin.ehcache.CacheInterceptor;
import com.jfinal.plugin.ehcache.CacheName;
import com.quark.api.auto.bean.ResponseValues;
import com.quark.interceptor.AppToken;
import com.quark.model.extend.User;
import com.quark.third.login.Twitter;
import com.quark.utils.DateUtils;
import com.quarkso.utils.ImgSavePathUtils;
import com.tenpay.ResponseHandler;
import com.tenpay.util.ConstantUtil;
import com.tenpay.util.WXUtil;

public class Init extends Controller {
	
	private static boolean isStart = false;

	public void index() {
		if (!isStart) {
			Thread th = new Thread(new AutoClose());
			th.start();
			isStart = true;
		}
		/**
		 * PC 端
		 */
		String name = getPara(0);
		if (name != null) {
			if (name.contains("~html")) {
				name = name.replace("~html", ".html");
			}
			render("/skillopedia/" + name);
		}else {
			render("/skillopedia/index.html");
		}
		/**
		 * end
		 */
	}
	
	/**
	 * 获取android apk
	 */
	public void apk() {
		String imgPath = getPara("name");
		String fullPath = config.save_path_root + imgPath;
		renderFile(new File(fullPath));
	}
	//////////////////////Twitter登陆////////////////////////////
	public void twitterOne(){
		String oauth_token = Twitter.startTwitterAuthentication();
		if (oauth_token.equals("")) {
			//获取oauth_token失败
			redirect("https://www.skillpier.com/#/landing");
		}else {
			redirect("https://api.twitter.com/oauth/authenticate?oauth_token="+oauth_token);
		}
	}
	//twitter官方配置回调函数
	public void getOauthVerifier(){
		String oauth_token = getPara("oauth_token");
		String oauth_verifier = getPara("oauth_verifier");
		String invoke = getPara("invoke","app");
		String t_uid = Twitter.getTwitterAccessTokenFromAuthorizationCode(oauth_verifier,oauth_token);
		String login_token = "";
		if (t_uid.equals("")) {
			//获取用户user_id失败
			redirect("https://www.skillpier.com/#/landing");
		}else {
			String return_string = Twitter.getTweetUser(t_uid.split("@")[1], t_uid.split("@")[2]);
			if (return_string.equals("")) {
				setAttr("flag", 0);
				setAttr("message",  "Sorry twitter email address not found,Login failed");
				render("/skillopedia/landing_twitter.html");
			}else {
				String icon_url = return_string.split("#")[2];
				String token = "",image_01="";int user_id= 0,agent_level = 1,is_third = 1;
				String sql = "select user_id,is_third,status,agent_level,image_01,is_third from user where email='"+return_string.split("#")[0]+"'";
				User user = User.dao.findFirst(sql); 
				if (user!=null) {
					is_third = user.get(user.is_third);
					if (is_third==1||is_third==2) {
						// 已经关联-登录  0-封号，1-正常,2-待审核
						int user_status = user.get(user.status);
						if (user_status==1) {
							//message =  "恭喜，登陆成功";
							image_01 = user.getStr(user.image_01);
							agent_level = user.get(user.agent_level);
							user_id = user.get(user.user_id);
							token = AppToken.sign(user_id + "",invoke);
							user.set(User.last_login_time, DateUtils.getCurrentDateTime()).update();
							// 设置cokie 
							setCookie("token", token, Integer.MAX_VALUE);
							setAttr("access_token", token);
							render("/skillopedia/sign_success.html");
						}
						if(user_status==0){
							setAttr("flag", 0);
							setAttr("message", "您的账号因异常，Skill安全中心已冻结处理。如有疑问问联系官方客服。");
							render("/skillopedia/landing_twitter.html");
						}
						if(user_status==2){
							setAttr("flag", 0);
							setAttr("message","您的账号未激活，请前往注册邮箱处理。如有疑问问联系官方客服。");
							render("/skillopedia/landing_twitter.html");
						}
						// end
					}
					if (is_third==0) {
						setAttr("flag", 1);
						setAttr("email", return_string.split("#")[0]);
						setAttr("nickname", return_string.split("#")[1]);
						setAttr("image_01", return_string.split("#")[2]);
						setAttr("message","未关联");
						render("/skillopedia/landing_twitter.html");
					}
				} else {
					image_01 = System.currentTimeMillis()+"";
					//立即关联
					User user2 = new User();
					boolean save = user2.set(user2.email, return_string.split("#")[0]).set(user2.password, "")
							.set(user2.post_time, DateUtils.getCurrentDateTime())
							.set(user2.last_login_time, DateUtils.getCurrentDateTime())
							.set(user2.nickname, return_string.split("#")[1])
							.set(user2.image_01, image_01+".jpg")
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
						/*status = 1;
						message =  "恭喜，登陆成功";*/
						is_third = 1;
						agent_level = user2.get(user2.agent_level);
						user_id = user2.get("user_id");
						token = AppToken.sign(user_id + "",invoke);
						if (!icon_url.equals("")) {
							ImgSavePathUtils.getImages(icon_url, image_01, config.images_path);
							image_01 = image_01+".jpg";
						}
						setAttr("access_token", token);
						render("/skillopedia/sign_success.html");
					}
				}
			}
		}
	}
	/**
	 * 跳转facebook绑定页面
	 */
	public void landingFacebook(){
		render("/skillopedia/landing_facebook.html");
	}
}
