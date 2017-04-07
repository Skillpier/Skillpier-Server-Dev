/**
 * 
 */
package com.quark.api.bean;

import java.io.Serializable;
import java.util.List;

import com.quark.api.annotation.Rp;


/**
 * @author kingsley
 *
 * @datetime 2014年12月3日 下午5:46:30
 */
public class ApiDescription implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Rp rp;//使用顿号隔开表示映射到两个Rp页面上去
	private String url;
	private String author;
	private int sort;
	private String explaination;
	private String returnJson;
	private List<Params> params;
	private List<Returns> returns;
	private List<Log> logs;
	private String javaRequestBean;
	private String iosRequestBean;
	private List<String> javaResponseBeans;
	private List<String> iosResponseBeans;
	private FormBean form;
	
	
	public String getJavaRequestBean() {
		return javaRequestBean;
	}
	public void setJavaRequestBean(String javaRequestBean) {
		this.javaRequestBean = javaRequestBean;
	}
	public String getIosRequestBean() {
		return iosRequestBean;
	}
	public void setIosRequestBean(String iosRequestBean) {
		this.iosRequestBean = iosRequestBean;
	}
	public List<String> getJavaResponseBeans() {
		return javaResponseBeans;
	}
	public void setJavaResponseBeans(List<String> javaResponseBeans) {
		this.javaResponseBeans = javaResponseBeans;
	}
	public List<String> getIosResponseBeans() {
		return iosResponseBeans;
	}
	public void setIosResponseBeans(List<String> iosResponseBeans) {
		this.iosResponseBeans = iosResponseBeans;
	}
	
	
	public Rp getRp() {
		return rp;
	}
	public void setRp(Rp rp) {
		this.rp = rp;
	}
	public FormBean getForm() {
		return form;
	}
	public void setForm(FormBean form) {
		this.form = form;
	}
	public List<String> getIosBean() {
		return iosResponseBeans;
	}
	public void setIosBean(List<String> iosBean) {
		this.iosResponseBeans = iosBean;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public String getReturnJson() {
		return returnJson;
	}
	public void setReturnJson(String returnJson) {
		this.returnJson = returnJson;
	}
	public List<String> getJavaBeans() {
		return javaResponseBeans;
	}
	public void setJavaBeans(List<String> javaBeans) {
		this.javaResponseBeans = javaBeans;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<Log> getLogs() {
		return logs;
	}
	public void setLogs(List<Log> logs) {
		this.logs = logs;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getExplaination() {
		return explaination;
	}
	public void setExplaination(String explaination) {
		this.explaination = explaination;
	}
	public List<Params> getParams() {
		return params;
	}
	public void setParams(List<Params> params) {
		this.params = params;
	}
	public List<Returns> getReturns() {
		return returns;
	}
	public void setReturns(List<Returns> returns) {
		this.returns = returns;
	}
	@Override
	public String toString() {
		return "ApiDescription [rp=" + rp + ", url=" + url + ", author="
				+ author + ", sort=" + sort + ", explaination=" + explaination
				+ ", returnJson=" + returnJson + ", params=" + params
				+ ", returns=" + returns + ", logs=" + logs
				+ ", javaRequestBean=" + javaRequestBean + ", iosRequestBean="
				+ iosRequestBean + ", javaResponseBeans=" + javaResponseBeans
				+ ", iosResponseBeans=" + iosResponseBeans + ", form=" + form
				+ "]";
	}
	
}
