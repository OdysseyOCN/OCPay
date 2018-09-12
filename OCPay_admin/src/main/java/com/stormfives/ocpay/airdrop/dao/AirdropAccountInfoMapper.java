package com.coinwallet.airdrop.dao;

import com.coinwallet.airdrop.entity.AirdropAccountInfo;
import com.coinwallet.airdrop.entity.AirdropAccountInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AirdropAccountInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AirdropAccountInfo record);

    int insertSelective(AirdropAccountInfo record);

    List<AirdropAccountInfo> selectByExample(AirdropAccountInfoExample example);

    AirdropAccountInfo selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AirdropAccountInfo record, @Param("example") AirdropAccountInfoExample example);

    int updateByExample(@Param("record") AirdropAccountInfo record, @Param("example") AirdropAccountInfoExample example);

    int updateByPrimaryKeySelective(AirdropAccountInfo record);

    int updateByPrimaryKey(AirdropAccountInfo record);
}