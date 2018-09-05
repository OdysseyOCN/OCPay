package com.stormfives.ocpay.account.service;

import com.stormfives.ocpay.common.util.DateUtils;
import com.stormfives.ocpay.member.dao.OcpayAddressBalanceMapper;
import com.stormfives.ocpay.member.dao.entity.OcpayAddressBalance;
import com.stormfives.ocpay.member.schedule.CheckAllAddressBalanceTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: lyc
 * Date: 2018/3/17
 * Time: 下午2:08
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@EnableAsync
public class AdminServiceTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private CheckAllAddressBalanceTask checkAllAddressBalanceTask;

    @Autowired
    private OcpayAddressBalanceMapper ocpayAddressBalance;


    @Test
    public void login() throws Exception {

        adminService.login("18721895830","123456");
    }

    @Test
    public void getAdminByName() throws Exception {
    }

    @Test
    public void addAdmin() throws Exception {

        String name ="18075854632";
        String phone = "18075854632";
        String realName = "laizi";
        String password = "123456";

        adminService.addAdmin(name,password,phone,realName,1);
    }

    @Test
    public void resetPassword() throws Exception {
        Date date = new Date();
        OcpayAddressBalance ocpayAddressBalance = new OcpayAddressBalance();

        for (int i = 3; i < 510; i++) {
            ocpayAddressBalance.setCreateTime(DateUtils.addDay(date,i));
            ocpayAddressBalance.setTotalBalance(new BigDecimal("3242342424"));
            ocpayAddressBalance.setAddressNum(30313);
            this.ocpayAddressBalance.insertSelective(ocpayAddressBalance);
        }
    }

    @Test
    public void getAdminById() throws Exception {
        checkAllAddressBalanceTask.checkDayUsdtOrderToGather();
    }

}