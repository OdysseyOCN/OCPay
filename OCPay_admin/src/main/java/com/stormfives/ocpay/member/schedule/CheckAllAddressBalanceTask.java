package com.stormfives.ocpay.member.schedule;

import com.stormfives.ocpay.member.dao.*;
import com.stormfives.ocpay.member.dao.entity.*;
import com.stormfives.ocpay.member.service.StatisticAddressBalanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;


/**
 * Created by liuhuan on 2018/7/25.
 */

@Component
public class CheckAllAddressBalanceTask {

    Logger logger = LoggerFactory.getLogger(CheckAllAddressBalanceTask.class);

    @Autowired
    private OcpayAddressBalanceMapper ocpayAddressBalanceMapper;

    @Autowired
    private EmailWalletAddressDAO emailWalletAddressDAO;
    @Autowired
    private OcpayAddressBalanceDualDAO ocpayAddressBalanceDualDAO;

    @Autowired
    private MemberDAO memberDAO;


    @Autowired
    private StatisticAddressBalanceService statisticAddressBalanceService;


    @Scheduled(cron = "0 0 8 * * ?")
    public void checkDayUsdtOrderToGather() {
        List<String> allAddresses = new ArrayList<>();
        ocpayAddressBalanceDualDAO.deleteDual();
        int emailAddress = emailWalletAddressDAO.selectCount();
        for (int i = 0; i < emailAddress; i = i + 100) {
            List<String> address = emailWalletAddressDAO.selectlimitAddress(i);
            try {
                statisticAddressBalanceService.queryBalance(address);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            allAddresses.addAll(address);
        }

        int memberAddressCount = memberDAO.selectCount();
        for (int i = 0; i < memberAddressCount; i = i + 100) {
            List<String> address = memberDAO.selectlimitAddress(i);
            List<String> memberAddress = new ArrayList<>();
            for (String s : address) {
                if (!allAddresses.contains(s)) {
                    memberAddress.add(s);
                }
            }
            try {
                statisticAddressBalanceService.queryBalance(memberAddress);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);

            }
            allAddresses.addAll(memberAddress);

        }

        int dual = ocpayAddressBalanceDualDAO.getCount();
        int totalCount = getDualCount(emailAddress) + getDualCount(memberAddressCount);

        while (dual != totalCount) {
            try {
                Thread.sleep(1000);
                dual = ocpayAddressBalanceDualDAO.getCount();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        BigDecimal totalBalance = ocpayAddressBalanceDualDAO.getTotalAmount();


        OcpayAddressBalance ocpayAddressBalance = new OcpayAddressBalance();
        ocpayAddressBalance.setAddressNum(allAddresses.size());
        ocpayAddressBalance.setTotalBalance(totalBalance);
        ocpayAddressBalance.setCreateTime(new Date());
        ocpayAddressBalanceMapper.insertSelective(ocpayAddressBalance);
    }

    private int getDualCount(int emailAddress) {
        return emailAddress % 100 != 0 ? emailAddress / 100 + 1 : emailAddress / 100;
    }



}



