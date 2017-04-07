package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2017-02-21 16:59:39
*/
public class Feedback extends Model<Feedback>{

    public static Feedback dao = new Feedback();

    public static final String feedback_id="columnName=feedback_id,remarks=,dataType=int,defaultValue=null";

    public static final String user_id="columnName=user_id,remarks=,dataType=int,defaultValue=null";

    public static final String content="columnName=content,remarks=内容,dataType=String,defaultValue=null";

    public static final String name="columnName=name,remarks=姓名,dataType=String,defaultValue=null";

    public static final String email="columnName=email,remarks=联系方式-emial,dataType=String,defaultValue=null";

    public static final String post_time="columnName=post_time,remarks=,dataType=String,defaultValue=null";

    public static final String phone="columnName=phone,remarks=email,dataType=String,defaultValue=";

}
