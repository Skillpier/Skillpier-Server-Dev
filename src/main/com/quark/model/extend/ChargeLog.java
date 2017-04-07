package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2017-02-21 16:59:36
*/
public class ChargeLog extends Model<ChargeLog>{

    public static ChargeLog dao = new ChargeLog();

    public static final String charge_log_id="columnName=charge_log_id,remarks=支付ID,dataType=int,defaultValue=null";

    public static final String user_id="columnName=user_id,remarks=支付用户ID,dataType=int,defaultValue=0";

    public static final String orders_id="columnName=orders_id,remarks=订单Id【多订单支付用#】,dataType=String,defaultValue=";

    public static final String type="columnName=type,remarks=支付方式：支付宝、visa、paypal,dataType=String,defaultValue=";

    public static final String pay_id="columnName=pay_id,remarks=支付平台对账交易号:支付宝成功付款不为空,dataType=String,defaultValue=";

    public static final String is_pay="columnName=is_pay,remarks=支付状态：0-未支付，1-已支付,dataType=int,defaultValue=0";

    public static final String total_money="columnName=total_money,remarks=支付金额,dataType=String,defaultValue=0.00";

    public static final String post_time="columnName=post_time,remarks=支付时间,dataType=String,defaultValue=null";

    public static final String first_name="columnName=first_name,remarks=visa支付,dataType=String,defaultValue=";

    public static final String last_name="columnName=last_name,remarks=visa支付,dataType=String,defaultValue=";

    public static final String street="columnName=street,remarks=visa支付,dataType=String,defaultValue=";

    public static final String country="columnName=country,remarks=visa支付,dataType=String,defaultValue=";

    public static final String city="columnName=city,remarks=visa支付,dataType=String,defaultValue=";

    public static final String v_state="columnName=v_state,remarks=visa支付,dataType=String,defaultValue=";

    public static final String v_code="columnName=v_code,remarks=visa支付,dataType=String,defaultValue=";

    public static final String v_phone="columnName=v_phone,remarks=visa支付,dataType=String,defaultValue=";

}
