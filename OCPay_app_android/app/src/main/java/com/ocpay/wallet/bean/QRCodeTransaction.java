package com.ocpay.wallet.bean;

import com.snow.commonlibrary.utils.StringUtil;

import java.io.Serializable;

/**
 * Created by y on 2018/6/3.
 */

public class QRCodeTransaction implements Serializable {

    String tokenName;
    String transactionFrom;
    String transactionTo;
    String gasLimit;
    String gasPrice;
    String nonce;
    String contractAddress;
    String txAction;
    String amount;
    String data;


    public QRCodeTransaction(String tokenName, String transactionFrom, String transactionTo, String gasLimit, String gasPrice, String contractAddress, String txAction, String amount) {
        this.tokenName = tokenName;
        this.transactionFrom = transactionFrom;
        this.transactionTo = transactionTo;
        this.gasLimit = gasLimit;
        this.gasPrice = gasPrice;
        this.contractAddress = contractAddress;
        this.txAction = txAction;
        this.amount = amount;
    }


    public QRCodeTransaction() {
    }


    public QRCodeTransaction(String tokenName, String transactionFrom, String transactionTo, String gasLimit, String gasPrice, String contractAddress, String amount) {
        this.tokenName = tokenName;
        this.transactionFrom = transactionFrom;
        this.transactionTo = transactionTo;
        this.gasLimit = gasLimit;
        this.gasPrice = gasPrice;
        this.contractAddress = contractAddress;
        this.amount = amount;
    }

    public QRCodeTransaction(String tokenName, String transactionFrom, String transactionTo, String gasLimit, String gasPrice, String contractAddress) {
        this.tokenName = tokenName;
        this.transactionFrom = transactionFrom;
        this.transactionTo = transactionTo;
        this.gasLimit = gasLimit;
        this.gasPrice = gasPrice;
        this.contractAddress = contractAddress;
    }

    public QRCodeTransaction(String tokenName, String transactionFrom, String transactionTo, String gasLimit, String gasPrice, String contractAddress, String txAction, String amount, String data) {
        this.tokenName = tokenName;
        this.transactionFrom = transactionFrom;
        this.transactionTo = transactionTo;
        this.gasLimit = gasLimit;
        this.gasPrice = gasPrice;
        this.contractAddress = contractAddress;
        this.txAction = txAction;
        this.amount = amount;
        this.data = data;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public String getTransactionFrom() {
        return transactionFrom;
    }

    public void setTransactionFrom(String transactionFrom) {
        this.transactionFrom = transactionFrom;
    }

    public String getTransactionTo() {
        return transactionTo;
    }

    public void setTransactionTo(String transactionTo) {
        this.transactionTo = transactionTo;
    }

    public String getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(String gasLimit) {
        this.gasLimit = gasLimit;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTxAction() {
        return txAction;
    }

    public void setTxAction(String txAction) {
        this.txAction = txAction;
    }


    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        if (StringUtil.isEmpty(data)) {
            return "";
        }
        return data;
    }
}
