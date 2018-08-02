package com.stormfives.ocpay.member.dao;

import com.stormfives.ocpay.member.dao.entity.OcpayAddressBalance;
import com.stormfives.ocpay.member.dao.entity.OcpayAddressBalanceExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

@Mapper
public interface OcpayAddressBalanceDAO {



    @Select("select * from ocpay_address_balance where create_time > DATE_FORMAT(NOW(),'%Y-%m-%d')")
    OcpayAddressBalance selectToday();


    @Update("update ocpay_address_balance set address_num = address_num + #{addressNum}, total_balance = total_balance + #{totalBalance} where id=#{id}")
    int updateOcpayAddressBlance(OcpayAddressBalance ocpayAddressBalance);


}