package com.quark.admin.controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.quark.interceptor.Login;
import com.quark.interceptor.Privilege;
import com.quark.model.extend.Constant;

@Before(Login.class)
public class Constants extends Controller {

	@Before(Privilege.class)
	public void add() {
		Constant con = Constant.dao.findFirst("select * from constant");
		setAttr("r", con);
		int message = getParaToInt("message", 0);
		if (message != 0) {
			if (message == 1) {
				setAttr("ok", "添加成功");
			}
			if (message == 2) {
				setAttr("ok", "修改成功");
			}
		}
		render("/admin/ConstantsAdd.html");
	}

	public void addConstants() {
		String first_joint_fee = getPara("first_joint_fee");
		String order_period_of_validity = getPara("order_period_of_validity");
		String booking_period_of_validity = getPara("booking_period_of_validity");
		String refund_priod_of_validity = getPara("refund_priod_of_validity");
		String warning_booking_hour = getPara("warning_booking_hour");
		String constant_id = getPara("constant_id", null);
		if (constant_id == null) {
			Constant con = new Constant();
			boolean save = con.set(con.first_joint_fee, first_joint_fee)
					.set(con.order_period_of_validity, order_period_of_validity)
					.set(con.booking_period_of_validity,booking_period_of_validity)
					.set(con.refund_priod_of_validity, refund_priod_of_validity)
					.set(con.warning_booking_hour, warning_booking_hour)
					.save();
			if (save) {
				redirect("/admin/Constants/add?message=1");
			}
		} else {
			Constant con = Constant.dao.findById(constant_id);
			boolean update = con
					.set(con.first_joint_fee, first_joint_fee)
					.set(con.order_period_of_validity, order_period_of_validity)
					.set(con.booking_period_of_validity,booking_period_of_validity)
					.set(con.refund_priod_of_validity, refund_priod_of_validity)
					.set(con.warning_booking_hour, warning_booking_hour)
					.update();
			if (update) {
				redirect("/admin/Constants/add?message=2");
			}
		}
	}
}
