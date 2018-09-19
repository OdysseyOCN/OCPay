package com.coinwallet.airdrop.service;

import com.coinwallet.airdrop.controller.req.AirdropAccountReq;
import com.coinwallet.airdrop.dao.AirdropAccountInfoMapper;
import com.coinwallet.airdrop.entity.AirdropAccountInfo;
import com.coinwallet.airdrop.entity.AirdropAccountInfoExample;
import com.coinwallet.common.domain.Page;
import com.coinwallet.common.response.FailResponse;
import com.coinwallet.common.response.ResponseValue;
import com.coinwallet.common.response.SuccessResponse;
import com.coinwallet.common.util.AES;
import com.coinwallet.common.util.InitConfig;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;


/**
 * Created by liuhuan on 2018/8/29.
 */
@Service
public class AirdropAccountService {


    @Autowired
    private InitConfig initConfig;

    @Autowired
    private AirdropAccountInfoMapper airdropAccountInfoMapper;

    public ResponseValue saveAirdropAccount(AirdropAccountReq airdropAccountReq) throws Exception {
        if (StringUtils.isBlank(airdropAccountReq.getAddress())){
            return new FailResponse("空投地址不能为空");
        }
        if (StringUtils.isBlank(airdropAccountReq.getName())){
            return new FailResponse("空投货币名称不能为空");
        }
        if (StringUtils.isBlank(airdropAccountReq.getContract())){
            return new FailResponse("空投货币合约地址不能为空");
        }
        AirdropAccountInfo airdropAccountInfo = new AirdropAccountInfo();
        BeanUtils.copyProperties(airdropAccountReq,airdropAccountInfo);
        Date date = new Date();
        airdropAccountInfo.setCreateTime(date);
        airdropAccountInfo.setUpdateTime(date);
        airdropAccountInfo.setPrivateKey(AES.encrypt(airdropAccountReq.getPrivateKey(),initConfig.deskey));
        airdropAccountInfoMapper.insertSelective(airdropAccountInfo);
        return new SuccessResponse("Successful");
    }


    public ResponseValue updateAirdropAccount(AirdropAccountReq airdropAccountReq) throws Exception {
        if (airdropAccountReq.getId()==null){
            return new FailResponse("空投Id不能为空");
        }
        AirdropAccountInfo airdropAccountInfo = new AirdropAccountInfo();
        BeanUtils.copyProperties(airdropAccountReq,airdropAccountInfo);
        if (StringUtils.isNotBlank(airdropAccountReq.getPrivateKey())){
            airdropAccountInfo.setPrivateKey(AES.encrypt(airdropAccountReq.getPrivateKey(),initConfig.deskey));
        }
        airdropAccountInfo.setUpdateTime(new Date());
        airdropAccountInfoMapper.updateByPrimaryKeySelective(airdropAccountInfo);
        return new SuccessResponse("Successful");
    }

    public ResponseValue getAirdropAccount(AirdropAccountReq airdropAccountReq) {
        PageHelper.startPage(airdropAccountReq.getPageNum(),airdropAccountReq.getPageSize());
        AirdropAccountInfoExample airdropAccountInfoExample = new AirdropAccountInfoExample();
        AirdropAccountInfoExample.Criteria criteria = airdropAccountInfoExample.createCriteria();
        if (StringUtils.isNotBlank(airdropAccountReq.getName())){
            criteria.andNameEqualTo(airdropAccountReq.getName());
        }
        if (StringUtils.isNotBlank(airdropAccountReq.getAddress())){
            criteria.andAddressLike(airdropAccountReq.getAddress()+"%");
        }
        List<AirdropAccountInfo> airdropAccountInfos = airdropAccountInfoMapper.selectByExample(airdropAccountInfoExample);
        return new SuccessResponse(Page.toPage(airdropAccountInfos));
    }
}
