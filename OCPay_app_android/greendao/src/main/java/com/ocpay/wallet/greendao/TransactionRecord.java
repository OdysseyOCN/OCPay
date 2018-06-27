package com.ocpay.wallet.greendao;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * Created by y on 2017/11/10.
 */

@Entity
public class TransactionRecord implements Serializable {

    @Id(autoincrement = true)
    private Long id;
    private String blockNumber;
    private String timeStamp;
    private String hash;
    private String nonce;
    private String blockHash;
    private String transactionIndex;
    private String from;
    private String to;
    private String value;
    private String gas;
    private String gasPrice;
    private String isError;
    private String txreceipt_status;
    private String input;
    private String contractAddress;
    private String cumulativeGasUsed;
    private String gasUsed;
    private String confirmations;
    private boolean isPending;
    private String tokenName;
    private static final long serialVersionUID = 536871008L;

    @Generated(hash = 1992233663)
    public TransactionRecord(Long id, String blockNumber, String timeStamp, String hash, String nonce, String blockHash, String transactionIndex, String from, String to, String value, String gas, String gasPrice, String isError, String txreceipt_status, String input, String contractAddress, String cumulativeGasUsed, String gasUsed, String confirmations, boolean isPending, String tokenName) {
        this.id = id;
        this.blockNumber = blockNumber;
        this.timeStamp = timeStamp;
        this.hash = hash;
        this.nonce = nonce;
        this.blockHash = blockHash;
        this.transactionIndex = transactionIndex;
        this.from = from;
        this.to = to;
        this.value = value;
        this.gas = gas;
        this.gasPrice = gasPrice;
        this.isError = isError;
        this.txreceipt_status = txreceipt_status;
        this.input = input;
        this.contractAddress = contractAddress;
        this.cumulativeGasUsed = cumulativeGasUsed;
        this.gasUsed = gasUsed;
        this.confirmations = confirmations;
        this.isPending = isPending;
        this.tokenName = tokenName;
    }




    @Generated(hash = 1215017002)
    public TransactionRecord() {
    }




    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getBlockNumber() {
        return this.blockNumber;
    }


    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }


    public String getTimeStamp() {
        return this.timeStamp;
    }


    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }


    public String getHash() {
        return this.hash;
    }


    public void setHash(String hash) {
        this.hash = hash;
    }


    public String getNonce() {
        return this.nonce;
    }


    public void setNonce(String nonce) {
        this.nonce = nonce;
    }


    public String getBlockHash() {
        return this.blockHash;
    }


    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }


    public String getTransactionIndex() {
        return this.transactionIndex;
    }


    public void setTransactionIndex(String transactionIndex) {
        this.transactionIndex = transactionIndex;
    }


    public String getFrom() {
        return this.from;
    }


    public void setFrom(String from) {
        this.from = from;
    }


    public String getTo() {
        return this.to;
    }


    public void setTo(String to) {
        this.to = to;
    }


    public String getValue() {
        return this.value;
    }


    public void setValue(String value) {
        this.value = value;
    }


    public String getGas() {
        return this.gas;
    }


    public void setGas(String gas) {
        this.gas = gas;
    }


    public String getGasPrice() {
        return this.gasPrice;
    }


    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }


    public String getIsError() {
        return this.isError;
    }


    public void setIsError(String isError) {
        this.isError = isError;
    }


    public String getTxreceipt_status() {
        return this.txreceipt_status;
    }


    public void setTxreceipt_status(String txreceipt_status) {
        this.txreceipt_status = txreceipt_status;
    }


    public String getInput() {
        return this.input;
    }


    public void setInput(String input) {
        this.input = input;
    }


    public String getContractAddress() {
        return this.contractAddress;
    }


    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }


    public String getCumulativeGasUsed() {
        return this.cumulativeGasUsed;
    }


    public void setCumulativeGasUsed(String cumulativeGasUsed) {
        this.cumulativeGasUsed = cumulativeGasUsed;
    }


    public String getGasUsed() {
        return this.gasUsed;
    }


    public void setGasUsed(String gasUsed) {
        this.gasUsed = gasUsed;
    }


    public String getConfirmations() {
        return this.confirmations;
    }


    public void setConfirmations(String confirmations) {
        this.confirmations = confirmations;
    }


    public boolean getIsPending() {
        return this.isPending;
    }


    public void setIsPending(boolean isPending) {
        this.isPending = isPending;
    }


}
