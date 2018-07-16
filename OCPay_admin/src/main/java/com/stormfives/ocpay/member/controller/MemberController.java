package com.stormfives.ocpay.member.controller;

import com.stormfives.ocpay.common.response.FailResponse;
import com.stormfives.ocpay.common.response.ResponseValue;
import com.stormfives.ocpay.common.response.SuccessResponse;
import com.stormfives.ocpay.common.util.DoubleClick;
import com.stormfives.ocpay.member.MemberService;
import com.stormfives.ocpay.member.controller.req.ForgetSetPasswordReq;
import com.stormfives.ocpay.member.controller.req.MailWalletReq;
import com.stormfives.ocpay.member.controller.req.SaveUserReq;
import com.stormfives.ocpay.member.dao.entity.EmailWalletAddress;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.WalletUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by liuhuan on 2018/7/13.
 */
@RestController
@RequestMapping("/api/ocpay/")
public class MemberController {


    @Autowired
    HttpServletRequest request;

    @Autowired
    private DoubleClick doubleClick;


    @Autowired
    private MemberService memberService;


    /**
     * 注册用户
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "token/v1/save-user", method = RequestMethod.POST)
    public @ResponseBody
    ResponseValue saveUser(@RequestBody SaveUserReq saveUserReq) throws Exception {
        doubleClick.compareAndSetRequest("register", "register", saveUserReq.getPhone(), 3);
        ResponseValue responseValue = memberService.saveUser(saveUserReq);
        return responseValue;
    }

    /**
     * 用户信息
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "v1/get-user", method = RequestMethod.POST)
    public @ResponseBody
    ResponseValue getUser() throws Exception {
        Integer memberId = (Integer) request.getAttribute("memberId");
        ResponseValue responseValue = memberService.getUser(memberId);
        return responseValue;
    }

    /**
     * 用户信息
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "v1/confirm-password", method = RequestMethod.POST)
    public @ResponseBody
    ResponseValue confirmPwd(@RequestBody SaveUserReq saveUserReq) throws Exception {
        Integer memberId = (Integer) request.getAttribute("memberId");
        ResponseValue responseValue = memberService.confirmPwd(saveUserReq,memberId);
        return responseValue;
    }


    /**
     * 用户登录
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "v1/user-login", method = RequestMethod.POST)
    public @ResponseBody
    ResponseValue userLogin(@RequestBody SaveUserReq userLoginReq) throws Exception {
        ResponseValue responseValue = memberService.login(userLoginReq);
        return responseValue;
    }

    /**
     * 修改钱包地址
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "v1/wallet-address", method = RequestMethod.POST)
    public @ResponseBody
    ResponseValue updateWalletAddress(@RequestBody SaveUserReq walletReq) throws Exception {
        Integer memberId = (Integer) request.getAttribute("memberId");
        ResponseValue responseValue = memberService.updateWalletAddress(walletReq, memberId);
        return responseValue;
    }


    /**
     * 不需要登录的填写邮箱和钱包地址
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "token/v1/save-mail-wallet", method = RequestMethod.POST)
    public @ResponseBody
    ResponseValue saveMailWallet(@RequestBody MailWalletReq mailWalletReq) throws Exception {
        doubleClick.compareAndSetRequest("saveMailWallet", "saveMailWallet", mailWalletReq.getWalletAddress(), 3);
        ResponseValue responseValue = memberService.saveMailWallet(mailWalletReq);
        return responseValue;
    }


    /**
     * 忘记密码 提交请求
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "token/v1/forget-pwd", method = RequestMethod.POST)
    public @ResponseBody
    ResponseValue forgetPwd(@RequestBody SaveUserReq forgetPwd) throws Exception {
        doubleClick.compareAndSetRequest("forgetPwd", "forgetPwd", forgetPwd.getPhone(), 3);
        ResponseValue responseValue = memberService.forgetPasswordRequest(forgetPwd);
        return responseValue;
    }

    //忘记密码 设置新密码
    @RequestMapping(value = "token/v1/forget-set-pwd", method = RequestMethod.POST)
    public @ResponseBody
    ResponseValue forgetSetPwd(@RequestBody ForgetSetPasswordReq req) throws Exception {
        memberService.forgetSetPasswordService(req);
        return new SuccessResponse("SUCCESS");
    }


    /**
     * 发送短信
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "token/v1/send-sms", method = RequestMethod.POST)
    public @ResponseBody
    ResponseValue sendSms(@RequestBody SaveUserReq saveUserReq) throws Exception {
        doubleClick.compareAndSetRequest("sendSms", "sendSms", saveUserReq.getPhone(), 3);
        if (StringUtils.isBlank(saveUserReq.getPhone()) || saveUserReq.getCountryCode() == null) {
            return new FailResponse("param is error");
        }
        ResponseValue responseValue = memberService.sendSms(saveUserReq);
        return responseValue;
    }


}
