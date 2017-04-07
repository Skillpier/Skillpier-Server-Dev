package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2017-02-21 16:59:39
*/
public class Experience extends Model<Experience>{

    public static Experience dao = new Experience();

    public static final String experience_id="columnName=experience_id,remarks=,dataType=int,defaultValue=null";

    public static final String user_id="columnName=user_id,remarks=,dataType=String,defaultValue=0";

    public static final String title="columnName=title,remarks=标题,dataType=String,defaultValue=";

    public static final String content="columnName=content,remarks=内容,dataType=String,defaultValue=null";

    public static final String images="columnName=images,remarks=证书图片【11.jpg#22.jpg#33.jpg】,dataType=String,defaultValue=";

    public static final String post_time="columnName=post_time,remarks=,dataType=String,defaultValue=null";

    public static final String status="columnName=status,remarks=1-上线，2-草案,dataType=int,defaultValue=1";

}
