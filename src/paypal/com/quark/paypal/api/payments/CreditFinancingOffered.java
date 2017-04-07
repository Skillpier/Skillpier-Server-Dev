package com.quark.paypal.api.payments;

import com.quark.paypal.base.rest.PayPalModel;

public class CreditFinancingOffered extends PayPalModel {

	/**
	 * This is the estimated total payment amount including interest and fees the user will pay during the lifetime of the loan.
	 */
	private Currency totalCost;

	/**
	 * Length of financing terms in month
	 */
	private float term;

	/**
	 * This is the estimated amount per month that the customer will need to pay including fees and interest.
	 */
	private Currency monthlyPayment;

	/**
	 * Estimated interest or fees amount the payer will have to pay during the lifetime of the loan.
	 */
	private Currency totalInterest;

	/**
	 * Status on whether the customer ultimately was approved for and chose to make the payment using the approved installment credit.
	 */
	private Boolean payerAcceptance;

	/**
	 * Indicates whether the cart amount is editable after payer's acceptance on PayPal side
	 */
	private Boolean cartAmountImmutable;

	/**
	 * Default Constructor
	 */
	public CreditFinancingOffered() {
	}

	/**
	 * Parameterized Constructor
	 */
	public CreditFinancingOffered(Currency totalCost, float term, Currency monthlyPayment, Currency totalInterest, Boolean payerAcceptance) {
		this.totalCost = totalCost;
		this.term = term;
		this.monthlyPayment = monthlyPayment;
		this.totalInterest = totalInterest;
		this.payerAcceptance = payerAcceptance;
	}

	public Currency getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(Currency totalCost) {
		this.totalCost = totalCost;
	}

	public float getTerm() {
		return term;
	}

	public void setTerm(float term) {
		this.term = term;
	}

	public Currency getMonthlyPayment() {
		return monthlyPayment;
	}

	public void setMonthlyPayment(Currency monthlyPayment) {
		this.monthlyPayment = monthlyPayment;
	}

	public Currency getTotalInterest() {
		return totalInterest;
	}

	public void setTotalInterest(Currency totalInterest) {
		this.totalInterest = totalInterest;
	}

	public Boolean getPayerAcceptance() {
		return payerAcceptance;
	}

	public void setPayerAcceptance(Boolean payerAcceptance) {
		this.payerAcceptance = payerAcceptance;
	}

	public Boolean getCartAmountImmutable() {
		return cartAmountImmutable;
	}

	public void setCartAmountImmutable(Boolean cartAmountImmutable) {
		this.cartAmountImmutable = cartAmountImmutable;
	}
	
}