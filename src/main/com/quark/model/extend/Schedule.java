package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2017-02-21 16:59:41
*/
public class Schedule extends Model<Schedule>{

    public static Schedule dao = new Schedule();

    public static final String schedule_id="columnName=schedule_id,remarks=,dataType=int,defaultValue=null";

    public static final String user_id="columnName=user_id,remarks=,dataType=String,defaultValue=";

    public static final String choice_currentdate="columnName=choice_currentdate,remarks=选择日期【2016-06-04】,dataType=String,defaultValue=0000-00-00";

    public static final String time_slot="columnName=time_slot,remarks=0-表示全天，1-2-3 ....分别对应时间格子,dataType=int,defaultValue=0";

    public static final String remarks="columnName=remarks,remarks=备注,dataType=String,defaultValue=null";

    public static final String username="columnName=username,remarks=用户名称,dataType=String,defaultValue=";

    public static final String orders_id="columnName=orders_id,remarks=订单ID,dataType=int,defaultValue=0";

    public static final String course_id="columnName=course_id,remarks=课程ID,dataType=int,defaultValue=0";

    public static final String course_name="columnName=course_name,remarks=课程名称,dataType=String,defaultValue=";

    public static final String course_time="columnName=course_time,remarks=课程时间,dataType=String,defaultValue=";

    public static final String course_location="columnName=course_location,remarks=课程地址,dataType=String,defaultValue=";

    public static final String type="columnName=type,remarks=0-被预约，1-unavaliable，2-busy,3-空闲时间添加备注,dataType=int,defaultValue=1";

    public static final String status="columnName=status,remarks=0-隐藏，1-显示,dataType=int,defaultValue=1";

}
