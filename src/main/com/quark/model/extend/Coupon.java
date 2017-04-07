package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2017-02-21 16:59:38
*/
public class Coupon extends Model<Coupon>{

    public static Coupon dao = new Coupon();

    public static final String coupon_id="columnName=coupon_id,remarks=,dataType=int,defaultValue=null";

    public static final String provider="columnName=provider,remarks=提供者,dataType=String,defaultValue=";

    public static final String coupon_name="columnName=coupon_name,remarks=优惠券名称,dataType=String,defaultValue=";

    public static final String coupon_money="columnName=coupon_money,remarks=优惠券金额,dataType=String,defaultValue=0.00";

    public static final String coupon_number="columnName=coupon_number,remarks=优惠券唯一编码一定要用8位,dataType=String,defaultValue=";

    public static final String usable_day="columnName=usable_day,remarks=有效天数,dataType=int,defaultValue=1";

    public static final String coupon_rule="columnName=coupon_rule,remarks=优惠券规则,dataType=String,defaultValue=null";

    public static final String consume_money="columnName=consume_money,remarks=单次消费需要超过的金额数【规则】,dataType=String,defaultValue=0.00";

    public static final String category_01_id="columnName=category_01_id,remarks=一级分类是否可使用：0-不限，其他-受限,dataType=int,defaultValue=0";

    public static final String category_02_id="columnName=category_02_id,remarks=二级分类是否可使用：0-不限，其他-受限,dataType=int,defaultValue=0";

    public static final String is_seller="columnName=is_seller,remarks=0-否，其他【是否指定的商家活动（即指定商家发布的课程才可使用）ID】,dataType=int,defaultValue=0";

    public static final String is_course="columnName=is_course,remarks=是否指定课程：0-否，1-是【课程ID】,dataType=int,defaultValue=0";

    public static final String number="columnName=number,remarks=优惠券数字【2-活动优惠券-供用户输入领取】,dataType=String,defaultValue=";

    public static final String num_coupon_amount="columnName=num_coupon_amount,remarks=优惠券数量【2-活动优惠券】,dataType=int,defaultValue=0";

    public static final String limit_num="columnName=limit_num,remarks=每个账户的兑换次数【2-活动优惠券】,dataType=int,defaultValue=0";

    public static final String coupon_type="columnName=coupon_type,remarks=优惠券类型：1-普通优惠券，2-活动优惠券,dataType=int,defaultValue=1";

    public static final String post_time="columnName=post_time,remarks=,dataType=String,defaultValue=null";

    public static final String status="columnName=status,remarks=0-隐藏，1-显示,dataType=int,defaultValue=1";

}
