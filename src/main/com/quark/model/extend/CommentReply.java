package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2017-02-21 16:59:37
*/
public class CommentReply extends Model<CommentReply>{

    public static CommentReply dao = new CommentReply();

    public static final String comment_reply_id="columnName=comment_reply_id,remarks=,dataType=int,defaultValue=null";

    public static final String user_id="columnName=user_id,remarks=,dataType=String,defaultValue=";

    public static final String content="columnName=content,remarks=回复内容,dataType=String,defaultValue=null";

    public static final String post_time="columnName=post_time,remarks=,dataType=String,defaultValue=null";

    public static final String is_read="columnName=is_read,remarks=0-未读，1-已读,dataType=int,defaultValue=0";

}
