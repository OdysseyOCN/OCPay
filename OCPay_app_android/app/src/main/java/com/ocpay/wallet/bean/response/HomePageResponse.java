/**
 * Copyright 2018 bejson.com
 */
package com.ocpay.wallet.bean.response;


import java.io.Serializable;
import java.util.List;

public class HomePageResponse implements Serializable{

    private HomeData data;
    private boolean success;

    public void setData(HomeData data) {
        this.data = data;
    }

    public HomeData getData() {
        return data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean getSuccess() {
        return success;
    }


    public class HomeData implements  Serializable{

        public  List<HomePageGroup> homePageVos;
    }

}