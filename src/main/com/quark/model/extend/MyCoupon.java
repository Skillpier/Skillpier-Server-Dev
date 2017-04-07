package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2017-02-21 16:59:40
*/
public class MyCoupon extends Model<MyCoupon>{

    public static MyCoupon dao = new MyCoupon();

    public static final String my_coupon_id="columnName=my_coupon_id,remarks=,dataType=int,defaultValue=null";

    public static final String user_id="columnName=user_id,remarks=领取者,dataType=int,defaultValue=0";

    public static final String provider="columnName=provider,remarks=提供者,dataType=String,defaultValue=";

    public static final String coupon_name="columnName=coupon_name,remarks=优惠券名称,dataType=String,defaultValue=";

    public static final String coupon_money="columnName=coupon_money,remarks=优惠券单价,dataType=String,defaultValue=0.00";

    public static final String coupon_num="columnName=coupon_num,remarks=优惠券数量,dataType=int,defaultValue=1";

    public static final String coupon_number="columnName=coupon_number,remarks=优惠券唯一编码一定要用8位,dataType=String,defaultValue=";

    public static final String begin_time="columnName=begin_time,remarks=有效期 开始时间,dataType=String,defaultValue=null";

    public static final String end_time="columnName=end_time,remarks=结束时间,dataType=String,defaultValue=null";

    public static final String coupon_rule="columnName=coupon_rule,remarks=优惠券规则,dataType=String,defaultValue=null";

    public static final String post_time="columnName=post_time,remarks=,dataType=String,defaultValue=null";

    public static final String consume_money="columnName=consume_money,remarks=单次消费需要超过的金额数【规则】,dataType=String,defaultValue=0.00";

    public static final String category_01_id="columnName=category_01_id,remarks=一级分类是否可使用：0-不限，其他-受限,dataType=int,defaultValue=0";

    public static final String category_02_id="columnName=category_02_id,remarks=二级分类是否可使用：0-不限，其他-受限,dataType=int,defaultValue=0";

    public static final String is_seller="columnName=is_seller,remarks=0-否，1-是【是否指定的商家活动（即指定商家发布的课程才可使用）】,dataType=int,defaultValue=0";

    public static final String is_course="columnName=is_course,remarks=是否指定课程：0-否，1-是【课程ID】,dataType=int,defaultValue=0";

    public static final String status="columnName=status,remarks=-1-过期，0-删除，1-正常，2-已使用,dataType=int,defaultValue=1";

}
