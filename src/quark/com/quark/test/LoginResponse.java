package com.quark.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.quark.api.auto.bean.*;

/**
 * @author kingsley
 * @copyright quarktimes.com
 * @datetime 2015-03-27 16:24:54
 *
 */
public class LoginResponse {
	// 1-登陆成功:2-手机号码未注册：3-登陆密码错误
	public int status;
	// 1-已签到:2-未签到
	public int signup;
	// token
	public String token;
	//
	public List<Banners> list;

	public LoginResponse() {
	}

	public LoginResponse(String json) {
		Map<String, LoginResponse> map = JSON.parseObject(json,new TypeReference<Map<String, LoginResponse>>() {});
		this.status = map.get("LoginResponse").getStatus();
		this.signup = map.get("LoginResponse").getSignup();
		this.token = map.get("LoginResponse").getToken();
		this.list = map.get("LoginResponse").getList();
	}

	@Override
	public String toString() {
		return "LoginResponse [status=" + status + ", signup=" + signup
				+ ", token=" + token + ", list=" + list + "]";
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return this.status;
	}

	public void setSignup(int signup) {
		this.signup = signup;
	}

	public int getSignup() {
		return this.signup;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getToken() {
		return this.token;
	}

	public void setList(List<Banners> list) {
		this.list = list;
	}

	public List<Banners> getList() {
		return this.list;
	}
	public static void main(String[] args) {
		String json= "{'LoginResponse':{'list':[{'cover':'11111.png','banner_id':1},{'cover':'2.jpng','banner_id':2},{'cover':'3.jpng','banner_id':3}],'signup':0,'status':1,'token':'94c604a081a36ce0703c93a455bad6e0'}}"; 
        LoginResponse testBean = new LoginResponse(json);
        System.out.println(testBean);
	}
}