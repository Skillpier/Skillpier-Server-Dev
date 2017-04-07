/**
 * 
 */
package com.quark.common;

import io.rong.ApiHttpClient;
import io.rong.models.FormatType;
import io.rong.models.Message;
import io.rong.models.SdkHttpResult;
import io.rong.models.TxtMessage;
import io.rong.util.HttpUtil;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.google.gson.JsonObject;
import com.quark.model.extend.Tokens;
import com.quark.utils.DateUtils;
import com.quark.utils.MD5Util;
import com.quarkso.utils.DateUitls;

/**
 * 账号	yuelizhanghu@sina.com 
 * 密码	yueli2016
 * @author cluo
 * 融云
 * @datetime 2014年12月4日 下午3:24:17
 */
public class RongToken {

	private static String key = "25wehl3uwciow";//替换成您的appkey
	private static String secret = "vNbtzErGYSS";//替换成匹配上面key的secret
	private static final String RONGCLOUDURI = "http://api.cn.ronghub.com";
	private static final String UTF8 = "UTF-8";
	
	static SdkHttpResult result = null;
	
	/**
	 * 获取token
	 * @param userId
	 * @param name
	 * @param avater_path:"http://aa.com/a.png"
	 * @throws Exception
	 */
	public static String getToken(String userId,String name,String avater_path){
		String token_str = "";
		try {
			result = ApiHttpClient.getToken(key, secret, userId, name,avater_path, FormatType.json);
			if (result!=null) {
				int code = result.getHttpCode();
				if (code==200) {
					String reString = result.getResult();
					JSONObject newsObject = new JSONObject(reString);
					token_str = newsObject.getString("token");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return token_str;
	}
	/**
	 * 登陆认证
	 */
	public static String sign(String user_id,String name,String avater_path) {
		Tokens tokenModel = Tokens.dao.findFirst("select token_id,token from tokens where user_id=?", user_id);
		if (tokenModel == null) {
			// 没有登陆
			tokenModel = new Tokens();
			String token_str = getToken(user_id,name,avater_path);
			tokenModel.set("user_id", user_id).set("token", token_str)
					.set("post_time", DateUitls.getCurrentDateTime())
					.set("post_date", DateUitls.getCurrentDate())
					.set("post_hour", DateUtils.getCurrentDateHours())
					.save();
			return token_str;
		} else {      
			// 登陆成功记录，则更新记录
			String token_str = getToken(user_id,name,avater_path);
			tokenModel.set("token", token_str)
					.set("post_time", DateUitls.getCurrentDateTime())
					.set("post_date", DateUitls.getCurrentDate())
					.set("post_hour", DateUtils.getCurrentDateHours())
					.update();
			return token_str;
		}
	}

	/**
	 * 刷新用户信息 方法
	 * 当您的用户昵称和头像变更时
	 * @param userId
	 * @param userName
	 * @param portraitUri
	 */
	public static void refreshUser(String userId,String userName,String portraitUri) {
		try {
			result = ApiHttpClient.refreshUser(key, secret, userId, userName,portraitUri, FormatType.json);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	public static void publishMessage(String fromUserId,List<String> toUserIds,String msg){
		try {
			//发消息(push内容为消息内容)
			//List<String> toIds = new ArrayList<String>();
			//toIds.add(toUserId);
			//toIds.add("id2");
			//toIds.add("id3");
			result = ApiHttpClient.publishMessage(key, secret, fromUserId, toUserIds,new TxtMessage(msg), FormatType.json);
			System.out.println("publishMessage=" + result);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	/**
	 * 在线状态，1为在线，0为不在线
	 * @param userId
	 * @return
	 */
	public static String checkOnline(String userId) {
		String online_status = "0";
		try {
			result = ApiHttpClient.checkOnline(key, secret, userId, FormatType.json);
			if (result!=null) {
				int code = result.getHttpCode();
				if (code==200) {
					String reString = result.getResult();
					JSONObject newsObject = new JSONObject(reString);
					online_status = newsObject.getString("status");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return online_status;
	}
	public static String test() {
		String online_status = "0";
		try {
			result = ApiHttpClient.getMessageHistoryUrl(key, secret, "2016030517", FormatType.json);
			System.out.println(result.toString());
			if (result!=null) {
				int code = result.getHttpCode();
				if (code==200) {
					String reString = result.getResult();
					JSONObject newsObject = new JSONObject(reString);
					online_status = newsObject.getString("status");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return online_status;
	}
	public static void main(String[] args) {
		test();
	}
}
