/**
 * 
 */
package com.unionpay.acp.demo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.quark.common.config;
import com.unionpay.acp.sdk.SDKConfig;

/**
 * @author kingsley
 *
 */
public class UnionPayUtils extends DemoBase{
	

	private static boolean init = false;
	
	public static String createOrder(int orderId,double money){
		if(!init){
			//配置SDK配置
			SDKConfig.getConfig().setFrontRequestUrl("https://gateway.95516.com/gateway/api/frontTransReq.do");
			SDKConfig.getConfig().setAppRequestUrl("https://gateway.95516.com/gateway/api/appTransReq.do");
			SDKConfig.getConfig().setSingleQueryUrl("https://gateway.95516.com/gateway/api/queryTrans.do");
			SDKConfig.getConfig().setBatchTransUrl("https://gateway.95516.com/gateway/api/batchTrans.do");
			SDKConfig.getConfig().setFileTransUrl("https://filedownload.95516.com/");
			SDKConfig.getConfig().setSignCertPath("C:/cer/cer.pfx");
			SDKConfig.getConfig().setSignCertPwd("134712");
			SDKConfig.getConfig().setSignCertType("PKCS12");
			SDKConfig.getConfig().setEncryptCertPath("C:/cer/UPOP_VERIFY.cer");
			SDKConfig.getConfig().setValidateCertDir("C:/cer");
			SDKConfig.getConfig().setSignCertDir("C:/cer");
			init = true;
		}
		/**
		 * 组装请求报文
		 */
		Map<String, String> data = new HashMap<String, String>();
		// 版本号
		data.put("version", "5.0.0");
		// 字符集编码 默认"UTF-8"
		String encoding = "UTF-8";
		data.put("encoding", encoding);
		// 签名方法 01 RSA
		data.put("signMethod", "01");
		// 交易类型 01-消费
		data.put("txnType", "01");
		// 交易子类型 01:自助消费 02:订购 03:分期付款
		data.put("txnSubType", "01");
		// 业务类型 000202 B2B业务
		data.put("bizType", "000201");
		// 渠道类型 07-互联网渠道
		data.put("channelType", "07");
		// 商户/收单后台接收地址 必送
		data.put("backUrl", "http://"+config.pay_notify_host+"/app/Pays/unionPayAysn");
		// 接入类型:商户接入填0 0- 商户 ， 1： 收单， 2：平台商户
		data.put("accessType", "0");
		// 商户号码，
		data.put("merId", DemoBase.merId);
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		// 订单号 商户根据自己规则定义生成，每订单日期内不重复。8到40位数字字母
		data.put("orderId", sdf.format(dt)+orderId);
		// 订单发送时间 格式： YYYYMMDDhhmmss 商户发送交易时间，根据自己系统或平台生成
		data.put("txnTime", sdf.format(dt));
		// 交易金额 分
		Float txnAmt = new Float(100*money);
		data.put("txnAmt", ""+ txnAmt.intValue());
		// 交易币种
		data.put("currencyCode", "156");
		Map<String, String> submitFromData = signData(data);
		Map<String, String> resmap = submitUrl(submitFromData, "https://gateway.95516.com/gateway/api/appTransReq.do");
		// resmap 里返回报文中
		// 银联订单号 tn 商户推送订单后银联移动支付系统返回该流水号，商户调用支付控件时使用
		String tn = resmap.get("tn");
		return tn;
	}
}
