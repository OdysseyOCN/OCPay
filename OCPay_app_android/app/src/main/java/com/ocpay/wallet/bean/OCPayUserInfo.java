package com.ocpay.wallet.bean;

import com.snow.commonlibrary.utils.StringUtil;

/**
 * Created by y on 2018/7/16.
 */

public class OCPayUserInfo {
    public String phone;
    public String walletAddress;
    public int countryCode;

    public boolean hasWalletAddress() {
        return !StringUtil.isEmpty(walletAddress);
    }
}
