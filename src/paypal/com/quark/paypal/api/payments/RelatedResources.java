package com.quark.paypal.api.payments;

import com.quark.paypal.base.rest.PayPalModel;

public class RelatedResources  extends PayPalModel {

	/**
	 * A sale transaction
	 */
	private Sale sale;

	/**
	 * An order transaction
	 */
	private Authorization authorization;

	/**
	 * A capture transaction
	 */
	private Capture capture;

	/**
	 * A refund transaction
	 */
	private Refund refund;
	
	/**
	 * An order transaction
	 */
	private Order order;

	/**
	 * Default Constructor
	 */
	public RelatedResources() {
	}
}
