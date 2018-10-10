package com.ocpay.wallet.bean.response;

/**
 * Auto-generated: 2018-06-12 17:12:5
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class OCPResponse {

    private String data;
    private boolean success;
    private long remainSecond;

    private int code;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getRemainSecond() {
        return remainSecond;
    }

    public void setRemainSecond(long remainSecond) {
        this.remainSecond = remainSecond;
    }

    public OCPResponse(String data, boolean success) {
        this.data = data;
        this.success = success;
    }


    public OCPResponse() {

    }
}