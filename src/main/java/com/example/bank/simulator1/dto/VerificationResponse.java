package com.example.bank.simulator1.dto;

public class VerificationResponse {

	private String status;
	private String prn;
	private String amount;
	private String bankReferenceNumber;
	private String message;
	private String checkval;
	private String data;

	public VerificationResponse() {
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPrn() {
		return prn;
	}

	public void setPrn(String prn) {
		this.prn = prn;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getBankReferenceNumber() {
		return bankReferenceNumber;
	}

	public void setBankReferenceNumber(String bankReferenceNumber) {
		this.bankReferenceNumber = bankReferenceNumber;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCheckval() {
		return checkval;
	}

	public void setCheckval(String checkval) {
		this.checkval = checkval;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}