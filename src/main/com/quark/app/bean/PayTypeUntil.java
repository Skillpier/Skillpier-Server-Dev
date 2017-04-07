package com.quark.app.bean;

import java.util.HashMap;

public class PayTypeUntil {
	public static HashMap<String, String> payMap = new HashMap<String, String>();
	static {
		payMap.put("1", "支付宝");
		payMap.put("2", "VISA");
		payMap.put("3", "PAYPAL");
	}
}
