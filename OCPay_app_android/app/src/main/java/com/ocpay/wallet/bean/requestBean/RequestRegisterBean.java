package com.ocpay.wallet.bean.requestBean;

/**
 * Created by y on 2018/7/16.
 */

public class RequestRegisterBean {

    public String phone;
    public int countryCode;
    public String smscode;
    public String password;
    public String accesstoken;
    public String newPassword;


    public RequestRegisterBean(String phone, int countryPreId) {
        this.phone = phone;
        this.countryCode = countryPreId;
    }

    public RequestRegisterBean(String phone, int countryCode, String smscode, String password) {
        this.phone = phone;
        this.countryCode = countryCode;
        this.smscode = smscode;
        this.password = password;
    }

    public RequestRegisterBean() {

    }


    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }
}
