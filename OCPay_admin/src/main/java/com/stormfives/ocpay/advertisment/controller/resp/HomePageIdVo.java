package com.stormfives.ocpay.advertisment.controller.resp;

import jnr.ffi.annotations.In;

/**
 * Created by liuhuan on 2018/5/25.
 */
public class HomePageIdVo {
    private Integer id;
    private Integer type;
    private String content;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
