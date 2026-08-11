package com.example.bank.simulator1.dto;


import jakarta.validation.constraints.NotBlank;

public class VerificationRequest {

    @NotBlank
    private String md;

    @NotBlank
    private String pid;

    @NotBlank
    private String prn;

    private String checkval;
    private String data;

    public VerificationRequest() {
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

    public String getPrn() {
        return prn;
    }

    public void setPrn(String prn) {
        this.prn = prn;
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