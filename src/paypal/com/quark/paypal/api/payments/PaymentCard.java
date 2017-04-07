package com.quark.paypal.api.payments;

import com.quark.paypal.base.rest.PayPalModel;


import java.util.List;

public class PaymentCard  extends PayPalModel {

	/**
	 * ID of the credit card being saved for later use.
	 */
	private String id;

	/**
	 * Card number.
	 */
	private String number;

	/**
	 * Type of the Card.
	 */
	private String type;

	/**
	 * 2 digit card expiry month.
	 */
	private String expireMonth;

	/**
	 * 4 digit card expiry year
	 */
	private String expireYear;

	/**
	 * 2 digit card start month. Needed for UK Maestro Card.
	 */
	private String startMonth;

	/**
	 * 4 digit card start year. Needed for UK Maestro Card. 
	 */
	private String startYear;

	/**
	 * Card validation code. Only supported when making a Payment but not when saving a payment card for future use.
	 */
	private String cvv2;

	/**
	 * Card holder's first name.
	 */
	private String firstName;

	/**
	 * Card holder's last name.
	 */
	private String lastName;

	/**
	 * 2 letter country code
	 */
	private String billingCountry;

	/**
	 * Billing Address associated with this card.
	 */
	private Address billingAddress;

	/**
	 * A unique identifier of the customer to whom this card account belongs to. Generated and provided by the facilitator. This is required when creating or using a stored funding instrument in vault.
	 */
	private String externalCustomerId;

	/**
	 * State of the funding instrument.
	 */
	private String status;

	/**
	 * Date/Time until this resource can be used fund a payment.
	 */
	private String validUntil;

	/**
	 * 1-2 digit card issue number. Needed for UK Maestro Card.
	 */
	private String issueNumber;

	/**
	 * Fields required to support 3d secure information when processing credit card payments. Only supported when the `payment_method` is set to `credit_card`.
	 */
	private Card3dSecureInfo card3dSecureInfo;

	/**
	 * 
	 */
	private List<DefinitionsLinkdescription> links;

	/**
	 * Default Constructor
	 */
	public PaymentCard() {
	}
	
	/**
	 * @deprecated Please use {@link #setCard3dSecureInfo(Card3dSecureInfo)} instead.
	 * 
	 * Setter for 3dSecureInfo
	 */
	public PaymentCard set3dSecureInfo(Card3dSecureInfo card3dSecureInfo) {
		this.card3dSecureInfo = card3dSecureInfo;
		return this;
	}

	/**
	 * @deprecated Please use {@link #getCard3dSecureInfo()} instead.
	 * 
	 * Getter for 3dSecureInfo
	 */
	public Card3dSecureInfo get3dSecureInfo() {
		return this.card3dSecureInfo;
	}
}
