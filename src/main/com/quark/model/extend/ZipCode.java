package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2017-02-21 16:59:42
*/
public class ZipCode extends Model<ZipCode>{

    public static ZipCode dao = new ZipCode();

    public static final String zip_code_id="columnName=zip_code_id,remarks=,dataType=int,defaultValue=null";

    public static final String zipcode="columnName=zipcode,remarks=邮编,dataType=String,defaultValue=null";

    public static final String city="columnName=city,remarks=城市(city),dataType=String,defaultValue=null";

    public static final String county="columnName=county,remarks=state　州（state),dataType=String,defaultValue=null";

    public static final String state="columnName=state,remarks=state,dataType=String,defaultValue=null";

}
