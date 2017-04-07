/**
 * 
 */
package com.quark.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.jfinal.core.Controller;
import com.quark.interceptor.AppToken;
import com.quark.model.extend.User;
import com.quark.utils.DateUtils;

/**
 * @author kingsley
 *
 */
public class AppData {

	private static String accessToken = "d0942118ea1a946f1113a6f10eabf95f";

	public static void analyze(final String url,final String event,Controller controller) {
		/*try {
			String user_token = controller.getPara("token","");
			final String user_id = AppToken.getUserId(user_token, controller);
			final String phone = controller.getPara("phone","-");//手机品牌名称
			final String operation = controller.getPara("operation","-");//操作系统：Android、IOS
			final String version = controller.getPara("version","-");//系统版本号:V1.2。。。。
			final String resolution = controller.getPara("resolution","-");//分辨率
			final String request_latitude = controller.getPara("request_latitude","-");//经纬度
			final String request_longitude = controller.getPara("request_longitude","-");//经纬度
			final String language = controller.getPara("language","-");//系统语言
			Jsoup.connect("http://yx.kksdapp.com:8999/company/distributed/analyze").data(new HashMap<String, String>() {
				{
					put("token", accessToken);
					put("uid", user_id);
					put("phone", phone);
					put("operation", operation);
					put("version", version);
					put("resolution", resolution);
					put("request_url", url);
					put("request_event", event);
					put("request_latitude", request_latitude);
					put("request_longitude", request_longitude);
					put("language", language);
				}
			}).ignoreContentType(true).get();
		} catch (Exception e2) {
			e2.printStackTrace();
		}*/
	}
	public static void main(String[] args) throws Exception {
	
		Document doc = Jsoup.connect("http://yx.kksdapp.com:8999/company/distributed/analyze").data(new HashMap<String, String>() {
			{
				put("token", accessToken);
				put("uid", "");
				put("phone", "");
				put("operation", "");
				put("version", "");
				put("resolution", "");
				put("request_url", "");
				put("request_event", "");
				put("request_latitude", "");
				put("request_longitude", "");
				put("language", "");
			}
		}).ignoreContentType(true).get();	
		System.out.println(doc);
	}
	 /**
	  * 验证邮箱
	  * @param email
	  * @return
	  */
	 public static boolean checkEmail(String email){
	  boolean flag = false;
	  try{
	    String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
	    Pattern regex = Pattern.compile(check);
	    Matcher matcher = regex.matcher(email);
	    flag = matcher.matches();
	   }catch(Exception e){
	    flag = false;
	   }
	  return flag;
	 }
}
