/**
 * 
 */
package com.quark.api.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author kingsley
 *
 */
public class BeanTree implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//根节点，有且只能有一个根节点
	private String root;
	
	private List<Bean> beans;

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public List<Bean> getBeans() {
		return beans;
	}

	public void setBeans(List<Bean> beans) {
		this.beans = beans;
	}

	@Override
	public String toString() {
		return "BeanTree [root=" + root + ", beans=" + beans + "]";
	}
	
	
}
