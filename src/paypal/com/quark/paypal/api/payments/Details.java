package com.quark.paypal.api.payments;

import com.quark.paypal.base.rest.PayPalModel;

public class Details  extends PayPalModel {

	/**
	 * Amount being charged for shipping.
	 */
	private String shipping;

	/**
	 * Sub-total (amount) of items being paid for.
	 */
	private String subtotal;

	/**
	 * Amount being charged as tax.
	 */
	private String tax;

	/**
	 * Fee charged by PayPal. In case of a refund, this is the fee amount refunded to the original receipient of the payment.
	 */
	private String fee;

	/**
	 * Amount being charged as handling fee.
	 */
	private String handlingFee;

	/**
	 * Amount being charged as gift wrap fee.
	 */
	private String giftWrap;

	/**
	 * Amount being charged for the insurance fee.
	 */
	private String insurance;
	
	/**
	 * Amount being discounted for the shipping fee.
	 */
	private String shippingDiscount;

	/**
	 * Default Constructor
	 */
	public Details() {
	}
}
