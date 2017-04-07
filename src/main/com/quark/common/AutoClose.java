/**
 * 
 */
package com.quark.common;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.api.examples.PushExample;

import com.quark.app.bean.EmailUntil;
import com.quark.app.bean.ScheduleBean2;
import com.quark.mail.SendMail;
import com.quark.model.extend.Course;
import com.quark.model.extend.MyCoupon;
import com.quark.model.extend.Orders;
import com.quark.model.extend.OrdersSchedule;
import com.quark.model.extend.User;
import com.quark.utils.DateUtils;


/**
 * @author cluo
 *
 */
public class AutoClose implements Runnable {

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 * A: 未选课程的订单有效期应为可配置变量（天为单位）
	 * 已选择课程时间： 需要的变量有：退款（refund）可操作时间（小时）
	 */
	@Override
	public void run() {
		while (true) {
			try {
				List<OrdersSchedule> oSchedules = OrdersSchedule.dao.find("select * from orders_schedule where status!=1 and is_pay=1");
				for(OrdersSchedule oSchedule : oSchedules){
					String schedule_ids = oSchedule.get(oSchedule.schedule_ids);
					int status = oSchedule.get(oSchedule.status);
					int orders_schedule_id = oSchedule.get(oSchedule.orders_schedule_id);
					int refund_status = oSchedule.get(oSchedule.refund_status);
					int finish_status = oSchedule.get(oSchedule.finish_status);
					String currentDateTime = DateUtils.getCurrentDateTime();
					//用户在预定完时间后/教练48小时内没有回复后才可以申请退款。
					if (refund_status==1) {
						String post_time = oSchedule.getTimestamp("post_time").toString();	
						String addDayCurrentDateTime = DateUtils.getAddDaysString(2, post_time);
						if (DateUtils.comString3(currentDateTime,addDayCurrentDateTime)>0) {
							oSchedule.set(oSchedule.refund_status, 10)
									 .update();
						}
					}
					
					//未选课程的订单有效期应为可配置变量（天为单位）未选课程可在有效时间内无条件退款
					if ((status==4&&refund_status==10)||(status==21&&refund_status==10)) {
						String order_period_start_time = oSchedule.getTimestamp("order_period_start_time").toString();	
						int order_period_of_validity = oSchedule.get(oSchedule.order_period_of_validity);
						String addDayCurrentDateTime = DateUtils.getAddDaysString(order_period_of_validity, order_period_start_time);
						if (DateUtils.comString3(currentDateTime,addDayCurrentDateTime)>0) {
							oSchedule.set(oSchedule.status, 1)
									 .set(oSchedule.refund_status, 4)
									 .set(oSchedule.finish_status, 2)
									 .update();
						}
					}
					//已选择课程时间: 需要的变量有：退款（refund）可操作时间（小时）
					if ((status==20||status==3)&&refund_status==10) {
						int refund_priod_of_validity = oSchedule.get(oSchedule.refund_priod_of_validity);
						String refund_priod_start_time = oSchedule.getTimestamp("refund_priod_start_time").toString();	
						String addHourCurrentDateTime = DateUtils.getAddHourString(refund_priod_of_validity, refund_priod_start_time);
						if (DateUtils.comString3(currentDateTime,addHourCurrentDateTime)>0) {
							int is_remind_class = oSchedule.get(oSchedule.is_remind_class);
							if (is_remind_class==1) {
								EmailUntil.sendEmailSessionReminder(oSchedule);
							}
							boolean update = oSchedule.set(oSchedule.refund_status, 4).set(oSchedule.is_remind_class, 2).update();
						}
					}
					// 当时间过了已确认时间，教练端将出现课程完成选项（finish）
					if ((status==20||status==3)&&refund_status==4) {
						String [][]schedule_time_array22 = ScheduleBean2.schedule_time_array2;
						String schedule_time_slot_array = oSchedule.getStr(oSchedule.schedule_time_slots);
						String schedule_time_slot = "0";
						if (!schedule_time_slot_array.equals("0")) {
							String[] schedule_time_slot_arrays =  schedule_time_slot_array.split("A");
							schedule_time_slot = schedule_time_slot_arrays[1];
						}
						String schedule_data = oSchedule.getStr(oSchedule.schedule_data);
						String schedule_time = schedule_data+" "+schedule_time_array22[Integer.parseInt(schedule_time_slot)][2]+":00";
						if (DateUtils.comString3(currentDateTime,schedule_time)>0) {
							oSchedule.set(oSchedule.status, 1)
									.set(oSchedule.refund_status, 4)
									.set(oSchedule.finish_status, 2)
									.update();
						}
					}
					//
					if (finish_status==1) {
						/*String [][]schedule_time_array22 = ScheduleBean2.schedule_time_array2;
						String schedule_time_slot_array = oSchedule.getStr(oSchedule.schedule_time_slots);
						String schedule_time_slot = "0";
						if (!schedule_time_slot_array.equals("0")) {
							String[] schedule_time_slot_arrays =  schedule_time_slot_array.split("A");
							schedule_time_slot = schedule_time_slot_arrays[1];
						}
						String schedule_data = oSchedule.getStr(oSchedule.schedule_data);
						String schedule_time = schedule_data+" "+schedule_time_array22[Integer.parseInt(schedule_time_slot)][2]+":00";
						System.out.println(schedule_data+"=asdddddss="+schedule_time);
						if (DateUtils.comString3(currentDateTime,schedule_time)>0) {
							oSchedule.set(oSchedule.finish_status, 2).update();
						}*/
					}
					
				}
				//订单完成
				List<Orders> ordersList = Orders.dao.find("select orders_id,buy_amount from orders where status=21 or status=22");
				for(Orders orders2:ordersList){
					int orders_id = orders2.get(orders2.orders_id);
					int buy_amount = orders2.get(orders2.buy_amount);
					List<OrdersSchedule> oSchedules2 = OrdersSchedule.dao.find("select orders_schedule_id from orders_schedule where orders_id=? and is_pay=1 and finish_status=2 ",orders_id);
					int order_schedule_finish_num = 0;
					for(OrdersSchedule ordersSchedule:oSchedules2){
						order_schedule_finish_num = order_schedule_finish_num+1;
					}
					if (order_schedule_finish_num == buy_amount ) {
						orders2.set(orders2.status, 30)
							   .set(orders2.booking_status, 2)
							   .update();
					}
				}
				//优惠券使用
				List<MyCoupon> myCoupons = MyCoupon.dao.find("select my_coupon_id,end_time from my_coupon where status=1");
				for(MyCoupon myCoupon:myCoupons){
					String currentDateTime = DateUtils.getCurrentDateTime();
					String end_time = myCoupon.getTimestamp("end_time").toString();	
					if (DateUtils.comString3(currentDateTime,end_time)>0) {
						myCoupon.set(myCoupon.status, -1)
								.update();
					}
				}
				//休眠 1分(min)=60000毫秒(ms)
				Thread.currentThread().sleep(1000*60*6);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
