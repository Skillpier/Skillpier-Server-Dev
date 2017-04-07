package com.quark.paypal.api.payments;

import com.quark.paypal.base.rest.PayPalModel;

public class InstallmentOption extends PayPalModel {

	/**
	 * Number of installments
	 */
	private int term;

	/**
	 * Monthly payment
	 */
	private Currency monthlyPayment;

	/**
	 * Discount amount applied to the payment, if any
	 */
	private Currency discountAmount;

	/**
	 * Discount percentage applied to the payment, if any
	 */
	private Percentage discountPercentage;

	/**
	 * Default Constructor
	 */
	public InstallmentOption() {
	}

	/**
	 * Parameterized Constructor
	 */
	public InstallmentOption(int term, Currency monthlyPayment) {
		this.term = term;
		this.monthlyPayment = monthlyPayment;
	}
}