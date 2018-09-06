package com.ocpay.wallet.bean;

import java.io.Serializable;

/**
 * Created by y on 2018/6/3.
 */

public class QRCodeBean implements Serializable {
    public String md5;
    public String ethereum;
    public String mode;
    public String data;
    public QRCodeTransaction transaction;

    public QRCodeBean() {

    }

    public QRCodeBean(String ethereum, String mode, String data) {
        this.ethereum = ethereum;
        this.mode = mode;
        this.data = data;
    }

    public String getEthereum() {
        return ethereum;
    }

    public void setEthereum(String ethereum) {
        this.ethereum = ethereum;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }


    public QRCodeTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(QRCodeTransaction transaction) {
        this.transaction = transaction;
    }
}
