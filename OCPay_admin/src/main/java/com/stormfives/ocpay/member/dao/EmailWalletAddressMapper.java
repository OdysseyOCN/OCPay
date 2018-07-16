package com.stormfives.ocpay.member.dao;

import com.stormfives.ocpay.member.dao.entity.EmailWalletAddress;
import com.stormfives.ocpay.member.dao.entity.EmailWalletAddressExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface EmailWalletAddressMapper {
    int countByExample(EmailWalletAddressExample example);

    int deleteByExample(EmailWalletAddressExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(EmailWalletAddress record);

    int insertSelective(EmailWalletAddress record);

    List<EmailWalletAddress> selectByExample(EmailWalletAddressExample example);

    EmailWalletAddress selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") EmailWalletAddress record, @Param("example") EmailWalletAddressExample example);

    int updateByExample(@Param("record") EmailWalletAddress record, @Param("example") EmailWalletAddressExample example);

    int updateByPrimaryKeySelective(EmailWalletAddress record);

    int updateByPrimaryKey(EmailWalletAddress record);
}