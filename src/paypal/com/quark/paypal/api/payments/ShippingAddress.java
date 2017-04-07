package com.quark.paypal.api.payments;


public class ShippingAddress extends Address {

	/**
	 * Address ID assigned in PayPal system.
	 */
	private String id;

	/**
	 * Name of the recipient at this address.
	 */
	private String recipientName;

	/**
	 * Default shipping address of the Payer.
	 */
	private Boolean defaultAddress;

	/**
	 * Default Constructor
	 */
	public ShippingAddress() {
	}

	/**
	 * Parameterized Constructor
	 */
	public ShippingAddress(String recipientName) {
		this.recipientName = recipientName;
	}
}
