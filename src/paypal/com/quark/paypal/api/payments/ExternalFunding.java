package com.quark.paypal.api.payments;

import com.quark.paypal.base.rest.PayPalModel;

public class ExternalFunding extends PayPalModel {

	/**
	 * Unique identifier for the external funding
	 */
	private String referenceId;

	/**
	 * Generic identifier for the external funding
	 */
	private String code;

	/**
	 * Encrypted PayPal Account identifier for the funding account
	 */
	private String fundingAccountId;

	/**
	 * Description of the external funding being applied
	 */
	private String displayText;

	/**
	 * Amount being funded by the external funding account
	 */
	private Amount amount;

	/**
	 * Default Constructor
	 */
	public ExternalFunding() {
	}

	/**
	 * Parameterized Constructor
	 */
	public ExternalFunding(String referenceId, String fundingAccountId, Amount amount) {
		this.referenceId = referenceId;
		this.fundingAccountId = fundingAccountId;
		this.amount = amount;
	}
}
