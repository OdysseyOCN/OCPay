package com.stormfives.ocpay.member.controller.resp;

import com.stormfives.ocpay.member.dao.entity.OcpDetail;

import java.util.List;

/**
 * Created by liuhuan on 2018/7/27.
 */
public class OcpayAddressBalanceResp {

    private OcpDetail lastDetail;

    private List<OcpDetail> ocpDetails;

    public OcpDetail getLastDetail() {
        return lastDetail;
    }

    public void setLastDetail(OcpDetail lastDetail) {
        this.lastDetail = lastDetail;
    }

    public List<OcpDetail> getOcpDetails() {
        return ocpDetails;
    }

    public void setOcpDetails(List<OcpDetail> ocpDetails) {
        this.ocpDetails = ocpDetails;
    }
}
