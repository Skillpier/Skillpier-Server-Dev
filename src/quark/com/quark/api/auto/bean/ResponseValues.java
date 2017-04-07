/**
 * 
 */
package com.quark.api.auto.bean;

import java.io.Serializable;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.core.Controller;

/**
 * @author kingsley
 *
 * @datetime 2014年12月20日 上午11:56:27
 */
public class ResponseValues extends HashMap<String, Object> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String type = "";
	private String root = "";

	public ResponseValues() {
		
	}
	public ResponseValues(Controller request, String root) {
		this.type = request.getPara("invoke", "app");
		this.root = root;
	}

	@Override
	public Object put(String key, Object value) {
		if (key.equalsIgnoreCase("Result")) {
			if (type.equals("app")) {
				return super.put(root + "Result", value);
			} else {
				return super.put(key, value);
			}
		} else {
			return super.put(key, value);
		}
	}
}
