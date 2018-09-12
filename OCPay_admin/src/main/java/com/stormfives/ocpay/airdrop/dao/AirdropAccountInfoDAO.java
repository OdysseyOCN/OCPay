package com.coinwallet.airdrop.dao;

import com.coinwallet.airdrop.entity.AirdropAccountInfo;
import com.coinwallet.airdrop.entity.AirdropAccountInfoExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface AirdropAccountInfoDAO {


    @Update("update airdrop_account_info set nonce = #{nonce},update_time = now() where id = #{id}")
    void updateAirdropAccount(@Param("nonce") BigInteger nonce,@Param("id")Integer id);
}