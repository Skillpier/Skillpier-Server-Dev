package com.quark.paypal.api.payments;

import com.quark.paypal.base.rest.PayPalModel;

public class CreditCardToken  extends PayPalModel {

	/**
	 * ID of a previously saved Credit Card resource using /vault/credit-card API.
	 */
	private String creditCardId;

	/**
	 * The unique identifier of the payer used when saving this credit card using /vault/credit-card API.
	 */
	private String payerId;

	/**
	 * Last 4 digits of the card number from the saved card.
	 */
	private String last4;

	/**
	 * Type of the Card (eg. visa, mastercard, etc.) from the saved card. Please note that the values are always in lowercase and not meant to be used directly for display.
	 */
	private String type;

	/**
	 * card expiry month from the saved card with value 1 - 12
	 */
	private int expireMonth;

	/**
	 * 4 digit card expiry year from the saved card
	 */
	private int expireYear;

	/**
	 * Default Constructor
	 */
	public CreditCardToken() {
	}

	/**
	 * Parameterized Constructor
	 */
	public CreditCardToken(String creditCardId) {
		this.creditCardId = creditCardId;
	}
}
