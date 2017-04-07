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
public class Returns implements Serializable {

	//返回值名
	private String name;
	//返回值解释
	private String explaination;
	//返回值类型
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Returns [name=" + name + ", explaination=" + explaination
				+ ", type=" + type + "]";
	}

}
