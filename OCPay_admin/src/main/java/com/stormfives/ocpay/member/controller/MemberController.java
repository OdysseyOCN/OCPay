package com.stormfives.ocpay.member.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPObject;
import com.stormfives.ocpay.common.exception.InvalidArgumentException;
import com.stormfives.ocpay.common.response.FailResponse;
import com.stormfives.ocpay.common.response.ResponseValue;
import com.stormfives.ocpay.common.response.SuccessResponse;
import com.stormfives.ocpay.common.util.DoubleClick;
import com.stormfives.ocpay.member.service.MemberService;
import com.stormfives.ocpay.member.controller.req.ForgetSetPasswordReq;
import com.stormfives.ocpay.member.controller.req.MailWalletReq;
import com.stormfives.ocpay.member.controller.req.SaveUserReq;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
    ResponseValue saveUser(@RequestBody SaveUserReq saveUserReq) {
        ResponseValue responseValue = null;
        try {
            doubleClick.compareAndSetRequest("register", "register", saveUserReq.getPhone(), 3);
            responseValue = memberService.saveUser(saveUserReq);
        } catch (InvalidArgumentException e) {
            return new FailResponse(e.getMessage());
        }
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
    ResponseValue getUser(HttpServletRequest request) throws Exception {
        Integer memberId = (Integer) request.getAttribute("memberId");
        return memberService.getUser(memberId);
    }

    /**
     * 用户确认密码
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "v1/confirm-password", method = RequestMethod.POST)
    public @ResponseBody
    ResponseValue confirmPwd(@RequestBody SaveUserReq saveUserReq) throws Exception {
        Integer memberId = (Integer) request.getAttribute("memberId");
        return memberService.confirmPwd(saveUserReq, memberId);
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
        return memberService.login(userLoginReq);
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
        return memberService.updateWalletAddress(walletReq, memberId);
    }


    /**
     * 不需要登录的填写邮箱和钱包地址
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "token/v1/save-mail-wallet", method = RequestMethod.GET)
    public @ResponseBody
    ResponseValue saveMailWallet(@RequestParam("email") String email, @RequestParam("walletAddress")String walletAddress) throws Exception {
        doubleClick.compareAndSetRequest("saveMailWallet", "saveMailWallet", walletAddress, 3);
        return memberService.saveMailWallet(email,walletAddress);
    }



        /**
         * 忘记密码 提交请求
         *
         * @return
         * @throws Exception
         */
    @RequestMapping(value = "token/v1/forget-pwd", method = RequestMethod.POST)
    public @ResponseBody
    ResponseValue forgetPwd(@RequestBody SaveUserReq forgetPwd) {
        ResponseValue responseValue = null;
        try {
            doubleClick.compareAndSetRequest("forgetPwd", "forgetPwd", forgetPwd.getPhone(), 3);
            responseValue = memberService.forgetPasswordRequest(forgetPwd);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
        return responseValue;
    }

    //忘记密码 设置新密码
    @RequestMapping(value = "token/v1/forget-set-pwd", method = RequestMethod.POST)
    public @ResponseBody
    ResponseValue forgetSetPwd(@RequestBody ForgetSetPasswordReq req) {
        ResponseValue responseValue = null;
        try {
            doubleClick.compareAndSetRequest("forget-set-pwd", "forget-set-pwd", req.getPhone(), 3);
            responseValue = memberService.forgetSetPasswordService(req);
        } catch (InvalidArgumentException e) {
            return new FailResponse(e.getMessage());
        }
        return responseValue;
    }


    /**
     * 发送短信
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "token/v1/send-sms", method = RequestMethod.POST)
    public @ResponseBody
    ResponseValue sendSms(@RequestBody SaveUserReq saveUserReq) {
        if (StringUtils.isBlank(saveUserReq.getPhone()) || saveUserReq.getCountryCode() == null) {
            return new FailResponse("param is error");
        }
        ResponseValue responseValue = null;
        try {
            doubleClick.compareAndSetRequest("sendSms", "sendSms", saveUserReq.getPhone(), 3);
            responseValue = memberService.sendSms(saveUserReq);
        } catch (InvalidArgumentException e) {
            return new FailResponse(e.getMessage());
        }
        return responseValue;
    }


}
