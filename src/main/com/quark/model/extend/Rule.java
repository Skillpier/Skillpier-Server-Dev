package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2017-02-21 16:59:41
*/
public class Rule extends Model<Rule>{

    public static Rule dao = new Rule();

    public static final String rule_id="columnName=rule_id,remarks=,dataType=int,defaultValue=null";

    public static final String consume_money="columnName=consume_money,remarks=用户单次消费金额,dataType=String,defaultValue=0.00";

    public static final String consume_total_money="columnName=consume_total_money,remarks=用户总消费金额,dataType=String,defaultValue=0.00";

    public static final String buy_num="columnName=buy_num,remarks=用户购买课时次数,dataType=int,defaultValue=0";

    public static final String new_regist="columnName=new_regist,remarks=新用户注册：0-否，1：是,dataType=int,defaultValue=0";

    public static final String post_time="columnName=post_time,remarks=,dataType=String,defaultValue=null";

}
