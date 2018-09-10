package com.ocpay.wallet.bean.response;

/**
 * Created by y on 2018/7/17.
 */

public class MessageResponse {

    public boolean success;
    public MessageData data;

    public static class MessageData {
        public String message;
        public int code;
        public String error;
        public String remainSecond;
    }
}
