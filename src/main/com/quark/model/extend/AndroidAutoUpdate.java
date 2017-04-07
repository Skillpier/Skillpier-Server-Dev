package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2017-02-21 16:59:34
*/
public class AndroidAutoUpdate extends Model<AndroidAutoUpdate>{

    public static AndroidAutoUpdate dao = new AndroidAutoUpdate();

    public static final String android_auto_update_id="columnName=android_auto_update_id,remarks=,dataType=int,defaultValue=null";

    public static final String new_versionCode="columnName=new_versionCode,remarks=最新版本号,dataType=int,defaultValue=0";

    public static final String out_versionCode="columnName=out_versionCode,remarks=展示版本号,dataType=String,defaultValue=";

    public static final String update_text="columnName=update_text,remarks=更新内容,dataType=String,defaultValue=null";

    public static final String apk_name="columnName=apk_name,remarks=,dataType=String,defaultValue=null";

    public static final String apk_size="columnName=apk_size,remarks=APK 大小,dataType=String,defaultValue=";

    public static final String post_time="columnName=post_time,remarks=,dataType=String,defaultValue=null";

    public static final String status="columnName=status,remarks=状态 2、有新版本 3、强制更新,dataType=int,defaultValue=2";

    public static final String type="columnName=type,remarks=1-客户端，2-商家端,dataType=int,defaultValue=1";

}
