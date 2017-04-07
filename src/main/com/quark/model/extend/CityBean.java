package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2017-02-21 16:59:36
*/
public class CityBean extends Model<CityBean>{

    public static CityBean dao = new CityBean();

    public static final String city_bean_id="columnName=city_bean_id,remarks=,dataType=int,defaultValue=null";

    public static final String county="columnName=county,remarks=,dataType=String,defaultValue=";

    public static final String city="columnName=city,remarks=,dataType=String,defaultValue=";

    public static final String zipcode="columnName=zipcode,remarks=,dataType=String,defaultValue=";

    public static final String longitude="columnName=longitude,remarks=,dataType=String,defaultValue=";

    public static final String zipClass="columnName=zipClass,remarks=,dataType=String,defaultValue=";

    public static final String state="columnName=state,remarks=,dataType=String,defaultValue=";

    public static final String latitude="columnName=latitude,remarks=,dataType=String,defaultValue=null";

}
