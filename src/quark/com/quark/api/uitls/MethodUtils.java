/**
 * 
 */
package com.quark.api.uitls;

import com.quark.api.annotation.Type;

/**
 * @author kingsley
 *
 * @datetime 2014年12月5日 上午12:07:55
 */
public class MethodUtils {

	public static String build(String method_name, String return_type) {
		return "   "+build_setter_method(method_name, return_type)
				+ "   "+build_getter_method(method_name, return_type);
	}

	/**
	 * 生成 setter方法
	 * 
	 * @param method_name
	 * @return
	 */
	private static String build_setter_method(String method_name,
			String return_type) {
		if(return_type.endsWith("_NotRequired")){
			return_type = return_type.replace("_NotRequired", "");
		}
		String method = null;
		method = "public void set" + method_name.substring(0, 1).toUpperCase()
				+ method_name.substring(1);
		method = method + "(" + return_type + " " + method_name.toLowerCase()
				+ "){" + "\n";
		method = method + "     this." + method_name.toLowerCase() + " = "
				+ method_name.toLowerCase() + ";\n";
		method = method + "   }" + "\n";
		return method;
	}

	/**
	 * 生成 getter方法
	 * 
	 * @param method_name
	 * @return
	 */
	private static String build_getter_method(String method_name,
			String return_type) {
		String method = null;
		method = "public " + return_type + " get"
				+ method_name.substring(0, 1).toUpperCase()
				+ method_name.substring(1);
		method = method + "(){" + "\n";
		method = method + "     return this." + method_name.toLowerCase() + ";\n";
		method = method + "   }" + "\n";
		return method;
	}

	public static void main(String[] args) {
		System.out.println(build("age", "int"));
	}
}
