package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2017-02-21 16:59:35
*/
public class Catetory extends Model<Catetory>{

    public static Catetory dao = new Catetory();

    public static final String catetory_id="columnName=catetory_id,remarks=,dataType=int,defaultValue=null";

    public static final String name="columnName=name,remarks=分类名称,dataType=String,defaultValue=";

    public static final String sub_title="columnName=sub_title,remarks=副标题,dataType=String,defaultValue=";

    public static final String image_01="columnName=image_01,remarks=封面,dataType=String,defaultValue=";

    public static final String big_image_01="columnName=big_image_01,remarks=pc封面,dataType=String,defaultValue=";

    public static final String is_hot_recommand="columnName=is_hot_recommand,remarks=是否热门推荐：0-否，1-是,dataType=int,defaultValue=0";

    public static final String sort="columnName=sort,remarks=从小优先排序,dataType=int,defaultValue=0";

    public static final String post_time="columnName=post_time,remarks=,dataType=String,defaultValue=null";

    public static final String status="columnName=status,remarks=0-隐藏，1-显示,dataType=int,defaultValue=1";

}
