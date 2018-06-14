package com.ocpay.wallet.greendao;


import android.content.Context;

import com.ocpay.wallet.greendao.manager.TokenBalanceDaoUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;

/**
 * Created by y on 2017/11/10.
 */

@Entity
public class WalletInfo implements Serializable {

    @Id(autoincrement = true)
    private Long id;

    private String userId;

    @Unique
    private String walletName;

    @NotNull
    private String walletAddress;

    private boolean isBackup;

    private String passwordTip;

    private int walletType;

    private static final long serialVersionUID = 536871008L;

    private String ethBalance;

    private String startBlock;

    @Generated(hash = 1149502771)
    public WalletInfo(Long id, String userId, String walletName, @NotNull String walletAddress, boolean isBackup, String passwordTip, int walletType, String ethBalance,
                      String startBlock) {
        this.id = id;
        this.userId = userId;
        this.walletName = walletName;
        this.walletAddress = walletAddress;
        this.isBackup = isBackup;
        this.passwordTip = passwordTip;
        this.walletType = walletType;
        this.ethBalance = ethBalance;
        this.startBlock = startBlock;
    }

    @Generated(hash = 1144910350)
    public WalletInfo() {
    }


    public Long getId() {
        return id;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getWalletAddress() {
        if (!walletAddress.startsWith("0x")) {
            return "0x" + walletAddress;
        }
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }


    public String getPasswordTip() {
        return passwordTip;
    }

    public void setPasswordTip(String passwordTip) {
        this.passwordTip = passwordTip;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public boolean isBackup() {
        return isBackup;
    }

    public void setBackup(boolean backup) {
        isBackup = backup;
    }

    public int getWalletType() {
        return walletType;
    }

    public void setWalletType(int walletType) {
        this.walletType = walletType;
    }


    public boolean getIsBackup() {
        return this.isBackup;
    }


    public void setIsBackup(boolean isBackup) {
        this.isBackup = isBackup;
    }


    public String getEthBalance() {
        return ethBalance;
    }

    public String getTokenBalance(Context context, String tokenName) {
        return TokenBalanceDaoUtils.getTokenBalance(context, walletAddress, tokenName).toString();
    }

    public void setEthBalance(String ethBalance) {
        this.ethBalance = ethBalance;
    }


    public String getStartBlock() {
        return startBlock;
    }

    public void setStartBlock(String startBlock) {
        this.startBlock = startBlock;
    }


}
