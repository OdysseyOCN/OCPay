package com.ocpay.wallet.bean;

import org.web3j.protocol.core.methods.request.RawTransaction;

/**
 * Created by y on 2018/6/4.
 */

public class SignBean {
    String sign;
    RawTransaction rawTransaction;


    public SignBean(String sign, RawTransaction rawTransaction) {
        this.sign = sign;
        this.rawTransaction = rawTransaction;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public RawTransaction getRawTransaction() {
        return rawTransaction;
    }

    public void setRawTransaction(RawTransaction rawTransaction) {
        this.rawTransaction = rawTransaction;
    }
}
