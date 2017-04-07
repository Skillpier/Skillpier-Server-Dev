package com.quark.paypal.api.payments;

import com.quark.paypal.base.rest.PayPalModel;


import java.util.ArrayList;
import java.util.List;


public class CreditCardHistory  extends PayPalModel {

	/**
	 * A list of credit card resources
	 */
	private List<CreditCard> items;
	
	/**
	 * Total number of items.
	 */
	private int totalItems;

	/**
	 * Total number of pages.
	 */
	private int totalPages;
	
	/**
	 * HATEOAS links related to this call. Value assigned by PayPal.
	 */
	private List<Links> links;

	/**
	 * Default Constructor
	 */
	public CreditCardHistory() {
		items = new ArrayList<CreditCard>();
		links = new ArrayList<Links>();
	}
}
