package com.stormfives.ocpay.member.service;

import com.alibaba.fastjson.JSON;
import com.stormfives.ocpay.common.web3j.api.Web3JClient;
import com.stormfives.ocpay.member.dao.OcpayAddressBalanceDAO;
import com.stormfives.ocpay.member.dao.OcpayAddressBalanceDualMapper;
import com.stormfives.ocpay.member.dao.OcpayAddressBalanceMapper;
import com.stormfives.ocpay.member.dao.entity.OcpayAddressBalance;
import com.stormfives.ocpay.member.dao.entity.OcpayAddressBalanceDual;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import static com.stormfives.ocpay.common.web3j.transaction.TransactionOnNode.balanceOfContractToken;

/**
 * Created by liuhuan on 2018/8/1.
 */
@Service
public class StatisticAddressBalanceService {

    private final String contractAddress = "0x4092678e4e78230f46a1534c0fbc8fa39780892b";

    private final Web3j client = Web3JClient.getClient();


    @Autowired
    private OcpayAddressBalanceDualMapper ocpayAddressBalanceDualMapper;

    @Async
    public void queryBalance(List<String> addresses) throws InterruptedException {
        BigDecimal allBalance = new BigDecimal("0");
        if (addresses != null && addresses.size() > 0) {
            for (String address : addresses) {
                BigDecimal ocnBalance = balanceOfContractToken(client, contractAddress, address);
                if (ocnBalance != null) {
                    allBalance = allBalance.add(ocnBalance);
                }
            }
        }
        OcpayAddressBalanceDual dual = new OcpayAddressBalanceDual();
        dual.setAddressesBalance(allBalance);
        dual.setCreatTime(new Date());
        ocpayAddressBalanceDualMapper.insertSelective(dual);
    }
}
