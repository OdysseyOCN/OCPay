package com.stormfives.ocpay.member.service;

import com.github.pagehelper.PageHelper;
import com.stormfives.ocpay.common.response.ResponseValue;
import com.stormfives.ocpay.common.response.SuccessResponse;
import com.stormfives.ocpay.member.controller.req.MailAddressReq;
import com.stormfives.ocpay.member.controller.req.MemberReq;
import com.stormfives.ocpay.member.controller.req.SmsCodeReq;
import com.stormfives.ocpay.member.dao.EmailWalletAddressMapper;
import com.stormfives.ocpay.member.dao.MemberMapper;
import com.stormfives.ocpay.member.dao.OcpaySmsCodeMapper;
import com.stormfives.ocpay.member.dao.entity.*;
import com.stormfives.ocpay.token.domain.Page;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.stormfives.ocpay.common.util.DateUtils.timeStamp2Date;

/**
 * Created by liuhuan on 2018/7/19.
 */
@Service
public class SmsCodeService {

    Logger logger = LoggerFactory.getLogger(SmsCodeService.class);

    @Autowired
    private OcpaySmsCodeMapper ocpaySmsCodeMapper;

    @Autowired
    private MemberMapper memberMapper;


    @Autowired
    private EmailWalletAddressMapper emailWalletAddressMapper;

    public ResponseValue getSmsCode(SmsCodeReq smsCodeReq) {
        PageHelper.startPage(smsCodeReq.getPageNum(), smsCodeReq.getPageSize());
        OcpaySmsCodeExample ocpaySmsCodeExample = new OcpaySmsCodeExample();
        OcpaySmsCodeExample.Criteria criteria = ocpaySmsCodeExample.createCriteria();

        if (smsCodeReq.getEscid() != null) {
            criteria.andEscidEqualTo(smsCodeReq.getEscid());
        }

        if (StringUtils.isNotBlank(smsCodeReq.getEsccode())) {
            criteria.andEsccodeEqualTo(smsCodeReq.getEsccode());
        }

        if (StringUtils.isNotBlank(smsCodeReq.getEscphone())) {
            criteria.andEscphoneLike(smsCodeReq.getEscphone() + "%");
        }

        if (StringUtils.isNotBlank(smsCodeReq.getEscphonepre())) {
            criteria.andEscphonepreEqualTo("+" + smsCodeReq.getEscphonepre().trim());
        }

        if (smsCodeReq.getEscvalid() != null) {
            criteria.andEscvalidEqualTo(smsCodeReq.getEscvalid());
        }


        try {
            if (StringUtils.isNotBlank(smsCodeReq.getBeginTime()) && StringUtils.isNotBlank(smsCodeReq.getEndTime())) {
                criteria.andEsccreatedateBetween(timeStamp2Date(smsCodeReq.getBeginTime()), timeStamp2Date(smsCodeReq.getEndTime()));
            }
        } catch (Exception e) {
            logger.error("时间戳解析出错", e);
        }


        try {
            if (StringUtils.isNotBlank(smsCodeReq.getExpireBeginTime()) && StringUtils.isNotBlank(smsCodeReq.getExpireEndTime())) {

                criteria.andEscexpiredateBetween(timeStamp2Date(smsCodeReq.getBeginTime()), timeStamp2Date(smsCodeReq.getEndTime()));
            }
        } catch (Exception e) {
            logger.error("时间戳解析出错", e);
        }

        ocpaySmsCodeExample.setOrderByClause(" escid desc");
        List<OcpaySmsCode> ocpaySmsCodes = ocpaySmsCodeMapper.selectByExample(ocpaySmsCodeExample);
        return new SuccessResponse(Page.toPage(ocpaySmsCodes));
    }

    public ResponseValue getMember(MemberReq memberReq) {
        PageHelper.startPage(memberReq.getPageNum(), memberReq.getPageSize());
        MemberExample memberExample = new MemberExample();
        MemberExample.Criteria criteria = memberExample.createCriteria();

        if (memberReq.getId() != null) {
            criteria.andIdEqualTo(memberReq.getId());
        }

        if (memberReq.getPhonepreid() != null) {
            criteria.andPhonepreidEqualTo(memberReq.getPhonepreid());
        }

        if (StringUtils.isNotBlank(memberReq.getPhone())) {
            criteria.andPhoneLike(memberReq.getPhone() + "%");
        }

        if (StringUtils.isNotBlank(memberReq.getWalletAddress())) {
            criteria.andWalletAddressLike(memberReq.getWalletAddress() + "%");
        }

        try {
            if (StringUtils.isNotBlank(memberReq.getBeginTime()) && StringUtils.isNotBlank(memberReq.getEndTime())) {
                criteria.andCreateTimeBetween(timeStamp2Date(memberReq.getBeginTime()), timeStamp2Date(memberReq.getEndTime()));
            }
        } catch (Exception e) {
            logger.error("时间戳解析出错", e);
        }

        memberExample.setOrderByClause(" id desc");
        List<Member> members = memberMapper.selectByExample(memberExample);
        return new SuccessResponse(Page.toPage(members));
    }

    public ResponseValue getMailAddress(MailAddressReq mailAddressReq) {
        PageHelper.startPage(mailAddressReq.getPageNum(), mailAddressReq.getPageSize());

        EmailWalletAddressExample emailWalletAddressExample = new EmailWalletAddressExample();
        EmailWalletAddressExample.Criteria criteria = emailWalletAddressExample.createCriteria();

        if (mailAddressReq.getId() != null) {
            criteria.andIdEqualTo(mailAddressReq.getId());
        }

        if (StringUtils.isNotBlank(mailAddressReq.getEmail())) {
            criteria.andEmailLike(mailAddressReq.getEmail() + "%");
        }

        if (StringUtils.isNotBlank(mailAddressReq.getWalletAddress())) {
            criteria.andWalletAddressLike(mailAddressReq.getWalletAddress() + "%");
        }

        try {
            if (StringUtils.isNotBlank(mailAddressReq.getBeginTime()) && StringUtils.isNotBlank(mailAddressReq.getEndTime())) {
                criteria.andCreateTimeBetween(timeStamp2Date(mailAddressReq.getBeginTime()), timeStamp2Date(mailAddressReq.getEndTime()));
            }
        } catch (Exception e) {
            logger.error("时间戳解析出错", e);
        }

        emailWalletAddressExample.setOrderByClause(" id desc");
        List<EmailWalletAddress> emailWalletAddresses = emailWalletAddressMapper.selectByExample(emailWalletAddressExample);
        return new SuccessResponse(Page.toPage(emailWalletAddresses));
    }
}
