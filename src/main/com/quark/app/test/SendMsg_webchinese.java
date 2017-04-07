package com.quark.app.test;

import java.io.UnsupportedEncodingException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

public class SendMsg_webchinese {
	public static void main(String[] args) throws Exception {
		HttpClient client = new HttpClient();
		PostMethod post = new PostMethod(
				"http://120.76.41.192/app/Home/getMessageParrten"); // 真实ip请参照开发文档
		post.addRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;charset=utf-8");// 在头文件中设置转码
		NameValuePair[] data = {
				new NameValuePair("receiver", "szkksd_locklove"),
				new NameValuePair("pswd", "Locklove2016"),
				new NameValuePair("moTime", "1208212205"),
				new NameValuePair("mobile", "15889610183"),
				new NameValuePair("msg", "你好【系爱APP】"),
				new NameValuePair("extno", "286401"),
				new NameValuePair("destcode", "10657109012345") };
		post.setRequestBody(data);
		client.executeMethod(post);
		Header[] headers = post.getResponseHeaders();
		int statusCode = post.getStatusCode();
		System.out.println("statusCode:" + statusCode);
		for (Header h : headers) {
			System.out.println(h.toString());
		}
		String result = new String(post.getResponseBodyAsString().getBytes());
		System.out.println(result); // 打印返回消息状态
		post.releaseConnection();
	}
}

// 说明：如果返回值是200，应该是整段程序代码没有执行完整，只获取到client.executeMethod(post)HTTP状态码的消息；接口是提交成功，没有执行下半部的返回消息代码。

// client.executeMethod(post);HTTP状态码参考：http://baike.baidu.com/view/1790469.htm