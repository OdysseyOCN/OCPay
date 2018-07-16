package com.stormfives.ocpay.member.controller.req;

public class ForgetSetPasswordReq {
    private String newPassword;     //新密码
    private String accesstoken;     //第一步返回的临时token
    private String smscode;  //邮箱或者手机号收到的验证码
    private String phone;   //邮箱或者手机号
    private Integer countryCode;      //选择的手机号码国家

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }

    public String getSmscode() {
        return smscode;
    }

    public void setSmscode(String smscode) {
        this.smscode = smscode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(Integer countryCode) {
        this.countryCode = countryCode;
    }
}
