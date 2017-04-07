/**
 * 
 */
package com.quark.app.controller;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinal.upload.UploadFile;
import com.quark.api.annotation.Author;
import com.quark.api.annotation.DataType;
import com.quark.api.annotation.Explaination;
import com.quark.api.annotation.ReturnDBParam;
import com.quark.api.annotation.ReturnOutlet;
import com.quark.api.annotation.Rp;
import com.quark.api.annotation.Type;
import com.quark.api.annotation.URLParam;
import com.quark.api.annotation.UpdateLog;
import com.quark.api.annotation.Value;
import com.quark.api.auto.bean.ResponseValues;
import com.quark.app.bean.CategoryBean;
import com.quark.app.bean.ScheduleBean;
import com.quark.app.bean.ScheduleBean2;
import com.quark.app.bean.SortClass;
import com.quark.app.logs.AppLog;
import com.quark.common.AppData;
import com.quark.common.RongToken;
import com.quark.common.Storage;
import com.quark.common.config;
import com.quark.interceptor.AppToken;
import com.quark.mail.SendMail;
import com.quark.model.extend.Category01;
import com.quark.model.extend.Category02;
import com.quark.model.extend.Catetory;
import com.quark.model.extend.Collection;
import com.quark.model.extend.Comment;
import com.quark.model.extend.CommentReply;
import com.quark.model.extend.Constant;
import com.quark.model.extend.Course;
import com.quark.model.extend.CourseCertification;
import com.quark.model.extend.Experience;
import com.quark.model.extend.IndexBanner;
import com.quark.model.extend.MyCoupon;
import com.quark.model.extend.Orders;
import com.quark.model.extend.OrdersSchedule;
import com.quark.model.extend.Schedule;
import com.quark.model.extend.Tokens;
import com.quark.model.extend.User;
import com.quark.rsa.RSAsecurity;
import com.quark.sign.RequestHandler;
import com.quark.utils.DateUtils;
import com.quark.utils.EmojiFilter;
import com.quark.utils.FileUtils;
import com.quark.utils.MD5Util;
import com.quark.utils.MessageUtils;
import com.quark.utils.RandomUtils;
import com.quarkso.utils.ImgSavePathUtils;
import com.quarkso.utils.StringUtils;

/**
 * @author C罗
 * 评论管理
 */
@Before(Tx.class)
public class CommentManage extends Controller {

	@Author("cluo")
	@Rp("我的消息")
	@Explaination(info = "我的消息列表[评论课程可以点击查看，只有教练才可以回复]")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "{1、2}", explain = "1-用户，2-教练", type = Type.String, name = "type")
	@URLParam(defaultValue = "1", explain = Value.Infer, type = Type.String, name = "pn")
	@URLParam(defaultValue = "5", explain = Value.Infer, type = Type.String, name = "page_size")
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	// page property
	@ReturnOutlet(name = "CommentListResponse{commentListResult:Comments:pageNumber}", remarks = "page number", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CommentListResponse{commentListResult:Comments:pageSize}", remarks = "result amount of this page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CommentListResponse{commentListResult:Comments:totalPage}", remarks = "total page", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CommentListResponse{commentListResult:Comments:totalRow}", remarks = "total row", dataType = DataType.Int, defaultValue = "")
	//
	@ReturnDBParam(name = "CommentListResponse{commentListResult:Comments:list[Comment:$]}", column = Comment.comment_id) 
	@ReturnDBParam(name = "CommentListResponse{commentListResult:Comments:list[Comment:$]}", column = Comment.course_id) 
	@ReturnDBParam(name = "CommentListResponse{commentListResult:Comments:list[Comment:$]}", column = Comment.comment_name) 
	@ReturnDBParam(name = "CommentListResponse{commentListResult:Comments:list[Comment:$]}", column = Comment.post_time) 
	@ReturnDBParam(name = "CommentListResponse{commentListResult:Comments:list[Comment:$]}", column = Comment.note) 
	@ReturnDBParam(name = "CommentListResponse{commentListResult:Comments:list[Comment:$]}", column = Comment.is_reply) 
	@ReturnDBParam(name = "CommentListResponse{commentListResult:Comments:list[Comment:$]}", column = Comment.comment_reply_id) 
	@ReturnDBParam(name = "CommentListResponse{commentListResult:Comments:list[Comment:$]}", column = Comment.type) 
	@ReturnOutlet(name = "CommentListResponse{commentListResult:Comments:list[Comment:content]}", remarks = "回复内容", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CommentListResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CommentListResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CommentListResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void commentList() {
		try {
			String token = getPara("token");
			int type = getParaToInt("type", 1);
			int pn = getParaToInt("pn", 1);
			int page_size = getParaToInt("page_size", 5);
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("page_size", page_size+"");
			packageParams.put("pn", pn+"");
			packageParams.put("token", token);
			packageParams.put("type", type+"");
			RequestHandler reqHandler = new RequestHandler(null, null);
			reqHandler.init(config.app_quark_id, config.app_quark_secret, config.app_quark_key);
			String sign = reqHandler.createSign(packageParams);
			String invoke = getPara("invoke","app");
			if (invoke.equals("h5")) {
				sign = "123456";
			}
			if (!app_sign.equals(sign)) {
				// Signature verification failed.
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Signature verification failed.");
				response2.put("code", 403);
				setAttr("CommentListResponse", response2);
				renderMultiJson("CommentListResponse");
				return;
			}
			/**
			 * 接口签名end
			 */
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("CommentListResponse", response2);
				renderMultiJson("CommentListResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			String filter_sql ="  ";
			if (type==1) {
				//1-用户，2-教练
				filter_sql =" (status=1 or status=3) and user_id="+user_id;
			}
			if (type==2) {
				filter_sql =" (status=1 or status=2) and public_course_user_id="+user_id;
			}
			String select = "select comment_id,course_id,comment_name,post_time,note,is_reply,comment_reply_id,type  ";
			final Page<Comment> commentPage = Comment.dao
					.paginate(
							pn,
							page_size,
							select," from comment where "+filter_sql+" order by post_time desc");
			for(Comment comment:commentPage.getList()){
				String content ="";
				int is_reply = comment.get(comment.is_reply);
				int comment_reply_id = comment.get(comment.comment_reply_id);
				if (is_reply==1) {
					CommentReply comReply = CommentReply.dao.findById(comment_reply_id);
					if (comReply!=null) {
						content = comReply.getStr(comReply.content);
					}
				}
				comment.put("content", content);
			}
			List<Comment> comments = Comment.dao.find("select comment_id,is_read from comment where "+filter_sql+" and is_read=0 ");
			for(Comment comment:comments){
				comment.set(comment.is_read, 1).update();
			}
			List<CommentReply> commentReplies = CommentReply.dao.find("select comment_reply_id from comment_reply where user_id=? and is_read=0",user_id);
			for(CommentReply commentReply:commentReplies){
				commentReply.set(commentReply.is_read, 1).update();
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("Result", new HashMap<String, Object>() {
				{
					put("Comments", commentPage);
				}
			});
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "Data request successful.");
			setAttr("CommentListResponse", responseValues);
			renderMultiJson("CommentListResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppLog.info("", getRequest());
			AppData.analyze("CommentManage/commentList", "我的消息", this);
		}
	}
	@Author("cluo")
	@Rp("评论")
	@Explaination(info = "评论")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = "第一次评论传0，第二次出现update comment时传comment_id", type = Type.String, name = Comment.comment_id)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Comment.orders_id)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Comment.course_id)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Comment.public_course_user_id)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Comment.note)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Comment.pro_skill)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Comment.teaching_environment)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Comment.teaching_attitude)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "CommentResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CommentResponse{status}", remarks = "0-失败，1-操作成功，2-不存在", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CommentResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void comment() {
		try {
			int comment_id = getParaToInt("comment_id",0);
			String token = getPara("token");
			String orders_id = getPara("orders_id");
			String course_id = getPara("course_id");
			String public_course_user_id = getPara("public_course_user_id");
			String note = getPara("note");
			String pro_skill = getPara("pro_skill","0");
			String teaching_environment = getPara("teaching_environment","0");
			String teaching_attitude = getPara("teaching_attitude","0");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("course_id", course_id);
			packageParams.put("note", note);
			packageParams.put("orders_id", orders_id);
			packageParams.put("pro_skill", pro_skill);
			packageParams.put("public_course_user_id", public_course_user_id);
			packageParams.put("teaching_attitude", teaching_attitude);
			packageParams.put("teaching_environment", teaching_environment);
			packageParams.put("token", token);
			RequestHandler reqHandler = new RequestHandler(null, null);
			reqHandler.init(config.app_quark_id, config.app_quark_secret, config.app_quark_key);
			String sign = reqHandler.createSign(packageParams);
			String invoke = getPara("invoke","app");
			if (invoke.equals("h5")) {
				sign = "123456";
			}
			/**
			 * 接口签名end
			 */
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("CommentResponse", response2);
				renderMultiJson("CommentResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="Comment failed, please check!";
			if (note==null) {
				status = 2;
				message="Please enter comments";
			}
			if (status==0) {
				User user = User.dao.findById(user_id);
				if (user!=null) {
					//	課程信息
					Course course = Course.dao.findById(course_id);
					String category_01_name = course.getStr(course.category_01_name);
					User public_course_user = User.dao.findById(public_course_user_id);
					String public_course_user_nickname = course.getStr(public_course_user.nickname);
					String comment_name = user.getStr(user.nickname);
					String comment_image = user.getStr(user.image_01);
					boolean save = false;
					if (comment_id==0) {
						Comment comment = new Comment();
						save = comment.set(comment.orders_id, orders_id)
								.set(comment.course_id, course_id)
								.set(comment.category_01_name, category_01_name)
								.set(comment.public_course_user_id, public_course_user_id)
								.set(comment.public_course_user_name, public_course_user_nickname)
								.set(comment.user_id, user_id)
								.set(comment.comment_name, comment_name)
								.set(comment.comment_image, comment_image)
								.set(comment.note, note)
								.set(comment.pro_skill, pro_skill)
								.set(comment.teaching_environment, teaching_environment)
								.set(comment.teaching_attitude, teaching_attitude)
								.set(comment.is_reply, 0)
								.set(comment.comment_reply_id, 0)
								.set(comment.type, 1)
								.set(comment.post_time, DateUtils.getCurrentDateTime())
								.set(comment.post_date, DateUtils.getCurrentDate())
								.set(comment.status, 1)
								.save();
						comment_id = comment.get("comment_id");
					}else {
						Comment comment = Comment.dao.findById(comment_id);	
						if (comment!=null) {
							save = comment.set(comment.note, note)
									.set(comment.pro_skill, pro_skill)
									.set(comment.user_id, 0)
									.set(comment.teaching_environment, teaching_environment)
									.set(comment.teaching_attitude, teaching_attitude)
									.set(comment.post_time, DateUtils.getCurrentDateTime())
									.set(comment.post_date, DateUtils.getCurrentDate())
									.update();
						}
					}
					if (save) {
						status = 1;
						message="Comment success!";
						Orders orders = Orders.dao.findById(orders_id);
						if (orders!=null) {
							orders.set(orders.status, 40).update();
						}
						//评价总分 
						int total_score = 0;
						List<Comment> commentList = Comment.dao.find("select comment_id,pro_skill,teaching_environment,teaching_attitude from comment where type=1 and course_id=? and status!=0",course_id);
						for(Comment comment2:commentList){
							int pro_skill2 = comment2.get(comment2.pro_skill); 
							int teaching_environment2 = comment2.get(comment2.teaching_environment); 
							int teaching_attitude2 = comment2.get(comment2.teaching_attitude); 
							total_score = total_score+(pro_skill2+teaching_environment2+teaching_attitude2)/3;
						}
						if (commentList.size()>0) {
							total_score = total_score/commentList.size();
							course.set(course.total_score, total_score).update();
						}
						//comment.put("total_score", total_score);
					}
				}else {
					// 登陆失败
					ResponseValues response2 = new ResponseValues(this, Thread
							.currentThread().getStackTrace()[1].getMethodName());
					response2.put("message", "Please sign-in again.");
					response2.put("code", 405);
					setAttr("CommentResponse", response2);
					renderMultiJson("CommentResponse");
					return;
				}
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("comment_id", comment_id);
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("CommentResponse", responseValues);
			renderMultiJson("CommentResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("OrdersManage/comment", "评论", this);
		}
	}
	@Author("cluo")
	@Rp("我的消息")
	@Explaination(info = "回复评论[只有教练才可以回复]")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Comment.comment_id)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = CommentReply.content)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "CommentReplyResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CommentReplyResponse{status}", remarks = "0-失败，1-操作成功，2-不存在", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CommentReplyResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void commentReply() {
		try {
			String token = getPara("token");
			String comment_id = getPara("comment_id");
			String content = getPara("content");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("comment_id", comment_id);
			packageParams.put("content", content);
			packageParams.put("token", token);
			RequestHandler reqHandler = new RequestHandler(null, null);
			reqHandler.init(config.app_quark_id, config.app_quark_secret, config.app_quark_key);
			String sign = reqHandler.createSign(packageParams);
			String invoke = getPara("invoke","app");
			if (invoke.equals("h5")) {
				sign = "123456";
			}
			if (!app_sign.equals(sign)) {
				// Signature verification failed.
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Signature verification failed.");
				response2.put("code", 403);
				setAttr("CommentReplyResponse", response2);
				renderMultiJson("CommentReplyResponse");
				return;
			}
			/**
			 * 接口签名end
			 */
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("CommentReplyResponse", response2);
				renderMultiJson("CommentReplyResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="回复失败，请检查";
			if (content==null) {
				status = 2;
				message="请输入回复内容";
			}
			if (status==0) {
				CommentReply commentReply = new CommentReply();
				boolean save = commentReply.set(commentReply.user_id, user_id)
					.set(commentReply.content, content)
					.set(commentReply.post_time, DateUtils.getCurrentDateTime())
					.save();
				if (save) {
					status = 1;
					message="回复成功";
					int comment_reply_id = commentReply.get("comment_reply_id");
					Comment comment = Comment.dao.findById(comment_id);
					if (comment!=null) {
						comment.set(comment.is_reply, 1)
							.set(comment.comment_reply_id, comment_reply_id)
							.update();
					}
				}
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("CommentReplyResponse", responseValues);
			renderMultiJson("CommentReplyResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("CommentManage/commentReply", "回复评论", this);
		}
	}
	@Author("cluo")
	@Rp("我的消息")
	@Explaination(info = "删除我的消息")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "{1、2}", explain = "1-用户，2-教练", type = Type.String, name = "delete_type")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Comment.comment_id)
	@URLParam(defaultValue = "", explain = "app的签名", type = Type.String, name = "app_sign")
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	@ReturnOutlet(name = "DeleteCommentResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "DeleteCommentResponse{status}", remarks = "0-失败，1-操作成功，2-不存在", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "DeleteCommentResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void deleteComment() {
		try {
			String token = getPara("token");
			int delete_type = getParaToInt("delete_type",1);
			String comment_id = getPara("comment_id");
			/**
			 * 接口签名
			 */ 
			String app_sign = getPara("app_sign");//不参与签名
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("delete_type", delete_type+"");
			packageParams.put("comment_id", comment_id);
			packageParams.put("token", token);
			RequestHandler reqHandler = new RequestHandler(null, null);
			reqHandler.init(config.app_quark_id, config.app_quark_secret, config.app_quark_key);
			String sign = reqHandler.createSign(packageParams);
			String invoke = getPara("invoke","app");
			if (invoke.equals("h5")) {
				sign = "123456";
			}
			if (!app_sign.equals(sign)) {
				// Signature verification failed.
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Signature verification failed.");
				response2.put("code", 403);
				setAttr("DeleteCommentResponse", response2);
				renderMultiJson("DeleteCommentResponse");
				return;
			}
			/**
			 * 接口签名end
			 */
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("DeleteCommentResponse", response2);
				renderMultiJson("DeleteCommentResponse");
				return;
			}
			String user_id = AppToken.getUserId(token, this,invoke);
			int status = 0;String message="Deletion failed. Please check for errors.";
			boolean delete = true;
			Comment comment = Comment.dao.findById(comment_id);
			if (comment!=null) {
				if (delete_type==1) {
					delete = comment.set(comment.status, 2).update();
				}
				if (delete_type==2) {
					delete = comment.set(comment.status, 3).update();
				}
				if (delete) {
					status = 1;
					message="Deletion successful.";
				}
			}else {
				status = 2;
				message="评论不存在，请检查";
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("DeleteCommentResponse", responseValues);
			renderMultiJson("DeleteCommentResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("CommentManage/deleteComment", "删除我的消息", this);
		}
	}
	/**
	 *  4. 确认时间的课程状态为已确认（confirmed），拒绝时间的课程状态回归未选择时间
	 */
	@Author("cluo")
	@Rp("评论")
	@Explaination(info = "课程信息")
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Tokens.token)
	@URLParam(defaultValue = "", explain = "第一次评论传0，第二次出现update comment时传comment_id", type = Type.String, name = Comment.comment_id)
	@URLParam(defaultValue = "", explain = Value.Infer, type = Type.String, name = Orders.orders_id)
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String, name = Course.latitude)
	@URLParam(defaultValue = "0", explain = Value.Infer, type = Type.String, name = Course.longitude)
	@URLParam(defaultValue = "{app、h5}", explain = Value.Infer, type = Type.String, name = "invoke")
	// 
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:$}", column = Orders.orders_id)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:$}", column = Orders.course_id)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:$}", column = Orders.user_id)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:$}", column = Orders.course_user_id)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:$}", column = Orders.session_rate)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:$}", column = Orders.take_partner_num)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:$}", column = Orders.order_number)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:$}", column = Orders.surcharge_for_each_cash)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:$}", column = Orders.go_door_traffic_cost)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:$}", column = Orders.buy_amount)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:$}", column = Orders.first_joint_fee)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:$}", column = Orders.discount_price)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:$}", column = Orders.my_coupon_money)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:$}", column = Orders.total_session_rate)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:$}", column = Orders.original_total_session_rate)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:$}", column = Orders.order_number)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:$}", column = Orders.post_time)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:$}", column = Orders.booking_status)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:$}", column = Orders.status)
	//
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:$}", column = Course.course_id)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:$}", column = Course.user_id)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:$}", column = Course.title)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:$}", column = Course.total_score)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:$}", column = Course.session_rate)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:$}", column = Course.category_01_id)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:$}", column = Course.category_02_id)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:$}", column = Course.category_02_name)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:$}", column = Course.travel_to_session)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:$}", column = Course.travel_to_session_distance)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:$}", column = Course.travel_to_session_trafic_surcharge)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:$}", column = Course.city)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:$}", column = Course.area)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:$}", column = Course.street)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:$}", column = Course.address)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:$}", column = Course.latitude)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:$}", column = Course.longitude)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:$}", column = Course.additional_partner)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:$}", column = Course.surcharge_for_each)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:$}", column = Course.discount_type)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:$}", column = Course.discount_onetion_pur_money_01)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:$}", column = Course.discount_price_01)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:$}", column = Course.discount_onetion_pur_money_02)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:$}", column = Course.discount_price_02)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:$}", column = Course.discount_onetion_pur_money_03)
	@ReturnDBParam(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:$}", column = Course.discount_price_03)
	@ReturnOutlet(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:course_is_official}", remarks = "教练是否官方认证：0-否，1-是", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:course_image_01}", remarks = "教练用户头像", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:course_nickname}", remarks = "教练昵称", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:course_telephone}", remarks = "教练电话", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CommentCourseInfoResponse{CommentCourseInfo:Course:total_coment_num}", remarks = "评论总数", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CommentCourseInfoResponse{message}", remarks = "", dataType = DataType.String, defaultValue = "")
	@ReturnOutlet(name = "CommentCourseInfoResponse{status}", remarks = "0-失败，1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "CommentCourseInfoResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void commentCourseInfo() {
		try {
			int comment_id = getParaToInt("comment_id",0);
			String token = getPara("token");
			String orders_id = getPara("orders_id");
			String latitude = getPara("latitude");
			String longitude = getPara("longitude");
			String invoke = getPara("invoke","app");
			if (!AppToken.check(token, this,invoke)) {
				// 登陆失败
				ResponseValues response2 = new ResponseValues(this, Thread
						.currentThread().getStackTrace()[1].getMethodName());
				response2.put("message", "Please sign-in again.");
				response2.put("code", 405);
				setAttr("CommentCourseInfoResponse", response2);
				renderMultiJson("CommentCourseInfoResponse");
				return;
			}
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			String distance_sql = "round(6378.138*2*asin(sqrt(pow(sin( ("
					+ latitude
					+ "*pi()/180-latitude*pi()/180)/2),2)+cos("
					+ latitude
					+ "*pi()/180)*cos(latitude*pi()/180)* pow(sin( ("
					+ longitude
					+ "*pi()/180-longitude*pi()/180)/2),2)))*1000)";
			String sqlString = "select course_id,user_id,user_images_01,title,category_02_name,total_score,travel_to_session,travel_to_session_distance,travel_to_session_trafic_surcharge,"+distance_sql+" as distance,session_rate,category_01_id,category_02_id,additional_partner,surcharge_for_each,"
					+ "city,area,street,address,latitude,longitude,discount_type,discount_onetion_pur_money_01,discount_price_01,discount_onetion_pur_money_02,discount_price_02,discount_onetion_pur_money_03,discount_price_03 ";
			final Orders orders = Orders.dao.findFirst("select orders_id,course_id,user_id,course_user_id,session_rate,order_number,take_partner_num,surcharge_for_each_cash,go_door_traffic_cost,buy_amount,first_joint_fee,discount_price,my_coupon_money,total_session_rate,original_total_session_rate,order_number,leave_message,booking_status,status,post_time from orders where orders_id=?",orders_id);
			if (orders!=null) {
				int hasnone_booking_course=0;
				int buy_amount = orders.get(orders.buy_amount);
				int course_id = orders.get(orders.course_id);
				Course course = Course.dao.findFirst(sqlString+" from course where course_id=?",course_id);
				String course_image_01= course.getStr(course.user_images_01);
				String course_nickname ="",course_telephone="";
				int course_is_official= 0;
				if (course!=null) {
					//教练
					int course_user_id = orders.get(orders.course_user_id);
					User courseUser = User.dao.findById(course_user_id);
					if (courseUser!=null) {
						course_is_official = courseUser.get(courseUser.is_official);
						course_nickname = courseUser.getStr(courseUser.nickname);
						course_telephone = courseUser.getStr(courseUser.telephone);
					}
					course.put("course_is_official", course_is_official);
					course.put("course_image_01", course_image_01);
					course.put("course_nickname", course_nickname);
					course.put("course_telephone", course_telephone);
					//距离
					String course_latitude = course.getStr(course.latitude);
					if (course_latitude.equals("")||course_latitude.equals("0")) {
						course.put("distance", "Distance cannot be calculated.");
					}else {
						double distance = course.getDouble("distance");
						double distance_miles = distance/1609.344;
						DecimalFormat df=new DecimalFormat(".##");
						distance_miles=Double.parseDouble(df.format(distance_miles));
						course.put("distance", distance_miles+" miles");
					}
					//评论总数
					Comment comment = Comment.dao.findFirst("select count(course_id) as total_coment_num from comment where course_id=? and status!=0",course_id);
					long total_coment_num = 0;
					if (comment!=null) {
						total_coment_num = comment.getLong("total_coment_num");
					}
					course.put("total_coment_num", total_coment_num);
				}
				orders.put("Course", course);
			}
			if (comment_id>0) {
				Comment comment = Comment.dao.findById(comment_id);
				responseValues.put("is_comment", 1);
				responseValues.put("comment", comment);
			}else {
				responseValues.put("is_comment", 0);
			}
			responseValues.put("CommentCourseInfo", orders);
			responseValues.put("status", 1);
			responseValues.put("code", 200);
			responseValues.put("message", "Data request successful.");
			setAttr("CommentCourseInfoResponse", responseValues);
			renderMultiJson("CommentCourseInfoResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("CommentManage/commentCourseInfo", "commentCourseInfo", this);
		}
	}
}
