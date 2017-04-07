package com.quark.model.extend;
import com.jfinal.plugin.activerecord.Model;

/**
* @author cluo
* 
* @info 
*
* @datetime2017-02-21 16:59:34
*/
public class AdminUser extends Model<AdminUser>{

    public static AdminUser dao = new AdminUser();

    public static final String id="columnName=id,remarks=,dataType=int,defaultValue=null";

    public static final String username="columnName=username,remarks=,dataType=String,defaultValue=null";

    public static final String password="columnName=password,remarks=,dataType=String,defaultValue=null";

    public static final String role="columnName=role,remarks=,dataType=String,defaultValue=null";

    public static final String email="columnName=email,remarks=,dataType=String,defaultValue=null";

}
