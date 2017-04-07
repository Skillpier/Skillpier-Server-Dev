package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2016-07-06 18:39:06
*/
public class Payment extends Model<Payment>{

    public static Payment dao = new Payment();

    public static final String payment_id="columnName=payment_id,remarks=,dataType=int,defaultValue=null";

    public static final String name="columnName=name,remarks=姓名-必填,dataType=String,defaultValue=";

    public static final String money="columnName=money,remarks=金额-必填,dataType=String,defaultValue=0.00";

    public static final String come_in_account="columnName=come_in_account,remarks=出落谁户口（一次性）,dataType=String,defaultValue=";

    public static final String change_payment_account="columnName=change_payment_account,remarks=要改Payment户口,dataType=String,defaultValue=";

    public static final String remarker="columnName=remarker,remarks=备注,dataType=String,defaultValue=";

    public static final String post_time="columnName=post_time,remarks=更新时间,dataType=String,defaultValue=null";

    public static final String submitters="columnName=submitters,remarks=提交者（审批流程）,dataType=String,defaultValue=";

    public static final String submitters_id="columnName=submitters_id,remarks=提交者ID,dataType=int,defaultValue=0";

    public static final String approvers="columnName=approvers,remarks=审判者（审批流程）,dataType=String,defaultValue=";

    public static final String status="columnName=status,remarks=状态：1-未提交（摸版），2-未处理，3-已审批，4-已拒绝,5-已转账,dataType=int,defaultValue=1";

}
