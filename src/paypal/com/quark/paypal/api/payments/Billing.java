package com.quark.paypal.api.payments;

import com.quark.paypal.base.rest.PayPalModel;

public class Billing extends PayPalModel {

	/**
	 * Identifier of the instrument in PayPal Wallet
	 */
	private String billingAgreementId;

	/**
	 * Default Constructor
	 */
	public Billing() {
	}
}
