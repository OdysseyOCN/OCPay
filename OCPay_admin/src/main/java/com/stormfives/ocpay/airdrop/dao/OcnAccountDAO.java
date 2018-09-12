package com.coinwallet.airdrop.dao;

import com.coinwallet.airdrop.controller.req.AirdropVo;
import com.coinwallet.airdrop.entity.OcnAccount;
import com.coinwallet.airdrop.entity.OcnAccountExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OcnAccountDAO {

    @Select("select LOWER(address) address,AVG(`value`)/1000000000000000000 `value` from ocn_account where `day` > #{before} and `day` < #{after} GROUP BY address HAVING  value > 5000")
    List<AirdropVo> selectAddressAndValue(@Param("before") String before,@Param("after") String after);
}