package com.example.bank.simulator1.dto;

public class PaymentCallback {

	private String status;
	private String prn;
	private String nar;
	private String bid;
	private String amt;
	private String accno;
	private String date;
	private String errormsg;
	private String checkval;
	private String data;

	public PaymentCallback() {
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

	public String getNar() {
		return nar;
	}

	public void setNar(String nar) {
		this.nar = nar;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public String getAmt() {
		return amt;
	}

	public void setAmt(String amt) {
		this.amt = amt;
	}

	public String getAccno() {
		return accno;
	}

	public void setAccno(String accno) {
		this.accno = accno;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getErrormsg() {
		return errormsg;
	}

	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
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