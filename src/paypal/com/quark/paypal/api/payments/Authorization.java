package com.quark.paypal.api.payments;

import com.quark.paypal.base.rest.*;


import java.util.List;


public class Authorization  extends PayPalResource {

	/**
	 * ID of the authorization transaction.
	 */
	private String id;

	/**
	 * Amount being authorized.
	 */
	private Amount amount;

	/**
	 * Specifies the payment mode of the transaction.
	 */
	private String paymentMode;

	/**
	 * State of the authorization.
	 */
	private String state;

	/**
	 * Reason code, `AUTHORIZATION`, for a transaction state of `pending`.
	 */
	private String reasonCode;

	/**
	 * [DEPRECATED] Reason code for the transaction state being Pending.Obsolete. use reason_code field instead.
	 */
	private String pendingReason;

	/**
	 * The level of seller protection in force for the transaction. Only supported when the `payment_method` is set to `paypal`. Allowed values:<br>  `ELIGIBLE`- Merchant is protected by PayPal's Seller Protection Policy for Unauthorized Payments and Item Not Received.<br> `PARTIALLY_ELIGIBLE`- Merchant is protected by PayPal's Seller Protection Policy for Item Not Received or Unauthorized Payments. Refer to `protection_eligibility_type` for specifics. <br> `INELIGIBLE`- Merchant is not protected under the Seller Protection Policy.
	 */
	private String protectionEligibility;

	/**
	 * The kind of seller protection in force for the transaction. This property is returned only when the `protection_eligibility` property is set to `ELIGIBLE`or `PARTIALLY_ELIGIBLE`. Only supported when the `payment_method` is set to `paypal`. Allowed values:<br> `ITEM_NOT_RECEIVED_ELIGIBLE`- Sellers are protected against claims for items not received.<br> `UNAUTHORIZED_PAYMENT_ELIGIBLE`- Sellers are protected against claims for unauthorized payments.<br> One or both of the allowed values can be returned.
	 */
	private String protectionEligibilityType;

	/**
	 * Fraud Management Filter (FMF) details applied for the payment that could result in accept, deny, or pending action. Returned in a payment response only if the merchant has enabled FMF in the profile settings and one of the fraud filters was triggered based on those settings. See [Fraud Management Filters Summary](https://developer.paypal.com/docs/classic/fmf/integration-guide/FMFSummary/) for more information.
	 */
	private FmfDetails fmfDetails;

	/**
	 * ID of the Payment resource that this transaction is based on.
	 */
	private String parentPayment;

	/**
	 * Authorization expiration time and date as defined in [RFC 3339 Section 5.6](http://tools.ietf.org/html/rfc3339#section-5.6).
	 */
	private String validUntil;

	/**
	 * Time of authorization as defined in [RFC 3339 Section 5.6](http://tools.ietf.org/html/rfc3339#section-5.6).
	 */
	private String createTime;

	/**
	 * Time that the resource was last updated.
	 */
	private String updateTime;

	/**
	 * 
	 */
	private List<Links> links;

	/**
	 * Default Constructor
	 */
	public Authorization() {
	}

	/**
	 * Parameterized Constructor
	 */
	public Authorization(Amount amount) {
		this.amount = amount;
	}

	/**
	 * Obtain the Authorization transaction resource for the given identifier.
	 * @deprecated Please use {@link #get(APIContext, String)} instead.
	 * @param accessToken
	 *            Access Token used for the API call.
	 * @param authorizationId
	 *            String
	 * @return Authorization
	 * @throws PayPalRESTException
	 */
	public static Authorization get(String accessToken, String authorizationId) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		return get(apiContext, authorizationId);
	}

	/**
	 * Obtain the Authorization transaction resource for the given identifier.
	 * @param apiContext
	 *            {@link APIContext} used for the API call.
	 * @param authorizationId
	 *            String
	 * @return Authorization
	 * @throws PayPalRESTException
	 */
	public static Authorization get(APIContext apiContext, String authorizationId) throws PayPalRESTException {

		if (authorizationId == null) {
			throw new IllegalArgumentException("authorizationId cannot be null");
		}
		Object[] parameters = new Object[] {authorizationId};
		String pattern = "v1/payments/authorization/{0}";
		String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		String payLoad = "";
		return configureAndExecute(apiContext, HttpMethod.GET, resourcePath, payLoad, Authorization.class);
	}


	/**
	 * Creates (and processes) a new Capture Transaction added as a related resource.
	 * @deprecated Please use {@link #capture(APIContext, Capture)} instead.
	 * @param accessToken
	 *            Access Token used for the API call.
	 * @param capture
	 *            Capture
	 * @return Capture
	 * @throws PayPalRESTException
	 */
	public Capture capture(String accessToken, Capture capture) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		return capture(apiContext, capture);
	}

	/**
	 * Creates (and processes) a new Capture Transaction added as a related resource.
	 * @param apiContext
	 *            {@link APIContext} used for the API call.
	 * @param capture
	 *            Capture
	 * @return Capture
	 * @throws PayPalRESTException
	 */
	public Capture capture(APIContext apiContext, Capture capture) throws PayPalRESTException {

		if (this.getId() == null) {
			throw new IllegalArgumentException("Id cannot be null");
		}
		if (capture == null) {
			throw new IllegalArgumentException("capture cannot be null");
		}
		Object[] parameters = new Object[] {this.getId()};
		String pattern = "v1/payments/authorization/{0}/capture";
		String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		String payLoad = capture.toJSON();
		return configureAndExecute(apiContext, HttpMethod.POST, resourcePath, payLoad, Capture.class);
	}


	/**
	 * Voids (cancels) an Authorization.
	 * @deprecated Please use {@link #doVoid(APIContext)} instead.
	 * @param accessToken
	 *            Access Token used for the API call.
	 * @return Authorization
	 * @throws PayPalRESTException
	 */
	public Authorization doVoid(String accessToken) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		return doVoid(apiContext);
	}

	/**
	 * Voids (cancels) an Authorization.
	 * @param apiContext
	 *            {@link APIContext} used for the API call.
	 * @return Authorization
	 * @throws PayPalRESTException
	 */
	public Authorization doVoid(APIContext apiContext) throws PayPalRESTException {

		if (this.getId() == null) {
			throw new IllegalArgumentException("Id cannot be null");
		}
		Object[] parameters = new Object[] {this.getId()};
		String pattern = "v1/payments/authorization/{0}/void";
		String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		String payLoad = "";
		return configureAndExecute(apiContext, HttpMethod.POST, resourcePath, payLoad, Authorization.class);
	}


	/**
	 * Reauthorizes an expired Authorization.
	 * @deprecated Please use {@link #reauthorize(APIContext)} instead.
	 * @param accessToken
	 *            Access Token used for the API call.
	 * @return Authorization
	 * @throws PayPalRESTException
	 */
	public Authorization reauthorize(String accessToken) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		return reauthorize(apiContext);
	}

	/**
	 * Reauthorizes an expired Authorization.
	 * @param apiContext
	 *            {@link APIContext} used for the API call.
	 * @return Authorization
	 * @throws PayPalRESTException
	 */
	public Authorization reauthorize(APIContext apiContext) throws PayPalRESTException {

		if (this.getId() == null) {
			throw new IllegalArgumentException("Id cannot be null");
		}
		Object[] parameters = new Object[] {this.getId()};
		String pattern = "v1/payments/authorization/{0}/reauthorize";
		String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		String payLoad = this.toJSON();
		return configureAndExecute(apiContext, HttpMethod.POST, resourcePath, payLoad, Authorization.class);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Amount getAmount() {
		return amount;
	}

	public void setAmount(Amount amount) {
		this.amount = amount;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	public String getPendingReason() {
		return pendingReason;
	}

	public void setPendingReason(String pendingReason) {
		this.pendingReason = pendingReason;
	}

	public String getProtectionEligibility() {
		return protectionEligibility;
	}

	public void setProtectionEligibility(String protectionEligibility) {
		this.protectionEligibility = protectionEligibility;
	}

	public String getProtectionEligibilityType() {
		return protectionEligibilityType;
	}

	public void setProtectionEligibilityType(String protectionEligibilityType) {
		this.protectionEligibilityType = protectionEligibilityType;
	}

	public FmfDetails getFmfDetails() {
		return fmfDetails;
	}

	public void setFmfDetails(FmfDetails fmfDetails) {
		this.fmfDetails = fmfDetails;
	}

	public String getParentPayment() {
		return parentPayment;
	}

	public void setParentPayment(String parentPayment) {
		this.parentPayment = parentPayment;
	}

	public String getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(String validUntil) {
		this.validUntil = validUntil;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public List<Links> getLinks() {
		return links;
	}

	public void setLinks(List<Links> links) {
		this.links = links;
	}


}