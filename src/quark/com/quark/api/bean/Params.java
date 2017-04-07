/**
 * 
 */
package com.quark.api.bean;

import java.io.Serializable;

/**
 * @author kingsley
 *
 * @datetime 2014年12月3日 下午7:36:50
 */
public class Params implements Serializable {

	//参数名
	private String name;
	//参数解释
	private String explaination;
	//默认测试值
	private String defaultValue;
	//参数类型
	private String type;

	public String getExplaination() {
		return explaination;
	}

	public void setExplaination(String explaination) {
		this.explaination = explaination;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
