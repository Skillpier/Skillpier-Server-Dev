package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2017-02-21 16:59:40
*/
public class Orders extends Model<Orders>{

    public static Orders dao = new Orders();

    public static final String orders_id="columnName=orders_id,remarks=,dataType=int,defaultValue=null";

    public static final String status="columnName=status,remarks=1-加入购物车,2-购物车失效产品，9-删除订单【未付款】，10-一般订单失效，11-待付款，12,-已完成删除订单【用户】，20-已完成删除订单【教练】，21-用户可退款【已付款】,22-用户不可退款【已付款】，30-已完成，40-已评价,dataType=int,defaultValue=1";

    public static final String booking_status="columnName=booking_status,remarks=是否出现booking按钮：1-可booking，2-不可booking,dataType=int,defaultValue=1";

    public static final String has_refund_status="columnName=has_refund_status,remarks=是否有退款：1-没有，2-有,dataType=int,defaultValue=1";

    public static final String user_id="columnName=user_id,remarks=购买者,dataType=int,defaultValue=0";

    public static final String buy_nickname="columnName=buy_nickname,remarks=用户昵称,dataType=String,defaultValue=";

    public static final String buy_email="columnName=buy_email,remarks=购买者email,dataType=String,defaultValue=";

    public static final String course_id="columnName=course_id,remarks=课程id,dataType=int,defaultValue=0";

    public static final String course_title="columnName=course_title,remarks=课程title,dataType=String,defaultValue=";

    public static final String course_user_id="columnName=course_user_id,remarks=教练id,dataType=int,defaultValue=0";

    public static final String course_nickname="columnName=course_nickname,remarks=教练昵称,dataType=String,defaultValue=";

    public static final String course_telephone="columnName=course_telephone,remarks=教练电话,dataType=String,defaultValue=";

    public static final String course_email="columnName=course_email,remarks=教练email,dataType=String,defaultValue=";

    public static final String first_joint_fee="columnName=first_joint_fee,remarks=首次对接费用百分比【如果不是首次时，此处为0】【费用3】,dataType=String,defaultValue=0.00";

    public static final String order_number="columnName=order_number,remarks=订单编号一定要用8位,dataType=String,defaultValue=";

    public static final String buy_amount="columnName=buy_amount,remarks=购买数量,dataType=int,defaultValue=0";

    public static final String session_rate="columnName=session_rate,remarks=课程单价【费用1】,dataType=String,defaultValue=0.00";

    public static final String go_door_status="columnName=go_door_status,remarks=是否上门服务：0-关闭，1-上门【如果选择上门服务，增加交通费，Teaching Location可选择】,dataType=int,defaultValue=0";

    public static final String go_door_city="columnName=go_door_city,remarks=州【travel to session选择no后，不显示地址，表示只提供上门服务，这个时候，用户课程下单的时候必须填写地址才能下单】,dataType=String,defaultValue=";

    public static final String go_door_area="columnName=go_door_area,remarks=城市,dataType=String,defaultValue=";

    public static final String go_door_street="columnName=go_door_street,remarks=街道,dataType=String,defaultValue=";

    public static final String go_door_address="columnName=go_door_address,remarks=详细地址,dataType=String,defaultValue=";

    public static final String go_door_latitude="columnName=go_door_latitude,remarks=维度,dataType=String,defaultValue=";

    public static final String go_door_longitude="columnName=go_door_longitude,remarks=经度,dataType=String,defaultValue=";

    public static final String go_door_zipcode="columnName=go_door_zipcode,remarks=城市编码,dataType=String,defaultValue=";

    public static final String go_door_traffic_cost="columnName=go_door_traffic_cost,remarks=如果选择上门服务，增加交通费课程单价【费用2】,dataType=String,defaultValue=0.00";

    public static final String my_coupon_id="columnName=my_coupon_id,remarks=0-表示没有，优惠券Id,,dataType=int,defaultValue=0";

    public static final String my_coupon_money="columnName=my_coupon_money,remarks=优惠券金额【优惠2】,dataType=String,defaultValue=0.00";

    public static final String leave_message="columnName=leave_message,remarks=给商家留言,dataType=String,defaultValue=null";

    public static final String post_time="columnName=post_time,remarks=,dataType=String,defaultValue=null";

    public static final String discount_type="columnName=discount_type,remarks=打折类型：1-by_total,2-by_account【优惠1】,dataType=int,defaultValue=1";

    public static final String discount_price="columnName=discount_price,remarks=打折价格，0-表示无,dataType=String,defaultValue=0.00";

    public static final String take_partner_num="columnName=take_partner_num,remarks=带的人数【费用4】,dataType=int,defaultValue=0";

    public static final String surcharge_for_each_cash="columnName=surcharge_for_each_cash,remarks=每个人的附加费用,dataType=String,defaultValue=0.00";

    public static final String total_session_rate="columnName=total_session_rate,remarks=实际支付课程总价格,dataType=String,defaultValue=0.00";

    public static final String original_total_session_rate="columnName=original_total_session_rate,remarks=原课程总价格,dataType=String,defaultValue=0.00";

    public static final String refund_success_money="columnName=refund_success_money,remarks=成功退款金额,dataType=String,defaultValue=0.00";

    public static final String transaction_id="columnName=transaction_id,remarks=交易号。,dataType=String,defaultValue=";

    public static final String first_name="columnName=first_name,remarks=支付,dataType=String,defaultValue=";

    public static final String last_name="columnName=last_name,remarks=支付,dataType=String,defaultValue=";

    public static final String street="columnName=street,remarks=支付,dataType=String,defaultValue=";

    public static final String city_town="columnName=city_town,remarks=支付,dataType=String,defaultValue=";

    public static final String state="columnName=state,remarks=支付,dataType=String,defaultValue=";

    public static final String zip_code="columnName=zip_code,remarks=支付,dataType=String,defaultValue=";

    public static final String phone_number="columnName=phone_number,remarks=支付,dataType=String,defaultValue=";

}
