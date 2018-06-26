package com.stormfives.ocpay.version.controller;

import com.stormfives.ocpay.common.exception.InvalidArgumentException;
import com.stormfives.ocpay.common.response.ResponseValue;
import com.stormfives.ocpay.version.controller.req.VersionReq;
import com.stormfives.ocpay.version.service.OcpVersionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by liuhuan on 2018/6/13.
 */
@RestController
@RequestMapping("/api/ocpay/")
public class OcpVersionController {


    @Autowired
    private HttpServletRequest request;

    @Autowired
    private OcpVersionService ocpVersionService;

    @PostMapping("token/v1/get-last-version")
    public ResponseValue getLastVersion() throws InvalidArgumentException {
        return ocpVersionService.getLastVersion();
    }



    @PostMapping("v1/add-version")
    public ResponseValue addVersion(@RequestBody VersionReq versionReq) throws InvalidArgumentException {
        if (StringUtils.isBlank(versionReq.getVersionId()) ||
                StringUtils.isBlank(versionReq.getVersionName()) ||
                StringUtils.isBlank(versionReq.getContent())) {
            throw new InvalidArgumentException("param is error!");
        }
        Integer adminId = (Integer) request.getAttribute("adminId");
        return ocpVersionService.addVersion(versionReq, adminId);
    }

    @PostMapping("v1/edit-version")
    public ResponseValue editVersion(@RequestBody VersionReq versionReq) throws InvalidArgumentException {
        if (StringUtils.isBlank(versionReq.getVersionId()) ||
                StringUtils.isBlank(versionReq.getVersionName()) ||
                StringUtils.isBlank(versionReq.getContent()) ||
                versionReq.getId() == null) {
            throw new InvalidArgumentException("param is error!");
        }
        Integer adminId = (Integer) request.getAttribute("adminId");
        return ocpVersionService.editVersion(versionReq, adminId);
    }

    @PostMapping("v1/delete-version")
    public ResponseValue deleteVersion(@RequestBody VersionReq versionReq) throws InvalidArgumentException {
        if (versionReq.getId() == null) {
            throw new InvalidArgumentException("param is error!");
        }
        return ocpVersionService.deleteVersion(versionReq);
    }

    @PostMapping("v1/get-version")
    public ResponseValue getVersion(@RequestBody VersionReq versionReq) throws InvalidArgumentException {
        return ocpVersionService.getVersion(versionReq);
    }

}
