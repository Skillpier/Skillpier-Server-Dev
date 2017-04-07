package com.quark.common;

import java.util.List;

import net.sf.ehcache.transaction.xa.commands.Command;
import cn.jpush.api.push.model.Message;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.Const;
import com.jfinal.core.Controller;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.ext.handler.UrlSkipHandler;
import com.jfinal.ext.interceptor.Restful;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.bonecp.BoneCpPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.upload.UploadFile;
import com.jolbox.bonecp.BoneCP;
import com.quark.admin.controller.AdminUsers;
import com.quark.admin.controller.Files;
import com.quark.admin.controller.JavaFiles;
import com.quark.admin.controller.Login;
import com.quark.admin.controller.SwiftFiles;
import com.quark.admin.controller.api;
import com.quark.admin.controller.rp;
import com.quark.handler.H5Handler;
import com.quark.handler.RpHandler;
import com.quark.model.*;
import com.quark.model.extend.AdminUser;
import com.quark.model.extend.AndroidAutoUpdate;
import com.quark.model.extend.Applogs;
import com.quark.model.extend.Category01;
import com.quark.model.extend.Category02;
import com.quark.model.extend.Catetory;
import com.quark.model.extend.ChargeLog;
import com.quark.model.extend.CityBean;
import com.quark.model.extend.Collection;
import com.quark.model.extend.Comment;
import com.quark.model.extend.CommentReply;
import com.quark.model.extend.Constant;
import com.quark.model.extend.Coupon;
import com.quark.model.extend.Course;
import com.quark.model.extend.CourseCertification;
import com.quark.model.extend.Experience;
import com.quark.model.extend.Feedback;
import com.quark.model.extend.IndexBanner;
import com.quark.model.extend.MyCoupon;
import com.quark.model.extend.Orders;
import com.quark.model.extend.OrdersSchedule;
import com.quark.model.extend.Rule;
import com.quark.model.extend.Schedule;
import com.quark.model.extend.Tokens;
import com.quark.model.extend.User;
import com.quark.model.extend.ZipCode;
import com.quark.utils.PackageUtils;

/**
 * API引导式配置
 */
public class config extends JFinalConfig {

	/**
	 * pay host
	 */
	public static final String server_web = "www.skillpier.com";
	public static final String server_ip = "47.88.79.54";
	public static final String pay_notify_host = "47.88.79.54:80";
	public static final String controllers = "com.quark.app.controller";

	public static final String images_path = "c:/images/";
	public static final String ueditor_images_path = "c:/images/ueditor/";
	public static final String relative_path = "/upload/";
	public static final String rsa_key_path = "c:/images/rsa/";
	public static final String save_path_root = PathKit.getWebRootPath() + relative_path;

	public static final String project = "";
	public static final int app_page_size = Controller.PAGE_SIZE;
	public static final String save_path = images_path;// PathKit.getWebRootPath()+ relative_path;
	/**
	 * 网易免费企业邮箱 pop：pop.ym.163.com smtp：smtp.ym.163.com 腾讯 ：smtp.exmail.qq.com
	 * 
	 * 网易收费企业邮箱 pop：pop.qiye.163.com smtp：smtp.qiye.163.com
	 */
	//美橙SMTP 企业邮箱：smtp.chengmail.cn
	public static final String email_smtp = "smtp.ym.163.com";
	public static final String email_username = "no-reply@yerletech.com";
	public static final String email_password = "series_love15889";
	/**
	 * iPhone自动升级id
	 */
	public static final String itunes_apple_id = "1037183608";
	public static final String hunxin_admin_id = "1";
	/**
	 * 数据库用户名及密码
	 */
	public static String db_username = "";
	public static String db_password = "";
	public static final String backup = PathKit.getWebRootPath() + "/backup";
	public static C3p0Plugin boneCpPlugin;
	/**
	 * 配置常量
	 */
	public static boolean devMode = false;
	/**
	 * 配置常量
	 */
	/**
	 * paypal对应的id
	 */
	//live
	public static final String paypal_client_id = "Af6T3jz6-gYqh3XTT-ByFStHrcqNE1DmjebIekF2SXojnkGi-aFYEiw8nBElsGv7GK32UW0pk8aQERAA";
	public static final String paypal_secret = "EGEzmUjgavCHDWysJFaYjopQ60BIH_Hq24XAmFOIpUddIzpR14_sNIlK6oewTZ-Wi9uUKP4PZk-_ctar";
	//SANDBOX 
	public static final String paypal_client_id_sandbox = "AZToOPZitfXZg1Fq3uuID3yUV4oNevrAt-UbuY5-qllyiSBvqHJ_tfeInh6k040kf-ZN9CTUNQ423rAZ";
	public static final String paypal_secret_sandbox = "EE918Mc_8Wnx3YXTLu3X55rFGeOSi9zVLOhxg0PkAHKh8xm8Kz9giFlrqyFKdB0yD48I5HuXcpHu0cOs"; 
	/**
	 * visa对应的id
	 */
	//测试环境
	//public static final String apiLoginId = "3X9Py7Fp4";
	//public static final String transactionKey = "9526e7Unp4U7TcHE";
	//真实环境
	public static final String apiLoginId = "965T5gkCz";
	public static final String transactionKey = "9J4X72TA5rz6qs6j";
	/**
	 * 接口需要的API
	 */
	public static final String app_quark_id = "2016SKILLopedia123419";
	public static final String app_quark_secret = "d1d998_SKILLopedia123_038ca9f";
	public static final String app_quark_key = "2016SKILLopedia1234191112";

	public void configConstant(Constants me) {
		// 加载少量必要配置，随后可用getProperty(..)获取值
		loadPropertyFile("config.txt");
		me.setDevMode(getPropertyToBoolean("devMode", true));
		// 过滤以^拼接的请求
		me.setUrlParaSeparator("^");
		// 文件上传默认10M,此处设置为最大1000M
		me.setMaxPostSize(100 * Const.DEFAULT_MAX_POST_SIZE);
	}

	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		/**
		 * 初始化
		 */
		me.add("/",Init.class);
		me.add("/api", api.class);
		me.add("/rp", rp.class);
		me.add("/java", JavaFiles.class);
		me.add("/swift", SwiftFiles.class);
		// 统一图片路径
		me.add("/files", Files.class);
		/**
		 * app功能
		 */
		List<Class> app_clazz = PackageUtils.getClasses("com.quark.app.controller");
		for (Class class1 : app_clazz) {
			Class<Controller> controller = class1;
			if (!"".equals(class1.getSimpleName()))
				me.add("/app/" + class1.getSimpleName(), controller);
		}
		/**
		 * end
		 */
		/**
		 * web admin功能
		 */
		List<Class> admin_clazz = PackageUtils
				.getClasses("com.quark.admin.controller");
		for (Class class2 : admin_clazz) {
			Class<Controller> controller = class2;
			me.add("/admin/" + class2.getSimpleName(), controller);
		}
	}

	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
		// 配置BoneCp数据库连接池插件
		db_username = getProperty("user").trim();
		db_password = getProperty("password").trim();
		boneCpPlugin = new C3p0Plugin(getProperty("jdbcUrl"), db_username, db_password);
		me.add(boneCpPlugin);
		// 配置ActiveRecord插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin(boneCpPlugin);
		arp.setShowSql(true);
		me.add(arp);
		//配置Cache缓存
		me.add(new EhCachePlugin());
		me.add(new EhCachePlugin(save_path_root  + "ehcache.xml")); 
		/**
		 * tables
		 */
		arp.addMapping("admin_user", AdminUser.class);
		arp.addMapping("applogs","log_id", Applogs.class);
		arp.addMapping("android_auto_update","android_auto_update_id", AndroidAutoUpdate.class);
		arp.addMapping("category_01","category_01_id", Category01.class);
		arp.addMapping("category_02","category_02_id", Category02.class);
		arp.addMapping("catetory","catetory_id", Catetory.class);
		arp.addMapping("collection","collection_id", Collection.class);
		arp.addMapping("comment","comment_id", Comment.class);
		arp.addMapping("comment_reply","comment_reply_id", CommentReply.class);
		arp.addMapping("coupon","coupon_id", Coupon.class);
		arp.addMapping("course","course_id", Course.class);
		arp.addMapping("course_certification","course_certification_id", CourseCertification.class);
		arp.addMapping("experience","experience_id", Experience.class);
		arp.addMapping("index_banner","index_banner_id", IndexBanner.class);
		arp.addMapping("my_coupon","my_coupon_id", MyCoupon.class);
		arp.addMapping("orders","orders_id", Orders.class);
		arp.addMapping("orders_schedule","orders_schedule_id", OrdersSchedule.class);
		arp.addMapping("rule","rule_id", Rule.class);
		arp.addMapping("schedule","schedule_id", Schedule.class);
		arp.addMapping("tokens","token_id", Tokens.class);
		arp.addMapping("user","user_id", User.class);
		arp.addMapping("city_bean","city_bean_id", CityBean.class);
		arp.addMapping("charge_log","charge_log_id", ChargeLog.class);
		arp.addMapping("constant","constant_id", Constant.class);
		arp.addMapping("feedback","feedback_id", Feedback.class);
		arp.addMapping("zip_code","zip_code_id", ZipCode.class);
		/**
		 * view
		 */
	}

	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
	}

	/**
	 * 配置处理器 处理伪静态请求
	 */
	public void configHandler(Handlers me) {
		me.add(new RpHandler());
		//过滤sevlet不使用jfinal  
	    me.add(new UrlSkipHandler("/PaypalNotify",false));  
	              //下面默认方法需要保留  
	    me.add(new ContextPathHandler("basePath")); 
	    
	}
}
