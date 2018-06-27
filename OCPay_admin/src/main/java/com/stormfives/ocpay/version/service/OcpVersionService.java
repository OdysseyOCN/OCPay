package com.stormfives.ocpay.version.service;

import com.github.pagehelper.PageHelper;
import com.stormfives.ocpay.common.Constants;
import com.stormfives.ocpay.common.response.FailResponse;
import com.stormfives.ocpay.common.response.ResponseValue;
import com.stormfives.ocpay.common.response.SuccessResponse;
import com.stormfives.ocpay.token.domain.Page;
import com.stormfives.ocpay.version.controller.req.VersionReq;
import com.stormfives.ocpay.version.dao.OcpVersionMapper;
import com.stormfives.ocpay.version.dao.entity.OcpVersion;
import com.stormfives.ocpay.version.dao.entity.OcpVersionExample;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by liuhuan on 2018/6/13.
 */
@Service
public class OcpVersionService {

    @Autowired
    private OcpVersionMapper ocpVersionMapper;

    public ResponseValue addVersion(VersionReq versionReq, Integer adminId) {
        OcpVersion ocpVersion = new OcpVersion();
        BeanUtils.copyProperties(versionReq, ocpVersion);
        ocpVersion.setCreateBy(adminId);
        Date date = new Date();
        ocpVersion.setCreateTime(date);
        ocpVersion.setUpdateTime(date);
        int i = ocpVersionMapper.insertSelective(ocpVersion);
        if (i > 0) {
            return new SuccessResponse("Successful!");
        }
        return new FailResponse("Failed!");
    }


    public ResponseValue editVersion(VersionReq versionReq, Integer adminId) {
        OcpVersion ocpVersion = new OcpVersion();
        BeanUtils.copyProperties(versionReq, ocpVersion);
        ocpVersion.setUpdateBy(adminId);
        ocpVersion.setUpdateTime(new Date());
        int i = ocpVersionMapper.updateByPrimaryKeySelective(ocpVersion);
        if (i > 0) {
            return new SuccessResponse("Successful!");
        }
        return new FailResponse("Failed!");
    }

    public ResponseValue deleteVersion(VersionReq versionReq) {
        int i = ocpVersionMapper.deleteByPrimaryKey(versionReq.getId());
        if (i > 0) {
            return new SuccessResponse("Successful!");
        }
        return new FailResponse("Failed!");
    }

    public ResponseValue getVersion(VersionReq versionReq) {
        PageHelper.startPage(versionReq.getPageNum(),versionReq.getPageSize());
        OcpVersionExample ocpVersionExample = new OcpVersionExample();
        if (versionReq.getId()!=null){
            ocpVersionExample.or().andIdEqualTo(versionReq.getId());
        }
        List<OcpVersion> ocpVersions = ocpVersionMapper.selectByExample(ocpVersionExample);
        return new SuccessResponse(Page.toPage(ocpVersions));
    }

    public ResponseValue getLastVersion() {
        OcpVersionExample ocpVersionExample = new OcpVersionExample();
        ocpVersionExample.setOrderByClause(" id desc");
        ocpVersionExample.or().andStatusEqualTo(Constants.TWO_INTEGER);
        OcpVersion ocpVersion = ocpVersionMapper.selectByExample(ocpVersionExample).stream().findFirst().orElse(null);
        if (ocpVersion!=null){
            return new SuccessResponse(ocpVersion.getVersionId());
        }
        return new FailResponse("");
    }
}
