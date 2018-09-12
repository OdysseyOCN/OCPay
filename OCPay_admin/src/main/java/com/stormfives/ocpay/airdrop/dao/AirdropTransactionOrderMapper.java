package com.coinwallet.airdrop.dao;

import com.coinwallet.airdrop.entity.AirdropTransactionOrder;
import com.coinwallet.airdrop.entity.AirdropTransactionOrderExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AirdropTransactionOrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AirdropTransactionOrder record);

    int insertSelective(AirdropTransactionOrder record);

    List<AirdropTransactionOrder> selectByExample(AirdropTransactionOrderExample example);

    AirdropTransactionOrder selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AirdropTransactionOrder record, @Param("example") AirdropTransactionOrderExample example);

    int updateByExample(@Param("record") AirdropTransactionOrder record, @Param("example") AirdropTransactionOrderExample example);

    int updateByPrimaryKeySelective(AirdropTransactionOrder record);

    int updateByPrimaryKey(AirdropTransactionOrder record);
}