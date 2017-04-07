package com.quark.paypal.api.payments;

import com.google.gson.GsonBuilder;
import com.quark.paypal.base.rest.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CreditCard  extends PayPalResource {

	/**
	 * ID of the credit card being saved for later use.
	 */
	private String id;

	/**
	 * Card number.
	 */
	private String number;

	/**
	 * Type of the Card (eg. Visa, Mastercard, etc.).
	 */
	private String type;

	/**
	 * 2 digit card expiry month.
	 */
	private int expireMonth;

	/**
	 * 4 digit card expiry year
	 */
	private int expireYear;

	/**
	 * Card validation code. Only supported when making a Payment but not when saving a credit card for future use.
	 */
	private Integer cvv2;

	/**
	 * Card holder's first name.
	 */
	private String firstName;

	/**
	 * Card holder's last name.
	 */
	private String lastName;

	/**
	 * Billing Address associated with this card.
	 */
	private Address billingAddress;

	/**
	 * A unique identifier of the customer to whom this bank account belongs to. Generated and provided by the facilitator. This is required when creating or using a stored funding instrument in vault.
	 */
	private String externalCustomerId;

	/**
	 * State of the funding instrument.
	 */
	private String state;

	/**
	 * Date/Time until this resource can be used to fund a payment.
	 */
	private String validUntil;

	/**
	 *
	 */
	private List<Links> links;

	/**
	 * Payer ID
	 */
	private String payerId;


	/**
	 * Default Constructor
	 */
	public CreditCard() {
	}

	/**
	 * Getter for cvv2
	 * Returns -1 if <code>cvv2</code> is null.
	 * Not autogenerating using lombok as it includes logic to return -1 on null.
	 */
	public int getCvv2() {
		if (this.cvv2 == null) {
			return -1;
		} else {
			return this.cvv2;
		}
	}
	
	/**
	 * Parameterized Constructor
	 */
	public CreditCard(String number, String type, int expireMonth, int expireYear) {
		this.number = number;
		this.type = type;
		this.expireMonth = expireMonth;
		this.expireYear = expireYear;
	}
	
	/**
	 * Creates a new Credit Card Resource (aka Tokenize).
	 * @deprecated Please use {@link #create(APIContext)} instead.
	 * @param accessToken
	 *            Access Token used for the API call.
	 * @return CreditCard
	 * @throws PayPalRESTException
	 */
	public CreditCard create(String accessToken) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		return create(apiContext);
	}

	/**
	 * Creates a new Credit Card Resource (aka Tokenize).
	 * @param apiContext
	 *            {@link APIContext} used for the API call.
	 * @return CreditCard
	 * @throws PayPalRESTException
	 */
	public CreditCard create(APIContext apiContext) throws PayPalRESTException {

		String resourcePath = "v1/vault/credit-cards";
		String payLoad = this.toJSON();
		return configureAndExecute(apiContext, HttpMethod.POST, resourcePath, payLoad, CreditCard.class);
	}


	/**
	 * Obtain the Credit Card resource for the given identifier.
	 * @deprecated Please use {@link #get(APIContext, String)} instead.
	 * @param accessToken
	 *            Access Token used for the API call.
	 * @param creditCardId
	 *            String
	 * @return CreditCard
	 * @throws PayPalRESTException
	 */
	public static CreditCard get(String accessToken, String creditCardId) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		return get(apiContext, creditCardId);
	}

	/**
	 * Obtain the Credit Card resource for the given identifier.
	 * @param apiContext
	 *            {@link APIContext} used for the API call.
	 * @param creditCardId
	 *            String
	 * @return CreditCard
	 * @throws PayPalRESTException
	 */
	public static CreditCard get(APIContext apiContext, String creditCardId) throws PayPalRESTException {

		if (creditCardId == null) {
			throw new IllegalArgumentException("creditCardId cannot be null");
		}
		Object[] parameters = new Object[] {creditCardId};
		String pattern = "v1/vault/credit-cards/{0}";
		String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		String payLoad = "";
		return configureAndExecute(apiContext, HttpMethod.GET, resourcePath, payLoad, CreditCard.class);
	}


	/**
	 * Delete the Credit Card resource for the given identifier.
	 * @deprecated Please use {@link #delete(APIContext)} instead.
	 * @param accessToken
	 *            Access Token used for the API call.

	 * @throws PayPalRESTException
	 */
	public void delete(String accessToken) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		delete(apiContext);
		return;
	}

	/**
	 * Delete the Credit Card resource for the given identifier.
	 * @param apiContext
	 *            {@link APIContext} used for the API call.
	 * @throws PayPalRESTException
	 */
	public void delete(APIContext apiContext) throws PayPalRESTException {

		if (this.getId() == null) {
			throw new IllegalArgumentException("Id cannot be null");
		}
			apiContext.setMaskRequestId(true);
		Object[] parameters = new Object[] {this.getId()};
		String pattern = "v1/vault/credit-cards/{0}";
		String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		String payLoad = "";
		configureAndExecute(apiContext, HttpMethod.DELETE, resourcePath, payLoad, null);
		return;
	}


	/**
	 * Update information in a previously saved card. Only the modified fields need to be passed in the request.
	 * @deprecated Please use {@link #update(APIContext, List)} instead.
	 * @param accessToken
	 *            Access Token used for the API call.
	 * @param patchRequest
	 *            List<Patch>
	 * @return CreditCard
	 * @throws PayPalRESTException
	 */
	public CreditCard update(String accessToken, List<Patch> patchRequest) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		return update(apiContext, patchRequest);
	}


	/**
	 * Update information in a previously saved card. Only the modified fields need to be passed in the request.
	 * @param apiContext
	 *            {@link APIContext} used for the API call.
	 * @param patchRequest
	 *            List<Patch>
	 * @return CreditCard
	 * @throws PayPalRESTException
	 */
	public CreditCard update(APIContext apiContext, List<Patch> patchRequest) throws PayPalRESTException {
		if (patchRequest == null) {
			throw new IllegalArgumentException("patchRequest cannot be null");
		}
		if (this.getId() == null) {
			throw new IllegalArgumentException("Id cannot be null");
		}
		Object[] parameters = new Object[] {this.getId()};
		String pattern = "v1/vault/credit-cards/{0}";
		String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		String payLoad = new GsonBuilder().create().toJson(patchRequest);
		return configureAndExecute(apiContext, HttpMethod.PATCH, resourcePath, payLoad, CreditCard.class);
	}


	/**
	 * Retrieves a list of Credit Card resources.
	 * @deprecated Please use {@link #list(APIContext, Map)} instead.
	 * @param accessToken
	 *            Access Token used for the API call.
	 * @param containerMap
	 *            Map<String, String>. See https://developer.paypal.com/webapps/developer/docs/api/#list-credit-card-resources
	 * @return CreditCardHistory
	 * @throws PayPalRESTException
	 */
	public static CreditCardHistory list(String accessToken, Map<String, String> containerMap) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);
		return list(apiContext, containerMap);
	}

	/**
	 * Retrieves a list of Credit Card resources.
	 * @param apiContext
	 *            {@link APIContext} used for the API call.
	 * @param containerMap
	 *            Map<String, String>. See https://developer.paypal.com/webapps/developer/docs/api/#list-credit-card-resources
	 * @return CreditCardHistory
	 * @throws PayPalRESTException
	 */
	public static CreditCardHistory list(APIContext apiContext, Map<String, String> containerMap) throws PayPalRESTException {

		if (containerMap == null) {
			throw new IllegalArgumentException("containerMap cannot be null");
		}
		Object[] parameters = new Object[] {containerMap};
		String pattern = "v1/vault/credit-cards?merchant_id={0}&external_card_id={1}&external_customer_id={2}&start_time={3}&end_time={4}&page={5}&page_size={6}&sort_order={7}&sort_by={8}&total_required={9}";
		String resourcePath = RESTUtil.formatURIPath(pattern, parameters);
		String payLoad = "";
		CreditCardHistory creditCardHistory = configureAndExecute(apiContext, HttpMethod.GET, resourcePath, payLoad, CreditCardHistory.class);

		return creditCardHistory;
	}

	/**
	 * Retrieves a list of Credit Card resources. containerMap (filters) are set to defaults.
	 * @deprecated Please use {@link #list(APIContext, Map)} instead.
	 * @param accessToken
	 *            Access Token used for the API call.
	 * @return CreditCardHistory
	 * @throws PayPalRESTException
	 */
	public static CreditCardHistory list(String accessToken) throws PayPalRESTException {
		APIContext apiContext = new APIContext(accessToken);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("merchant_id", "");
		parameters.put("external_card_id", "");
		parameters.put("external_customer_id", "");
		parameters.put("start_time", "");
		parameters.put("end_time", "");
		parameters.put("page", "1");
		parameters.put("page_size", "10");
		parameters.put("sort_order", "asc");
		parameters.put("sort_by", "create_time");
		parameters.put("total_required", "true");

		return list(apiContext, parameters);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getExpireMonth() {
		return expireMonth;
	}

	public void setExpireMonth(int expireMonth) {
		this.expireMonth = expireMonth;
	}

	public int getExpireYear() {
		return expireYear;
	}

	public void setExpireYear(int expireYear) {
		this.expireYear = expireYear;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Address getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(Address billingAddress) {
		this.billingAddress = billingAddress;
	}

	public String getExternalCustomerId() {
		return externalCustomerId;
	}

	public void setExternalCustomerId(String externalCustomerId) {
		this.externalCustomerId = externalCustomerId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(String validUntil) {
		this.validUntil = validUntil;
	}

	public List<Links> getLinks() {
		return links;
	}

	public void setLinks(List<Links> links) {
		this.links = links;
	}

	public String getPayerId() {
		return payerId;
	}

	public void setPayerId(String payerId) {
		this.payerId = payerId;
	}

	public void setCvv2(Integer cvv2) {
		this.cvv2 = cvv2;
	}

	
}
