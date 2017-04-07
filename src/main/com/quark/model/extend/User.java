package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2017-02-21 16:59:42
*/
public class User extends Model<User>{

    public static User dao = new User();

    public static final String user_id="columnName=user_id,remarks=,dataType=int,defaultValue=null";

    public static final String email="columnName=email,remarks=邮件登陆,dataType=String,defaultValue=";

    public static final String password="columnName=password,remarks=密码,dataType=String,defaultValue=";

    public static final String nickname="columnName=nickname,remarks=昵称,dataType=String,defaultValue=";

    public static final String image_01="columnName=image_01,remarks=用户头像,dataType=String,defaultValue=";

    public static final String sex="columnName=sex,remarks=性别：0-女，1-男，2-保密,dataType=int,defaultValue=2";

    public static final String money="columnName=money,remarks=钱,dataType=String,defaultValue=0.00";

    public static final String frozen_money="columnName=frozen_money,remarks=冻结金额【待体现冻结】,dataType=String,defaultValue=0.00";

    public static final String apply_money="columnName=apply_money,remarks=已提现金额,dataType=String,defaultValue=0.00";

    public static final String province="columnName=province,remarks=省,dataType=String,defaultValue=";

    public static final String city="columnName=city,remarks=市,dataType=String,defaultValue=";

    public static final String area="columnName=area,remarks=区,dataType=String,defaultValue=";

    public static final String birthday="columnName=birthday,remarks=,dataType=String,defaultValue=";

    public static final String age="columnName=age,remarks=年龄,dataType=int,defaultValue=0";

    public static final String telephone="columnName=telephone,remarks=电话号码,dataType=String,defaultValue=";

    public static final String cover_ID_01="columnName=cover_ID_01,remarks=身份证正面,dataType=String,defaultValue=";

    public static final String cover_ID_02="columnName=cover_ID_02,remarks=身份证反面,dataType=String,defaultValue=";

    public static final String is_official="columnName=is_official,remarks=是否官方认证：0-否，1-是,dataType=int,defaultValue=0";

    public static final String coupon_change_max_num="columnName=coupon_change_max_num,remarks=最多领取优惠券次数,dataType=int,defaultValue=0";

    public static final String last_login_time="columnName=last_login_time,remarks=最后登陆时间,dataType=String,defaultValue=null";

    public static final String post_time="columnName=post_time,remarks=日期,dataType=String,defaultValue=null";

    public static final String last_read_time="columnName=last_read_time,remarks=最后一次访问系统消息的时间,dataType=String,defaultValue=2015-11-11 11:10:10";

    public static final String status="columnName=status,remarks=0-封号，1-正常，2-未激活,dataType=int,defaultValue=1";

    public static final String agent_level="columnName=agent_level,remarks=1-普通用户，2-教练,dataType=int,defaultValue=1";

    public static final String authen_status="columnName=authen_status,remarks=教练审核：0-未认证，1-审核中，2-审核通过，3-审核不通过,dataType=int,defaultValue=0";

    public static final String authen_num="columnName=authen_num,remarks=最多申请认证三次,dataType=int,defaultValue=3";

    public static final String authen_time="columnName=authen_time,remarks=认证日期,dataType=String,defaultValue=null";

    public static final String authen_nickname="columnName=authen_nickname,remarks=认证昵称,dataType=String,defaultValue=";

    public static final String is_stop_course="columnName=is_stop_course,remarks=今后暂停课程 0-否，1-是,dataType=int,defaultValue=0";

    public static final String stop_course_time="columnName=stop_course_time,remarks=最后停课时间,dataType=String,defaultValue=null";

    public static final String category_name="columnName=category_name,remarks=分类名称,dataType=String,defaultValue=";

    public static final String experiences="columnName=experiences,remarks=经验是1年 -15年,dataType=String,defaultValue=";

    public static final String setting_notify_start_course="columnName=setting_notify_start_course,remarks=1-开启开课前通知，0-关闭,dataType=int,defaultValue=0";

    public static final String is_third="columnName=is_third,remarks=是否第三方：0-正常注册{可修改密码}，1-第三方注册{不可修改密码}，2-已绑定{可修改密码},dataType=int,defaultValue=0";

}
