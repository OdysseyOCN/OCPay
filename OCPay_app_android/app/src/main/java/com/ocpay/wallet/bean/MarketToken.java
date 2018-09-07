package com.ocpay.wallet.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by y on 2018/8/4.
 */

public class MarketToken implements Serializable {

    public static final int TYPE_TOKEN = 0x9;
    public static final int TYPE_TOKEN_LIST = 0x3;
    public static final int TYPE_TOKEN_FAVORITE = 0x2;
    public static final int TYPE_TOKEN_EXCHANGE = 0x6;

    public String ID;
    public String tokenName;
    public String token;
    public String value;
    public String currency;

    public String tokenValue;
    public String close; //价格 美元
    public String tokenChange;
    public String tokenVolume;
    public double degree;
    public String exchange_name;
    public String collect_status;
    public String vol_format;
    public int type;
    public boolean expand;
    public int itemType = -1;


    //exchange
    public String pair;
    public String icon;


    public List<MarketToken> child;


}
