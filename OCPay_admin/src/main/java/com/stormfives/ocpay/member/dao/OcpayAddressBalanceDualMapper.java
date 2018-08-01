package com.stormfives.ocpay.member.dao;

import com.stormfives.ocpay.member.dao.entity.OcpayAddressBalanceDual;
import com.stormfives.ocpay.member.dao.entity.OcpayAddressBalanceDualExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OcpayAddressBalanceDualMapper {
    int countByExample(OcpayAddressBalanceDualExample example);

    int deleteByExample(OcpayAddressBalanceDualExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(OcpayAddressBalanceDual record);

    int insertSelective(OcpayAddressBalanceDual record);

    List<OcpayAddressBalanceDual> selectByExample(OcpayAddressBalanceDualExample example);

    OcpayAddressBalanceDual selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") OcpayAddressBalanceDual record, @Param("example") OcpayAddressBalanceDualExample example);

    int updateByExample(@Param("record") OcpayAddressBalanceDual record, @Param("example") OcpayAddressBalanceDualExample example);

    int updateByPrimaryKeySelective(OcpayAddressBalanceDual record);

    int updateByPrimaryKey(OcpayAddressBalanceDual record);
}