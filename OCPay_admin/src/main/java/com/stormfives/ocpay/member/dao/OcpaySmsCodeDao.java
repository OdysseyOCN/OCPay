package com.stormfives.ocpay.member.dao;

import com.stormfives.ocpay.member.dao.entity.OcpaySmsCode;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OcpaySmsCodeDao {


    @Select("select * from ocpay_sms_code where escphone=#{escphone} and escphonepre=#{escphonepre} order by escid desc limit 1")
    OcpaySmsCode getLatestCodeByPhone(@Param("escphone") String escphone, @Param("escphonepre") String escphonepre);

    @Insert("insert into ocpay_sms_code (esccode, escphone, esccreatedate, escexpiredate, escvalid, escphonepre)" +
            " values (#{esccode},#{escphone},#{esccreatedate},#{escexpiredate},#{escvalid}, #{escphonepre})")
    @Options(useGeneratedKeys = true, keyProperty = "escid")
    void insertExchangeSMSCode(OcpaySmsCode smsCode);

    @Update("update ocpay_sms_code set escvalid=#{escvalid} where escid=#{escid}")
    void updateSmsCodeValid(@Param("escid") int escid, @Param("escvalid") int escvalid);
}