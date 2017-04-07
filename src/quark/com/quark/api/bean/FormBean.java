/**
 * 
 */
package com.quark.api.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * http form wrapp
 * 
 * @author kingsley
 *
 */
public class FormBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Input> inputs;

	@Override
	public String toString() {
		return "FormBean [method=" + method + ", action=" + action
				+ ", multipart_form_data=" + multipart + ", inputs="
				+ inputs + "]";
	}

	private String method = "get";
	
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	private String action;
	
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}



	public List<Input> getInputs() {
		return inputs;
	}

	public void setInputs(List<Input> inputs) {
		this.inputs = inputs;
	}

	private int multipart = 0;//multipart/form-data
	
	public int getMultipart() {
		return multipart;
	}

	public void setMultipart(int multipart) {
		this.multipart = multipart;
	}


}


