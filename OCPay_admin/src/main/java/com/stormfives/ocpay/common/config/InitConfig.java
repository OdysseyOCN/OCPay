package com.stormfives.ocpay.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by y on 2018/3/22.
 */
@Configuration
public class InitConfig {

    @Value("${aes.deskey}")
    public String deskey;


    @Value("${emailaddress}")
    public String emailaddress;
    @Value("${emailnickname}")
    public String emailnickname;
    @Value("${emailpassword}")
    public String emailpassword;
    @Value("${emailsmtphost}")
    public String emailsmtphost;
    @Value("${emailsmtpport}")
    public String emailsmtpport;
}
