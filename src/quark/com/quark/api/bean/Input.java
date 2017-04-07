/**
 * 
 */
package com.quark.api.bean;

import java.io.Serializable;
import java.util.List;

public class Input implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//值原型，对应语言的Int,String,File等
	private String prototype = "";
	private boolean require = true;;
	
	
	
	public boolean isRequire() {
		return require;
	}
	public void setRequire(boolean require) {
		this.require = require;
	}
	public String getPrototype() {
		return prototype;
	}
	public void setPrototype(String prototype) {
		this.prototype = prototype;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	private String explain = "";
	
	public String getExplain() {
		return explain;
	}
	public void setExplain(String explain) {
		this.explain = explain;
	}
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	private List<InputValue> values;
	private String type;

	public List<InputValue> getValues() {
		return values;
	}
	public void setValues(List<InputValue> values) {
		this.values = values;
	}
	@Override
	public String toString() {
		return "Input [prototype=" + prototype + ", explain=" + explain
				+ ", name=" + name + ", values=" + values + ", type=" + type
				+ "]";
	}
}