package com.quark.paypal.api.payments;

import com.quark.paypal.base.rest.PayPalModel;

public class Currency extends PayPalModel {

	/**
	 * 3 letter currency code as defined by ISO 4217.
	 */
	private String currency;

	/**
	 * amount up to N digit after the decimals separator as defined in ISO 4217 for the appropriate currency code.
	 */
	private String value;

	/**
	 * Default Constructor
	 */
	public Currency() {
	}

	/**
	 * Parameterized Constructor
	 */
	public Currency(String currency, String value) {
		this.currency = currency;
		this.value = value;
	}
}
