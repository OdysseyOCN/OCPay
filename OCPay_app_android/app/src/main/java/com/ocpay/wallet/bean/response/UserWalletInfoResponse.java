package com.ocpay.wallet.bean.response;

import com.ocpay.wallet.bean.OCPayUserInfo;

/**
 * Created by y on 2018/7/16.
 */

public class UserWalletInfoResponse {

    boolean success;

    int code;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    OCPayUserInfo data;

    public OCPayUserInfo getData() {
        return data;
    }

    public void setData(OCPayUserInfo data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }




}
