package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2017-02-21 16:59:36
*/
public class Collection extends Model<Collection>{

    public static Collection dao = new Collection();

    public static final String collection_id="columnName=collection_id,remarks=,dataType=int,defaultValue=null";

    public static final String user_id="columnName=user_id,remarks=收藏者id,dataType=String,defaultValue=";

    public static final String course_id="columnName=course_id,remarks=,dataType=int,defaultValue=0";

    public static final String post_time="columnName=post_time,remarks=记录日期,dataType=String,defaultValue=null";

}
