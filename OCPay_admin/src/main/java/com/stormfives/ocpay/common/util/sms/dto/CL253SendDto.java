package com.stormfives.ocpay.common.util.sms.dto;

public class CL253SendDto {
    private String account;
    private String password;
    private String msg;  //发送内容
    private String mobile;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public CL253SendDto(String account, String password){
        this.account = account;
        this.password = password;
    }
}
