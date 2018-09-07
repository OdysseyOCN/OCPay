package com.ocpay.wallet.bean;

import com.snow.commonlibrary.utils.StringUtil;

/**
 * Created by y on 2018/7/13.
 */

public class CountryData {


    private String name;
    private String dial_code;
    private String code;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDial_code(String dial_code) {
        this.dial_code = dial_code;
    }

    public String getDial_code() {
        return dial_code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public CountryData() {

    }

    public CountryData(String name, String dial_code) {
        this.name = name;
        this.dial_code = dial_code;
    }


    public int getDialCodeInteger() {
        String dialCode = dial_code.replace("+", "");
        if (StringUtil.isNumber(dialCode)) {
            return Integer.valueOf(dialCode);
        }
        return 0;

    }
}
