package com.quark.app.bean;

import java.util.HashMap;

import cn.jpush.api.examples.PushExample;

import com.quark.mail.SendMail;
import com.quark.model.extend.Comment;
import com.quark.model.extend.Course;
import com.quark.model.extend.Orders;
import com.quark.model.extend.OrdersSchedule;
import com.quark.model.extend.User;
import com.quark.utils.DateUtils;

public class EmailUntil {
	
	static String respContent = "Please try to handle exceptions!";
	/**
	 * 邮件通知系统-教练发布课程成功 
	 * @param user_id
	 * @param course_title
	 */
	public static void sendEmailAddCourse(String user_id,String course_title,String message) {
		User user = User.dao.findById(user_id);
		if (user!=null) {
			String nickname = user.getStr(user.nickname);
			SendMail.send("New course notice","ivanz@skillpier.com", "【Skillpier】Coach："+nickname+" published："+course_title+" course, please check");
		}
		//系统消息
		Comment comment = new Comment();
		comment.set(comment.user_id, user_id)
			   .set(comment.note, message)
			   .set(comment.public_course_user_id, 0)
			   .set(comment.type, 2)
			   .set(comment.post_time, DateUtils.getCurrentDateTime())
			   .set(comment.post_date, DateUtils.getCurrentDate())
			   .set(comment.status, 1)
			   .save();
	}
	/**
	 * 邮件通知系统- 教练端，有人下订单以及预定时间
	 * @param user_id
	 * @param course_title
	 */
	public static void sendEmailCoach(String user_id,String course_title,String message) {
		User user = User.dao.findById(user_id);
		if (user!=null) {
			String nickname = user.getStr(user.nickname);
			SendMail.send("New course notice","ivanz@skillpier.com", "【Skillpier】Coach："+nickname+" published："+course_title+" course, please check");
		}
		//系统消息
		Comment comment = new Comment();
		comment.set(comment.user_id, user_id)
			   .set(comment.note, message)
			   .set(comment.type, 2)
			   .set(comment.post_time, DateUtils.getCurrentDateTime())
			   .set(comment.post_date, DateUtils.getCurrentDate())
			   .set(comment.status, 1)
			   .save();
	}
	/**
	 * 邮件通知系统- 教练端预定时间
	 * @param user_id
	 * @param course_title
	 */
	public static void sendEmailCoachBooking(Orders orders,String nickname,String course_title,String booking_time) {
		int course_user_id = orders.get(orders.course_user_id);
		String buy_nickname = orders.getStr(orders.buy_nickname);
		String course_nickname = orders.getStr(orders.course_nickname);
		User user = User.dao.findById(course_user_id);
		String message = "User "+nickname+" has booked a session of your course “"+course_title+"”,Please view details in your order";
		if (user!=null) {
			StringBuffer buffer = new StringBuffer();  
	        buffer.append("<h5>Dear "+course_nickname+"，</h5>");  
	        buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;User "+buy_nickname+" has booked a session of your course “"+course_title+"”,  The booking time is listed below:").append("<br>");  
	        buffer.append(booking_time+"<br>");  
	        buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;Please confirm this booking through the Skillpier website or App within 24 hours.  If you decide to decline this booking, please call the client to reschedule your session.");  
	        buffer.append("<br>");  
	        respContent = buffer.toString();  
			String email = user.getStr(user.email);
			SendMail.send("Skillpier Booking Reminder",email, respContent);
		}
		//系统消息
		Comment comment = new Comment();
		boolean save = comment.set(comment.public_course_user_id, course_user_id)
			   .set(comment.note, message)
			   .set(comment.user_id, 0)
			   .set(comment.type, 2)
			   .set(comment.post_time, DateUtils.getCurrentDateTime())
			   .set(comment.post_date, DateUtils.getCurrentDate())
			   .set(comment.status, 1)
			   .save();
		if (save) {
			PushExample.pushToUser(course_user_id+"", message);
		}
	}
	/**
	 * 邮件通知系统- 客户端，教练确定时间或者拒绝时间
	 * @param user_id
	 * @param course_title
	 */
	public static void sendEmailCoachRejectConfirm(Orders orders,int schedule_type) {
		StringBuffer buffer = new StringBuffer();  
		int course_user_id = orders.get(orders.course_user_id);
		int user_id = orders.get(orders.user_id);
		User course_user = User.dao.findById(course_user_id);
		String course_nickname = course_user.getStr(course_user.nickname);
		String course_title = orders.getStr(orders.course_title);
		String course_telephone = course_user.getStr(course_user.telephone);
		String course_email = course_user.getStr(course_user.email);
		String message="";
		User user = User.dao.findById(user_id);
		if (user!=null) {
			String email = user.getStr(user.email);
			String nickname = user.getStr(user.nickname);
			if (schedule_type==2) {
				message = "Coach "+course_nickname+" has confirmed a session of your course '"+course_title+"' Course time, Please view details in your order";
				//msg_type = "confirm";
				buffer.append("<h5>"+nickname+"，</h5>");  
		        buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;").append("Your booking of “"+course_title+"“” is confirmed by your coach/teacher.  You can view your order details through our website, Skillpier.com or the Skillpier Mobile App.");
		        buffer.append("If you are unsure what materials you need to bring for this session,");
		        buffer.append("feel free to contact your instructor at : ");
		        buffer.append("<br>").append("&nbsp;&nbsp;&nbsp;&nbsp;").append("Coach First Name:"+course_nickname);
		        buffer.append("<br>").append("&nbsp;&nbsp;&nbsp;&nbsp;").append("Cell Phone Number:"+course_telephone);
		        buffer.append("<br>").append("&nbsp;&nbsp;&nbsp;&nbsp;").append("Email Address:"+course_email);
		        buffer.append("<br>");  
		        respContent = buffer.toString(); 
		        SendMail.send("Booking Confirmed",email, respContent);
			}
			if (schedule_type==1) {
				message = "Coach "+course_nickname+" has declined a session of your course '"+course_title+"' Course time, Please view details in your order";
				buffer.append("<h5>"+nickname+"，</h5>");  
		        buffer.append("&nbsp;&nbsp;&nbsp;&nbsp; We are sorry to inform you that your booking of “"+course_title+"“ was declined by your coach/teacher, Your coach will contact you regarding this booking.");  
		        buffer.append(" You are also entitled to be able to reschedule your session now.").append("<br>");  
		        buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;Sit tight, your coach will contact you regarding this booking.You are also entitled to be able to reschedule your session now.");
		        buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;").append("<br>").append("Teacher/Coach Information:");
		        buffer.append("<br>").append("&nbsp;&nbsp;&nbsp;&nbsp;").append("Coach First Name:"+course_nickname);
		        buffer.append("<br>").append("&nbsp;&nbsp;&nbsp;&nbsp;").append("Cell Phone Number:"+course_telephone);
		        buffer.append("<br>").append("&nbsp;&nbsp;&nbsp;&nbsp;").append("Email Address:"+course_email);
		        buffer.append("<br>");  
		        buffer.append("&nbsp;&nbsp;&nbsp;&nbsp; If you have any problems with this process, please contact our support team at 714-213-9873"); 
		        respContent = buffer.toString(); 
		        SendMail.send("Booking Declined",email, respContent);
			}
		}
		//系统消息
		Comment comment = new Comment();
		boolean save = comment.set(comment.user_id, user_id)
			   .set(comment.note, message)
			   .set(comment.type, 2)
			   .set(comment.post_time, DateUtils.getCurrentDateTime())
			   .set(comment.post_date, DateUtils.getCurrentDate())
			   .set(comment.status, 1)
			   .save();
		if (save) {
			PushExample.pushToUser(user_id+"", message);
		}
	}
	/**
	 * 邮件通知系统教练- 退款申请
	 * @param user_id
	 * @param course_title
	 */
	public static void sendEmailRefundMoney(String order_number,int course_user_id,String buy_nickname,String course_email ) {
		String message = "User："+buy_nickname+" has applied for course refund, please view the details in your orders.";
		SendMail.send("Refund Notice",course_email, message);
		//系统消息
		Comment comment = new Comment();
		boolean save = comment.set(comment.public_course_user_id, course_user_id)
			   .set(comment.note, message)
			   .set(comment.user_id, 0)
			   .set(comment.type, 2)
			   .set(comment.post_time, DateUtils.getCurrentDateTime())
			   .set(comment.post_date, DateUtils.getCurrentDate())
			   .set(comment.status, 1)
			   .save();
		if (save) {
			PushExample.pushToUser(course_user_id+"", message);
		}
	}
	/**
	 * 注册用户
	 * @param email
	 */
	public static void sendEmailRegistUser(String email) {
		SendMail.send("Skillpier Regist","ivanz@skillpier.com", "【Skillpier】"+email+" register the system!");
	}
	/**
	 * 申请教练
	 * @param email
	 */
	public static void sendEmailApplyCourse(String user_id,String email) {
		SendMail.send("Skillpier Regist","ivanz@skillpier.com", "【Skillpier】user_id:"+user_id+",email:"+email+" Apply for the coach!");
	}
	/**
	 * 教练申请审核通知
	 * @param email
	 * @param status
	 */
	public static void sendEmailApplyStatus(String email,int status,String nickname) {
		StringBuffer buffer = new StringBuffer();  
		if (status==2) {
			 buffer.append("<h5>Dear "+nickname+"，</h5>");  
	         buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;Congratulations!  We have reviewed all the information in your application and we are excited to inform you that your application was approved.<br>");  
	         buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;It is our honor to be your business partner and we look forward to working with you.  You are now eligible to publish your courses.<br>");  
	         buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;After signing in to your account, click on the profile icon on the top right of the website.  On the drop-down menu, you’ll see the tab “As a Coach.”  After navigating to the next page, you’ll see the button “Publish New Course.”<br>");
	         buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;If you have any problems publishing your course, please contact our support team at 714-213-9873.");  
	         buffer.append("<br>");  
	         respContent = buffer.toString(); 
			SendMail.send("COACH ACCEPTED",email, respContent);
		}
		if (status==3) {
			 buffer.append("<h5>Dear "+nickname+"，</h5>");  
	         buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;We are sorry to inform you that your coach application is declined.  We found some information in your application to be mismatched.  If you believe this information was submitted by mistake, you can submit another application and our support team will address it right away.<br>");  
	         buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;If you have an concerns with this process, please contact our support team at 714-213-9873.<br>");  
	         buffer.append("<br>");  
	         respContent = buffer.toString(); 
			SendMail.send("COACH DECLINED",email, respContent);
		}
	}
	public static void main(String[] args) {
		sendEmailRegistUser("1@qq.com");
	}
	/**
	 * 邮件通知系统- 激活用户
	 * @param user_id
	 * @param course_title
	 */
	public static void sendEmailActivateUser(String email,String token ,String nickname) {
		StringBuffer buffer = new StringBuffer();  
        buffer.append("<h5>Dear "+nickname+"，</h5>");  
        buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;Before we get started, please activate your account by clicking the link below.").append("<br>");  
        buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;").append("<a href='https://www.skillpier.com/app/UserCenter/activate?token="+token+"'>https://www.skillpier.com/app/UserCenter/activate?token="+token+"</a>").append("<br>");
        buffer.append("<br>");  
        respContent = buffer.toString();  
		SendMail.send("EMAIL ACTIVATION",email, respContent);
	}
	/**
	 * 邮件通知系统- 重置密码
	 * @param user_id
	 * @param course_title
	 */
	public static void sendEmailResetPwd(String email,String nickname ,String token ) {
		StringBuffer buffer = new StringBuffer();  
        buffer.append("<h5>Hello, "+nickname+"!</h5>");  
        buffer.append("Forgot your <a href=\"https://www.skillpier.com\">www.skillpier.com</a> username or password?").append("<br>");  
        buffer.append("<br>");  
        buffer.append("Your username: "+nickname).append("<br>");  
        buffer.append("<br>");  
        buffer.append("To reset your password, please click or tap the link below:").append("<br>");  
        buffer.append("<br>");  
        buffer.append("<a href='https://www.skillpier.com/#/forget?token="+token+"'>https://www.skillpier.com/#/forget?token="+token+"</a>").append("<br>");
        buffer.append("<br>");  
        buffer.append("If clicking the link doesn't work, you can copy and paste it into your browser address bar.").append("<br>");
        buffer.append("<br>");  
        buffer.append("Note: The password reset link will only work for 60 minutes from when it was requested, and if you have requested a password reset more than once, only the last link sent to you is valid. This is for security reasons.").append("<br>");
        buffer.append("<br>").append("<br>");
        buffer.append("<div style='color: #888; font-size: 12px'>-</div>").append("<br>");
        buffer.append("<div style='color: #888; font-size: 12px'>You have received this email because an account is registered with your email address at www.skillpier.com, and a password reset for this account was requested. If you did not request this, it means that someone else did. The reasons could be many. Someone might wish to gain access to your account, or someone has mistaken your username to be theirs. Anyway you should not worry; password reset links can *only* be sent to your email account. If you have a strong password, your account is safe.</div>").append("<br>");
        respContent = buffer.toString();  
        SendMail.sendText(email,"Password reset", respContent);
	}
	/**
	 * 邮件通知系统- 重置密码
	 * @param user_id
	 * @param course_title
	 */
	public static void sendEmailModifyPwd(String email,String token ) {
		SendMail.send("Modify password",email, "【Skillpier】<a href='https://www.skillpier.com/app/UserCenter/resetPasswordHtml?token="+token+"'>Modify Password</a>");
	}
	
	/**
	 * 邮件通知系统- 后台退款
	 * @param user_id
	 * @param course_title
	 */
	public static void sendEmailActivateUserd(String email,String token ,String nickname) {
		StringBuffer buffer = new StringBuffer();  
        buffer.append("<h5>Dear "+nickname+"，</h5>");  
        buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;Before we get started, please activate your account by clicking the link below.").append("<br>");  
        buffer.append("https://www.skillpier.com/app/UserCenter/activate?token="+token+"");  
        buffer.append("<a href='https://www.skillpier.com/app/UserCenter/activate?token="+token+"'>https://www.skillpier.com/app/UserCenter/activate?token="+token+"</a>").append("<br>");
        buffer.append("<br>");  
        respContent = buffer.toString();  
		SendMail.send("EMAIL ACTIVATION",email, respContent);
	}
	/**
	 * 邮件通知系统- 课程到时间提醒
	 * @param user_id
	 * @param course_title
	 */
	public static void sendEmailSessionReminder(OrdersSchedule oSchedule) {
		String user_id = oSchedule.getStr(oSchedule.user_id);
		String orders_id = oSchedule.getStr(oSchedule.orders_id);
		User user = User.dao.findById(user_id);
		String nickname = user.getStr(user.nickname);
		Orders orders = Orders.dao.findById(orders_id);
		String buy_user_id = oSchedule.getStr(orders.user_id);
		String course_user_id = oSchedule.getStr(orders.course_user_id);
		String buy_phone_number = orders.getStr(orders.phone_number);
		String course_title = orders.getStr(orders.course_title);
		String session_location = orders.getStr(orders.go_door_address);
		int go_door_status = orders.get(orders.go_door_status);
		if (go_door_status==0) {
			int course_id = orders.get(orders.course_id);
			Course course = Course.dao.findById(course_id);
			if (course!=null) {
				String area = course.getStr(course.area);
				String street = course.getStr(course.street);
				session_location = area+""+street;
			}
		}
		String course_email = orders.getStr(orders.course_email);
		String buy_email = orders.getStr(orders.buy_email);
		String buy_nickname = orders.getStr(orders.buy_nickname);
		String course_nickname = orders.getStr(orders.course_nickname);
		String course_telephone = orders.getStr(orders.course_telephone);
		String schedule_hours = oSchedule.getStr(oSchedule.schedule_hours);
		//客户端
		StringBuffer buffer = new StringBuffer();  
        buffer.append("<h5>"+nickname+"，</h5>");  
        buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;").append("Your session “"+course_title+"” will be start at "+schedule_hours+", Session Location : "+session_location).append("<br>");  
        buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;").append("Coach/Teacher : "+course_nickname).append("<br>");
        buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;").append("You can contact your Coach/Teacher at : "+course_telephone).append("<br>");
        buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;").append("As always, if you have any issues, you can call our support team at 714-213-9873").append("<br>");  
        buffer.append("<br>");  
        respContent = buffer.toString();  
		SendMail.send("Skillpier session reminder",buy_email, respContent);
		
		Comment comments = new Comment();
		String note = "Your session “"+course_title+"” will be start at "+schedule_hours+", Session Location : "+session_location+": Coach/Teacher : "+course_nickname+". You can contact your Coach/Teacher at : "+course_telephone;
		boolean save = comments.set(comments.note, note)
				.set(comments.user_id, buy_user_id)
				.set(comments.post_date, DateUtils.getCurrentDate())
				.set(comments.post_time, DateUtils.getCurrentDateTime())
				.set(comments.type, 2)
				.set(comments.status, 1)
				.set(comments.is_reply, 0)
				.save();
		if (save) {
			PushExample.pushToUser(buy_user_id+"", note);
		}
		
		//教练端
		StringBuffer buffer2 = new StringBuffer();  
        buffer2.append("<h5>"+nickname+"，</h5>");  
        buffer2.append("&nbsp;&nbsp;&nbsp;&nbsp;").append("Your session “"+course_title+"” will be start at "+schedule_hours+", Session Location : "+session_location).append("<br>");  
        buffer2.append("&nbsp;&nbsp;&nbsp;&nbsp;").append("Client name : "+buy_nickname).append("<br>");
        buffer2.append("&nbsp;&nbsp;&nbsp;&nbsp;").append("You can contact your client at : "+buy_phone_number).append("<br>");
        buffer2.append("&nbsp;&nbsp;&nbsp;&nbsp;").append("As always, if you have any issues, you can call our support team at 714-213-9873").append("<br>");  
        buffer2.append("<br>");  
        String respContent2 = buffer2.toString();  
		SendMail.send("Skillpier session reminder",course_email, respContent2);
		Comment comment2 = new Comment();
		note = "Your session “"+course_title+"” will be start at "+schedule_hours+", Session Location : "+session_location+": Client name : "+buy_nickname+". You can contact your client at : "+buy_phone_number;
		save = comment2.set(comment2.note, note)
				.set(comment2.user_id, course_user_id)
				.set(comment2.post_date, DateUtils.getCurrentDate())
				.set(comment2.post_time, DateUtils.getCurrentDateTime())
				.set(comment2.type, 2)
				.set(comment2.status, 1)
				.set(comment2.is_reply, 0)
				.save();
		if (save) {
			PushExample.pushToUser(course_user_id+"", note);
		}
	}
}
