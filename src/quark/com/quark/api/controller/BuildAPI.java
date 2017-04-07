package com.quark.api.controller;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.quark.api.annotation.Author;
import com.quark.api.annotation.Explaination;
import com.quark.api.annotation.ReturnDBParam;
import com.quark.api.annotation.ReturnDBParams;
import com.quark.api.annotation.ReturnJson;
import com.quark.api.annotation.ReturnOutlet;
import com.quark.api.annotation.ReturnOutlets;
import com.quark.api.annotation.Rp;
import com.quark.api.annotation.Sort;
import com.quark.api.annotation.URLParam;
import com.quark.api.annotation.URLParams;
import com.quark.api.annotation.UpdateLog;
import com.quark.api.annotation.UpdateLogs;
import com.quark.api.annotation.Value;
import com.quark.api.bean.ApiDescription;
import com.quark.api.bean.Bean;
import com.quark.api.bean.FormBean;
import com.quark.api.bean.Log;
import com.quark.api.bean.Params;
import com.quark.api.bean.Returns;
import com.quark.api.uitls.AndroidBeanBuilder;
import com.quark.api.uitls.IOSBeanBuilder;
import com.quark.api.uitls.Parser;
import com.quark.api.uitls.RequestFormBuilder;
import com.quark.common.config;
import com.quark.utils.JsonFormater;
import com.quark.utils.PackageUtils;

public class BuildAPI {

	public static void main(String[] args) {
		getApis();
	}

	private static HashMap<String, List<ApiDescription>> apis_rp = null;
	private static List<ApiDescription> apis = null;
	private static List<Log> logs = null;

	public static List<ApiDescription> getApis() {
		if (apis == null) {
			buildBeans();
		}
		return apis;
	}

	public static HashMap<String, List<ApiDescription>> getApisOfRp() {
		if (apis_rp == null) {
			apis_rp = new HashMap<String, List<ApiDescription>>();
			buildBeans();
			for (final ApiDescription apiBean : apis) {
				Rp rp = apiBean.getRp();
				if (rp!=null) {
					String[] rpValue = rp.value().split("、");
					for (String values : rpValue) {
						if (apis_rp.containsKey(values)) {
							List<ApiDescription> list = apis_rp.get(values);
							list.add(apiBean);
							apis_rp.put(values, list);
						} else {
							apis_rp.put(values, new ArrayList<ApiDescription>() {
								{
									add(apiBean);
								}
							});
						}
					}
				}
			}
		}
		return apis_rp;
	}

	public static List<Log> getLogs() {
		if (logs == null) {
			buildBeans();
		}
		return logs;
	}

	private static void buildBeans() {
		/**
		 * app功能
		 */
		apis = new ArrayList<ApiDescription>();
		logs = new ArrayList<Log>();
		@SuppressWarnings("rawtypes")
		List<Class> app_controller = PackageUtils
				.getClasses(config.controllers);
		for (@SuppressWarnings("rawtypes")
		Class class1 : app_controller) {
			Method[] methods = class1.getDeclaredMethods();

			for (Method method : methods) {
				String url = "/app/" + class1.getSimpleName() + "/"
						+ method.getName();
				System.out.println("annotation:" + url);
				Annotation[] annotations = method.getDeclaredAnnotations();
				ApiDescription description = new ApiDescription();
				description.setUrl(url);
				for (Annotation annotation : annotations) {
					if (annotation instanceof Rp) {
						// 映射接口到Rp
						System.out.println("    |----" + url + ":Rp");
						Rp rp = (Rp) annotation;
						description.setRp(rp);
						System.out.println("rp:" + rp.value());
					} else if (annotation instanceof Sort) {
						System.out.println("    |----" + url + ":Sort");
						Sort sort = (Sort) annotation;
						description.setSort(sort.value());
					} else if (annotation instanceof Author) {
						System.out.println("    |----" + url + ":Author");
						Author author = (Author) annotation;
						description.setAuthor(author.value());
					} else if (annotation instanceof Explaination) {
						System.out.println("    |----" + url + ":Exlaination");
						Explaination explaination = (Explaination) annotation;
						description.setExplaination(explaination.info());
					} else if (annotation instanceof UpdateLog) {
						System.out.println("    |----" + url + ":UpdateLog");
						UpdateLog updateLog = (UpdateLog) annotation;
						try {
							final DateFormat df = new SimpleDateFormat(
									"yyyy-MM-dd hh:mm");
							Date date = df.parse(updateLog.date());
						} catch (ParseException e) {
							e.printStackTrace();
						}
						List<Log> logs_list = new ArrayList<Log>();
						Log log = new Log();
						log.setDate(updateLog.date());
						log.setInfo(updateLog.log());
						logs_list.add(log);
						description.setLogs(logs_list);
					} else if (annotation instanceof UpdateLogs) {
						System.out.println("    |----" + url + ":UpdateLog");
						UpdateLogs logs_annotaioni = (UpdateLogs) annotation;
						UpdateLog[] logs = logs_annotaioni.value();
						List<Log> logs_list = new ArrayList<Log>();
						for (UpdateLog updateLog : logs) {
							Log log = new Log();
							log.setDate(updateLog.date());
							try {
								final DateFormat df = new SimpleDateFormat(
										"yyyy-MM-dd hh:mm");
								Date date = df.parse(updateLog.date());
							} catch (ParseException e) {
								e.printStackTrace();
							}
							log.setInfo(updateLog.log());
							logs_list.add(log);
						}
						description.setLogs(logs_list);
					} else if (annotation instanceof URLParam) {
						System.out.println("    |----" + url + ":URLParam");
						URLParam urlParam = (URLParam) annotation;
						List<Params> params_list = new ArrayList<Params>();
						Params param = new Params();
						String name = urlParam.name();
						String defaultValue = urlParam.defaultValue();
						String explaination = urlParam.explain();
						if (urlParam.name().startsWith("columnName=")) {
							String[] string = urlParam.name().split(",");
							name = string[0].replace("columnName=", "");
							if (urlParam.defaultValue().equalsIgnoreCase(
									Value.Infer)) {
								defaultValue = string[3].replace(
										"defaultValue=", "");
							}
							if (urlParam.explain()
									.equalsIgnoreCase(Value.Infer)) {
								explaination = string[1]
										.replace("remarks=", "");
							}
						}
						param.setDefaultValue(defaultValue);
						param.setExplaination(explaination);
						param.setName(name);
						param.setType(urlParam.type());
						params_list.add(param);
						description.setParams(params_list);
						FormBean form = RequestFormBuilder
								.buildForm(description);
						description.setForm(form);
						// build android request
						// build ios request
						IOSBeanBuilder.buildRequestBean(description);
						AndroidBeanBuilder.buildRequest(description);
					} else if (annotation instanceof URLParams) {
						System.out.println("    |----" + url + ":URLParams");
						URLParams urlParams = (URLParams) annotation;
						URLParam[] params = urlParams.value();
						List<Params> params_list = new ArrayList<Params>();
						for (URLParam p : params) {
							Params param = new Params();
							String name = p.name();
							String defaultValue = p.defaultValue();
							String explaination = p.explain();
							if (p.name().startsWith("columnName=")) {
								String[] string = p.name().split(",");
								name = string[0].replace("columnName=", "");
								if (p.defaultValue().equalsIgnoreCase(
										Value.Infer)) {
									defaultValue = string[3].replace(
											"defaultValue=", "");
								}
								if (p.explain().equalsIgnoreCase(Value.Infer)) {
									explaination = string[1].replace(
											"remarks=", "");
								}
							}
							param.setDefaultValue(defaultValue);
							param.setExplaination(explaination);
							param.setName(name);
							param.setType(p.type());
							params_list.add(param);
						}
						description.setParams(params_list);
						FormBean form = RequestFormBuilder
								.buildForm(description);
						description.setForm(form);
						// build android request
						// build ios request
						IOSBeanBuilder.buildRequestBean(description);
						AndroidBeanBuilder.buildRequest(description);
					} else if (annotation instanceof ReturnDBParam) {
						System.out.println("    |----" + url
								+ ":ReturnDBColumnParam");
						ReturnDBParam returnParam = (ReturnDBParam) annotation;
						List<Returns> returns_list = description.getReturns();
						if (returns_list == null) {
							returns_list = new ArrayList<Returns>();
							description.setReturns(returns_list);
						}
						// columnName=note,remarks=,dataType=String,defaultValue=String
						String[] string = returnParam.column().split(",");
						String columnName = string[0]
								.replace("columnName=", "");
						String remarks = string[1].replace("remarks=", "");
						String dataType = string[2].replace("dataType=", "");
						String defaultValue = string[3].replace(
								"defaultValue=", "");
						Returns returns = new Returns();
						returns.setName(returnParam.name().replace(
								"${columnName}", columnName).replace("$", columnName));
						returns.setExplaination(remarks);
						returns.setType(dataType);
						returns_list.add(returns);
					} else if (annotation instanceof ReturnDBParams) {
						System.out.println("    |----" + url
								+ ":ReturnDBColumnParams");
						ReturnDBParams returnParams = (ReturnDBParams) annotation;
						ReturnDBParam[] params = returnParams.value();
						List<Returns> returns_list = description.getReturns();
						if (returns_list == null) {
							returns_list = new ArrayList<Returns>();
							description.setReturns(returns_list);
						}
						for (ReturnDBParam returnParam : params) {
							String[] string = returnParam.column().split(",");
							String columnName = string[0].replace(
									"columnName=", "");
							String remarks = string[1].replace("remarks=", "");
							String dataType = string[2]
									.replace("dataType=", "");
							String defaultValue = string[3].replace(
									"defaultValue=", "");
							Returns returns = new Returns();
							returns.setName(returnParam.name().replace(
									"${columnName}", columnName).replace("$", columnName));
							returns.setExplaination(remarks);
							returns.setType(dataType);
							returns_list.add(returns);
						}
					} else if (annotation instanceof ReturnOutlet) {
						System.out.println("    |----" + url + ":ReturnOutlet");
						ReturnOutlet returnOutlet = (ReturnOutlet) annotation;
						List<Returns> returns_list = description.getReturns();
						if (returns_list == null) {
							returns_list = new ArrayList<Returns>();
							description.setReturns(returns_list);
						}
						// columnName=note,remarks=,dataType=String,defaultValue=String
						Returns returns = new Returns();
						returns.setName(returnOutlet.name());
						returns.setExplaination(returnOutlet.remarks());
						returns.setType(returnOutlet.dataType());
						returns_list.add(returns);
					} else if (annotation instanceof ReturnOutlets) {
						System.out
								.println("    |----" + url + ":ReturnOutlets");
						ReturnOutlets returnOutlets = (ReturnOutlets) annotation;
						ReturnOutlet[] outlets = returnOutlets.value();
						List<Returns> returns_list = description.getReturns();
						if (returns_list == null) {
							returns_list = new ArrayList<Returns>();
							description.setReturns(returns_list);
						}
						for (ReturnOutlet outlet : outlets) {
							Returns returns = new Returns();
							returns.setName(outlet.name());
							returns.setExplaination(outlet.remarks());
							returns.setType(outlet.dataType());
							returns_list.add(returns);
						}
					} else if (annotation instanceof ReturnJson) {
						System.out.println("    |----" + url + ":ReturnJson");
						ReturnJson json = (ReturnJson) annotation;
						if (!"".equals(description)) {
							description.setReturnJson(JsonFormater.format(
									json.value()).trim());

						} else {
							description.setReturnJson(json.value());
						}
					}
				}
				Parser parser = new Parser();
				if (description.getReturns() != null) {
					List<Bean> bean = parser.token(description.getReturns());
					// ios bean
					description.setIosBean(IOSBeanBuilder.buildList(bean));
					description
							.setJavaBeans(AndroidBeanBuilder.buildList(bean));
				}
				apis.add(description);
			}
		}
		/**
		 * end
		 */
		/**
		 * logs 排序
		 */
		Collections.sort(apis, new Comparator<ApiDescription>() {
			@Override
			public int compare(ApiDescription o1, ApiDescription o2) {
				return o1.getSort() - o2.getSort();
			}

		});
		for (ApiDescription api : apis) {
			List<Log> logs_list = api.getLogs();
			if (logs_list != null)
				for (Log log : logs_list) {
					logs.add(log);
				}
		}
		Collections.sort(logs, new Comparator<Log>() {
			@Override
			public int compare(Log o1, Log o2) {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
				Date date1 = null;
				try {
					date1 = df.parse(o1.getDate());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				Date date2 = null;
				try {
					date2 = df.parse(o2.getDate());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				java.util.Calendar c1 = java.util.Calendar.getInstance();
				java.util.Calendar c2 = java.util.Calendar.getInstance();
				try {
					c1.setTime(df.parse(o1.getDate()));
					c2.setTime(df.parse(o2.getDate()));
					int result = c1.compareTo(c2);
					return -result;
				} catch (java.text.ParseException e) {
					System.err.println("格式不正确");
				}
				return 0;
			}
		});
		System.out.println("end annotation...");
	}
}
