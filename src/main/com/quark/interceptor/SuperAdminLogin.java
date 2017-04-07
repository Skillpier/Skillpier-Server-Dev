package com.quark.interceptor;

import java.util.HashMap;

import com.jfinal.aop.Interceptor;
import com.jfinal.config.Interceptors;
import com.jfinal.core.ActionInvocation;
import com.quark.utils.Base64Util;


public class SuperAdminLogin implements Interceptor {

	public static HashMap<String, String> hash = new HashMap<String, String>();

	@Override
	public void intercept(ActionInvocation ai) {
		String u = ai.getController().getCookie("usession");
		if (u == null) {
			ai.getController().setAttr("request",
					ai.getController().getRequest());
			ai.getController().render("/common/Login.html");
			return;
		}
		if (u != null && !hash.containsKey(u)) {
			ai.getController().setAttr("request",
					ai.getController().getRequest());
			ai.getController().render("/common/Login.html");
			return;
		}
		
		if (hash.get(u) != null) {
			ai.getController().setAttr("role", "admin");
			ai.getController().setAttr("username", Base64Util.decode(u));
			ai.getController().setAttr("id", hash.get(u));
			ai.invoke();
		}
		return;
	}
}
