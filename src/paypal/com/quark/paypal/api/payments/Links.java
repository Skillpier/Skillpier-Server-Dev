package com.quark.paypal.api.payments;

import com.quark.paypal.base.rest.PayPalModel;



public class Links extends PayPalModel {

	/**
	 * 
	 */
	private String href;

	/**
	 * 
	 */
	private String rel;

	/**
	 * 
	 */
	private HyperSchema targetSchema;

	/**
	 * 
	 */
	private String method;

	/**
	 * 
	 */
	private String enctype;

	/**
	 * 
	 */
	private HyperSchema schema;

	/**
	 * Default Constructor
	 */
	public Links() {
	}

	/**
	 * Parameterized Constructor
	 */
	public Links(String href, String rel) {
		this.href = href;
		this.rel = rel;
	}
}
