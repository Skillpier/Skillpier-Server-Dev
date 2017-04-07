package com.quark.interceptor;

import java.util.HashMap;

import com.jfinal.aop.Interceptor;
import com.jfinal.config.Interceptors;
import com.jfinal.core.ActionInvocation;
import com.quark.common.config;
import com.quark.utils.Base64Util;

/**
 * 
 * @author cluo
 * 
 *         2014年6月17日
 */
public class CompanyLogin implements Interceptor {

	public static HashMap<String, String> hash = new HashMap<String, String>();

	@Override
	public void intercept(ActionInvocation ai) {
		if (config.devMode) {
			ai.getController().setAttr("username", "devModel");
			ai.invoke();
			return;
		}
		String u = ai.getController().getCookie("usession");
		if (u == null) {
			ai.getController().setAttr("request", ai.getController().getRequest());
			ai.getController().render("/company/Login.html");
			return;
		}
		String user_id = hash.get(u);
		if (user_id == null) {
			System.out.println("Log:["+"CompanyLogin/intercept/user_id=null"+"]");
			ai.getController().setAttr("request", ai.getController().getRequest());
			ai.getController().render("/company/Login.html");
			return;
		} else {
			System.out.println("Log:["+"CompanyLogin/intercept/user_id!=null"+"]");
			ai.getController().setAttr("username", Base64Util.decode(u));
			System.out.println("set username:" + Base64Util.decode(u));
			ai.getController().setAttr("id", hash.get(u));
			ai.invoke();
		}
		return;

	}
}
