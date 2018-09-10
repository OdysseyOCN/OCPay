package com.coinwallet.airdrop.entity;

import java.math.BigInteger;

/**
 * Created by liuhuan on 2018/8/29.
 */

public class ValidateTxDto {
    private BigInteger gasUsed;
    private boolean success;  //交易状态

    public BigInteger getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(BigInteger gasUsed) {
        this.gasUsed = gasUsed;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
