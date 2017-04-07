/**
 * 
 */
package com.quark.api.uitls;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

import com.jfinal.kit.PathKit;
import com.quark.api.annotation.Type;
import com.quark.api.bean.ApiDescription;
import com.quark.api.bean.Bean;
import com.quark.api.bean.Params;
import com.quark.api.bean.Property;
import com.quark.api.bean.Returns;
import com.quark.api.controller.BuildAPI;
import com.quark.utils.DateUtils;
import com.quark.utils.FileManager;
import com.quark.utils.StringUtils;

/**
 * @author kingsley
 *
 * @datetime 2014年12月5日 上午12:27:34
 */
public class IOSBeanBuilder {

	private static final String path = PathKit.getWebRootPath();
	
	
	public static List<String> buildList(List<Bean> beans) {
		List<String> list = new ArrayList<String>();
		for (Bean bean : beans) {
			String className = bean.getClassName();
			String buildedBean = buildRsponseBean(bean);
			list.add(className);
			File file = new File(path+"/api/IOSBeans/"+className+".swift");
			if(file.exists()){
				file.delete();
			}
			try {
				FileManager.write(buildedBean, file, "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public static void buildRequestBean(ApiDescription description){
		String url = description.getUrl();
		List<Params> params = description.getParams();
		String method = description.getForm().getMethod();
		StringBuffer sb = new StringBuffer();
		sb.append("/**\n");
		sb.append(" * \n");
		sb.append(" * @author kingsley\n");
		sb.append(" * @copyright quarktimes.com\n");
		sb.append(" * @datetime "+DateUtils.getCurrentDateTime()+"\n");
		sb.append(" *\n");
		sb.append(" */\n");
		sb.append("import UIKit\n");
		String []url_split = url.split("/");
		String request_method = StringUtils.UpperCaseFirstLatter(url_split[url_split.length-1]);
		sb.append("class "+request_method+"Request:QuarkBaseRequest{\n");
		for (Params param : params) {
			String name = param.getName();
			if(name.equals("token"))
				continue;
			sb.append("     var "+name+":String! //"+param.getExplaination()+"\n");
		}
		sb.append("\n");
		sb.append("     override func loadRequest(){\n");
		sb.append("       super.loadRequest();\n");
		sb.append("       super.METHOD = \""+method.toUpperCase()+"\";\n");
		sb.append("       super.PATH = \""+url+"\";\n");
		sb.append("}\n");
		sb.append("}\n");
		File file = new File(path+"/api/IOSBeans/"+request_method+"Request.swift");
		if(file.exists()){
			file.delete();
		}
		try {
			FileManager.write(sb.toString(), file, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		description.setIosRequestBean(request_method+"Request");
	}
	public static String buildRsponseBean(Bean bean) {
		List<Property> property = bean.getProperties();
		String java_name = bean.getClassName();
		StringBuffer sb = new StringBuffer();
		// sb.append("import Foundation\n");
		// sb.append("import SwiftyJSON\n");
		sb.append("class " + java_name + ":NSObject{\n");
		// build properies
		for (Property p : property) {
			String type = "";
			if(p.getName().equals("code")){
				continue;
			}
			
			if (p.getType().equalsIgnoreCase(Type.Int) || p.getType().equalsIgnoreCase(Type.Int_NotRequired)) {
				// int
				sb.append("\n");
				sb.append("   var " + p.getName() + ":Int = 0 "
						+ "//" +p.getComment()+ "\n ");
			} else if (p.getType().equalsIgnoreCase(Type.String) || p.getType().equalsIgnoreCase(Type.String_NotRequired)) {
				// string
				sb.append("\n");
				sb.append("   var " + p.getName() + ":String = \"\" "
						+ "//" + p.getComment()+ "\n ");
			} else if (p.getType().startsWith("List<")) {
				// list
				type = p.getType().replace("List<", "").replace(">", "");
				sb.append("\n");
				sb.append("   var list:Array<" + type + "> = [" + type
						+ "]() " + "//" + p.getComment()+ "\n ");
			} else {
				// obj
				sb.append("   //" + "\n");
				sb.append("   var " + p.getName() + " " + ":" + p.getName()
						 + "//" + p.getComment()+ "\n ");
			}

		}
		// build inite method
		sb.append("\n\n");
		//sb.append("   init(json j:JSON){\n"); //for swiftJson+almofire
		sb.append("   init(json j:JSON){\n");//for swiftJson+easyIOS
		for (Property p : property) {
			if (p.getType().equalsIgnoreCase(Type.Int)) {
				// int
				sb.append("     self." + p.getName() + " = " + "j[\""+java_name+"\"][\""
						+ p.getName() + "\"].intValue\n");
			} else if (p.getType().equalsIgnoreCase(Type.String)) {
				// string
				sb.append("     self." + p.getName() + " = " + "j[\""+java_name+"\"][\""
						+ p.getName() + "\"].stringValue\n");
			} else if (p.getType().startsWith("List<")) {
				// list
				String type = p.getType().replace("List<", "").replace(">", "");
				sb.append("     for(index,jsonObj) in j[\""+java_name+"\"][\"list\"]{\n");
				sb.append("         var tmp_" + type + " = " + type
						+ "(json:jsonObj)\n");
				sb.append("         list.append(tmp_" + type + ")\n");
				sb.append("    }\n");
			} else {
				// obj
				sb.append("     self." + p.getName() + " = "
						+ StringUtils.UpperCaseFirstLatter(p.getName())
						+ "(json:j[\"" + p.getName() + "\"]\n");
			}
		}
		sb.append("   }\n");
		sb.append("}");
		//return StringEscapeUtils.escapeHtml4(sb.toString());
		return sb.toString();
	}

	public static void main(String[] args) {
		// JavaBean bean = new JavaBean();
		// bean.setJava_name("test");
		// List<Property> properties = new ArrayList<Property>();
		//
		// properties.add(new Property("status", "int", "返回状态说明"));
		// properties.add(new Property("info", "String", "评论内容"));
		// bean.setProperties(properties);
		//
		// build(bean);;
		BuildAPI api = new BuildAPI();
		List<ApiDescription> list = api.getApis();
		System.out.println("\n\n\n\n======swift bean=======\n");
		for (ApiDescription apiBean : list) {
			List<Returns> returns_list = apiBean.getReturns();
			Parser parser = new Parser();
			IOSBeanBuilder.buildList(parser.token(returns_list));
		}
	}
}
