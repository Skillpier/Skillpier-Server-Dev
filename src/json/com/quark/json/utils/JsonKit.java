/**
 * 
 */
package com.quark.json.utils;

import com.alibaba.fastjson.JSON;
import com.quark.test.bean.Activity;
import com.quark.test.bean.ActivityResponse;

/**
 * @author kingsley
 *
 */
public class JsonKit {

	public static void main(String[] args) {
		String json = "{\"totalRow\":13,\"pageNumber\":1,\"totalPage\":2,\"pageSize\":10,\"list\":[{\"type\":\"服务员;\"},{\"type\":\"其它\"},{\"type\":\"安保人员\"}]}";
		ActivityResponse response = JSON.parseObject(json,ActivityResponse.class);
        for (Activity activity : response.getList()) {
			System.out.println(activity);
		}
        //app中使用以下方式
        String json2 = "{\"ActivityResponse\":{\"totalRow\":13,\"list\":[{\"start_time\":\"2015-01-11\",\"publish_time\":\"2015-01-09\",\"activity_id\":2,\"county\":\"罗湖\",\"confirmed_count\":2,\"pay\":120,\"days\":5,\"title\":\"宝安招车迷\",\"type\":\"服务员;\",\"left_count\":31},{\"start_time\":\"2015-01-11\",\"publish_time\":\"2015-01-06\",\"activity_id\":13,\"county\":\"宝安\",\"confirmed_count\":0,\"pay\":120,\"days\":5,\"title\":\"华清宝马拉松比赛\",\"type\":\"其它\",\"left_count\":22},{\"start_time\":\"2015-01-11\",\"publish_time\":\"2015-01-06\",\"activity_id\":5,\"county\":\"罗湖\",\"confirmed_count\":0,\"pay\":120,\"days\":5,\"title\":\"华清宝马拉松比赛\",\"type\":\"安保人员\",\"left_count\":67},{\"start_time\":\"2015-01-11\",\"publish_time\":\"2015-01-06\",\"activity_id\":6,\"county\":\"宝安\",\"confirmed_count\":0,\"pay\":120,\"days\":5,\"title\":\"华清宝马拉松比赛\",\"type\":\"模特\",\"left_count\":25},{\"start_time\":\"2015-01-11\",\"publish_time\":\"2015-01-06\",\"activity_id\":7,\"county\":\"罗湖\",\"confirmed_count\":0,\"pay\":120,\"days\":5,\"title\":\"华清宝马拉松比赛\",\"type\":\"派发\",\"left_count\":23},{\"start_time\":\"2015-01-11\",\"publish_time\":\"2015-01-06\",\"activity_id\":8,\"county\":\"罗湖\",\"confirmed_count\":0,\"pay\":120,\"days\":5,\"title\":\"华清宝马拉松比赛\",\"type\":\"主持\",\"left_count\":10},{\"start_time\":\"2015-01-13\",\"publish_time\":\"2015-01-06\",\"activity_id\":11,\"county\":\"宝安\",\"confirmed_count\":0,\"pay\":120,\"days\":3,\"title\":\"华清宝马拉松比赛\",\"type\":\"其它\",\"left_count\":10},{\"start_time\":\"2015-01-01\",\"publish_time\":\"2015-01-06\",\"activity_id\":1,\"county\":\"宝安\",\"confirmed_count\":2,\"pay\":120,\"days\":1,\"title\":\"京东网购用户访谈兼职\",\"type\":\"派发\",\"left_count\":8},{\"start_time\":\"2015-01-11\",\"publish_time\":\"2015-01-06\",\"activity_id\":9,\"county\":\"宝安\",\"confirmed_count\":0,\"pay\":120,\"days\":5,\"title\":\"华清宝马拉松比赛\",\"type\":\"翻译\",\"left_count\":100},{\"start_time\":\"2015-01-11\",\"publish_time\":\"2015-01-06\",\"activity_id\":10,\"county\":\"罗湖\",\"confirmed_count\":0,\"pay\":120,\"days\":5,\"title\":\"华清宝马拉松比赛\",\"type\":\"其它\",\"left_count\":40}]}}";
        ActivityResponse response2 = JSON.parseObjectFromRootKey(json2, ActivityResponse.class);
        for (Activity activity : response2.getList()) {
			System.out.println(activity);
		}
	}
}
