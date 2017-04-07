package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2017-02-21 16:59:41
*/
public class Tokens extends Model<Tokens>{

    public static Tokens dao = new Tokens();

    public static final String token_id="columnName=token_id,remarks=,dataType=int,defaultValue=null";

    public static final String user_id="columnName=user_id,remarks=用户ID,dataType=int,defaultValue=null";

    public static final String token="columnName=token,remarks=token,dataType=String,defaultValue=";

    public static final String pc_token="columnName=pc_token,remarks=pc_token,dataType=String,defaultValue=";

    public static final String post_time="columnName=post_time,remarks=登陆日期,dataType=String,defaultValue=null";

}
