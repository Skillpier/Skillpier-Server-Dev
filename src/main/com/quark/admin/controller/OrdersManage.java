package com.quark.admin.controller;


import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
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
import com.quark.mail.SendMail;
import com.quark.model.extend.Comment;
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
public class OrdersManage extends Controller {
	
	/**
	 * 订单明细
	 * @throws ParseException
	 */
	public void list() throws ParseException{
		int status = getParaToInt("status", 11);
		int currentPage = getParaToInt("pn", 1);
		String start_time=getPara("start_time","");
		String end_time=getPara("end_time","");
		String kw = getPara("kw","");
		String message="list";
		Page<Orders> orders = null;
		String filter_sql=" status="+status;
		if(status==21){
			filter_sql=" (status=21 or status=22) ";
		}
		if(status==40){
			filter_sql=" (status=21 or status=22) and orders_id in(select orders_id from orders_schedule where (refund_status=1 or refund_status=2 or refund_status=30) and is_pay=1 group by orders_id)";
		}
		if (status==30) {
			filter_sql=" status in(12,20,30,40)";
		}
		String exper_sql=" and 1=1 ";
		if (!kw.equals("")) {
			kw = kw.trim();
			exper_sql = exper_sql + "  and (order_number like '%" + kw + "%' or buy_nickname like '%" + kw + "%' or buy_email like '%" + kw + "%') ";
			message="search";
		}
		if(!start_time.equals("") && !end_time.equals("")){
			exper_sql=exper_sql+" and post_time between '"+start_time+"' and '"+end_time+"' ";
			message="search";
		}
		setAttr("start_time",start_time);
		setAttr("end_time",end_time);
		setAttr("status", status);
		setAttr("kw", kw);
		setAttr("action", message);
		orders = Orders.dao.paginate(currentPage, PAGE_SIZE,
				"select * ",
				"from orders where "+filter_sql+exper_sql+" order by post_time desc");
		for(Orders ordersBase:orders.getList()){
			int orders_id = ordersBase.get(ordersBase.orders_id);
			List<OrdersSchedule> ordersSchedules = OrdersSchedule.dao.find("select * from orders_schedule where orders_id=?",orders_id);
			ordersBase.put("oSchedules", ordersSchedules);
		}
		int daifukuan_count=0,yifukuan_count=0,yiwancheng_count=0;
		List<Orders>orBases=Orders.dao.find("select orders_id,status from orders where status>10 "+exper_sql);
		for(Orders oBase:orBases){
			int oBase_status = oBase.get(oBase.status);
			if (oBase_status==11) {
				daifukuan_count = daifukuan_count+1;
			}
			if (oBase_status==21 || oBase_status==22) {
				yifukuan_count = yifukuan_count+1;	
			}
			if (oBase_status==12 || oBase_status==20 || oBase_status==30 || oBase_status==40) {
				yiwancheng_count = yiwancheng_count+1;	
			}
		}
		Orders orders2 = Orders.dao.findFirst("select count(orders_id) as daicaozuo from orders where (status=21 or status=22) and orders_id in(select orders_id from orders_schedule where (refund_status=1 or refund_status=2 or refund_status=30) and is_pay=1 group by orders_id)");
		setAttr("daifukuan_count", daifukuan_count);
		setAttr("yifukuan_count", yifukuan_count);
		setAttr("kecaozuo_count", orders2.getLong("daicaozuo"));
		setAttr("yiwancheng_count", yiwancheng_count);
		setAttr("list", orders);
		setAttr("pn", currentPage);
		render("/admin/OrdersList.html");
	}
	/**
	 * 完成退款
	 */
	@Before(Privilege.class)
	public void finishUserRefund(){
		int orders_schedule_id = getParaToInt("orders_schedule_id");
		String course_title = getPara("course_title");
		OrdersSchedule ordersSchedule = OrdersSchedule.dao.findById(orders_schedule_id);
		if (ordersSchedule!=null) {
			boolean update = ordersSchedule.set(ordersSchedule.refund_status, 31)
							.set(ordersSchedule.finish_status, 2)
							.update();
			if (update) {
				String buy_user_id = ordersSchedule.getStr(ordersSchedule.user_id);
				//推送
				User user = User.dao.findById(buy_user_id);
				int agent_level = user.get(user.agent_level);
				String email = user.getStr(user.email);
				Comment comments = new Comment();
				if (agent_level==1) {
					//用户
					comments.set(comments.user_id, buy_user_id);
					String nickname = user.getStr(user.nickname);
					int orders_id = ordersSchedule.get(ordersSchedule.orders_id);
					Orders orders = Orders.dao.findById(orders_id);
					String order_number = "";double refund_success_money = 0.00;
					if (orders!=null) {
						order_number = orders.getStr(orders.order_number);
						refund_success_money = orders.getDouble(orders.refund_success_money);
					}
					String note = "Your refund application has been approved. In most situations, your payment will be back to your card in a couple days.  This process is primarily dependent on the bank’s processing speed, however  it is likely you will see a refunded amount posted within 24-48 hours.";
					boolean save = comments.set(comments.note, note)
							.set(comments.user_id, 0)
							.set(comments.post_date, DateUtils.getCurrentDate())
							.set(comments.post_time, DateUtils.getCurrentDateTime())
							.set(comments.type, 2)
							.set(comments.status, 1)
							.set(comments.is_reply, 0)
							.save();
					if (save) {
						PushExample.pushToUser(buy_user_id+"", note);
					}
				}
				/*if (agent_level==2) {
					//教练
					comments.set(comments.public_course_user_id, buy_user_id);
				}*/
			}
		}
		redirect("/admin/OrdersManage/list?status=21");
	}
	/**
	 * 是否同意教练退款
	 */
	@Before(Privilege.class)
	public void agreeCoachRefund(){
		int type = getParaToInt("type",1);
		int orders_schedule_id = getParaToInt("orders_schedule_id");
		OrdersSchedule ordersSchedule = OrdersSchedule.dao.findById(orders_schedule_id);		
		if (ordersSchedule!=null) {
			if (type==1) {
				boolean update = ordersSchedule.set(ordersSchedule.status, 1)
							  	.set(ordersSchedule.refund_status, 31)
							  	.set(ordersSchedule.finish_status, 2)
							  	.update();
				if (update) {
					//时间表管理
					String schedule_ids = ordersSchedule.getStr(ordersSchedule.schedule_ids);
					if (!schedule_ids.equals("0")) {
						String[] schedule_ids_array = schedule_ids.split("#");
						for(int j=0;j<schedule_ids_array.length;j++){
							String schedule_id = schedule_ids_array[j];
							Schedule schedule = Schedule.dao.findById(schedule_id);
							if (schedule!=null) {
								schedule.delete();
							}
						}
					}
					//退款于用户-获得最大化退款： 金额计算：已付款金额 * 退款课程数/总课程数 + 首次对接费用 。
					String orders_id = ordersSchedule.getStr(ordersSchedule.orders_id);
					Orders orders = Orders.dao.findById(orders_id);
					if (orders!=null) {
						int buy_user_id = orders.get(orders.user_id);
						double total_session_rate = orders.getDouble(orders.total_session_rate);
						int buy_amount = orders.get(orders.buy_amount);
						double first_joint_fee = orders.getDouble(orders.first_joint_fee);
						double refund_success_money = orders.getDouble(orders.refund_success_money);
						String course_title = orders.getStr(orders.course_title);
						double refund_one_course = total_session_rate*1/buy_amount+first_joint_fee+refund_success_money;
						update = orders.set(orders.refund_success_money, refund_one_course)
									   .update();
						//推送
						User user = User.dao.findById(buy_user_id);
						int agent_level = user.get(user.agent_level);
						Comment comments = new Comment();
						if (agent_level==1) {
							//用户
							comments.set(comments.user_id, buy_user_id);
						}
						if (agent_level==2) {
							//教练
							comments.set(comments.public_course_user_id, buy_user_id);
						}
						String note = "The single section you buy："+course_title+" have a refund to your account, please check";
						boolean save = comments.set(comments.note, note)
								.set(comments.user_id, 0)
								.set(comments.post_date, DateUtils.getCurrentDate())
								.set(comments.post_time, DateUtils.getCurrentDateTime())
								.set(comments.type, 2)
								.set(comments.status, 1)
								.set(comments.is_reply, 1)
								.save();
						if (save) {
							PushExample.pushToUser(buy_user_id+"", note);
						}
					}else {
						update = false;
					}
				}
				if (update==false) {
					ordersSchedule.set(ordersSchedule.status, 20)
						  	.set(ordersSchedule.refund_status, 1)
						  	.update();
				}
			}
			if (type==2) {
				ordersSchedule.set(ordersSchedule.refund_status, 10).update();
			}
			if (type==3) {
				boolean update = ordersSchedule.set(ordersSchedule.schedule_ids, 0)
						.set(ordersSchedule.schedule_data, "")
						.set(ordersSchedule.schedule_hours, "")
						.set(ordersSchedule.schedule_time_slots, 0)
						.set(ordersSchedule.status, 4)
						.set(ordersSchedule.refund_status, 10)
						.update();
				if (update) {
					String orders_id = ordersSchedule.getStr(ordersSchedule.orders_id);
					Orders orders = Orders.dao.findById(orders_id);
					orders.set(orders.booking_status, 1).set(orders.has_refund_status, 2).update();
					
				}
			}
		}
		redirect("/admin/OrdersManage/list?status=21");
	}
	/**
	 * 订单数量
	 * @throws ParseException
	 */
	public void oNumberList() throws ParseException{
		int currentPage = getParaToInt("pn", 1);
		String start_time=getPara("start_time","");
		String end_time=getPara("end_time","");
		String kw = getPara("kw","");
		String message="list";
		Page<Orders> orders = null;
		String filter_sql=" status in(12,21,22,20,30,40) ";
		String exper_sql=" and 1=1 ";
		if (!kw.equals("")) {
			kw = kw.trim();
			exper_sql = exper_sql +"  and (order_number like '%" + kw + "%') ";
			message="search";
		}
		if(!start_time.equals("") && !end_time.equals(""))
		{
			exper_sql=exper_sql+" and post_time between '"+start_time+"' and '"+end_time+"' ";
			message="search";
		}
		setAttr("start_time",start_time);
		setAttr("end_time",end_time);
		setAttr("kw", kw);
		setAttr("action", message);
		orders = Orders.dao.paginate(currentPage, PAGE_SIZE,
				"select * ",
				"from orders where "+filter_sql+exper_sql);
		int daifukuan_count=0,yifukuan_count=0,yiwancheng_count=0,total_buy_amount=0;
		double total_session_rate = 0;
		List<Orders>orBases=Orders.dao.find("select orders_id,buy_amount,session_rate,status from orders where status>10 "+exper_sql);
		for(Orders oBase:orBases){
			int oBase_status = oBase.get(oBase.status);
			int buy_amount = oBase.get(oBase.buy_amount);
			double session_rate = oBase.getDouble(oBase.session_rate);
			if (oBase_status==11) {
				daifukuan_count = daifukuan_count+1;
			}
			if (oBase_status==21 || oBase_status==22) {
				yifukuan_count = yifukuan_count+1;	
				total_buy_amount = total_buy_amount+buy_amount;
				total_session_rate = total_session_rate+(buy_amount*session_rate);
			}
			if (oBase_status==12 || oBase_status==20 || oBase_status==30 || oBase_status==40) {
				yiwancheng_count = yiwancheng_count+1;	
				total_buy_amount = total_buy_amount+buy_amount;
				total_session_rate = total_session_rate+(buy_amount*session_rate);
			}
		}
		DecimalFormat df=new DecimalFormat(".##");
		total_session_rate=Double.parseDouble(df.format(total_session_rate));
		setAttr("total_session_rate", total_session_rate);
		setAttr("total_order_count", (yifukuan_count+yiwancheng_count));
		setAttr("total_buy_amount", total_buy_amount);
		setAttr("yifukuan_count", yifukuan_count);
		setAttr("yiwancheng_count", yiwancheng_count);
		setAttr("list", orders);
		setAttr("pn", currentPage);
		render("/admin/OrderNumberList.html");
	}
	/**
	 * 订单明细
	 */
	public void orderDetail() {
		int status = getParaToInt("status", 21);
		int currentPage = getParaToInt("pn", 1);
		String start_time=getPara("start_time","");
		String end_time=getPara("end_time","");
		String kw = getPara("kw","");
		String message="list";
		String filter_sql=" a.status>11";
		if (!kw.equals("")) {
			kw = kw.trim();
			filter_sql = filter_sql + " and (a.course_email like '%" + kw + "%' or a.course_nickname like '%" + kw + "%') ";
			message="search";
		}
		if(!start_time.equals("") && !end_time.equals("")){
			filter_sql=filter_sql+" and a.post_time between '"+start_time+"' and '"+end_time+"' ";
			message="search";
		}
		setAttr("start_time",start_time);
		setAttr("end_time",end_time);
		setAttr("status", status);
		setAttr("kw", kw);
		setAttr("action", message);
		Page<Orders> ordersPage = Orders.dao.paginate(currentPage, PAGE_SIZE,
				"select a.*,b.* ",
				"from orders a,orders_schedule b where "+filter_sql+"  and a.orders_id= b.orders_id and b.status=1 order by a.post_time desc");
		double count_session_rate = 0;
		for(Orders orders:ordersPage.getList()){
			//注：金额=（订单总金额-介绍费）/课程数
			double total_session_rate = orders.getDouble(orders.total_session_rate);
			double first_joint_fee = orders.getDouble(orders.first_joint_fee);
			int buy_amount = orders.get(orders.buy_amount);
			double dd_per_session_rate = (total_session_rate-first_joint_fee)/buy_amount;
			DecimalFormat df = new DecimalFormat(".#");
			double per_session_rate = Double.parseDouble(df.format(dd_per_session_rate));
			orders.put("per_session_rate", per_session_rate);
			count_session_rate = count_session_rate+per_session_rate;
		}
		List<User> users = User.dao.find("select * from user where agent_level=2 and status=1");
		setAttr("users", users);
		setAttr("count_session_rate", count_session_rate);
		setAttr("list", ordersPage);
		setAttr("pn", currentPage);
		render("/admin/OrderDetail.html");
	}
}
	

