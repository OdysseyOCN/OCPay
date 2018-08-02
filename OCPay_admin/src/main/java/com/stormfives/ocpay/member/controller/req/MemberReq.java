package com.stormfives.ocpay.member.controller.req;

import com.stormfives.ocpay.member.dao.entity.Member;

/**
 * Created by liuhuan on 2018/7/19.
 */
public class MemberReq extends Member{

    private Integer pageNum = 1;
    private Integer pageSize = 10;

    private String beginTime;

    private String endTime;

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
