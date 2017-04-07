package com.quark.paypay.api.sample;

import java.util.HashMap;
import java.util.Map;

import com.quark.paypal.base.rest.OAuthTokenCredential;
import com.quark.paypal.base.rest.PayPalRESTException;


public class ConstructingPaypal {
	
	public static void main(String[] args) {
		Map<String, String> sdkConfig = new HashMap<String, String>();
		sdkConfig.put("mode", "sandbox");

		try {
			String accessToken = new OAuthTokenCredential("ARfQSB9_dtIZnqQst128jO6F_L0-1Pd_dFF0nGhvZpBfIgCi3VjyVs0Q8UAI_KVo2uuT2EJu-YNloMvL", "EN17jBNQeD14-5vidNaEJOzzLIyPsoBKpmFONyUl5kmBx1fSqCiRfpXBlSaBlPHLV5mOpF5HdCtidXoa", sdkConfig).getAccessToken();
			System.out.println("yyy="+accessToken);
		} catch (PayPalRESTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
