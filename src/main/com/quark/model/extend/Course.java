package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2017-02-21 16:59:38
*/
public class Course extends Model<Course>{

    public static Course dao = new Course();

    public static final String course_id="columnName=course_id,remarks=,dataType=int,defaultValue=null";

    public static final String user_id="columnName=user_id,remarks=发布者,dataType=String,defaultValue=";

    public static final String user_images_01="columnName=user_images_01,remarks=课程头像,dataType=String,defaultValue=null";

    public static final String title="columnName=title,remarks=课程标题,dataType=String,defaultValue=";

    public static final String category_01_id="columnName=category_01_id,remarks=第一大类Id,dataType=int,defaultValue=0";

    public static final String category_01_name="columnName=category_01_name,remarks=第一类名称,dataType=String,defaultValue=";

    public static final String category_02_id="columnName=category_02_id,remarks=第二大类Id,dataType=int,defaultValue=0";

    public static final String category_02_name="columnName=category_02_name,remarks=第二类名称,dataType=String,defaultValue=";

    public static final String catetory_id="columnName=catetory_id,remarks=0-不属于自定义分类，其他-属于自定义分类,dataType=int,defaultValue=0";

    public static final String catetory_name="columnName=catetory_name,remarks=自定义分类名称,dataType=String,defaultValue=";

    public static final String overview="columnName=overview,remarks=简介,dataType=String,defaultValue=null";

    public static final String session_length="columnName=session_length,remarks=课程时长60min  以60min为单位的,dataType=int,defaultValue=60";

    public static final String session_rate="columnName=session_rate,remarks=课程费用,dataType=String,defaultValue=0.00";

    public static final String teaching_age="columnName=teaching_age,remarks=教育年限【age 5 and up】,dataType=String,defaultValue=";

    public static final String teaching_since="columnName=teaching_since,remarks=开始教育时间【since Jun 2007】,dataType=String,defaultValue=";

    public static final String travel_to_session="columnName=travel_to_session,remarks=是否上门服务:1-是，0-否【如果选择no,下面的distance（距离）、trafic surcharge（交通费）变灰，无法操作】,dataType=int,defaultValue=0";

    public static final String travel_to_session_distance="columnName=travel_to_session_distance,remarks=上门服务距离【miles】【可以接受多大范围内的上门服务】,dataType=int,defaultValue=0";

    public static final String travel_to_session_distance_double="columnName=travel_to_session_distance_double,remarks=上门服务距离【米】【可以接受多大范围内的上门服务】,dataType=String,defaultValue=0.00";

    public static final String travel_to_session_trafic_surcharge="columnName=travel_to_session_trafic_surcharge,remarks=上门服务交通费,dataType=String,defaultValue=0.00";

    public static final String city="columnName=city,remarks=州【上课地址】,dataType=String,defaultValue=";

    public static final String area="columnName=area,remarks=城市【上课地址】,dataType=String,defaultValue=";

    public static final String street="columnName=street,remarks=街道【上课地址】,dataType=String,defaultValue=";

    public static final String address="columnName=address,remarks=详细地址【上课地址】,dataType=String,defaultValue=";

    public static final String zipcode="columnName=zipcode,remarks=城市编码,dataType=String,defaultValue=";

    public static final String latitude="columnName=latitude,remarks=维度【上课地址】,dataType=String,defaultValue=";

    public static final String longitude="columnName=longitude,remarks=经度【上课地址】,dataType=String,defaultValue=";

    public static final String additional_partner="columnName=additional_partner,remarks=额外的最多人【打折幅度】,dataType=int,defaultValue=0";

    public static final String surcharge_for_each="columnName=surcharge_for_each,remarks=每个人的附加费用【打折幅度】,dataType=String,defaultValue=0.00";

    public static final String discount_type="columnName=discount_type,remarks=打折类型：1-by_total,2-by_account,dataType=int,defaultValue=1";

    public static final String discount_onetion_pur_money_01="columnName=discount_onetion_pur_money_01,remarks=A:选择 By account的意思：一次性购买多少课程，打折多少B:选择 By total的意思：一次性购买多少钱，打折多少,dataType=String,defaultValue=0.00";

    public static final String discount_price_01="columnName=discount_price_01,remarks=打折价格,dataType=String,defaultValue=0.00";

    public static final String discount_onetion_pur_money_02="columnName=discount_onetion_pur_money_02,remarks=如果选中by total:One-time Purchase $XX,discount price $XX如果选中by account:One-time Purchase XX,discount price $XX,dataType=String,defaultValue=0.00";

    public static final String discount_price_02="columnName=discount_price_02,remarks=打折价格,dataType=String,defaultValue=0.00";

    public static final String discount_onetion_pur_money_03="columnName=discount_onetion_pur_money_03,remarks=,dataType=String,defaultValue=0.00";

    public static final String discount_price_03="columnName=discount_price_03,remarks=打折价格,dataType=String,defaultValue=0.00";

    public static final String banners="columnName=banners,remarks=banners【拼接如：11.jpg#22.jpg#11.jpg#22.jpg【图片名称#图片名称】】,dataType=String,defaultValue=";

    public static final String videos="columnName=videos,remarks=视频s【拼接如：http://#http://#http://#http://【url名称#url名称】】,dataType=String,defaultValue=";

    public static final String is_hot_recommand="columnName=is_hot_recommand,remarks=是否热门推荐：0-否，1-是,dataType=int,defaultValue=0";

    public static final String hot="columnName=hot,remarks=热度【每浏览一次加1】,dataType=int,defaultValue=0";

    public static final String status="columnName=status,remarks=-1-待提交，0-隐藏，1-显示，2-待审核,dataType=int,defaultValue=1";

    public static final String sort="columnName=sort,remarks=从小优先排序,dataType=int,defaultValue=0";

    public static final String total_score="columnName=total_score,remarks=评论总分【每次评价更新，最高分5分】,dataType=String,defaultValue=0.00";

    public static final String post_time="columnName=post_time,remarks=,dataType=String,defaultValue=null";

    public static final String hours="columnName=hours,remarks=上课时间段：【1@1,dataType=String,defaultValue=null";

    public static final String achievements="columnName=achievements,remarks=成果,dataType=String,defaultValue=null";

    public static final String specialist="columnName=specialist,remarks=擅长领域,dataType=String,defaultValue=null";

    public static final String is_frozen="columnName=is_frozen,remarks=是否教练被冻结：1-否，2-冻结,dataType=int,defaultValue=1";

    public static final String is_auth_public="columnName=is_auth_public,remarks=是否是认证发布的：1-否，2-是,dataType=int,defaultValue=1";

}
