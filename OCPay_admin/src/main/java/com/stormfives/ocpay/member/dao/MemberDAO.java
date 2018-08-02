package com.stormfives.ocpay.member.dao;

import com.stormfives.ocpay.member.dao.entity.Member;
import com.stormfives.ocpay.member.dao.entity.MemberExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MemberDAO {



    @Select("select count(DISTINCT wallet_address) from member m where not EXISTS (select exchange_address from exchange_address ea where m.wallet_address = ea.exchange_address) and m.wallet_address <>''")
    int selectCount();

    @Select("select DISTINCT wallet_address from member m  where not EXISTS (select 1 from exchange_address ea where m.wallet_address = ea.exchange_address) and m.wallet_address <>'' limit #{page}, 100")
    List<String> selectlimitAddress(@Param("page") int page);

}