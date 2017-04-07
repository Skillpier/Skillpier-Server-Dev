package com.quark.admin.controller;

import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.quark.interceptor.Login;
import com.quark.interceptor.Privilege;
import com.quark.model.extend.Category01;
import com.quark.model.extend.Category02;
import com.quark.model.extend.Coupon;
import com.quark.model.extend.Course;
import com.quark.model.extend.Orders;
import com.quark.model.extend.User;
import com.quark.utils.DateUtils;

/**
 * 活动优惠劵
 * @author Administrator
 *
 */
@Before(Login.class)
public class ActiveCouponManage extends Controller {
	
	@Before(Privilege.class)
	public void list() {
		int CurrentPage = getParaToInt("pn", 1);
		String start_time = getPara("start_time", "");
		String end_time = getPara("end_time", "");
		String kw = getPara("kw", "");
		String my_sql = "";
		String message = "list";
		String message1 = getPara("message1", null);
		if (!kw.equals("")) {
			kw = kw.trim();
			my_sql = my_sql + "  and (provider like '%" + kw + "%') ";
			message = "search";
		}
		if (!start_time.equals("") && !end_time.equals("")) {
			my_sql = my_sql + " and ( post_time between '" + start_time+ "' and '" + end_time + "') ";
			message = "search";
		}
		setAttr("start_time", start_time);
		setAttr("end_time", end_time);
		setAttr("kw", kw);
		setAttr("action", message);
		Page<Coupon> coupon = Coupon.dao.paginate(CurrentPage, PAGE_SIZE,
				"select * ", "from coupon where  status=1 and coupon_type=2 "+ my_sql + "order by post_time desc");
		for (Coupon coupons : coupon.getList()) {
			int category_02_id = coupons.get(coupons.category_02_id);
			String category_02_name = "";
			if (category_02_id != 0) {
				Category02 category02 = Category02.dao.findById(category_02_id);
				if (category02 != null) {
					category_02_name = category02.getStr(category02.category_02_name);
					coupons.put("category_02_name", category_02_name);
				}
			}
			int is_seller = coupons.get(coupons.is_seller);
			String nickname = "";
			if (is_seller!=0) {
				User user = User.dao.findFirst("select nickname from user where user_id=?", is_seller);
				if (user != null) {
					nickname = user.getStr(user.nickname);
				}
			}
			coupons.put("userxy", nickname);
			int is_course = coupons.get(coupons.is_course);
			String title = "";
			if (is_course!=0) {
				Course courses = Course.dao.findFirst("select title from course where course_id=?", is_course);
				if (courses != null) {
					title = courses.getStr(courses.title);
				}
			}
			coupons.put("title", title);
		}
		if (message1 != null) {
			if (message1.equals("1")) {
				setAttr("ok", "添加成功");
			}
			if (message1.equals("2")) {
				setAttr("ok", "添加失败");
			}
			if (message1.equals("3")) {
				setAttr("ok", "修改成功");
			}
			if (message1.equals("4")) {
				setAttr("ok", "修改失败");
			}
		}

		setAttr("list", coupon);
		setAttr("pn", CurrentPage);
		render("/admin/ActiveCouponManageList.html");
	}

	// 添加优惠劵
	public void addActiveCoupon() { // 商家指定
		// 商家指定
		List<User> users = User.dao.find("select user_id,nickname from user where status=1 and agent_level=2 and is_stop_course=0");
		setAttr("users", users);
		// 一级分类
		List<Category01> categorys01 = Category01.dao.find("select * from category_01 where status=1");
		setAttr("categorys01", categorys01);
		render("/admin/ActiveCouponManageAdd.html");
	}

	// 添加优惠劵
	public void addCoupons() {
		String provider = getPara("provider");
		String coupon_name = getPara("coupon_name");
		String coupon_money = getPara("coupon_money");
		String usable_day = getPara("usable_day");
		String consume_money = getPara("consume_money");
		String number = getPara("number");
		String num_coupon_amount = getPara("num_coupon_amount");
		String limit_num = getPara("limit_num");
		int coupon_type = getParaToInt("coupon_type",2);
		int category_01_name = getParaToInt("category_01_name",0);
		int category_02_name = getParaToInt("category_02_name",0);
		int user_id = getParaToInt("user_id",0);
		int title = getParaToInt("title",0);
		String coupon_rule = getPara("coupon_rule");
		Coupon coupon = new Coupon();
		boolean save = coupon.set(coupon.provider, provider)
				.set(coupon.coupon_name, coupon_name)
				.set(coupon.coupon_money, coupon_money)
				.set(coupon.usable_day, usable_day)
				.set(coupon.consume_money, consume_money)
				.set(coupon.number, number)
				.set(coupon.limit_num, limit_num)
				.set(coupon.coupon_type, coupon_type)
				.set(coupon.num_coupon_amount, num_coupon_amount)
				.set(coupon.is_seller, user_id)
				.set(coupon.is_course, title)
				.set(coupon.category_01_id, category_01_name)
				.set(coupon.category_02_id, category_02_name)
				.set(coupon.coupon_rule, coupon_rule)
				.set(coupon.post_time, DateUtils.getCurrentDateTime())
				.set(coupon.coupon_number, DateUtils.getTimeStampNo())
				.save();
		if (save) {
			redirect("/admin/ActiveCouponManage/list?message1=1");
		} else {
			redirect("/admin/ActiveCouponManage/list?message1=2");
		}
	}

	// 删除优惠劵
	public void delete() {
		int coupon_id = getParaToInt("coupon_id");
		Coupon coupon = Coupon.dao.findById(coupon_id);
		if (coupon != null) {
			coupon.delete();
		}
		redirect("/admin/ActiveCouponManage/list");
	}

	public void addEditor() {
		List<User> users = User.dao.find("select user_id,nickname from user where status=1 and agent_level=2 and is_stop_course=0");
		setAttr("users", users);
		// 一级分类
		List<Category01> categorys01 = Category01.dao.find("select * from category_01 where status=1");
		setAttr("categorys01", categorys01);
		int coupon_id = getParaToInt("coupon_id");
		Coupon coupon = Coupon.dao.findById(coupon_id);
		setAttr("coupon", coupon);
		if(coupon!=null){
			int cateid=coupon.get(coupon.category_01_id);
			List<Category02> categorys02 = Category02.dao.find("select * from category_02 where category_01_id =? and status=1",cateid);
			setAttr("categorys02", categorys02);
			// 是否指定课程
			List<Course> courses = Course.dao.find("select course_id,title from course where status=1 and user_id = ? and category_01_id=? and category_02_id=?",coupon.get(coupon.is_seller),coupon.get(coupon.category_01_id),coupon.get(coupon.category_02_id));
			setAttr("courses", courses);
		}
		render("/admin/ActiveCouponManageEditor.html");
	}

	public void addCouponsEditor() {
		String provider = getPara("provider");
		String coupon_name = getPara("coupon_name");
		String coupon_money = getPara("coupon_money");
		String usable_day = getPara("usable_day");
		String consume_money = getPara("consume_money");
		String number = getPara("number");
		String limit_num = getPara("limit_num");
		int coupon_type = getParaToInt("coupon_type",2);
		String num_coupon_amount = getPara("num_coupon_amount");
		int user_id = getParaToInt("user_id",0);
		int title = getParaToInt("title",0);
		int category_01_name = getParaToInt("category_01_name",0);
		int category_02_name = getParaToInt("category_02_name",0);
		String coupon_rule = getPara("coupon_rule");
		int coupon_id = getParaToInt("coupon_id");
		Coupon coupon = Coupon.dao.findById(coupon_id);
		boolean update = coupon.set("provider", provider)
				.set("coupon_name", coupon_name)
				.set("coupon_money", coupon_money)
				.set("usable_day", usable_day)
				.set("consume_money", consume_money).set("number", number)
				.set("limit_num", limit_num).set("coupon_type", coupon_type)
				.set("num_coupon_amount", num_coupon_amount)
				.set("is_seller", user_id).set("is_course", title)
				.set("category_01_id", category_01_name)
				.set("category_02_id", category_02_name)
				.set("coupon_rule", coupon_rule)
				.set("post_time", DateUtils.getCurrentDateTime())
				.update();
		if (update) {
			redirect("/admin/ActiveCouponManage/list?message1=3");
		} else {
			redirect("/admin/ActiveCouponManage/list?message1=4");
		}
	}
	
	
	//根据一级分类查询二级分类
	public void selectCategory_02(){
		int sid = getParaToInt("sid");
		List<Category02> lists=null;
		if (sid != 0){
			 lists = Category02.dao.find("select category_02_id,category_02_name from category_02 where status=1 and category_01_id=?",sid);
		}
		renderJson("category02s",lists);
	}
	//根据一级二级商家查询课程
	public void selectTitle(){
		String user_id = getPara("user_id");
		String category_01_id = getPara("category_01_id");
		String category_02_id = getPara("category_02_id");
		if (user_id==null) {
			user_id = "0";
		}
		String sql = "select course_id,title from course where status =1 and user_id ="+user_id;
		if (category_01_id!=null) {
			sql = sql+" and category_01_id = "+category_01_id;
		}
		if (category_02_id!=null) {
			sql = sql+" and category_02_id = "+category_02_id;
		}
		List<Course> lists = Course.dao.find(sql);
		renderJson("courses",lists);
	}
	public void selectUser(){
		String category_01_id = getPara("category_01_id");
		String category_02_id = getPara("category_02_id");
		if (category_01_id==null) {
			category_01_id = "0";
		}
		if (category_02_id==null) {
			category_02_id = "0";
		}
		String filter_sql = "user_id in(select user_id from course where status=1 and category_01_id="+category_01_id+" and category_02_id="+category_02_id+")";
		List<User> usersList = User.dao.find("select user_id,nickname from user where status=1 and agent_level=2 and is_stop_course=0 and "+filter_sql);
		renderJson("usersList",usersList);
	}
}
