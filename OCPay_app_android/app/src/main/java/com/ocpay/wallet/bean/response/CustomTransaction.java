package com.ocpay.wallet.bean.response;

import android.support.annotation.NonNull;

import com.ocpay.wallet.OCPWallet;
import com.ocpay.wallet.utils.BalanceUtils;
import com.ocpay.wallet.utils.TokenUtils;
import com.ocpay.wallet.utils.eth.OCPWalletUtils;
import com.snow.commonlibrary.utils.StringUtil;

import java.io.Serializable;
import java.math.BigDecimal;

import static com.ocpay.wallet.Constans.ETH.ETH_SCALE;
import static com.ocpay.wallet.Constans.ETH.ZERO_18;
import static com.ocpay.wallet.utils.TokenUtils.ETH;

public class CustomTransaction extends BaseTransaction implements Serializable, Comparable<CustomTransaction> {

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
    private String querier;


    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getBlockNumber() {
        return blockNumber;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTimeStamp() {
        if (!StringUtil.isNumber(timeStamp)) {
            return (System.currentTimeMillis() / 1000) + "";
        }
        if (!(System.currentTimeMillis() / Long.valueOf(timeStamp) > 100)) {
            return (Long.valueOf(timeStamp) / 1000) + "";
        }
        return timeStamp;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getNonce() {
        return nonce;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setTransactionIndex(String transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public String getTransactionIndex() {
        return transactionIndex;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTo() {
        return to;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setGas(String gas) {
        this.gas = gas;
    }

    public String getGas() {
        return gas;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public void setIsError(String isError) {
        this.isError = isError;
    }

    public String getIsError() {
        return isError;
    }

    public void setTxreceipt_status(String txreceipt_status) {
        this.txreceipt_status = txreceipt_status;
    }

    public String getTxreceipt_status() {
        return txreceipt_status;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getInput() {
        if (!input.startsWith("0x")) {
            input = "0x" + input;
        }
        return input;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setCumulativeGasUsed(String cumulativeGasUsed) {
        this.cumulativeGasUsed = cumulativeGasUsed;
    }

    public String getCumulativeGasUsed() {
        return cumulativeGasUsed;
    }

    public void setGasUsed(String gasUsed) {
        this.gasUsed = gasUsed;
    }

    public String getGasUsed() {
        return gasUsed;
    }

    public void setConfirmations(String confirmations) {
        this.confirmations = confirmations;
    }

    public String getConfirmations() {
        return confirmations;
    }

    public String getTransactionAmountString() {
        if (StringUtil.isEmpty(value)) return "0" + " " + ETH;
        //ETH
        BigDecimal amount = new BigDecimal(value);
        if (amount.compareTo(new BigDecimal("0")) > 0) {
            BigDecimal divide = amount.divide(new BigDecimal(ZERO_18), ETH_SCALE, BigDecimal.ROUND_DOWN);
            return BalanceUtils.decimalFormat(divide) + " " + getTokenName();
        }

        //token
        if (getInput() != null && getInput().trim().length() == 138) {
            BigDecimal stAmount = OCPWalletUtils.getSTAmount(getInput().trim());
            return BalanceUtils.decimalFormat(stAmount) + " " + getTokenName();
        }
        return "";
    }


    public BigDecimal getAmount(int scale) {

        if (StringUtil.isEmpty(value)) new BigDecimal(0).setScale(scale);

        //token
        if (getInput() != null && getInput().length() == 138) {
            BigDecimal stAmount = OCPWalletUtils.getSTAmount(getInput());

            return stAmount.setScale(2, BigDecimal.ROUND_DOWN);
        }
        //ETH
        BigDecimal amount = new BigDecimal(value);
        if (amount.compareTo(new BigDecimal("0")) > 0) {
            return amount.divide(new BigDecimal(ZERO_18), scale, BigDecimal.ROUND_DOWN);
        }
        return new BigDecimal(0).setScale(scale);
    }


    public boolean isSend() {
        return getQuerier().equals(from);


    }


    public boolean isSuccess() {
        return "0".equals(isError) && "1".equals(txreceipt_status);
    }

    public BigDecimal getTransactionFee() {
        return OCPWalletUtils.getTransactionFee(new BigDecimal(getGasPrice()), new BigDecimal(getGasUsed()));
    }

    public String getTransferTo() {
        return isEthTransaction() ? to : OCPWalletUtils.getTransactionTo(getInput());

    }

    public boolean isEthTransaction() {
        if (StringUtil.isEmpty(value)) return false;
        //ETH
        BigDecimal amount = new BigDecimal(value);
        return amount.compareTo(new BigDecimal("0")) > 0 && getInput() != null && getInput().length() == 2;
    }


    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }


    public String getTokenName() {
        if (isEthTransaction()) {
            return ETH;
        }
        return TokenUtils.getTokenNameByAddress(to);
    }


    public String getQuerier() {
        if (querier == null) {
            querier = OCPWallet.getCurrentWallet().getWalletAddress();
        }
        return querier;
    }

    public void setQuerier(String querier) {
        this.querier = querier;
    }


    @Override
    public int compareTo(@NonNull CustomTransaction o) {
        return Long.valueOf(o.getTimeStamp()).compareTo(Long.valueOf(this.getTimeStamp()));
    }
}


