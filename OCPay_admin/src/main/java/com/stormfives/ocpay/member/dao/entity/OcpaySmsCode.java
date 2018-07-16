package com.stormfives.ocpay.member.dao.entity;

import java.util.Date;

public class OcpaySmsCode {
    private Integer escid;

    private String esccode;

    private String escphone;

    private Date esccreatedate;

    private Date escexpiredate;

    private Integer escvalid;

    private String escphonepre;

    public Integer getEscid() {
        return escid;
    }

    public void setEscid(Integer escid) {
        this.escid = escid;
    }

    public String getEsccode() {
        return esccode;
    }

    public void setEsccode(String esccode) {
        this.esccode = esccode;
    }

    public String getEscphone() {
        return escphone;
    }

    public void setEscphone(String escphone) {
        this.escphone = escphone;
    }

    public Date getEsccreatedate() {
        return esccreatedate;
    }

    public void setEsccreatedate(Date esccreatedate) {
        this.esccreatedate = esccreatedate;
    }

    public Date getEscexpiredate() {
        return escexpiredate;
    }

    public void setEscexpiredate(Date escexpiredate) {
        this.escexpiredate = escexpiredate;
    }

    public Integer getEscvalid() {
        return escvalid;
    }

    public void setEscvalid(Integer escvalid) {
        this.escvalid = escvalid;
    }

    public String getEscphonepre() {
        return escphonepre;
    }

    public void setEscphonepre(String escphonepre) {
        this.escphonepre = escphonepre;
    }
}