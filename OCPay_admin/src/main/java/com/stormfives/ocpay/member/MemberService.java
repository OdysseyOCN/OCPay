package com.stormfives.ocpay.member;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.stormfives.ocpay.common.Constants;
import com.stormfives.ocpay.common.exception.InvalidArgumentException;
import com.stormfives.ocpay.common.response.FailResponse;
import com.stormfives.ocpay.common.response.ResponseValue;
import com.stormfives.ocpay.common.response.SuccessResponse;
import com.stormfives.ocpay.common.util.DateUtils;
import com.stormfives.ocpay.common.util.Rand;
import com.stormfives.ocpay.common.util.StringUtil;
import com.stormfives.ocpay.common.util.sms.CL253SMSUtil;
import com.stormfives.ocpay.member.controller.req.ForgetSetPasswordReq;
import com.stormfives.ocpay.member.controller.req.MailWalletReq;
import com.stormfives.ocpay.member.controller.req.SaveUserReq;
import com.stormfives.ocpay.member.controller.resp.MemberVo;
import com.stormfives.ocpay.member.dao.EmailWalletAddressMapper;
import com.stormfives.ocpay.member.dao.MemberMapper;
import com.stormfives.ocpay.member.dao.OcpaySmsCodeDao;
import com.stormfives.ocpay.member.dao.entity.*;
import com.stormfives.ocpay.token.common.BCrypt;
import com.stormfives.ocpay.token.domain.Token;
import com.stormfives.ocpay.token.service.OauthService;
import com.stormfives.ocpay.token.vo.TokenVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.web3j.crypto.WalletUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Created by liuhuan on 2018/7/13.
 */
@Service
public class MemberService {

    Logger logger = LoggerFactory.getLogger(MemberService.class);

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private EmailWalletAddressMapper emailWalletAddressMapper;

    @Autowired
    private OauthService oauthService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private OcpaySmsCodeDao ocpaySmsCodeDao;

    private static PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();


    public ResponseValue saveUser(SaveUserReq saveUserReq) throws InvalidArgumentException {
        if (StringUtils.isBlank(saveUserReq.getPhone())) {
            return new FailResponse("phone is null");
        }
        if (saveUserReq.getCountryCode() == null) {
            return new FailResponse("Verification code error");
        }
        Member member = getMember(saveUserReq.getPhone(), saveUserReq.getCountryCode());
        if (member != null) {
            return new FailResponse("Your phone number is exist, please log in");
        }
        checkSmsCode(saveUserReq.getPhone(), saveUserReq.getSmscode(), saveUserReq.getCountryCode().toString());
        //1. 创建用户
        member = new Member();
        member.setPhone(saveUserReq.getPhone());
        String passwordDigest = BCrypt.hashpw(saveUserReq.getPassword(), BCrypt.gensalt());
        member.setPassword(passwordDigest);
        if (StringUtils.isNotBlank(saveUserReq.getWalletAddress())) {
            boolean validAddress = WalletUtils.isValidAddress(saveUserReq.getWalletAddress());
            if (!validAddress) {
                return new FailResponse("Your wallet address is error");
            }
            member.setWalletAddress(saveUserReq.getWalletAddress());
        }
        member.setCreateTime(new Date());
        member.setPhonepreid(saveUserReq.getCountryCode());
        member.setUpdateTime(new Date());
        int i = memberMapper.insertSelective(member);
        if (i > 0) {
            return new SuccessResponse("SUCCESS");
        }
        return new FailResponse("regist error");
    }

    public ResponseValue sendSms(SaveUserReq saveUserReq) throws InvalidArgumentException {
        if (StringUtils.isBlank(saveUserReq.getPhone())) {
            return new FailResponse("phone is null");
        }
        if (saveUserReq.getCountryCode() == null) {
            return new FailResponse("countryId is null");
        }
        String countryCode = saveUserReq.getCountryCode().toString().replace("+", "");
        String phone = handlePhone(saveUserReq.getPhone(), Integer.valueOf(countryCode));
        sendSmsToPhone(phone, countryCode);
        return new SuccessResponse("successful");
    }


    public void sendSmsToPhone(String phone, String phonepre) throws InvalidArgumentException {

        phonepre = "+" + phonepre;
        //限制：2分钟以内发送过短信，不能再次发送
        OcpaySmsCode latestCode = ocpaySmsCodeDao.getLatestCodeByPhone(phone, phonepre);
        if (latestCode != null) {
            Date lastCreate = latestCode.getEsccreatedate();
            //验证当前时间与创建时间差，2分钟以内 并且没有被验证的，不再发送
            if (latestCode.getEscvalid() == 0 && DateUtils.addMinute(lastCreate, 2).compareTo(new Date()) >= 0) {
                throw new InvalidArgumentException("Please send it again after 2 minutes");
            }
        }

        String code = Rand.getRandomInt(6);

        //通用保存短信验证码功能
        saveSmsCodeToDb(phone, phonepre, code);

        //发送给用户
        CL253SMSUtil.sendVerifyCode(phonepre + phone, code);
    }

    public void checkSmsCode(String phone, String code, String phonepre) throws InvalidArgumentException {
        phonepre = "+" + phonepre;
        OcpaySmsCode latestCode = ocpaySmsCodeDao.getLatestCodeByPhone(phone, phonepre);
        if (latestCode != null) {
            Date now = new Date();
            if (!code.equals(latestCode.getEsccode())) {
                throw new InvalidArgumentException("Verification code error");
            }
            if (now.compareTo(latestCode.getEscexpiredate()) > 0) {
                throw new InvalidArgumentException("Verification code has expired");
            }
            if (latestCode.getEscvalid() == 1) {
                throw new InvalidArgumentException("Verification code is in use");
            }
            //验证通过，设置验证码已使用
            ocpaySmsCodeDao.updateSmsCodeValid(latestCode.getEscid(), 1);
        } else {
            throw new InvalidArgumentException("Verification code does not exist");
        }
    }

    public void saveSmsCodeToDb(String phone, String phonepre, String code) {
        OcpaySmsCode smsCode = new OcpaySmsCode();
        smsCode.setEsccode(code);
        smsCode.setEscphone(phone);
        Date now = new Date();
        smsCode.setEsccreatedate(now);
        smsCode.setEscvalid(Constants.ZERO_INTEGER);
        //5分钟过期
        smsCode.setEscexpiredate(DateUtils.getDateByDistenceSeconds(now, 300));
        smsCode.setEscphonepre(phonepre);
        ocpaySmsCodeDao.insertExchangeSMSCode(smsCode);
    }

    private Member getMember(String phone, Integer countryPhnoenId) {
        MemberExample memberExample = new MemberExample();
        memberExample.or().andPhoneEqualTo(phone).andPhonepreidEqualTo(countryPhnoenId);
        return memberMapper.selectByExample(memberExample).stream().findFirst().orElse(null);
    }


    public String handlePhone(String phone, Integer countryCode) throws InvalidArgumentException {
        if (StringUtils.isBlank(phone)) {
            logger.error("phone is null");
            throw new InvalidArgumentException("Please input valid phone number");
        }
        //长度校验
        if (phone.length() < 5) {
            logger.error("phone.length:{}", phone.length());
            throw new InvalidArgumentException("Please input valid phone number");
        }
        //去0处理
        phone = phone.replaceAll("^(0+)", "");
        //去空格
        phone = phone.replaceAll(" ", "");
        //纯数字
        boolean result = isNum(phone);
        if (!result) {
            logger.error("phone exists non-numeric. phone:{}", phone);
            throw new InvalidArgumentException("Please input valid phone number");
        }


        //google国际化校验 phone和 country
        result = checkPhoneNumber(phone, countryCode);
        if (!result) {
            throw new InvalidArgumentException("Please input valid phone number");
        }

        return phone;
    }


    //纯数字校验
    public static boolean isNum(String str) {
        Pattern pattern = Pattern.compile("^[0-9]+");
        return pattern.matcher(str).matches();
    }

    //google国际化校验 phone和countryCode
    public boolean checkPhoneNumber(String phoneNumber, Integer countryCode) {
        long phone;
        try {
            phone = Long.parseLong(phoneNumber);
        } catch (Exception e) {
            logger.error("转型失败phoneNumber:{} to long", phoneNumber);
            return false;
        }
        Phonenumber.PhoneNumber pn = new Phonenumber.PhoneNumber();
        pn.setCountryCode(countryCode);
        pn.setNationalNumber(phone);

        return phoneNumberUtil.isValidNumber(pn);
    }

    public ResponseValue login(SaveUserReq userLoginReq) throws InvalidArgumentException {
        if (StringUtils.isBlank(userLoginReq.getPhone())) {
            return new FailResponse("phone is null");
        }
        if (userLoginReq.getCountryCode() == null) {
            return new FailResponse("countryId is null");
        }
        Member member = getMember(userLoginReq.getPhone(), userLoginReq.getCountryCode());
        if (member == null) {
            return new FailResponse("The member is not exist");
        }
        boolean flag = BCrypt.checkpw(userLoginReq.getPassword(), member.getPassword());
        if (flag) {
            TokenVo tokenVo = new TokenVo();
            Token token = oauthService.generateToken(member.getId(), "member");
            BeanUtils.copyProperties(token, tokenVo);
            return new SuccessResponse(tokenVo);
        }
        return new FailResponse("phone or password error!");
    }

    //获取一个随机token
    private String getToken() {
        OAuthIssuerImpl authIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
        String accessToken = null;
        try {
            accessToken = authIssuerImpl.accessToken();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return accessToken;
    }

    public ResponseValue forgetPasswordRequest(SaveUserReq forgetPwd) throws InvalidArgumentException {
        if (StringUtils.isBlank(forgetPwd.getPhone())) {
            return new FailResponse("phone is null");
        }
        if (forgetPwd.getCountryCode() == null) {
            return new FailResponse("countryId is null");
        }
        String countryCode = forgetPwd.getCountryCode().toString();
        countryCode = countryCode.replace("+", "");
        String phone = handlePhone(forgetPwd.getPhone(), Integer.valueOf(countryCode));
        sendSmsToPhone(phone, countryCode);
        //生成token保存到redis，再返回给前端
        String accessToken = getToken();
        redisTemplate.opsForValue().set(accessToken, phone, 5, TimeUnit.MINUTES);
        return new SuccessResponse(accessToken);
    }

    public ResponseValue forgetSetPasswordService(ForgetSetPasswordReq req) throws InvalidArgumentException {
        if (StringUtils.isBlank(req.getPhone())) {
            return new FailResponse("phone is null");
        }
        if (StringUtils.isBlank(req.getSmscode())) {
            return new FailResponse("Verification code error");
        }
        if (req.getCountryCode() == null) {
            return new FailResponse("countryId is null");
        }
        if (StringUtils.isBlank(req.getAccesstoken())) {
            return new FailResponse("token is null");
        }
        String phone = redisTemplate.opsForValue().get(req.getAccesstoken());
        if (StringUtils.isBlank(phone) || !req.getPhone().equals(phone)) {
            return new FailResponse("token is error");
        }
        redisTemplate.delete(req.getAccesstoken());
        checkSmsCode(req.getPhone(), req.getSmscode(), req.getCountryCode().toString());
        Member member = getMember(req.getPhone(), req.getCountryCode());
        if (member != null) {
            Member update = new Member();
            update.setId(member.getId());
            String passwordDigest = BCrypt.hashpw(req.getNewPassword(), BCrypt.gensalt());
            update.setPassword(passwordDigest);
            update.setUpdateTime(new Date());
            int i = memberMapper.updateByPrimaryKeySelective(update);
            if (i > 0) {
                return new SuccessResponse("Success");
            }
        }
        return new FailResponse("reset password error");
    }

    public ResponseValue saveMailWallet(MailWalletReq mailWalletReq) {
        if (!StringUtil.isEmail(mailWalletReq.getEmail())) {
            return new FailResponse("email format error!");
        }
        boolean validAddress = WalletUtils.isValidAddress(mailWalletReq.getWalletAddress());
        if (!validAddress) {
            return new FailResponse("Your wallet address is error");
        }
        EmailWalletAddress emailWalletAddress = new EmailWalletAddress();
        emailWalletAddress.setEamil(mailWalletReq.getEmail());
        emailWalletAddress.setCreateTime(new Date());
        emailWalletAddress.setWalletAddress(mailWalletReq.getWalletAddress());
        int i = emailWalletAddressMapper.insertSelective(emailWalletAddress);
        if (i > 0) {
            return new SuccessResponse("Success");
        }
        return new FailResponse("Faild");
    }

    public ResponseValue updateWalletAddress(SaveUserReq walletReq, Integer memberId) {
        if (memberId == null) {
            return new FailResponse("memberId is null");
        }
        Member member = memberMapper.selectByPrimaryKey(memberId);
        if (member == null) {
            return new FailResponse("member is null");
        }
        if (StringUtils.isBlank(walletReq.getWalletAddress())) {
            return new FailResponse("walletAddress is null");
        }
        boolean validAddress = WalletUtils.isValidAddress(walletReq.getWalletAddress());
        if (!validAddress) {
            return new FailResponse("Your wallet address is error");
        }
        Member update = new Member();
        update.setId(member.getId());
        update.setWalletAddress(walletReq.getWalletAddress());
        update.setUpdateTime(new Date());
        int i = memberMapper.updateByPrimaryKeySelective(update);
        if (i > 0) {
            return new SuccessResponse("SUCCESS");
        }
        return new FailResponse("Faild");
    }

    public ResponseValue getUser(Integer memberId) {
        if (memberId == null) {
            return new FailResponse("member is null");
        }
        Member member = memberMapper.selectByPrimaryKey(memberId);
        if (member == null) {
            return new FailResponse("member is null");
        }
        MemberVo memberVo = new MemberVo();
        memberVo.setPhone(member.getPhone());
        memberVo.setWalletAddress(member.getWalletAddress());
        return new SuccessResponse(memberVo);
    }

    public ResponseValue confirmPwd(SaveUserReq saveUserReq, Integer memberId) {
        if (memberId == null) {
            return new FailResponse("member is null");
        }
        Member member = memberMapper.selectByPrimaryKey(memberId);
        if (member == null) {
            return new FailResponse("member is null");
        }
        boolean flag = BCrypt.checkpw(saveUserReq.getPassword(), member.getPassword());
        if (flag){
            return new SuccessResponse("successful");
        }
        return new FailResponse("failed");
    }
}
