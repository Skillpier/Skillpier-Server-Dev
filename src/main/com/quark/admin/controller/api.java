/**
 * 
 */
package com.quark.admin.controller;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.jfinal.core.Controller;
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
import com.quark.utils.JsonFormater;
import com.quark.utils.PackageUtils;
import com.sun.xml.internal.ws.spi.db.OldBridge;

/**
 * @author kingsley
 *
 * @datetime 2014年12月3日 下午5:08:49
 */
public class api extends Controller {

	
	public void rp(){
		render("/rp/start.html");
	}
	/**
	 * 日志
	 */
	public void logs(){
		setAttr("logs", BuildAPI.getLogs());
		render("/admin/logs.html");
	}
	/**
	 * 接口
	 * 
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 */
	public void index() throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			InstantiationException {
		setAttr("apis", BuildAPI.getApis());
		render("/admin/API.html");
	}
}
