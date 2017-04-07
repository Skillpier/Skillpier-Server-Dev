package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2017-02-21 16:59:37
*/
public class Comment extends Model<Comment>{

    public static Comment dao = new Comment();

    public static final String comment_id="columnName=comment_id,remarks=评价ID,dataType=int,defaultValue=null";

    public static final String orders_id="columnName=orders_id,remarks=订单编号,dataType=int,defaultValue=0";

    public static final String course_id="columnName=course_id,remarks=课程id,dataType=int,defaultValue=0";

    public static final String category_01_name="columnName=category_01_name,remarks=课程一级分类,dataType=String,defaultValue=";

    public static final String public_course_user_id="columnName=public_course_user_id,remarks=发布课程用户Id,dataType=int,defaultValue=0";

    public static final String public_course_user_name="columnName=public_course_user_name,remarks=发布者名称,dataType=String,defaultValue=";

    public static final String user_id="columnName=user_id,remarks=用户ID-评论者,dataType=int,defaultValue=0";

    public static final String comment_name="columnName=comment_name,remarks=评论昵称,dataType=String,defaultValue=";

    public static final String comment_image="columnName=comment_image,remarks=评论头像,dataType=String,defaultValue=";

    public static final String note="columnName=note,remarks=评语，消息,dataType=String,defaultValue=null";

    public static final String pro_skill="columnName=pro_skill,remarks=专业技能,dataType=int,defaultValue=5";

    public static final String teaching_environment="columnName=teaching_environment,remarks=教学环境,dataType=int,defaultValue=5";

    public static final String teaching_attitude="columnName=teaching_attitude,remarks=教学态度,dataType=int,defaultValue=5";

    public static final String is_reply="columnName=is_reply,remarks=0-未回复，1-已回复,dataType=int,defaultValue=0";

    public static final String comment_reply_id="columnName=comment_reply_id,remarks=回复评论ID,dataType=int,defaultValue=0";

    public static final String type="columnName=type,remarks=1-评论消息，2-推送消息,dataType=int,defaultValue=1";

    public static final String post_time="columnName=post_time,remarks=,dataType=String,defaultValue=null";

    public static final String post_date="columnName=post_date,remarks=日期：2016-3-23,dataType=String,defaultValue=";

    public static final String status="columnName=status,remarks=0-隐藏{平台}，1-显示，2-用户删除，3-教练删除,dataType=int,defaultValue=1";

    public static final String is_read="columnName=is_read,remarks=0-未读，1-已读,dataType=int,defaultValue=0";

}
