package com.quark.common;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.druid.support.logging.Log;
import com.quark.app.logs.AppLog;
import com.quark.model.extend.Applogs;
import com.quark.model.extend.ChargeLog;
import com.quark.model.extend.Constant;
import com.quark.model.extend.Orders;
import com.quark.model.extend.OrdersSchedule;
import com.quark.utils.DateUtils;

/**
 * 账号安全支付值，只支付单线程操作
 * 
 * @author cluo
 *
 */
public class OrderUtils {

	public static boolean Dall(String orders_id,String transaction_id) throws Exception{
		//处理请求
		boolean update = false; 
		if (orders_id!=null&&!orders_id.equals("")) {
			String[]orders_id_array = orders_id.split("A");
			for(int i=0;i<orders_id_array.length;i++){
				Orders orders = Orders.dao.findById(orders_id_array[i]);
				if (orders!=null) {
					int buy_amount = orders.get(orders.buy_amount);
					int order_period_of_validity = 3,refund_priod_of_validity=3;
					Constant constant = Constant.dao.findFirst("select constant_id,order_period_of_validity,refund_priod_of_validity from constant ");
					if (constant!=null) {
						order_period_of_validity = constant.get(constant.order_period_of_validity);
						refund_priod_of_validity = constant.get(constant.refund_priod_of_validity);
					}
					update = orders.set(orders.transaction_id, transaction_id).set(orders.status, 21).update();
					if (update) {
						int pay_num = 0;
						List<OrdersSchedule> oSchedules = OrdersSchedule.dao.find("select orders_schedule_id,schedule_ids,status from orders_schedule where is_pay=0 and orders_id=?",orders_id);
						for(OrdersSchedule oSchedule : oSchedules){
							oSchedule.set(oSchedule.order_period_of_validity, order_period_of_validity)
								.set(oSchedule.refund_priod_of_validity, refund_priod_of_validity)
								.set(oSchedule.post_time, DateUtils.getCurrentDateTime())
								.set(oSchedule.order_period_start_time, DateUtils.getCurrentDateTime())
								.set(oSchedule.refund_priod_start_time, DateUtils.getCurrentDateTime())
								.set(oSchedule.is_pay, 1)
								.update();
							int os_status = oSchedule.get(oSchedule.status);
							if (os_status==3) {
								pay_num = pay_num+1;
							}
						}
						if (buy_amount==pay_num) {
							update = orders.set(orders.booking_status, 2).update();
						}else {
							update = orders.set(orders.booking_status, 1).update();
						}
					}else {
						update = false;
						break;
					}
				}else {
					update = false;
					break;
				}
			}
			return update;
		}else {
			return update;
		}
	}
}
