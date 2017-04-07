package com.quark.paypal.api.payments;

import com.quark.paypal.base.rest.PayPalModel;



public class CarrierAccount extends PayPalModel{

	/**
	 * ID that identifies the payer�s carrier account. Can be used in subsequent REST API calls, e.g. for making payments.
	 */
	private String id;

	/**
	 * The payer�s phone number in E.164 format.
	 */
	private String phoneNumber;

	/**
	 * User identifier as created by the merchant.
	 */
	private String externalCustomerId;

	/**
	 * The method of obtaining the phone number (USER_PROVIDED or READ_FROM_DEVICE).
	 */
	private String phoneSource;

	/**
	 * The country where the phone number is registered. Specified in 2-character IS0-3166-1 format.
	 */
	private CountryCode countryCode;

	/**
	 * Default Constructor
	 */
	public CarrierAccount() {
	}

	/**
	 * Parameterized Constructor
	 */
	public CarrierAccount(String externalCustomerId, CountryCode countryCode) {
		this.externalCustomerId = externalCustomerId;
		this.countryCode = countryCode;
	}
}
