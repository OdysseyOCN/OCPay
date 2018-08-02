package com.stormfives.ocpay.member.dao;

import com.stormfives.ocpay.member.dao.entity.ExchangeAddress;
import com.stormfives.ocpay.member.dao.entity.ExchangeAddressExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ExchangeAddressMapper {
    int countByExample(ExchangeAddressExample example);

    int deleteByExample(ExchangeAddressExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ExchangeAddress record);

    int insertSelective(ExchangeAddress record);

    List<ExchangeAddress> selectByExample(ExchangeAddressExample example);

    List<String> selectAddress();

    ExchangeAddress selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ExchangeAddress record, @Param("example") ExchangeAddressExample example);

    int updateByExample(@Param("record") ExchangeAddress record, @Param("example") ExchangeAddressExample example);

    int updateByPrimaryKeySelective(ExchangeAddress record);

    int updateByPrimaryKey(ExchangeAddress record);
}