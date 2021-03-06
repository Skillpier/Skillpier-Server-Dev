package com.quark.paypal.api.payments;

import com.quark.paypal.base.rest.PayPalModel;

public class Credit  extends PayPalModel {

	/**
	 * Unique identifier of credit resource.
	 */
	private String id;

	/**
	 * specifies type of credit
	 */
	private String type;


	/**
	 * Default Constructor
	 */
	public Credit() {
	}

	/**
	 * Parameterized Constructor
	 */
	public Credit(String type) {
		this.type = type;
	}
}
