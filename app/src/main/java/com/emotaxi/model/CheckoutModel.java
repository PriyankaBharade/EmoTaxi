package com.emotaxi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class CheckoutModel {

    @SerializedName("version")
    @Expose
    private Version version;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("error_code")
    @Expose
    private Integer errorCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Checks {

        @SerializedName("address_line1_check")
        @Expose
        private Object addressLine1Check;
        @SerializedName("address_postal_code_check")
        @Expose
        private Object addressPostalCodeCheck;
        @SerializedName("cvc_check")
        @Expose
        private String cvcCheck;

        public Object getAddressLine1Check() {
            return addressLine1Check;
        }

        public void setAddressLine1Check(Object addressLine1Check) {
            this.addressLine1Check = addressLine1Check;
        }

        public Object getAddressPostalCodeCheck() {
            return addressPostalCodeCheck;
        }

        public void setAddressPostalCodeCheck(Object addressPostalCodeCheck) {
            this.addressPostalCodeCheck = addressPostalCodeCheck;
        }

        public String getCvcCheck() {
            return cvcCheck;
        }

        public void setCvcCheck(String cvcCheck) {
            this.cvcCheck = cvcCheck;
        }

    }

    public class Data {

        @SerializedName("status")
        @Expose
        private Integer status;
        @SerializedName("response")
        @Expose
        private Response response;
        @SerializedName("msg")
        @Expose
        private String msg;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public Response getResponse() {
            return response;
        }

        public void setResponse(Response response) {
            this.response = response;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

    }


    public class Metadata {

        @SerializedName("user_id")
        @Expose
        private String userId;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

    }


    public class Outcome {

        @SerializedName("network_status")
        @Expose
        private String networkStatus;
        @SerializedName("reason")
        @Expose
        private Object reason;
        @SerializedName("risk_level")
        @Expose
        private String riskLevel;
        @SerializedName("risk_score")
        @Expose
        private Integer riskScore;
        @SerializedName("seller_message")
        @Expose
        private String sellerMessage;
        @SerializedName("type")
        @Expose
        private String type;

        public String getNetworkStatus() {
            return networkStatus;
        }

        public void setNetworkStatus(String networkStatus) {
            this.networkStatus = networkStatus;
        }

        public Object getReason() {
            return reason;
        }

        public void setReason(Object reason) {
            this.reason = reason;
        }

        public String getRiskLevel() {
            return riskLevel;
        }

        public void setRiskLevel(String riskLevel) {
            this.riskLevel = riskLevel;
        }

        public Integer getRiskScore() {
            return riskScore;
        }

        public void setRiskScore(Integer riskScore) {
            this.riskScore = riskScore;
        }

        public String getSellerMessage() {
            return sellerMessage;
        }

        public void setSellerMessage(String sellerMessage) {
            this.sellerMessage = sellerMessage;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }


    public class PaymentMethodDetails {

        @SerializedName("card")
        @Expose
        private Card card;
        @SerializedName("type")
        @Expose
        private String type;

        public Card getCard() {
            return card;
        }

        public void setCard(Card card) {
            this.card = card;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }

    public class Refunds {

        @SerializedName("object")
        @Expose
        private String object;
        @SerializedName("data")
        @Expose
        private List<Object> data = null;
        @SerializedName("has_more")
        @Expose
        private Boolean hasMore;
        @SerializedName("total_count")
        @Expose
        private Integer totalCount;
        @SerializedName("url")
        @Expose
        private String url;

        public String getObject() {
            return object;
        }

        public void setObject(String object) {
            this.object = object;
        }

        public List<Object> getData() {
            return data;
        }

        public void setData(List<Object> data) {
            this.data = data;
        }

        public Boolean getHasMore() {
            return hasMore;
        }

        public void setHasMore(Boolean hasMore) {
            this.hasMore = hasMore;
        }

        public Integer getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }


    public class Response {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("object")
        @Expose
        private String object;
        @SerializedName("amount")
        @Expose
        private Integer amount;
        @SerializedName("amount_captured")
        @Expose
        private Integer amountCaptured;
        @SerializedName("amount_refunded")
        @Expose
        private Integer amountRefunded;
        @SerializedName("application")
        @Expose
        private Object application;
        @SerializedName("application_fee")
        @Expose
        private Object applicationFee;
        @SerializedName("application_fee_amount")
        @Expose
        private Object applicationFeeAmount;
        @SerializedName("balance_transaction")
        @Expose
        private String balanceTransaction;
        @SerializedName("billing_details")
        @Expose
        private BillingDetails billingDetails;
        @SerializedName("calculated_statement_descriptor")
        @Expose
        private String calculatedStatementDescriptor;
        @SerializedName("captured")
        @Expose
        private Boolean captured;
        @SerializedName("created")
        @Expose
        private Integer created;
        @SerializedName("currency")
        @Expose
        private String currency;
        @SerializedName("customer")
        @Expose
        private String customer;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("destination")
        @Expose
        private Object destination;
        @SerializedName("dispute")
        @Expose
        private Object dispute;
        @SerializedName("disputed")
        @Expose
        private Boolean disputed;
        @SerializedName("failure_code")
        @Expose
        private Object failureCode;
        @SerializedName("failure_message")
        @Expose
        private Object failureMessage;
        @SerializedName("fraud_details")
        @Expose
        private List<Object> fraudDetails = null;
        @SerializedName("invoice")
        @Expose
        private Object invoice;
        @SerializedName("livemode")
        @Expose
        private Boolean livemode;
        @SerializedName("metadata")
        @Expose
        private Metadata metadata;
        @SerializedName("on_behalf_of")
        @Expose
        private Object onBehalfOf;
        @SerializedName("order")
        @Expose
        private Object order;
        @SerializedName("outcome")
        @Expose
        private Outcome outcome;
        @SerializedName("paid")
        @Expose
        private Boolean paid;
        @SerializedName("payment_intent")
        @Expose
        private Object paymentIntent;
        @SerializedName("payment_method")
        @Expose
        private String paymentMethod;
        @SerializedName("payment_method_details")
        @Expose
        private PaymentMethodDetails paymentMethodDetails;
        @SerializedName("receipt_email")
        @Expose
        private Object receiptEmail;
        @SerializedName("receipt_number")
        @Expose
        private Object receiptNumber;
        @SerializedName("receipt_url")
        @Expose
        private String receiptUrl;
        @SerializedName("refunded")
        @Expose
        private Boolean refunded;
        @SerializedName("refunds")
        @Expose
        private Refunds refunds;
        @SerializedName("review")
        @Expose
        private Object review;
        @SerializedName("shipping")
        @Expose
        private Object shipping;
        @SerializedName("source")
        @Expose
        private Source source;
        @SerializedName("source_transfer")
        @Expose
        private Object sourceTransfer;
        @SerializedName("statement_descriptor")
        @Expose
        private Object statementDescriptor;
        @SerializedName("statement_descriptor_suffix")
        @Expose
        private Object statementDescriptorSuffix;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("transfer_data")
        @Expose
        private Object transferData;
        @SerializedName("transfer_group")
        @Expose
        private Object transferGroup;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getObject() {
            return object;
        }

        public void setObject(String object) {
            this.object = object;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }

        public Integer getAmountCaptured() {
            return amountCaptured;
        }

        public void setAmountCaptured(Integer amountCaptured) {
            this.amountCaptured = amountCaptured;
        }

        public Integer getAmountRefunded() {
            return amountRefunded;
        }

        public void setAmountRefunded(Integer amountRefunded) {
            this.amountRefunded = amountRefunded;
        }

        public Object getApplication() {
            return application;
        }

        public void setApplication(Object application) {
            this.application = application;
        }

        public Object getApplicationFee() {
            return applicationFee;
        }

        public void setApplicationFee(Object applicationFee) {
            this.applicationFee = applicationFee;
        }

        public Object getApplicationFeeAmount() {
            return applicationFeeAmount;
        }

        public void setApplicationFeeAmount(Object applicationFeeAmount) {
            this.applicationFeeAmount = applicationFeeAmount;
        }

        public String getBalanceTransaction() {
            return balanceTransaction;
        }

        public void setBalanceTransaction(String balanceTransaction) {
            this.balanceTransaction = balanceTransaction;
        }

        public BillingDetails getBillingDetails() {
            return billingDetails;
        }

        public void setBillingDetails(BillingDetails billingDetails) {
            this.billingDetails = billingDetails;
        }

        public String getCalculatedStatementDescriptor() {
            return calculatedStatementDescriptor;
        }

        public void setCalculatedStatementDescriptor(String calculatedStatementDescriptor) {
            this.calculatedStatementDescriptor = calculatedStatementDescriptor;
        }

        public Boolean getCaptured() {
            return captured;
        }

        public void setCaptured(Boolean captured) {
            this.captured = captured;
        }

        public Integer getCreated() {
            return created;
        }

        public void setCreated(Integer created) {
            this.created = created;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getCustomer() {
            return customer;
        }

        public void setCustomer(String customer) {
            this.customer = customer;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Object getDestination() {
            return destination;
        }

        public void setDestination(Object destination) {
            this.destination = destination;
        }

        public Object getDispute() {
            return dispute;
        }

        public void setDispute(Object dispute) {
            this.dispute = dispute;
        }

        public Boolean getDisputed() {
            return disputed;
        }

        public void setDisputed(Boolean disputed) {
            this.disputed = disputed;
        }

        public Object getFailureCode() {
            return failureCode;
        }

        public void setFailureCode(Object failureCode) {
            this.failureCode = failureCode;
        }

        public Object getFailureMessage() {
            return failureMessage;
        }

        public void setFailureMessage(Object failureMessage) {
            this.failureMessage = failureMessage;
        }

        public List<Object> getFraudDetails() {
            return fraudDetails;
        }

        public void setFraudDetails(List<Object> fraudDetails) {
            this.fraudDetails = fraudDetails;
        }

        public Object getInvoice() {
            return invoice;
        }

        public void setInvoice(Object invoice) {
            this.invoice = invoice;
        }

        public Boolean getLivemode() {
            return livemode;
        }

        public void setLivemode(Boolean livemode) {
            this.livemode = livemode;
        }

        public Metadata getMetadata() {
            return metadata;
        }

        public void setMetadata(Metadata metadata) {
            this.metadata = metadata;
        }

        public Object getOnBehalfOf() {
            return onBehalfOf;
        }

        public void setOnBehalfOf(Object onBehalfOf) {
            this.onBehalfOf = onBehalfOf;
        }

        public Object getOrder() {
            return order;
        }

        public void setOrder(Object order) {
            this.order = order;
        }

        public Outcome getOutcome() {
            return outcome;
        }

        public void setOutcome(Outcome outcome) {
            this.outcome = outcome;
        }

        public Boolean getPaid() {
            return paid;
        }

        public void setPaid(Boolean paid) {
            this.paid = paid;
        }

        public Object getPaymentIntent() {
            return paymentIntent;
        }

        public void setPaymentIntent(Object paymentIntent) {
            this.paymentIntent = paymentIntent;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public PaymentMethodDetails getPaymentMethodDetails() {
            return paymentMethodDetails;
        }

        public void setPaymentMethodDetails(PaymentMethodDetails paymentMethodDetails) {
            this.paymentMethodDetails = paymentMethodDetails;
        }

        public Object getReceiptEmail() {
            return receiptEmail;
        }

        public void setReceiptEmail(Object receiptEmail) {
            this.receiptEmail = receiptEmail;
        }

        public Object getReceiptNumber() {
            return receiptNumber;
        }

        public void setReceiptNumber(Object receiptNumber) {
            this.receiptNumber = receiptNumber;
        }

        public String getReceiptUrl() {
            return receiptUrl;
        }

        public void setReceiptUrl(String receiptUrl) {
            this.receiptUrl = receiptUrl;
        }

        public Boolean getRefunded() {
            return refunded;
        }

        public void setRefunded(Boolean refunded) {
            this.refunded = refunded;
        }

        public Refunds getRefunds() {
            return refunds;
        }

        public void setRefunds(Refunds refunds) {
            this.refunds = refunds;
        }

        public Object getReview() {
            return review;
        }

        public void setReview(Object review) {
            this.review = review;
        }

        public Object getShipping() {
            return shipping;
        }

        public void setShipping(Object shipping) {
            this.shipping = shipping;
        }

        public Source getSource() {
            return source;
        }

        public void setSource(Source source) {
            this.source = source;
        }

        public Object getSourceTransfer() {
            return sourceTransfer;
        }

        public void setSourceTransfer(Object sourceTransfer) {
            this.sourceTransfer = sourceTransfer;
        }

        public Object getStatementDescriptor() {
            return statementDescriptor;
        }

        public void setStatementDescriptor(Object statementDescriptor) {
            this.statementDescriptor = statementDescriptor;
        }

        public Object getStatementDescriptorSuffix() {
            return statementDescriptorSuffix;
        }

        public void setStatementDescriptorSuffix(Object statementDescriptorSuffix) {
            this.statementDescriptorSuffix = statementDescriptorSuffix;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Object getTransferData() {
            return transferData;
        }

        public void setTransferData(Object transferData) {
            this.transferData = transferData;
        }

        public Object getTransferGroup() {
            return transferGroup;
        }

        public void setTransferGroup(Object transferGroup) {
            this.transferGroup = transferGroup;
        }

    }


    public class Source {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("object")
        @Expose
        private String object;
        @SerializedName("address_city")
        @Expose
        private Object addressCity;
        @SerializedName("address_country")
        @Expose
        private Object addressCountry;
        @SerializedName("address_line1")
        @Expose
        private Object addressLine1;
        @SerializedName("address_line1_check")
        @Expose
        private Object addressLine1Check;
        @SerializedName("address_line2")
        @Expose
        private Object addressLine2;
        @SerializedName("address_state")
        @Expose
        private Object addressState;
        @SerializedName("address_zip")
        @Expose
        private Object addressZip;
        @SerializedName("address_zip_check")
        @Expose
        private Object addressZipCheck;
        @SerializedName("brand")
        @Expose
        private String brand;
        @SerializedName("country")
        @Expose
        private String country;
        @SerializedName("customer")
        @Expose
        private String customer;
        @SerializedName("cvc_check")
        @Expose
        private String cvcCheck;
        @SerializedName("dynamic_last4")
        @Expose
        private Object dynamicLast4;
        @SerializedName("exp_month")
        @Expose
        private Integer expMonth;
        @SerializedName("exp_year")
        @Expose
        private Integer expYear;
        @SerializedName("fingerprint")
        @Expose
        private String fingerprint;
        @SerializedName("funding")
        @Expose
        private String funding;
        @SerializedName("last4")
        @Expose
        private String last4;
        @SerializedName("metadata")
        @Expose
        private List<Object> metadata = null;
        @SerializedName("name")
        @Expose
        private Object name;
        @SerializedName("tokenization_method")
        @Expose
        private Object tokenizationMethod;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getObject() {
            return object;
        }

        public void setObject(String object) {
            this.object = object;
        }

        public Object getAddressCity() {
            return addressCity;
        }

        public void setAddressCity(Object addressCity) {
            this.addressCity = addressCity;
        }

        public Object getAddressCountry() {
            return addressCountry;
        }

        public void setAddressCountry(Object addressCountry) {
            this.addressCountry = addressCountry;
        }

        public Object getAddressLine1() {
            return addressLine1;
        }

        public void setAddressLine1(Object addressLine1) {
            this.addressLine1 = addressLine1;
        }

        public Object getAddressLine1Check() {
            return addressLine1Check;
        }

        public void setAddressLine1Check(Object addressLine1Check) {
            this.addressLine1Check = addressLine1Check;
        }

        public Object getAddressLine2() {
            return addressLine2;
        }

        public void setAddressLine2(Object addressLine2) {
            this.addressLine2 = addressLine2;
        }

        public Object getAddressState() {
            return addressState;
        }

        public void setAddressState(Object addressState) {
            this.addressState = addressState;
        }

        public Object getAddressZip() {
            return addressZip;
        }

        public void setAddressZip(Object addressZip) {
            this.addressZip = addressZip;
        }

        public Object getAddressZipCheck() {
            return addressZipCheck;
        }

        public void setAddressZipCheck(Object addressZipCheck) {
            this.addressZipCheck = addressZipCheck;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCustomer() {
            return customer;
        }

        public void setCustomer(String customer) {
            this.customer = customer;
        }

        public String getCvcCheck() {
            return cvcCheck;
        }

        public void setCvcCheck(String cvcCheck) {
            this.cvcCheck = cvcCheck;
        }

        public Object getDynamicLast4() {
            return dynamicLast4;
        }

        public void setDynamicLast4(Object dynamicLast4) {
            this.dynamicLast4 = dynamicLast4;
        }

        public Integer getExpMonth() {
            return expMonth;
        }

        public void setExpMonth(Integer expMonth) {
            this.expMonth = expMonth;
        }

        public Integer getExpYear() {
            return expYear;
        }

        public void setExpYear(Integer expYear) {
            this.expYear = expYear;
        }

        public String getFingerprint() {
            return fingerprint;
        }

        public void setFingerprint(String fingerprint) {
            this.fingerprint = fingerprint;
        }

        public String getFunding() {
            return funding;
        }

        public void setFunding(String funding) {
            this.funding = funding;
        }

        public String getLast4() {
            return last4;
        }

        public void setLast4(String last4) {
            this.last4 = last4;
        }

        public List<Object> getMetadata() {
            return metadata;
        }

        public void setMetadata(List<Object> metadata) {
            this.metadata = metadata;
        }

        public Object getName() {
            return name;
        }

        public void setName(Object name) {
            this.name = name;
        }

        public Object getTokenizationMethod() {
            return tokenizationMethod;
        }

        public void setTokenizationMethod(Object tokenizationMethod) {
            this.tokenizationMethod = tokenizationMethod;
        }

    }


    public class Version {

        @SerializedName("status")
        @Expose
        private Integer status;
        @SerializedName("versioncode")
        @Expose
        private Integer versioncode;
        @SerializedName("versionmessage")
        @Expose
        private String versionmessage;
        @SerializedName("current_version")
        @Expose
        private String currentVersion;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public Integer getVersioncode() {
            return versioncode;
        }

        public void setVersioncode(Integer versioncode) {
            this.versioncode = versioncode;
        }

        public String getVersionmessage() {
            return versionmessage;
        }

        public void setVersionmessage(String versionmessage) {
            this.versionmessage = versionmessage;
        }

        public String getCurrentVersion() {
            return currentVersion;
        }

        public void setCurrentVersion(String currentVersion) {
            this.currentVersion = currentVersion;
        }

    }

    public class Address {

        @SerializedName("city")
        @Expose
        private Object city;
        @SerializedName("country")
        @Expose
        private Object country;
        @SerializedName("line1")
        @Expose
        private Object line1;
        @SerializedName("line2")
        @Expose
        private Object line2;
        @SerializedName("postal_code")
        @Expose
        private Object postalCode;
        @SerializedName("state")
        @Expose
        private Object state;

        public Object getCity() {
            return city;
        }

        public void setCity(Object city) {
            this.city = city;
        }

        public Object getCountry() {
            return country;
        }

        public void setCountry(Object country) {
            this.country = country;
        }

        public Object getLine1() {
            return line1;
        }

        public void setLine1(Object line1) {
            this.line1 = line1;
        }

        public Object getLine2() {
            return line2;
        }

        public void setLine2(Object line2) {
            this.line2 = line2;
        }

        public Object getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(Object postalCode) {
            this.postalCode = postalCode;
        }

        public Object getState() {
            return state;
        }

        public void setState(Object state) {
            this.state = state;
        }

    }


    public class BillingDetails {

        @SerializedName("address")
        @Expose
        private Address address;
        @SerializedName("email")
        @Expose
        private Object email;
        @SerializedName("name")
        @Expose
        private Object name;
        @SerializedName("phone")
        @Expose
        private Object phone;

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Object getEmail() {
            return email;
        }

        public void setEmail(Object email) {
            this.email = email;
        }

        public Object getName() {
            return name;
        }

        public void setName(Object name) {
            this.name = name;
        }

        public Object getPhone() {
            return phone;
        }

        public void setPhone(Object phone) {
            this.phone = phone;
        }

    }


    public class Card {

        @SerializedName("brand")
        @Expose
        private String brand;
        @SerializedName("checks")
        @Expose
        private Checks checks;
        @SerializedName("country")
        @Expose
        private String country;
        @SerializedName("exp_month")
        @Expose
        private Integer expMonth;
        @SerializedName("exp_year")
        @Expose
        private Integer expYear;
        @SerializedName("fingerprint")
        @Expose
        private String fingerprint;
        @SerializedName("funding")
        @Expose
        private String funding;
        @SerializedName("installments")
        @Expose
        private Object installments;
        @SerializedName("last4")
        @Expose
        private String last4;
        @SerializedName("network")
        @Expose
        private String network;
        @SerializedName("three_d_secure")
        @Expose
        private Object threeDSecure;
        @SerializedName("wallet")
        @Expose
        private Object wallet;

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public Checks getChecks() {
            return checks;
        }

        public void setChecks(Checks checks) {
            this.checks = checks;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public Integer getExpMonth() {
            return expMonth;
        }

        public void setExpMonth(Integer expMonth) {
            this.expMonth = expMonth;
        }

        public Integer getExpYear() {
            return expYear;
        }

        public void setExpYear(Integer expYear) {
            this.expYear = expYear;
        }

        public String getFingerprint() {
            return fingerprint;
        }

        public void setFingerprint(String fingerprint) {
            this.fingerprint = fingerprint;
        }

        public String getFunding() {
            return funding;
        }

        public void setFunding(String funding) {
            this.funding = funding;
        }

        public Object getInstallments() {
            return installments;
        }

        public void setInstallments(Object installments) {
            this.installments = installments;
        }

        public String getLast4() {
            return last4;
        }

        public void setLast4(String last4) {
            this.last4 = last4;
        }

        public String getNetwork() {
            return network;
        }

        public void setNetwork(String network) {
            this.network = network;
        }

        public Object getThreeDSecure() {
            return threeDSecure;
        }

        public void setThreeDSecure(Object threeDSecure) {
            this.threeDSecure = threeDSecure;
        }

        public Object getWallet() {
            return wallet;
        }

        public void setWallet(Object wallet) {
            this.wallet = wallet;
        }

    }

}

