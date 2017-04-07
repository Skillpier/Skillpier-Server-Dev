/**
 * 
 */
package com.quark.api.uitls;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

import sun.security.x509.AVA;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.kit.PathKit;
import com.quark.api.annotation.Type;
import com.quark.api.bean.ApiDescription;
import com.quark.api.bean.Bean;
import com.quark.api.bean.Params;
import com.quark.api.bean.Property;
import com.quark.api.bean.Returns;
import com.quark.api.controller.BuildAPI;
import com.quark.test.LoginResponse;
import com.quark.utils.DateUtils;
import com.quark.utils.FileManager;
import com.quark.utils.StringUtils;

/**
 * @author kingsley
 * @copyright quarktimes.com
 * @datetime 2014年12月5日 上午12:27:34
 * 
 */
public class AndroidBeanBuilder {

	private static final String path = PathKit.getWebRootPath();

	public static List<String> buildList(List<Bean> beans) {
		List<String> list = new ArrayList<String>();
		for (Bean bean : beans) {
			String className = bean.getClassName();
			String buildedBean = buildResponse(bean);
			list.add(className);
			File file = new File(path + "/api/AndroidBeans/" + className
					+ ".java");
			if (file.exists()) {
				file.delete();
				file = new File(path + "/api/AndroidBeans/" + className
						+ ".java");
			}
			try {
				FileManager.write(buildedBean, file, "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	public static void buildRequest(ApiDescription description) {
		String url = description.getUrl();
		String[] url_split = url.split("/");
		String request_method = StringUtils
				.UpperCaseFirstLatter(url_split[url_split.length - 1]);
		List<Params> params = description.getParams();
		String method = description.getForm().getMethod();
		StringBuffer sb = new StringBuffer();
		sb.append("package com.quark.api.auto.bean;\n");
		sb.append("import java.util.ArrayList;\n");
		sb.append("import java.util.List;\n");
		sb.append("import com.quark.api.auto.bean.*;\n\n");
		sb.append("/**\n");
		sb.append(" * @author kingsley\n");
		sb.append(" * @copyright quarktimes.com\n");
		sb.append(" * @datetime " + DateUtils.getCurrentDateTime() + "\n");
		sb.append(" *\n");
		sb.append(" */\n");
		sb.append("public class " + request_method + "Request" + "{\n");
		sb.append("   public String url = \"" + description.getUrl() + "\";\n");
		sb.append("   public String method = \"" + method + "\";\n");
		for (Params p : params) {
			sb.append("   private String " + p.getName() + ";//"
					+ p.getExplaination() + "\n");
		}
		sb.append("   public void setUrl(String url){this.url = url;}\n");
		sb.append("   public String getUrl(){return this.url;}\n");
		sb.append("   public void setMethod(String method){this.method = method;}\n");
		sb.append("   public String getMethod(){return this.method;}\n");
		sb.append("\n");
		for (Params p : params) {
			sb.append(MethodUtils.build(p.getName(), p.getType()) + "\n");
		}
		sb.append("}\n");
		File file = new File(path + "/api/AndroidBeans/" + request_method
				+ "Request.java");
		if (file.exists()) {
			file.delete();
		}
		try {
			FileManager.write(sb.toString(), file, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		description.setJavaRequestBean(request_method + "Request");
	}

	public static String buildResponse(Bean bean) {
		List<Property> property = bean.getProperties();
		String java_name = bean.getClassName();
		String java_source = "package com.quark.api.auto.bean;\n";
		java_source = java_source + "import java.util.ArrayList;\n";
		java_source = java_source + "import java.util.List;\n";
		java_source = java_source + "import com.quark.api.auto.bean.*;\n\n";
		java_source = java_source + "/**\n";
		java_source = java_source + " * @author kingsley\n";
		java_source = java_source + " * @copyright quarktimes.com\n";
		java_source = java_source + " * @datetime "
				+ DateUtils.getCurrentDateTime() + "\n";
		java_source = java_source + " *\n";
		java_source = java_source + " */\n";

		java_source = java_source + "public class " + java_name + "{\n";
		for (Property p : property) {
			java_source = java_source + "   //" + p.getComment() + "\n";
			String type = p.getType();
			if (type.equalsIgnoreCase(Type.Int)
					|| type.equalsIgnoreCase(Type.Int_NotRequired)) {
				type = "int";
				p.setType(type);
			}
			if (type.equalsIgnoreCase(Type.String)
					|| type.equalsIgnoreCase(Type.String_NotRequired)) {
				type = "String";
				p.setType(type);
			}
			java_source = java_source + "   public " + p.getType() + " "
					+ p.getName() + ";\n";
		}
		String method = "\n";

		if (java_name.endsWith("Response")) {
			// 序列化工作
			java_source = java_source + "    public " + java_name + "() {\n";
			java_source = java_source + "    }\n";
			
			java_source = java_source + "    public " + java_name + "(String json) {\n";
			java_source = java_source
					+ "      Map<String, "
					+ java_name
					+ "> map = JSON.parseObject(json,new TypeReference<Map<String, "
					+ java_name + ">>() {});\n";
			for (Property p : property) {
				String type = p.getType();
				if (type.equalsIgnoreCase(Type.Int)
						|| type.equalsIgnoreCase(Type.Int_NotRequired)) {
					type = "int";
					p.setType(type);
				}
				if (type.equalsIgnoreCase(Type.String)
						|| type.equalsIgnoreCase(Type.String_NotRequired)) {
					type = "String";
					p.setType(type);
				}
				java_source = java_source + "      this." + p.getName()
						+ " = map.get(\"" + java_name + "\").get"
						+ StringUtils.UpperCaseFirstLatter(p.getName()) + "();\n";
			}			
			java_source = java_source + "    }\n";
		}
		for (Property p : property) {
			method = method + MethodUtils.build(p.getName(), p.getType());
		}
		java_source = java_source + method + "}";
		// return StringEscapeUtils.escapeHtml4(java_source);
		return java_source;
	}

	public static void main(String[] args) {
		List<ApiDescription> list = BuildAPI.getApis();
		for (ApiDescription apiBean : list) {
			List<Returns> returns_list = apiBean.getReturns();
			Parser parser = new Parser();
			AndroidBeanBuilder.buildList(parser.token(returns_list));
		}

	}
}
