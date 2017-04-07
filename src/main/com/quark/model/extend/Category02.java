package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2017-02-21 16:59:35
*/
public class Category02 extends Model<Category02>{

    public static Category02 dao = new Category02();

    public static final String category_02_id="columnName=category_02_id,remarks=,dataType=int,defaultValue=null";

    public static final String category_01_id="columnName=category_01_id,remarks=,dataType=int,defaultValue=0";

    public static final String category_01_name="columnName=category_01_name,remarks=一级名称,dataType=String,defaultValue=";

    public static final String category_02_name="columnName=category_02_name,remarks=二级名称,dataType=String,defaultValue=";

    public static final String category_describe="columnName=category_describe,remarks=描述,dataType=String,defaultValue=null";

    public static final String image_01="columnName=image_01,remarks=封面,dataType=String,defaultValue=";

    public static final String big_image_01="columnName=big_image_01,remarks=pc封面,dataType=String,defaultValue=";

    public static final String sort="columnName=sort,remarks=从小优先排序,dataType=int,defaultValue=0";

    public static final String post_time="columnName=post_time,remarks=,dataType=String,defaultValue=null";

    public static final String status="columnName=status,remarks=0-隐藏，1-显示,dataType=int,defaultValue=1";

}
