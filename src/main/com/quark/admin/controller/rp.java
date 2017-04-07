/**
 * 
 */
package com.quark.admin.controller;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.quark.api.annotation.Author;
import com.quark.api.annotation.Explaination;
import com.quark.api.annotation.ReturnDBParam;
import com.quark.api.annotation.ReturnDBParams;
import com.quark.api.annotation.ReturnJson;
import com.quark.api.annotation.ReturnDBParam;
import com.quark.api.annotation.ReturnDBParams;
import com.quark.api.annotation.Sort;
import com.quark.api.annotation.URLParam;
import com.quark.api.annotation.URLParams;
import com.quark.api.annotation.UpdateLog;
import com.quark.api.annotation.UpdateLogs;
import com.quark.api.bean.ApiDescription;
import com.quark.api.bean.Input;
import com.quark.api.bean.Log;
import com.quark.api.bean.Params;
import com.quark.api.bean.Returns;
import com.quark.api.controller.BuildAPI;
import com.quark.api.uitls.AndroidBeanBuilder;
import com.quark.api.uitls.Parser;
import com.quark.utils.FileManager;
import com.quark.utils.JsonFormater;
import com.quark.utils.PackageUtils;
import com.sun.xml.internal.ws.spi.db.OldBridge;

/**
 * @author kingsley
 *
 * @datetime 2014年12月3日 下午5:08:49
 */
public class rp extends Controller {

	public void index() throws UnsupportedEncodingException {
		String name = getPara(0);
		if (name != null) {
			name = name.replace("~html", "");
			name = URLDecoder.decode(name, "UTF-8");
			System.out.println(name);
			// List<APIBean> list = new ArrayList<APIBean>();
			// for (APIBean api : BuildAPI.getApis()) {
			// if (api.getRp().value().equals(name)) {
			// File file = new File(PathKit.getWebRootPath() + "/rp/"
			// + name + ".html");
			// try {
			// Document doc = Jsoup.parse(file, "UTF-8");
			// Element ele = doc.select("#api").first();
			// if (ele == null) {
			// String html = FileManager
			// .read(file, "UTF-8")
			// .replace("</body>",
			// "<div id=\"api\"><#include \"../common/api.html\"></div>");
			// file.delete();
			//
			// FileManager.write(html, PathKit.getWebRootPath()
			// + "/rp/" + name + ".html", "UTF-8");
			// }
			// list.add(api);
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// }
			// }
			List<ApiDescription> list = new ArrayList<ApiDescription>();
			System.out.println(name);
			List<ApiDescription> apiList = BuildAPI.getApisOfRp().get(name);
			if (apiList != null) {
				for (ApiDescription api : apiList) {
					File file = new File(PathKit.getWebRootPath() + "/rp/"
							+ name + ".html");
					try {
						Document doc = Jsoup.parse(file, "UTF-8");
						Element ele = doc.select("#api").first();
						if (ele == null) {
							String html = FileManager
									.read(file, "UTF-8")
									.replace("</body>",
											"<div id=\"api\"><#include \"../common/api.html\"></div>");
							file.delete();

							FileManager.write(html, PathKit.getWebRootPath()
									+ "/rp/" + name + ".html", "UTF-8");
						}
						list.add(api);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			//
			setAttr("apis", list);
			render("/rp/" + name + ".html");
		}else {
			
		}
	}
}
