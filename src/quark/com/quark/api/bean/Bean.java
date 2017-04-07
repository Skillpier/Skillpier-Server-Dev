/**
 * 
 */
package com.quark.api.bean;

import java.util.List;

/**
 * @author kingsley
 *
 * @datetime 2014年12月5日 上午12:33:19
 */
public class Bean {

	private String className;
	private List<Property> properties;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}


	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	@Override
	public String toString() {
		return "Bean [className=" + className + ", properties=" + properties
				+ "]";
	}
	
}
