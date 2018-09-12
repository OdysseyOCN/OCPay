package com.coinwallet.airdrop.dao;

import com.coinwallet.airdrop.entity.AirdropInfo;
import com.coinwallet.airdrop.entity.AirdropInfoExample;

import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AirdropInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AirdropInfo record);

    int insertSelective(AirdropInfo record);

    List<AirdropInfo> selectByExample(AirdropInfoExample example);

    AirdropInfo selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AirdropInfo record, @Param("example") AirdropInfoExample example);

    int updateByExample(@Param("record") AirdropInfo record, @Param("example") AirdropInfoExample example);

    int updateByPrimaryKeySelective(AirdropInfo record);

    int updateByPrimaryKey(AirdropInfo record);

    int updateStatusById(@Param("id") Integer id);

    void addBatchAirdropInfo(@Param("airdropInfos") List<AirdropInfo> airdropInfos);

    BigDecimal selectAirdropNum(@Param("coinName")String coinName);
}