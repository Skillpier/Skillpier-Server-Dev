/**
 * 
 */
package com.quark.common;

import java.io.Serializable;

/**
 * @author kingsley
 *
 * @datetime 2014年12月4日 下午3:24:17
 */
public class Token implements Serializable{

	/**
	 * token值
	 */
	private String access_token;
	/**
	 * 有效时间,秒为单位, 默认是七天,在有效期内是不需要重复获取的
	 */
	private int expires_in;
	/**
	 * 有效时间,秒为单位, 默认是七天,在有效期内是不需要重复获取的
	 */
	private String application;
	
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public int getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(int expires_in) {
		this.expires_in = expires_in;
	}
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	@Override
	public String toString() {
		return "Token [access_token=" + access_token + ", expires_in="
				+ expires_in + ", application=" + application + "]";
	}
}
