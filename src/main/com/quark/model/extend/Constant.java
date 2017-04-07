package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2017-02-21 16:59:37
*/
public class Constant extends Model<Constant>{

    public static Constant dao = new Constant();

    public static final String constant_id="columnName=constant_id,remarks=,dataType=int,defaultValue=null";

    public static final String first_joint_fee="columnName=first_joint_fee,remarks=首次对接费用百分比,dataType=String,defaultValue=0.00";

    public static final String order_period_of_validity="columnName=order_period_of_validity,remarks=订单有效期【天】未选课程的订单有效期应为可配置变量（天为单位）,dataType=int,defaultValue=1";

    public static final String booking_period_of_validity="columnName=booking_period_of_validity,remarks=选择课程时间,距离现在不可选择的时间（小时）,dataType=int,defaultValue=0";

    public static final String refund_priod_of_validity="columnName=refund_priod_of_validity,remarks=离上课时间前多久可以退款-6个小时,dataType=int,defaultValue=6";

    public static final String warning_booking_hour="columnName=warning_booking_hour,remarks=教练回应用户预约时间的系统警告,dataType=int,defaultValue=1";

}
