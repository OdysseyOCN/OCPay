package com.stormfives.ocpay.member.controller.resp;

/**
 * Created by liuhuan on 2018/7/16.
 */
public class MemberVo {
    private String phone;
    private String walletAddress;
    private Integer countryCode;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public Integer getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(Integer countryCode) {
        this.countryCode = countryCode;
    }
}
