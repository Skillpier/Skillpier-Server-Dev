package com.quark.app.logs;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.quark.model.extend.Applogs;
import com.quark.model.extend.Tokens;
import com.quarkso.utils.DateUitls;

/**
 * app日志
 * 
 * @author kingsley
 *
 */
public class AppLog {

	public static boolean devModel = true;

	public static void info(String token, String info, String request, String params) {
		saveLog("info", token, info, request, params);
	}

	public static void info(String info, HttpServletRequest params) {
		saveLog("info", info, params);
	}

	public static void warm(String token, String info, String request, String params) {

		saveLog("warm", token, info, request, params);
	}

	public static void warm(String info, HttpServletRequest params) {

		saveLog("warm", info, params);
	}

	public static void debug(String token, String info, String request, String params) {
		saveLog("debug", token, info, request, params);
	}

	public static void debug(String info, HttpServletRequest params) {
		saveLog("debug", info, params);
	}

	public static void error(String token, String info, String request, String params) {
		saveLog("error", token, info, request, params);
	}

	public static void error(String info, HttpServletRequest params) {
		saveLog("error", info, params);
	}

	public static void error(Exception e, HttpServletRequest params) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		//saveLog("error", sw.toString(), params);
	}

	private static void saveLog(String level, String token, String info, String request, String params) {
		Applogs log = new Applogs();
		String post_time = DateUitls.getCurrentDateTime();
		log.set(Applogs.info, info).set(Applogs.level, level).set(Applogs.request, request).set(Applogs.params, params)
				.set(Applogs.post_time, post_time).set(Applogs.user_id, getUserId(token)).save();
		if (info == null)
			info = "";
		if (devModel) {
			// 开发模式下，输出到控制台
			System.out.println("[" + post_time + " - " + level + ":" + info + "," + params + "]");
		}
	}

	private static void saveLog(String level, String info, HttpServletRequest params) {
		try {
			Applogs log = new Applogs();
			String post_time = DateUitls.getCurrentDateTime();
			Map<String, String> res = new HashMap<String, String>();
			Enumeration<?> temp = params.getParameterNames();
			if (null != temp) {
				while (temp.hasMoreElements()) {
					String en = (String) temp.nextElement();
					String value = params.getParameter(en);
					res.put(en, value);
				}
			}
			String token = res.get("token");
			/*log.set(Applogs.info, info).set(Applogs.level, level).set(Applogs.request, params.getRequestURI())
					.set(Applogs.params, res.toString()).set(Applogs.post_time, post_time)
					.set(Applogs.user_id, getUserId(token)).save();*/
			if (info == null)
				info = params.getRequestURI();
			if (devModel) {
				// 开发模式下，输出到控制台
				System.out.println("[" + post_time + " - " + level + ":" + info + "," + res + "]");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getUserId(String token) {
		if (token == null)
			return "-1";
		Tokens tokenModel = Tokens.dao.findFirst("select user_id from tokens where token=?", token);
		if (tokenModel != null) {
			return "" + tokenModel.get("user_id");
		}
		return "-1";
	}
}
