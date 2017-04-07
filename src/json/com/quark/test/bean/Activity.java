/**
 * 
 */
package com.quark.test.bean;

/**
 * @author kingsley
 *
 */
import java.util.ArrayList;
import java.util.List;

import com.quark.api.uitls.*;

public class Activity{
   //兼职类型：全部，派发，促销，家教，服务员，礼仪，安保人员，模特，主持，翻译，其它
   public String type;

   public void setType(String type){
     this.type = type;
   }
   public String getType(){
     return this.type;
   }
@Override
public String toString() {
	return "Activity [type=" + type + "]";
}
   
}