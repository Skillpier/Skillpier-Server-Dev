package com.quark.paypay.api.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.quark.paypal.api.payments.Amount;
import com.quark.paypal.api.payments.Payer;
import com.quark.paypal.api.payments.Payment;
import com.quark.paypal.api.payments.RedirectUrls;
import com.quark.paypal.api.payments.Transaction;
import com.quark.paypal.base.rest.APIContext;
import com.quark.paypal.base.rest.OAuthTokenCredential;
import com.quark.paypal.base.rest.PayPalRESTException;


public class GetPaypalToken {
	
	public static void main(String[] args) throws PayPalRESTException {
		
		String paypal_client_id = "ARfQSB9_dtIZnqQst128jO6F_L0-1Pd_dFF0nGhvZpBfIgCi3VjyVs0Q8UAI_KVo2uuT2EJu-YNloMvL";
		String paypal_secret = "EN17jBNQeD14-5vidNaEJOzzLIyPsoBKpmFONyUl5kmBx1fSqCiRfpXBlSaBlPHLV5mOpF5HdCtidXoa"; 
		String paypal_response_id = "PAY-18X32451H0459092JK07KFUI";
		
		Map<String, String> sdkConfig = new HashMap<String, String>();
		sdkConfig.put("mode", "sandbox");

		String accessToken = new OAuthTokenCredential(paypal_client_id, paypal_secret, sdkConfig).getAccessToken();
		
		APIContext apiContext = new APIContext(accessToken);
		apiContext.setConfigurationMap(sdkConfig);

		/*Amount amount = new Amount();
		amount.setCurrency("USD");
		amount.setTotal("12");

		Transaction transaction = new Transaction();
		transaction.setDescription("creating a payment");
		transaction.setAmount(amount);

		List<Transaction> transactions = new ArrayList<Transaction>();
		transactions.add(transaction);

		Payer payer = new Payer();
		payer.setPaymentMethod("paypal");

		Payment payment = new Payment();
		payment.setIntent("sale");
		payment.setPayer(payer);
		payment.setTransactions(transactions);
		RedirectUrls redirectUrls = new RedirectUrls();
		redirectUrls.setCancelUrl("https://devtools-paypal.com/guide/pay_paypal/java?cancel=true");
		redirectUrls.setReturnUrl("https://devtools-paypal.com/guide/pay_paypal/java?success=true");
		payment.setRedirectUrls(redirectUrls);

		Payment createdPayment = payment.create(apiContext);
		String stateString = createdPayment.getState();
		System.out.println(stateString+"=paypal pay state");*/
		
		Payment verifyPayment = Payment.get(apiContext,paypal_response_id);
		String verifyPaymentState = verifyPayment.getState();
		System.out.println("verifyPaymentState====="+verifyPaymentState);
	}
}
