package com.quark.paypal.api.payments;

import com.quark.paypal.base.rest.PayPalModel;


import java.util.ArrayList;
import java.util.List;


public class ItemList  extends PayPalModel {

	/**
	 * List of items.
	 */
	private List<Item> items;

	/**
	 * Shipping address.
	 */
	private ShippingAddress shippingAddress;
	
	/**
	 * Shipping method used for this payment like USPSParcel etc.
	 */
	private String shippingMethod;

	/**
	 * Allows merchant's to share payer’s contact number with PayPal for the current payment. Final contact number of payer associated with the transaction might be same as shipping_phone_number or different based on Payer’s action on PayPal. The phone number must be represented in its canonical international format, as defined by the E.164 numbering plan
	 */
	private String shippingPhoneNumber;

	/**
	 * Default Constructor
	 */
	public ItemList() {
		items = new ArrayList<Item>();
	}
}
