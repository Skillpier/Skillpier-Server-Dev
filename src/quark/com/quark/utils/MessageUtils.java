package com.quark.utils;

import java.io.UnsupportedEncodingException;

import com.bcloud.msg.http.HttpSender;

/**
 *	http://120.24.167.205/msg/index.jsp
 *	巨辰科技
 *	http://www.juchn.com/a/sdk.html
 * @author Administrator
 */
public class MessageUtils {

	/**
	 * account config
	 */
	private static final String uri = "http://120.24.167.205/msg/HttpSendSM";//应用地址
	private static final String account = "szkksd_locklove";//账号
	private static final String pswd = "Locklove2016";//密码
	private static final boolean needstatus = true;//是否需要状态报告，需要true，不需要false
	private static final String product = "";//产品ID
	private static final String extno = "64";//扩展码
	
	private static String code = "【系爱APP】您的手机短信验证码为：${code}。如果不是您本人操作，可以忽略此次操作！";
	
 
    /**
	 * 发送验证码
	 */
	public static String sendCode(String telephone) {
		String ram = "";
		for (int i = 0; i < 6; i++) {
			int x = (int) (Math.random() * 10);
			ram = ram + x;
		}
		String tmp_code = code;
		tmp_code = tmp_code.replace("${code}", ram);
		send(telephone, tmp_code);
		return ram;
	}

	public static String send(final String telephone, final String content) {
		try {
			String returnString = HttpSender.send(uri, account, pswd, telephone, content, needstatus, product, extno);
			//TODO 处理返回值,参见HTTP协议文档
			System.out.println(returnString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		sendCode("15710764537");
	}
	public static void sendCode2(String telephone,String message) {
		send(telephone, message);
	}
}
