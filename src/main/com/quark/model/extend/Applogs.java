package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2017-02-21 16:59:34
*/
public class Applogs extends Model<Applogs>{

    public static Applogs dao = new Applogs();

    public static final String log_id="columnName=log_id,remarks=,dataType=int,defaultValue=null";

    public static final String level="columnName=level,remarks=级别：info、debug、warm、error,dataType=String,defaultValue=";

    public static final String user_id="columnName=user_id,remarks=用户ID,dataType=int,defaultValue=null";

    public static final String info="columnName=info,remarks=描述,dataType=String,defaultValue=null";

    public static final String post_time="columnName=post_time,remarks=记录日期,dataType=String,defaultValue=null";

    public static final String request="columnName=request,remarks=请求的url,dataType=String,defaultValue=null";

    public static final String params="columnName=params,remarks=参数,dataType=String,defaultValue=null";

}
