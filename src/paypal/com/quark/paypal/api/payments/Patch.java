package com.quark.paypal.api.payments;

import com.quark.paypal.base.rest.PayPalModel;

public class Patch  extends PayPalModel {

	/**
	 * Patch operation to perform.Value required for add & remove operation can be any JSON value.
	 */
	private String op;

	/**
	 * string containing a JSON-Pointer value that references a location within the target document (the target location) where the operation is performed.
	 */
	private String path;
	
	private Object value;

	private String from;

	/**
	 * Default Constructor
	 */
	public Patch() {
	}

	/**
	 * Parameterized Constructor
	 */
	public Patch(String op, String path) {
		this.op = op;
		this.path = path;
	}
}
