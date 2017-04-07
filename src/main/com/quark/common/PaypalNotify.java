package com.quark.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jpush.api.examples.PushExample;

import com.quark.app.bean.HttpRequest;
import com.quark.model.extend.ChargeLog;
import com.quark.model.extend.Comment;
import com.quark.model.extend.Orders;
import com.quark.utils.DateUtils;

//import com.brdinfo.framework.client.HTTPWeb;

/**
 * 网站支付
 * http://www.bozhiyue.com/anroid/boke/2016/0429/44776.html
 * @ClassName PaypalNotify
 * @author Administrator
 * @date 2016年4月11日 上午10:04:10
 * @Description TODO(这里用一句话描述这个类的作用)
 */
public class PaypalNotify extends HttpServlet{

    private static final long serialVersionUID = 1L;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        service(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        service(request, response);
    }

    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	logger.info("PaypalNotify>>>>>>>>>进入paypal后台支付通知");
        PrintWriter out = response.getWriter();
        try {
            // 从 PayPal 出读取 POST 信息同时添加变量?cmd?
            Enumeration en = request.getParameterNames();
            String str = "cmd=_notify-validate";
            int i=0;
            while(en.hasMoreElements()){
                String paramName = (String) en.nextElement();
                String paramValue = request.getParameter(paramName);
                str = str + "&" +paramName + "=" + URLEncoder.encode(paramValue,"UTF-8");
                i++;
                if (i>50) {
					break;//防止死循环，等待过久
				}
            }
            logger.info("paypal支付请求验证参数，验证是否来自paypal消息：" + str);
            // 将信息 POST 回给 PayPal 进行验证    测试环境先省略这一步
            //HTTPWEB是我自己的类 网上有很多HTTP请求的方法
            //String result = HttpRequest.sendPost("https://www.sandbox.paypal.com/cgi-bin/webscr", str);
            String result = HttpRequest.sendPost("https://www.paypal.com/cgi-bin/webscr", str);
            //String result = HTTPWeb.postLocalSSL(PayConfig.requestUrl, str);
            logger.info("paypal支付确认结果result="+result);

            // 将 POST 信息分配给本地变量，可以根据您的需要添加
            // 该付款明细所有变量可参考：
            // https://www.paypal.com/IntegrationCenter/ic_ipn-pdt-variable-reference.html
            String paymentStatus = request.getParameter("payment_status");
            //付款金额
            String paymentAmount = request.getParameter("mc_gross");
            //订单id
            String orderId = request.getParameter("invoice");
            if ("VERIFIED".equals(result)) { 
            	//测试环境-- Pending
            	//if("Pending".equals(paymentStatus)){
            	//生产环境--Completed
            	if("Completed".equals(paymentStatus)){
                      //根据项目实际需要处理其业务逻辑begin
	            	  if (!orderId.equals("")) {
	            			String orders_id = orderId.substring(13, orderId.length());
	            			System.out.println("orders_id=="+orders_id);
	            			try {
								if (OrderUtils.Dall( orders_id,"P"+DateUtils.getTimeStampNo() )) {
									Orders orders = Orders.dao.findById(orders_id);
									int order_user_id = orders.get(orders.user_id);
									int course_user_id = orders.get(orders.course_user_id);
									String buy_nickname = orders.getStr(orders.buy_nickname);
									String course_title = orders.getStr(orders.course_title);
									String order_number = orders.getStr(orders.order_number);
									ChargeLog chargeLog = ChargeLog.dao.findFirst("select * from charge_log where user_id=? and orders_id=? and is_pay=0",order_user_id,orders_id);
									if (chargeLog!=null) {
										chargeLog.set(chargeLog.is_pay, 1).update();
									}
									//推送--系统消息
									Comment comment = new Comment();
									boolean save = comment.set(comment.public_course_user_id, course_user_id)
										   .set(comment.note, buy_nickname+" has purchased your course "+course_title+" , order ID："+order_number)
										   .set(comment.user_id, 0)
										   .set(comment.type, 2)
										   .set(comment.post_time, DateUtils.getCurrentDateTime())
										   .set(comment.post_date, DateUtils.getCurrentDate())
										   .set(comment.status, 1)
										   .save();
									if (save) {
										PushExample.pushToUser(course_user_id+"", buy_nickname+" has purchased your course "+course_title+" , order ID："+order_number);
									}
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	            		}
                	/////////////////////////end////////
                }else{
                	logger.info("Completed参数：" + str);     
                }

           }else if ("INVALID".equals(result)) {
                logger.info("paypal完成支付发送IPN通知返回状态非法，请联系管理员，请求参数：" + str);     
                out.println("confirmError");   
           } else {
                logger.info("paypal完成支付发送IPN通知发生其他异常，请联系管理员，请求参数：" + str);  
                out.println("confirmError");   
           }
        } catch (IOException e){
            logger.info("确认付款信息发生IO异常" + e.getMessage());
            out.println("confirmError");   
        }
        out.flush();
        out.close();        
    }
}