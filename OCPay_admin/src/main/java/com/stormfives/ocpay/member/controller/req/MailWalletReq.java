package com.stormfives.ocpay.member.controller.req;

/**
 * Created by liuhuan on 2018/7/13.
 */
public class MailWalletReq {

    private String email;

    private String walletAddress;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }
}
