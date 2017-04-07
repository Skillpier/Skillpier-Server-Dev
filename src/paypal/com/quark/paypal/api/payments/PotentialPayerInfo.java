package com.quark.paypal.api.payments;

import com.quark.paypal.base.rest.PayPalModel;


public class PotentialPayerInfo extends PayPalModel {

	/**
	 * Email address representing the potential payer.
	 */
	private String email;

	/**
	 * ExternalRememberMe id representing the potential payer
	 */
	private String externalRememberMeId;

	/**
	 * Account Number representing the potential payer
	 */
	private String accountNumber;

	/**
	 * Billing address of the potential payer.
	 */
	private Address billingAddress;

	/**
	 * Default Constructor
	 */
	public PotentialPayerInfo() {
	}
}
