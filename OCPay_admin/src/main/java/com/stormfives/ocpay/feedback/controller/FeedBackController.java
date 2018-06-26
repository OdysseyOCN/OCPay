package com.stormfives.ocpay.feedback.controller;

import com.stormfives.ocpay.common.exception.InvalidArgumentException;
import com.stormfives.ocpay.common.response.ResponseValue;
import com.stormfives.ocpay.feedback.controller.req.FeedBackReq;
import com.stormfives.ocpay.feedback.service.FeedBackService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liuhuan on 2018/6/8.
 */
@RestController
@RequestMapping("/api/ocpay/")
public class FeedBackController {

    @Autowired
    private FeedBackService feedBackService;


    @PostMapping("v1/token/add-feedback")
    public ResponseValue addFeedBack(@RequestBody FeedBackReq feedBackReq) throws InvalidArgumentException {
        if (StringUtils.isBlank(feedBackReq.getEmail()) ||
                StringUtils.isBlank(feedBackReq.getDescription()) ||
                StringUtils.isBlank(feedBackReq.getTheme())) {
            throw new InvalidArgumentException("param is error!");
        }

        return feedBackService.addFeedBack(feedBackReq);
    }

    /**
     * 查询用户反馈
     *
     * @return
     */
    @PostMapping("v1/token/get-feedback")
    public ResponseValue getFeedBack(@RequestBody FeedBackReq feedBackReq) throws InvalidArgumentException {
        return feedBackService.getFeedBack(feedBackReq);
    }
}




