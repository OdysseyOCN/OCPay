package com.stormfives.ocpay.member.dao.entity;

import java.math.BigDecimal;
import java.util.Date;

public class OcpayAddressBalanceDual {
    private Integer id;

    private BigDecimal addressesBalance;

    private Date creatTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getAddressesBalance() {
        return addressesBalance;
    }

    public void setAddressesBalance(BigDecimal addressesBalance) {
        this.addressesBalance = addressesBalance;
    }

    public Date getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
    }
}