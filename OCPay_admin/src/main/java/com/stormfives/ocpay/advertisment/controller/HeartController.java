package com.stormfives.ocpay.advertisment.controller;

import com.stormfives.ocpay.advertisment.controller.req.HomePageReq;
import com.stormfives.ocpay.advertisment.service.HomePageService;
import com.stormfives.ocpay.common.exception.InvalidArgumentException;
import com.stormfives.ocpay.common.response.ResponseValue;
import com.stormfives.ocpay.common.response.SuccessResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuhuan on 2018/5/22.
 */
@RestController
@RequestMapping("/api/ocpay/")
public class HeartController {


    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HomePageService homePageService;


    /**
     * 新增类型
     *
     * @return
     */
    @GetMapping("/token/heart")
    public ResponseValue heart() throws InvalidArgumentException {

        Map<String,Object> msg = new HashMap<>();
        msg.put("msg","ok");
        return new SuccessResponse(msg);
    }


}
