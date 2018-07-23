package com.stormfives.ocpay.member.rabbit;


import com.alibaba.fastjson.JSON;
import com.stormfives.ocpay.member.controller.req.MailWalletReq;
import com.stormfives.ocpay.member.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class SendAddressMailReceiver {
    Logger logger = LoggerFactory.getLogger(SendAddressMailReceiver.class);

    @Autowired
    private MemberService memberService;



    /**
     * @param msg
     */
    @RabbitListener(queues = RabbitConfig.SEND_ADDRESS_MAIL,containerFactory = "myConnectionFactory")
    public void sendAddressMail(String msg) {
        MailWalletReq walletReq = JSON.parseObject(msg, MailWalletReq.class);
        memberService.sendMail(walletReq);
    }




}
