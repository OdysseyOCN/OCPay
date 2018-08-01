package com.stormfives.ocpay.member.dao;

import com.stormfives.ocpay.member.dao.entity.EmailWalletAddress;
import com.stormfives.ocpay.member.dao.entity.EmailWalletAddressExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmailWalletAddressDAO {


    @Select("select count(DISTINCT wallet_address) from email_wallet_address ewa where not EXISTS (select 1 from exchange_address ea where ewa.wallet_address = ea.exchange_address)")
    int selectCount();

    @Select("select DISTINCT wallet_address from email_wallet_address ewa  where not EXISTS (select 1 from exchange_address ea where ewa.wallet_address = ea.exchange_address) limit #{page}, 100")
    List<String> selectlimitAddress(@Param("page") int page);

}