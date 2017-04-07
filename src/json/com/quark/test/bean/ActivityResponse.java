package com.quark.test.bean;

import java.util.ArrayList;
import java.util.List;

import com.quark.api.uitls.*;

public class ActivityResponse{
   //
   public List<Activity> list;
   //总记录数
   public int totalRow;

   public void setList(List<Activity> list){
     this.list = list;
   }
   public List<Activity> getList(){
     return this.list;
   }
   public void setTotalRow(int totalrow){
     this.totalRow = totalrow;
   }
   public int getTotalRow(){
     return this.totalRow;
   }
}