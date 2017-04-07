/**
 * 
 */
package com.quark.api.bean;

/**
 * @author kingsley
 *
 * @datetime 2014年12月5日 上午12:34:07
 */
public class Property {

	private String name;
	private String type;
	private String comment;
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
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Property(String name, String type, String comment) {
		super();
		this.name = name;
		this.type = type;
		this.comment = comment;
	}
}
