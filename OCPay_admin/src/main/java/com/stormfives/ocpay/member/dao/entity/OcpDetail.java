package com.stormfives.ocpay.member.dao.entity;

import java.util.Date;

/**
 * Created by liuhuan on 2018/7/27.
 */
public class OcpDetail {

    private String period;
//    private String ocpDistributed;
    private String ocnRegistered;
//    private String totalOcp;
    private String addressNum;
//    private String averageOcpDistributed;
    private Date screenTime;

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getOcnRegistered() {
        return ocnRegistered;
    }

    public void setOcnRegistered(String ocnRegistered) {
        this.ocnRegistered = ocnRegistered;
    }

    public String getAddressNum() {
        return addressNum;
    }

    public void setAddressNum(String addressNum) {
        this.addressNum = addressNum;
    }

    public Date getScreenTime() {
        return screenTime;
    }

    public void setScreenTime(Date screenTime) {
        this.screenTime = screenTime;
    }
}
