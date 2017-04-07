package com.quark.admin.controller;


import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.eclipse.jetty.util.UrlEncoded;

import cn.jpush.api.examples.PushExample;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheInterceptor;
import com.jfinal.upload.UploadFile;
import com.mysql.jdbc.Buffer;
import com.quark.common.config;
import com.quark.interceptor.Login;
import com.quark.interceptor.Privilege;
import com.quark.model.extend.Course;
import com.quark.model.extend.Orders;
import com.quark.model.extend.OrdersSchedule;
import com.quark.model.extend.Schedule;
import com.quark.model.extend.User;
import com.quark.utils.DateUtils;
import com.quark.utils.FileUtils;
import com.quark.utils.MD5Util;
import com.quarkso.utils.DateUitls;


@Before(Login.class)
public class RefundManage extends Controller {
	
	public void list() throws ParseException{
		int refund_status = getParaToInt("refund_status", 2);
		int currentPage = getParaToInt("pn", 1);
		String start_time=getPara("start_time","");
		String end_time=getPara("end_time","");
		String kw = getPara("kw","");
		String message="list";
		Page<Orders> orders = null;
		String filter_sql="";
		if (refund_status==2) {
			filter_sql=" (os.refund_status=2 or os.refund_status=30)";
		}
		if(refund_status==3){
			filter_sql=" os.refund_status=31 ";
		}
		String exper_sql=" and 1=1 ";
		if (!kw.equals("")) {
			kw = kw.trim();
			exper_sql = exper_sql +"  and (o.order_number like '%" + kw + "%') ";
			message="search";
		}
		if(start_time!="" && end_time!=""){
			exper_sql=exper_sql+"and o.post_time between '"+start_time+"'and '"+end_time+"'";
			message="search";
		}
		setAttr("start_time",start_time);
		setAttr("end_time",end_time);
		setAttr("refund_status", refund_status);
		setAttr("kw", kw);
		setAttr("action", message);
		orders = Orders.dao.paginate(currentPage, PAGE_SIZE,
				"select * ",
				"from orders_schedule os,orders o where "+filter_sql+exper_sql+" AND os.`orders_id`=o.`orders_id`");
		for(Orders ordersBase:orders.getList()){
			int orders_id = ordersBase.get(ordersBase.orders_id);
			List<OrdersSchedule> ordersSchedules = OrdersSchedule.dao.find("select * from orders_schedule where orders_id=?",orders_id);
			ordersBase.put("oSchedules", ordersSchedules);
		}
		int tuikuanzhong_count=0,tuikuanchenggong_count=0;
		List<OrdersSchedule> orBases = OrdersSchedule.dao.find("select os.orders_schedule_id,os.refund_status from orders_schedule os,orders o where os.refund_status!=1 AND os.`orders_id`=o.`orders_id` "+exper_sql);
		for(OrdersSchedule ordersSchedule : orBases){
			int o_refund_status = ordersSchedule.get(ordersSchedule.refund_status);
			if (o_refund_status==2||o_refund_status==30) {
				tuikuanzhong_count = tuikuanzhong_count+1;	
			}
			if (o_refund_status==31) {
				tuikuanchenggong_count = tuikuanchenggong_count+1;	
			}
		}
		setAttr("tuikuanzhong_count", tuikuanzhong_count);
		setAttr("tuikuanchenggong_count", tuikuanchenggong_count);
		setAttr("list", orders);
		setAttr("pn", currentPage);
		render("/admin/RefundList.html");
	}
	
}
	

