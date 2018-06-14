package com.ocpay.wallet.greendao;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by y on 2017/11/10.
 */

@Entity
public class TokenBalance implements Serializable {

    @Id(autoincrement = true)
    private Long id;


    @NotNull
    private String tokenName;

    @NotNull
    private String walletAddress;

    private String amount;


    private static final long serialVersionUID = 536871008L;

    @Generated(hash = 550756620)
    public TokenBalance(Long id, @NotNull String tokenName,
            @NotNull String walletAddress, String amount) {
        this.id = id;
        this.tokenName = tokenName;
        this.walletAddress = walletAddress;
        this.amount = amount;
    }


    @Generated(hash = 259691381)
    public TokenBalance() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
