package com.coinwallet.airdrop.dao;

import com.coinwallet.airdrop.entity.OcnAccount;
import com.coinwallet.airdrop.entity.OcnAccountExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OcnAccountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OcnAccount record);

    int insertSelective(OcnAccount record);

    List<OcnAccount> selectByExample(OcnAccountExample example);

    OcnAccount selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") OcnAccount record, @Param("example") OcnAccountExample example);

    int updateByExample(@Param("record") OcnAccount record, @Param("example") OcnAccountExample example);

    int updateByPrimaryKeySelective(OcnAccount record);

    int updateByPrimaryKey(OcnAccount record);
}