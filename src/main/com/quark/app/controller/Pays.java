/**
 * 
 */
package com.quark.app.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.authorize.Environment;
import net.authorize.api.contract.v1.CreateTransactionRequest;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import net.authorize.api.contract.v1.CreditCardType;
import net.authorize.api.contract.v1.MerchantAuthenticationType;
import net.authorize.api.contract.v1.MessageTypeEnum;
import net.authorize.api.contract.v1.OpaqueDataType;
import net.authorize.api.contract.v1.PaymentType;
import net.authorize.api.contract.v1.TransactionRequestType;
import net.authorize.api.contract.v1.TransactionResponse;
import net.authorize.api.contract.v1.TransactionTypeEnum;
import net.authorize.api.controller.CreateTransactionController;
import net.authorize.api.controller.base.ApiOperationBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jpush.api.examples.PushExample;

import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipayNotify;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import com.quark.api.annotation.Author;
import com.quark.api.annotation.DataType;
import com.quark.api.annotation.Explaination;
import com.quark.api.annotation.ReturnDBParam;
import com.quark.api.annotation.ReturnJson;
import com.quark.api.annotation.ReturnOutlet;
import com.quark.api.annotation.Rp;
import com.quark.api.annotation.Type;
import com.quark.api.annotation.URLParam;
import com.quark.api.annotation.UpdateLog;
import com.quark.api.annotation.Value;
import com.quark.api.auto.bean.ResponseValues;
import com.quark.app.bean.HttpRequest;
import com.quark.app.logs.AppLog;
import com.quark.common.AppData;
import com.quark.common.OrderUtils;
import com.quark.common.config;
import com.quark.interceptor.AppToken;
import com.quark.model.extend.ChargeLog;
import com.quark.model.extend.Collection;
import com.quark.model.extend.Comment;
import com.quark.model.extend.Orders;
import com.quark.model.extend.Tokens;
import com.quark.model.extend.User;
import com.quark.paypal.api.payments.Payment;
import com.quark.paypal.base.rest.APIContext;
import com.quark.paypal.base.rest.OAuthTokenCredential;
import com.quark.utils.DateUtils;
import com.quark.utils.MD5Util;
import com.sun.jmx.snmp.Timestamp;
import com.tenpay.ResponseHandler;
import com.tenpay.util.ConstantUtil;
import com.unionpay.acp.demo.BackRcvResponse;
import com.unionpay.acp.demo.FrontRcvResponse;
import com.unionpay.acp.sdk.SDKConstants;
import com.unionpay.acp.sdk.SDKUtil;


/**
 * @author C罗
 *
 */
public class Pays extends Controller{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	 
	@Author("C罗")
	@Rp("支付")
	@Explaination(info = "paypal校验verify payments")
	@UpdateLog(date = "2015-10-28 11:12", log = "初次添加")
	@URLParam(defaultValue = "", explain = "订单id", type = Type.String, name = Orders.orders_id)
	@URLParam(defaultValue = "", explain = "app发起支付时paypal返回的 payment id", type = Type.String, name = "paypal_response_id")
	@ReturnOutlet(name = "VerifyPaypalPaymentsResponse{message}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "VerifyPaypalPaymentsResponse{status}", remarks = "1-操作成功", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "VerifyPaypalPaymentsResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void verifyPaypalPayments () throws Exception {
		try {
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			String orders_id = getPara("orders_id","");
			String paypal_response_id = getPara("paypal_response_id");
			
			int status= 0;String message = "网络出错，请检查";
			Map<String, String> sdkConfig = new HashMap<String, String>();
			//sdkConfig.put("mode", "live");
			sdkConfig.put("mode", "sandbox");
			String accessToken = new OAuthTokenCredential(config.paypal_client_id_sandbox, config.paypal_secret_sandbox, sdkConfig).getAccessToken();
			APIContext apiContext = new APIContext(accessToken);
			apiContext.setConfigurationMap(sdkConfig);
			try {
				Payment verifyPayment = Payment.get(apiContext,paypal_response_id);
				String verifyPaymentState = verifyPayment.getState();
				System.out.println(verifyPayment+"verifyPaymentState====="+verifyPaymentState);
				if ("approved".equals(verifyPaymentState)) { 
					//根据项目实际需要处理其业务逻辑begin
	            	  if (!orders_id.equals("")) {
	            			System.out.println("orders_id=="+orders_id);
	            			try {
								if (OrderUtils.Dall( orders_id,"P"+paypal_response_id)) {
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
										   .set(comment.note, buy_nickname+" has purchased your course "+course_title+" ，order ID："+order_number)
										   .set(comment.type, 2)
										   .set(comment.user_id, 0)
										   .set(comment.post_time, DateUtils.getCurrentDateTime())
										   .set(comment.post_date, DateUtils.getCurrentDate())
										   .set(comment.status, 1)
										   .save();
									if (save) {
										PushExample.pushToUser(course_user_id+"", buy_nickname+" has purchased your course  "+course_title+" ，order ID："+order_number);
									}
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	            		}
	            	/////////////////////////end////////
					status= 1;
					message = "校验成功";
				}else {
					status= 2;
					message = "校验失败";
				}
			} catch (Exception e) {
				status= 2;
				message = "校验失败";
			}
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("VerifyPaypalPaymentsResponse", responseValues);
			renderMultiJson("VerifyPaypalPaymentsResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("Pays/verifyPaypalPayments", "", this);
		}
	}
	/**
	 * http://developer.authorize.net/api/reference/index.html#mobile-inapp-transactions-create-an-accept-transaction
	 * @throws Exception
	 */
	@Author("C罗")
	@Rp("支付")
	@Explaination(info = "Visa校验verify payments【app】")
	@UpdateLog(date = "2015-10-28 11:12", log = "初次添加")
	@URLParam(defaultValue = "", explain = "多订单ID同时支付【11A22A1A23】A分割，支付一个就传一个值", type = Type.String, name = "orders_ids")
	@URLParam(defaultValue = "", explain = "支付金额", type = Type.String, name = "amount")
	@URLParam(defaultValue = "", explain = "app发起支付时visa返回的 dataValue", type = Type.String, name = "dataValue")
	@URLParam(defaultValue = "", explain = "app发起支付时visa返回的 dataDescriptor", type = Type.String, name = "dataDescriptor")
	@ReturnOutlet(name = "VerifyVisaPaymentsResponse{message}", remarks = "", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "VerifyVisaPaymentsResponse{status}", remarks = "1-支付成功，2-支付失败", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "VerifyVisaPaymentsResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void verifyVisaPayments () throws Exception {
		try {
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			String amount = getPara("amount","0");
			String orders_ids = getPara("orders_ids","");
			String dataValue = getPara("dataValue");
			String dataDescriptor = getPara("dataDescriptor","COMMON.ACCEPT.INAPP.PAYMENT");
			int status = 0; String message = "Order's Id Error";
			if (!orders_ids.equals("")) {
				//////begin//////////
				//ApiOperationBase.setEnvironment(Environment.SANDBOX);
		        ApiOperationBase.setEnvironment(Environment.PRODUCTION);
		        // Giving the merchant authentication information
		        MerchantAuthenticationType merchantAuthenticationType  = new MerchantAuthenticationType() ;
		        merchantAuthenticationType.setName(config.apiLoginId);
		        merchantAuthenticationType.setTransactionKey(config.transactionKey);
		        ApiOperationBase.setMerchantAuthentication(merchantAuthenticationType);
		        // Setting the payment
		        OpaqueDataType op = new OpaqueDataType();
		        op.setDataDescriptor(dataDescriptor);
		        op.setDataValue(dataValue);
		        PaymentType paymentOne = new PaymentType();
		        paymentOne.setOpaqueData(op);
		        // Setting the transaction
		        TransactionRequestType transactionRequest = new TransactionRequestType();
		        transactionRequest.setAmount(new BigDecimal(amount));
		        transactionRequest.setTransactionType(TransactionTypeEnum.AUTH_CAPTURE_TRANSACTION.value());
		        transactionRequest.setPayment(paymentOne);
		        // Making the api request
		        CreateTransactionRequest apiRequest = new CreateTransactionRequest();
		        apiRequest.setTransactionRequest(transactionRequest);
		        // Creating the controller
		        CreateTransactionController controller = new CreateTransactionController(apiRequest);
		        controller.execute();
		        // Getting the response
		        CreateTransactionResponse response = controller.getApiResponse();
		        if (response!=null) {
		        	// If API Response is ok, go ahead and check the transaction response
		        	if (response.getMessages().getResultCode() == MessageTypeEnum.OK) {
		        		TransactionResponse result = response.getTransactionResponse();
		        		if(result.getMessages() != null){
		        			System.out.println("Successfully created transaction with Transaction ID: " + result.getTransId());
		        			System.out.println("Response Code: " + result.getResponseCode());
		        			System.out.println("Message Code: " + result.getMessages().getMessage().get(0).getCode());
		        			System.out.println("Description: " + result.getMessages().getMessage().get(0).getDescription());
		        			System.out.println("Auth code : " + result.getAuthCode());
		        			status = 1;message = "Successfully";
							//根据项目实际需要处理其业务逻辑begin
	            			System.out.println("orders_id=="+orders_ids);
	            			try {
								if (OrderUtils.Dall( orders_ids,"V"+result.getTransId() )) {
									String[]orders_id_array = orders_ids.split("A");
									Orders orders = Orders.dao.findById(orders_id_array[0]);
									int order_user_id = orders.get(orders.user_id);
									int course_user_id = orders.get(orders.course_user_id);
									String buy_nickname = orders.getStr(orders.buy_nickname);
									String course_title = orders.getStr(orders.course_title);
									String order_number = orders.getStr(orders.order_number);
									ChargeLog chargeLog = ChargeLog.dao.findFirst("select * from charge_log where user_id=? and orders_id=? and is_pay=0",order_user_id,orders_id_array[0]);
									if (chargeLog!=null) {
										chargeLog.set(chargeLog.is_pay, 1).update();
									}
									//推送--系统消息 
									Comment comment = new Comment();
									boolean save = comment.set(comment.public_course_user_id, course_user_id)
										   .set(comment.note, buy_nickname+" has purchased your course  "+course_title+" , order ID："+order_number)
										   .set(comment.type, 2)
										   .set(comment.user_id, 0)
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
			            	/////////////////////////end////////
		        		}
		        		else {
		        			System.out.println("Failed Transaction.");
		        			if(response.getTransactionResponse().getErrors() != null){
		        				System.out.println("Error Code: " + response.getTransactionResponse().getErrors().getError().get(0).getErrorCode());
		        				System.out.println("Error message: " + response.getTransactionResponse().getErrors().getError().get(0).getErrorText());
		        				status = 2;message = response.getTransactionResponse().getErrors().getError().get(0).getErrorText();
		        			}
		        		}
		        	}
		        	else {
		        		System.out.println("Failed Transaction.");
		        		if(response.getTransactionResponse() != null && response.getTransactionResponse().getErrors() != null){
		        			System.out.println("Error Code: " + response.getTransactionResponse().getErrors().getError().get(0).getErrorCode());
		        			System.out.println("Error message: " + response.getTransactionResponse().getErrors().getError().get(0).getErrorText());
		        			status = 2;message = response.getTransactionResponse().getErrors().getError().get(0).getErrorText();
		        		}
		        		else {
		        			System.out.println("Error Code: " + response.getMessages().getMessage().get(0).getCode());
		        			System.out.println("Error message: " + response.getMessages().getMessage().get(0).getText());
		        			status = 2;message = response.getMessages().getMessage().get(0).getText();
		        		}
		        	}
		        }
		        else {
		        	System.out.println("Null Response.");
		        	status = 2;message = "Null Response.";
		        }
				//return response;
			}
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("VerifyVisaPaymentsResponse", responseValues);
			renderMultiJson("VerifyVisaPaymentsResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("Pays/verifyVisaPayments", "", this);
		}
	}
	/**
	 * http://developer.authorize.net/hello_world/
	 * @throws Exception
	 */
	@Author("C罗")
	@Rp("支付")
	@Explaination(info = "VisaWeb校验verify payments【网站】")
	@UpdateLog(date = "2015-10-28 11:12", log = "初次添加")
	@URLParam(defaultValue = "", explain = "多订单ID同时支付【11A22A1A23】A分割，支付一个就传一个值", type = Type.String, name = "orders_ids")
	@URLParam(defaultValue = "", explain = "支付金额", type = Type.String, name = "amount")
	@URLParam(defaultValue = "", explain = "信用卡号cardNumber：4111111111111111", type = Type.String, name = "cardNumber")
	@URLParam(defaultValue = "", explain = "信用卡月年如1130：11月30年expirationDate", type = Type.String, name = "expirationDate")
	@URLParam(defaultValue = "", explain = "信用卡月年如", type = Type.String, name = ChargeLog.first_name)
	@URLParam(defaultValue = "", explain = "信用卡月年如", type = Type.String, name = ChargeLog.last_name)
	@URLParam(defaultValue = "", explain = "信用卡月年如", type = Type.String, name = ChargeLog.street)
	@URLParam(defaultValue = "", explain = "信用卡月年如", type = Type.String, name = ChargeLog.country)
	@URLParam(defaultValue = "", explain = "信用卡月年如", type = Type.String, name = ChargeLog.city)
	@URLParam(defaultValue = "", explain = "信用卡月年如", type = Type.String, name = ChargeLog.v_state)
	@URLParam(defaultValue = "", explain = "信用卡月年如", type = Type.String, name = ChargeLog.v_code)
	@URLParam(defaultValue = "", explain = "信用卡月年如", type = Type.String, name = ChargeLog.v_phone)
	@ReturnOutlet(name = "VerifyVisaWebPaymentsResponse{message}", remarks = "", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "VerifyVisaWebPaymentsResponse{status}", remarks = "1-支付成功，2-支付失败", dataType = DataType.Int, defaultValue = "")
	@ReturnOutlet(name = "VerifyVisaWebPaymentsResponse{code}", remarks = "200-正常返回，405-重新登陆", dataType = DataType.Int, defaultValue = "")
	public void verifyVisaWebPayments () throws Exception {
		try {
			ResponseValues responseValues = new ResponseValues(this,
					Thread.currentThread().getStackTrace()[1].getMethodName());
			String amount_str = getPara("amount","0");
			String orders_ids = getPara("orders_ids","");
			String cardNumber = getPara("cardNumber");
			String expirationDate = getPara("expirationDate","0822");
			
			String first_name = getPara("first_name","");
			String last_name = getPara("last_name","");
			String street = getPara("street","");
			String country = getPara("country","");
			String city = getPara("city","");
			String v_state = getPara("v_state","");
			String v_code = getPara("v_code","");
			String v_phone = getPara("v_phone","");
			
			int status = 0; String message = "Order's Id Error";
			double amount = Double.parseDouble(amount_str);
			if (!orders_ids.equals("")) {
			//////begin//////////
		        //Common code to set for all requests
				//ApiOperationBase.setEnvironment(Environment.SANDBOX);
		        ApiOperationBase.setEnvironment(Environment.PRODUCTION);

		        MerchantAuthenticationType merchantAuthenticationType  = new MerchantAuthenticationType() ;
		        merchantAuthenticationType.setName(config.apiLoginId);
		        merchantAuthenticationType.setTransactionKey(config.transactionKey);
		        ApiOperationBase.setMerchantAuthentication(merchantAuthenticationType);

		        // Populate the payment data
		        PaymentType paymentType = new PaymentType();
		        CreditCardType creditCard = new CreditCardType();
		        creditCard.setCardNumber(cardNumber);
		        creditCard.setExpirationDate(expirationDate);
		        paymentType.setCreditCard(creditCard);

		        // Create the payment transaction request
		        TransactionRequestType txnRequest = new TransactionRequestType();
		        txnRequest.setTransactionType(TransactionTypeEnum.AUTH_CAPTURE_TRANSACTION.value());
		        txnRequest.setPayment(paymentType);
		        txnRequest.setAmount(new BigDecimal(amount).setScale(2, RoundingMode.CEILING));

		        // Make the API Request
		        CreateTransactionRequest apiRequest = new CreateTransactionRequest();
		        apiRequest.setTransactionRequest(txnRequest);
		        CreateTransactionController controller = new CreateTransactionController(apiRequest);
		        controller.execute();


		        CreateTransactionResponse response = controller.getApiResponse();

		        if (response!=null) {
		        	// If API Response is ok, go ahead and check the transaction response
		        	if (response.getMessages().getResultCode() == MessageTypeEnum.OK) {
		        		TransactionResponse result = response.getTransactionResponse();
		        		if(result.getMessages() != null){
		        			System.out.println("Successfully created transaction with Transaction ID: " + result.getTransId());
		        			System.out.println("Response Code: " + result.getResponseCode());
		        			System.out.println("Message Code: " + result.getMessages().getMessage().get(0).getCode());
		        			System.out.println("Description: " + result.getMessages().getMessage().get(0).getDescription());
		        			System.out.println("Auth Code: " + result.getAuthCode());
		        			status = 1;message = "Successfully";
							//根据项目实际需要处理其业务逻辑begin
	            			System.out.println("orders_id=="+orders_ids);
	            			try {
								if (OrderUtils.Dall( orders_ids,"V"+result.getTransId() )) {
									String[]orders_id_array = orders_ids.split("A");
									Orders orders = Orders.dao.findById(orders_id_array[0]);
									int order_user_id = orders.get(orders.user_id);
									int course_user_id = orders.get(orders.course_user_id);
									String buy_nickname = orders.getStr(orders.buy_nickname);
									String course_title = orders.getStr(orders.course_title);
									String order_number = orders.getStr(orders.order_number);
									ChargeLog chargeLog = ChargeLog.dao.findFirst("select * from charge_log where user_id=? and orders_id=? and is_pay=0",order_user_id,orders_id_array[0]);
									if (chargeLog!=null) {
										chargeLog.set(chargeLog.is_pay, 1)
												.set(chargeLog.first_name, first_name)
												.set(chargeLog.last_name, last_name)
												.set(chargeLog.street, street)
												.set(chargeLog.country, country)
												.set(chargeLog.city, city)
												.set(chargeLog.v_state, v_state)
												.set(chargeLog.v_code, v_code)
												.set(chargeLog.v_phone, v_phone)
												.update();
									}
									//推送--系统消息
									Comment comment = new Comment();
									boolean save = comment.set(comment.public_course_user_id, course_user_id)
										   .set(comment.note, buy_nickname+" has purchased your course "+course_title+" , order ID："+order_number)
										   .set(comment.type, 2)
										   .set(comment.user_id, 0)
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
			            	/////////////////////////end////////
		        		}
		        		else {
		        			System.out.println("Failed Transaction.");
		        			if(response.getTransactionResponse().getErrors() != null){
		        				System.out.println("Error Code: " + response.getTransactionResponse().getErrors().getError().get(0).getErrorCode());
		        				System.out.println("Error message: " + response.getTransactionResponse().getErrors().getError().get(0).getErrorText());
		        				status = 2;message = response.getTransactionResponse().getErrors().getError().get(0).getErrorText();
		        			}
		        		}
		        	}
		        	else {
		        		System.out.println("Failed Transaction.");
		        		if(response.getTransactionResponse() != null && response.getTransactionResponse().getErrors() != null){
		        			System.out.println("Error Code: " + response.getTransactionResponse().getErrors().getError().get(0).getErrorCode());
		        			System.out.println("Error message: " + response.getTransactionResponse().getErrors().getError().get(0).getErrorText());
		        			status = 2;message = response.getTransactionResponse().getErrors().getError().get(0).getErrorText();
		        		}
		        		else {
		        			System.out.println("Error Code: " + response.getMessages().getMessage().get(0).getCode());
		        			System.out.println("Error message: " + response.getMessages().getMessage().get(0).getText());
		        			status = 2;message = response.getMessages().getMessage().get(0).getText();
		        		}
		        	}
		        }else {
		        	status = 2;
		        	System.out.println("Null Response.");
		        	message = "expirationDate' element is invalid";
		        }
				//return response;
			}
			responseValues.put("status", status);
			responseValues.put("code", 200);
			responseValues.put("message", message);
			setAttr("VerifyVisaWebPaymentsResponse", responseValues);
			renderMultiJson("VerifyVisaWebPaymentsResponse");
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.error(e, getRequest());
		} finally {
			AppData.analyze("Pays/verifyVisaWebPayments", "", this);
		}
	}
	
}
