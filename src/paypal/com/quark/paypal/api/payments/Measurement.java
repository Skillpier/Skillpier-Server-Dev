package com.quark.paypal.api.payments;

import com.quark.paypal.base.rest.PayPalModel;

public class Measurement extends PayPalModel {
	
	/**
	 * Value this measurement represents.
	 */
	private String value;
	
	/**
	 * Unit in which the value is represented.
	 */
	private String unit;
}
