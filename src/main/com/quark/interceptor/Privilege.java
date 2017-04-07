package com.quark.interceptor;

import java.util.HashMap;
import com.jfinal.aop.Interceptor;
import com.jfinal.config.Interceptors;
import com.jfinal.core.ActionInvocation;
import com.quarkso.utils.Base64Util;

/**
 * 
 * @author kingsley
 * 
 *         2014年6月17日
 */
public class Privilege implements Interceptor {

	@Override
	public void intercept(ActionInvocation ai) {
		String u = ai.getController().getCookie("usession");
		if (SuperAdminLogin.hash.get(u) != null) {
			ai.getController().setAttr("id", SuperAdminLogin.hash.get(u));
			ai.getController().setAttr("role", "admin");
			ai.invoke();
		} else {
			ai.getController().render("/common/privilege.html");
		}
	}
}
