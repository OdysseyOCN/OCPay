package com.stormfives.ocpay.member.dao;

import com.stormfives.ocpay.member.dao.entity.OcpayAddressBalance;
import com.stormfives.ocpay.member.dao.entity.OcpayAddressBalanceExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OcpayAddressBalanceMapper {
    int countByExample(OcpayAddressBalanceExample example);

    int deleteByExample(OcpayAddressBalanceExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(OcpayAddressBalance record);

    int insertSelective(OcpayAddressBalance record);

    List<OcpayAddressBalance> selectByExample(OcpayAddressBalanceExample example);

    OcpayAddressBalance selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") OcpayAddressBalance record, @Param("example") OcpayAddressBalanceExample example);

    int updateByExample(@Param("record") OcpayAddressBalance record, @Param("example") OcpayAddressBalanceExample example);

    int updateByPrimaryKeySelective(OcpayAddressBalance record);

    int updateByPrimaryKey(OcpayAddressBalance record);
}