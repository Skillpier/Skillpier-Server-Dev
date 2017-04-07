package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2016-06-22 13:51:38
*/
public class Messagesold extends Model<Messagesold>{

    public static Messagesold dao = new Messagesold();

    public static final String messages_id="columnName=messages_id,remarks=,dataType=int,defaultValue=null";

    public static final String content="columnName=content,remarks=内容,dataType=String,defaultValue=null";

    public static final String writer="columnName=writer,remarks=作者,dataType=String,defaultValue=";

    public static final String type="columnName=type,remarks=1-系统消息，2-站内回复,dataType=int,defaultValue=1";

    public static final String post_time="columnName=post_time,remarks=,dataType=String,defaultValue=null";

    public static final String user_id="columnName=user_id,remarks=接受者,dataType=int,defaultValue=0";

    public static final String is_read="columnName=is_read,remarks=0-未读，1-已读,dataType=int,defaultValue=0";

    public static final String serial_number="columnName=serial_number,remarks=序列号：同一条消息,dataType=String,defaultValue=";

}
