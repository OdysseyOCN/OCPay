package com.ocpay.wallet.bean.response;

import java.io.Serializable;
import java.util.List;

public class HomePageGroup implements Serializable {

    public int id;
    public int type;
    public int sort;
    public String content;
    public long createTime;
    public int createBy;
    public String updateTime;
    public String updateBy;
    public List<HomePageItem> advertisments;
}