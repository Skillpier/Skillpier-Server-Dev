package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2017-02-21 16:59:38
*/
public class CourseCertification extends Model<CourseCertification>{

    public static CourseCertification dao = new CourseCertification();

    public static final String course_certification_id="columnName=course_certification_id,remarks=,dataType=int,defaultValue=null";

    public static final String course_id="columnName=course_id,remarks=课程id,dataType=int,defaultValue=0";

    public static final String name="columnName=name,remarks=证书名称,dataType=String,defaultValue=";

    public static final String get_time="columnName=get_time,remarks=获取时间,dataType=String,defaultValue=";

    public static final String institue="columnName=institue,remarks=机构名称,dataType=String,defaultValue=";

    public static final String image_01="columnName=image_01,remarks=图片,dataType=String,defaultValue=";

    public static final String post_time="columnName=post_time,remarks=上传时间,dataType=String,defaultValue=null";

    public static final String status="columnName=status,remarks=0-隐藏，1-显示,dataType=int,defaultValue=1";

}
