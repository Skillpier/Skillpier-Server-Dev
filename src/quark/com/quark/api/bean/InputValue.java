package com.quark.api.bean;

import java.io.Serializable;

public class InputValue implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public String toString() {
		return "InputValue [value=" + value + ", info=" + info + "]";
	}
	private String value;
	private String info = "";
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
}