package com.coinwallet.airdrop.entity;

/**
 * Created by liuhuan on 2018/8/29.
 */
public class MessageResult {
    private Object data;
    private int code;
    private String message;
    private Object Data;

    public MessageResult(int code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public MessageResult(int code, String msg, Object object) {
        this.code = code;
        this.message = msg;
        this.data = object;
    }

    public static MessageResult success() {
        return new MessageResult(0, "SUCCESS");
    }

    public static MessageResult success(String msg) {
        return new MessageResult(0, msg);
    }

    public static MessageResult error(int code, String msg) {
        return new MessageResult(code, msg);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
