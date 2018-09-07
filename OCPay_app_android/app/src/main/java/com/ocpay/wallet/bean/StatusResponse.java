package com.ocpay.wallet.bean;

/**
 * Created by y on 2018/6/2.
 */

public class StatusResponse {

    boolean success;
    String responseTip;


    public StatusResponse() {
    }

    public StatusResponse(boolean status) {
        this.success = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public StatusResponse(boolean status, String responseTip) {
        this.success = status;
        this.responseTip = responseTip;
    }

    public String getResponseTip() {
        return responseTip;
    }

    public void setResponseTip(String responseTip) {
        this.responseTip = responseTip;
    }
}

