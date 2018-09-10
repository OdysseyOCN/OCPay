package com.coinwallet.airdrop.entity;

import java.math.BigDecimal;
import java.util.Date;

public class OcnAccount {
    private Integer id;

    private String address;

    private String value;

    private BigDecimal valueFloat;

    private Integer nonce;

    private Date createTime;

    private String day;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public BigDecimal getValueFloat() {
        return valueFloat;
    }

    public void setValueFloat(BigDecimal valueFloat) {
        this.valueFloat = valueFloat;
    }

    public Integer getNonce() {
        return nonce;
    }

    public void setNonce(Integer nonce) {
        this.nonce = nonce;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}