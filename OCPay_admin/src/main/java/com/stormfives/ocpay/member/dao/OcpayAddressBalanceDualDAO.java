package com.stormfives.ocpay.member.dao;

import com.stormfives.ocpay.member.dao.entity.OcpayAddressBalanceDual;
import com.stormfives.ocpay.member.dao.entity.OcpayAddressBalanceDualExample;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface OcpayAddressBalanceDualDAO {


    @Select("select sum(addresses_balance) from ocpay_address_balance_dual")
    BigDecimal getTotalAmount();

    @Select("select count(1) from ocpay_address_balance_dual")
    int getCount();

    @Delete("delete from ocpay_address_balance_dual")
    void deleteDual();
}