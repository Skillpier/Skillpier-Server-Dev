package com.quark.test;

import java.util.ArrayList;
import java.util.List;
import com.quark.api.auto.bean.*;

/**
 * @author kingsley
 * @copyright quarktimes.com
 * @datetime 2015-03-27 17:40:49
 *
 */
public class GetForgetPasswordCodeRequest{
   public String url = "/app/UserCenter/getForgetPasswordCode";
   public String method = "get";
   private String telephone;//手机号码
   public void setUrl(String url){this.url = url;}
public String getUrl(){return this.url;}
public void setMethod(String method){this.method = method;}
public String getMethod(){return this.method;}

   public void setTelephone(String telephone){
     this.telephone = telephone;
   }
   public String getTelephone(){
     return this.telephone;
   }

}
