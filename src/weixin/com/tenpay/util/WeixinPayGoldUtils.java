package com.tenpay.util;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;

import com.jfinal.kit.HttpKit;
import com.quark.api.auto.bean.ResponseValues;
import com.quark.common.config;
import com.quark.utils.StringMD5;
import com.tenpay.util.Sha1Util;
import com.tenpay.util.WXUtil;

/**
 * app
 * @author Administrator
 *
 */
public class WeixinPayGoldUtils {

	private static final String appid = "wx8fdf71df86bedbd4";
	private static final String mch_id = "1311632601";
	private static final String notify_url = "http://"+config.pay_notify_host+"/app/PaysGold/WeixinAsynNotify";
	private static final String trade_type = "APP";
	private static final String APIKey = "sweetheartonliesweetheartonlie12";


	/**
	 * appid:公众账号ID mch_id:商户号 nonce_str:随机字符串 APIKey:api密钥 out_trade_no:商户订单号
	 * total_fee:总金额 notify_url:通知地址
	 * 
	 */
	public static ResponseValues createOrder(String body, String attach, String out_trade_no, String total_fee) {
		String sign = "";
		String timestamp = WXUtil.getTimeStamp();
		String nonce_str = WXUtil.getNonceStr();
		out_trade_no = timestamp+out_trade_no;
		// body = new String(body.getBytes(),"UTF-8");
		sign = "appid=" + appid + "&attach=" + attach + "&body=" + body + "&mch_id=" + mch_id + "&nonce_str="
				+ nonce_str + "&notify_url=" + notify_url + "&out_trade_no=" + out_trade_no + "&spbill_create_ip="
				+ config.server_ip + "&total_fee=" + total_fee + "&trade_type=" + trade_type + "&key=" + APIKey;
		System.out.println("hahahhahahhsign="+sign);
		sign = com.jfinal.kit.EncryptionKit.md5Encrypt(sign).toUpperCase();
		String xml = ("<xml>" + "<appid><![CDATA[" + appid + "]]></appid>" + "<attach><![CDATA[" + attach
				+ "]]></attach>" + "<body><![CDATA[" + body + "]]></body>" + "<mch_id><![CDATA[" + mch_id
				+ "]]></mch_id>" + "<nonce_str><![CDATA[" + nonce_str + "]]></nonce_str>" + "<notify_url><![CDATA["
				+ notify_url + "]]></notify_url>" + "<out_trade_no><![CDATA[" + out_trade_no + "]]></out_trade_no>"
				+ "<spbill_create_ip><![CDATA[" + config.server_ip + "]]></spbill_create_ip>" + "<total_fee><![CDATA["
				+ total_fee + "]]></total_fee>" + "<trade_type><![CDATA[" + trade_type + "]]></trade_type>"
				+ "<sign><![CDATA[" + sign + "]]></sign>" + "</xml>");
		Map<String, String> postHeader = new HashMap<String, String>();
		postHeader.put("Content-Type", "text/html;charset=UTF-8");
		String unifiedOrder_result_str = HttpKit
				.post("https://api.mch.weixin.qq.com/pay/unifiedorder", xml, postHeader);
		System.out.println(unifiedOrder_result_str);
		String prepayid = Jsoup.parse(unifiedOrder_result_str).select("prepay_id").text();
		ResponseValues values = new ResponseValues();
		values.put("api_key", APIKey);
		values.put("appid", appid);
		values.put("partnerid", mch_id);
		values.put("prepayid", prepayid);
		values.put("package", "Sign=WXPay");
		values.put("noncestr", nonce_str);
		values.put("timestamp", timestamp);
		/*String appsign = Sha1Util.getSha1("appid=" + appid + "&partnerid=" + mch_id + "&prepayid=" + prepayid
				+ "&package=" + "Sign=WXPay" + "&noncestr=" + nonce_str + "&timestamp=" + timestamp);*/
		String appsign = com.jfinal.kit.EncryptionKit.md5Encrypt("appid=" + appid + "&noncestr=" + nonce_str + "&package=" + "Sign=WXPay" + "&partnerid=" + mch_id + "&prepayid=" + prepayid
				  + "&timestamp=" + timestamp + "&key=" + APIKey).toUpperCase();
		values.put("sign", appsign);
		return values;
	}
}
