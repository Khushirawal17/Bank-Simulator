package com.example.bank.simulator1.dto;

import jakarta.validation.constraints.NotBlank;

public class PaymentRequest {

	@NotBlank
	private String md;
	
	@NotBlank
	private String pid;

	@NotBlank
	private String nar;;

	@NotBlank
	private String prn;

	@NotBlank
	private String amt;

	@NotBlank
	private String crn;

	private String ru;

	private String accno;

	private String date;

	private String checkVal;

	private String data;

	public PaymentRequest() {
		super();
	}

	public String getMd() {
		return md;
	}

	public void setMd(String md) {
		this.md = md;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getNar() {
		return nar;
	}

	public void setNar(String nar) {
		this.nar = nar;
	}

	public String getPrn() {
		return prn;
	}

	public void setPrn(String prn) {
		this.prn = prn;
	}

	public String getAmt() {
		return amt;
	}

	public void setAmt(String amt) {
		this.amt = amt;
	}

	public String getCrn() {
		return crn;
	}

	public void setCrn(String crn) {
		this.crn = crn;
	}

	public String getRu() {
		return ru;
	}

	public void setRu(String ru) {
		this.ru = ru;
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

	public String getCheckVal() {
		return checkVal;
	}

	public void setCheckVal(String checkVal) {
		this.checkVal = checkVal;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	
	

}
