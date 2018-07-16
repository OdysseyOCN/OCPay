package com.stormfives.ocpay.common.util.sms;


import com.stormfives.ocpay.common.util.sms.dto.CL253SendDto;
import com.stormfives.ocpay.common.web3j.utils.HttpRequestUtil;

//创蓝 253 短息发送
public class CL253SMSUtil {
    private static final String ApiHost = "";
    private static final String UserName = "";
    private static final String Password = "";
    private static final String VerifyCodeStr = "";


    //发送验证码
    public static void sendVerifyCode(String phone, String code){
        sendMsg(phone, String.format(VerifyCodeStr, code));
    }

    //直接发送短信内容
    public static void sendMsg(String phone, String msg){
        CL253SendDto sendDto = new CL253SendDto(UserName, Password);
        sendDto.setMobile(phone.replace("+",""));  //该平台国际号码不用"+"
        sendDto.setMsg(msg);
        String result = HttpRequestUtil.doPostRequestBody(ApiHost, sendDto);
        System.out.println(result);
    }
//
    public static void main(String[] args) {

    }
}
