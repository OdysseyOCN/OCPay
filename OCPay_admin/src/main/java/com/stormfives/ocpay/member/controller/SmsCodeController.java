package com.stormfives.ocpay.member.controller;

import com.stormfives.ocpay.common.response.ResponseValue;
import com.stormfives.ocpay.member.controller.req.MailAddressReq;
import com.stormfives.ocpay.member.controller.req.MemberReq;
import com.stormfives.ocpay.member.controller.req.SaveUserReq;
import com.stormfives.ocpay.member.controller.req.SmsCodeReq;
import com.stormfives.ocpay.member.service.SmsCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by liuhuan on 2018/7/19.
 */
@RestController
@RequestMapping("/api/ocpay/")
public class SmsCodeController {


    @Autowired
    private SmsCodeService smsCodeService;


    /**
     * 短信列表
     *
     * @return
     * @throws Exception
     */
    @GetMapping(value = "v1/sms-code")
    public ResponseValue getSmsCode(@ModelAttribute SmsCodeReq smsCodeReq) throws Exception {
        return smsCodeService.getSmsCode(smsCodeReq);
    }


    /**
     * 注册用户列表
     *
     * @return
     * @throws Exception
     */
    @GetMapping(value = "v1/member")
    public ResponseValue getMember(@ModelAttribute MemberReq memberReq) throws Exception {
        return smsCodeService.getMember(memberReq);
    }


    /**
     * 注册用户列表
     *
     * @return
     * @throws Exception
     */
    @GetMapping(value = "v1/email-address")
    public ResponseValue getMailAddress(@ModelAttribute MailAddressReq mailAddressReq) throws Exception {
        return smsCodeService.getMailAddress(mailAddressReq);
    }


}
