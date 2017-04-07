package com.quark.paypal.api.payments;

import com.quark.paypal.base.rest.PayPalModel;


public class Payee  extends PayPalModel {

	/**
	 * Email Address associated with the Payee's PayPal Account. If the provided email address is not associated with any PayPal Account, the payee can only receiver PayPal Wallet Payments. Direct Credit Card Payments will be denied due to card compliance requirements.
	 */
	private String email;

	/**
	 * Encrypted PayPal account identifier for the Payee.
	 */
	private String merchantId;

	/**
	 * First Name of the Payee.
	 */
	private String firstName;

	/**
	 * Last Name of the Payee.
	 */
	private String lastName;

	/**
	 * Unencrypted PayPal account Number of the Payee
	 */
	private String accountNumber;

	/**
	 * Information related to the Payer. In case of PayPal Wallet payment, this information will be filled in by PayPal after the user approves the payment using their PayPal Wallet. 
	 */
	private Phone phone;

	/**
	 * Default Constructor
	 */
	public Payee() {
	}
}
