package com.stormfives.ocpay.member.dao;

import com.stormfives.ocpay.member.dao.entity.OcpaySmsCode;
import com.stormfives.ocpay.member.dao.entity.OcpaySmsCodeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OcpaySmsCodeMapper {
    int countByExample(OcpaySmsCodeExample example);

    int deleteByExample(OcpaySmsCodeExample example);

    int deleteByPrimaryKey(Integer escid);

    int insert(OcpaySmsCode record);

    int insertSelective(OcpaySmsCode record);

    List<OcpaySmsCode> selectByExample(OcpaySmsCodeExample example);

    OcpaySmsCode selectByPrimaryKey(Integer escid);

    int updateByExampleSelective(@Param("record") OcpaySmsCode record, @Param("example") OcpaySmsCodeExample example);

    int updateByExample(@Param("record") OcpaySmsCode record, @Param("example") OcpaySmsCodeExample example);

    int updateByPrimaryKeySelective(OcpaySmsCode record);

    int updateByPrimaryKey(OcpaySmsCode record);
}