package com.quark.interceptor;

import java.util.HashMap;

import com.jfinal.aop.Interceptor;
import com.jfinal.config.Interceptors;
import com.jfinal.core.ActionInvocation;
import com.quark.utils.Base64Util;


public class Login implements Interceptor {

	@Override
	public void intercept(ActionInvocation ai) {
		String u = ai.getController().getCookie("usession");
		System.out.println(u);
		if (u == null) {
			System.out.println("null");
			ai.getController().setAttr("request",
					ai.getController().getRequest());
			ai.getController().render("/common/Login.html");
			return;
		}
		if ((u != null && !SuperAdminLogin.hash.containsKey(u))
				&& (u != null && !AdminLogin.hash.containsKey(u))) {
			ai.getController().setAttr("request",
					ai.getController().getRequest());
			ai.getController().render("/common/Login.html");
			return;
		}
		
		if ((SuperAdminLogin.hash.get(u) != null)
				|| (AdminLogin.hash.get(u) != null)) {
			ai.getController().setAttr("username", Base64Util.decode(u));
			if(SuperAdminLogin.hash.get(u)!=null){
				ai.getController().setAttr("id", SuperAdminLogin.hash.get(u));
				ai.getController().setAttr("role","admin");
			}else{
				ai.getController().setAttr("id", AdminLogin.hash.get(u));
			}
			ai.invoke();
		}
		return;
	}
}
