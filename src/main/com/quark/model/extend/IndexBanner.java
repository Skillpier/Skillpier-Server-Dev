package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2017-02-21 16:59:39
*/
public class IndexBanner extends Model<IndexBanner>{

    public static IndexBanner dao = new IndexBanner();

    public static final String index_banner_id="columnName=index_banner_id,remarks=banner,dataType=int,defaultValue=null";

    public static final String cover="columnName=cover,remarks=封面,dataType=String,defaultValue=";

    public static final String big_cover="columnName=big_cover,remarks=pc端的banner,dataType=String,defaultValue=";

    public static final String type="columnName=type,remarks=类型：1-引导页，2-首页banner,3-闪屏页,dataType=int,defaultValue=1";

    public static final String content="columnName=content,remarks=内容,dataType=String,defaultValue=null";

    public static final String post_time="columnName=post_time,remarks=上传时间,dataType=String,defaultValue=null";

    public static final String status="columnName=status,remarks=0-下线-1-上线,dataType=int,defaultValue=1";

}
