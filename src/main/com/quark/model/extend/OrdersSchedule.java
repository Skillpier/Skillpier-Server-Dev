package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2017-02-21 16:59:40
*/
public class OrdersSchedule extends Model<OrdersSchedule>{

    public static OrdersSchedule dao = new OrdersSchedule();

    public static final String orders_schedule_id="columnName=orders_schedule_id,remarks=,dataType=int,defaultValue=null";

    public static final String orders_id="columnName=orders_id,remarks=订单Id,dataType=String,defaultValue=";

    public static final String user_id="columnName=user_id,remarks=购买者,dataType=String,defaultValue=";

    public static final String schedule_ids="columnName=schedule_ids,remarks=时间安排id：0-未选择课程，1-已选择时间[11,dataType=String,defaultValue=0";

    public static final String schedule_data="columnName=schedule_data,remarks=课程日期,dataType=String,defaultValue=";

    public static final String schedule_hours="columnName=schedule_hours,remarks=课程时间段,dataType=String,defaultValue=null";

    public static final String schedule_time_slots="columnName=schedule_time_slots,remarks=0-表示全天，1-2-3 ....分别对应时间格子,A连接多个,dataType=String,defaultValue=0";

    public static final String status="columnName=status,remarks=1-完成课程(finished)，20-教练确认时间(confirmed),21-教练拒绝用户时间(未选择时间),22-教练取消订单(cancel),3-教练未确认时间(unconfirmed),4-用户未选择时间(unchoice),dataType=int,defaultValue=4";

    public static final String refund_status="columnName=refund_status,remarks=退款状态：1-预定完时间教练48小时内没有回复后才可以申请退款，10-用户未申请退款，2-退款中[pending]，30-教练申请取消订单退款中[completed],31-管理员确认退款完成[completed]，4-不能退款[unrefund]也不能选择时间，课程已完成也不能,dataType=int,defaultValue=10";

    public static final String refund_reason="columnName=refund_reason,remarks=教练refuse用户时间的原因,dataType=String,defaultValue=";

    public static final String post_time="columnName=post_time,remarks=提交时间,dataType=String,defaultValue=null";

    public static final String order_period_of_validity="columnName=order_period_of_validity,remarks=订单有效期【天】未选课程的订单有效期应为可配置变量（天为单位）,dataType=int,defaultValue=1";

    public static final String order_period_start_time="columnName=order_period_start_time,remarks=有效起始时间,dataType=String,defaultValue=null";

    public static final String refund_priod_of_validity="columnName=refund_priod_of_validity,remarks=离上课时间前多久可以退款-6个小时,dataType=int,defaultValue=1";

    public static final String refund_priod_start_time="columnName=refund_priod_start_time,remarks=退款开始时间,dataType=String,defaultValue=null";

    public static final String finish_status="columnName=finish_status,remarks=是否出现finish按钮：1-不可finish_status，2-可finish_status,dataType=int,defaultValue=1";

    public static final String is_pay="columnName=is_pay,remarks=是否付款：1-已支付，0-未付款,dataType=int,defaultValue=0";

    public static final String is_remind_class="columnName=is_remind_class,remarks=1-未上课通知，2-已上课通知,dataType=int,defaultValue=1";

    public static final String is_read="columnName=is_read,remarks=0-未读，1-已读,dataType=int,defaultValue=0";

}
