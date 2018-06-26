package com.stormfives.ocpay.feedback.service;

import com.github.pagehelper.PageHelper;
import com.stormfives.ocpay.common.response.FailResponse;
import com.stormfives.ocpay.common.response.ResponseValue;
import com.stormfives.ocpay.common.response.SuccessResponse;
import com.stormfives.ocpay.feedback.controller.req.FeedBackReq;
import com.stormfives.ocpay.feedback.dao.FeedBackMapper;
import com.stormfives.ocpay.feedback.dao.entity.FeedBack;
import com.stormfives.ocpay.feedback.dao.entity.FeedBackExample;
import com.stormfives.ocpay.token.domain.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by liuhuan on 2018/6/8.
 */
@Service
public class FeedBackService {


    @Autowired
    private FeedBackMapper feedBackMapper;

    public ResponseValue addFeedBack(FeedBackReq feedBackReq) {
        FeedBack feedBack = new FeedBack();
        BeanUtils.copyProperties(feedBackReq, feedBack);
        feedBack.setCreateTime(new Date());
        int i = feedBackMapper.insertSelective(feedBack);
        if (i > 0) {
            return new SuccessResponse("Successful!");
        }
        return new FailResponse("insert error!");
    }

    public ResponseValue getFeedBack(FeedBackReq feedBackReq) {
        PageHelper.startPage(feedBackReq.getPageNum(), feedBackReq.getPageSize());
        FeedBackExample feedBackExample = new FeedBackExample();
        feedBackExample.setOrderByClause(" id desc");
        List<FeedBack> feedBacks = feedBackMapper.selectByExample(feedBackExample);
        return new SuccessResponse(Page.toPage(feedBacks));
    }
}
