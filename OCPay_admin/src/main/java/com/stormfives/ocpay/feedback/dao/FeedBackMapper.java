package com.stormfives.ocpay.feedback.dao;

import com.stormfives.ocpay.feedback.dao.entity.FeedBack;
import com.stormfives.ocpay.feedback.dao.entity.FeedBackExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface FeedBackMapper {
    int countByExample(FeedBackExample example);

    int deleteByExample(FeedBackExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(FeedBack record);

    int insertSelective(FeedBack record);

    List<FeedBack> selectByExample(FeedBackExample example);

    FeedBack selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") FeedBack record, @Param("example") FeedBackExample example);

    int updateByExample(@Param("record") FeedBack record, @Param("example") FeedBackExample example);

    int updateByPrimaryKeySelective(FeedBack record);

    int updateByPrimaryKey(FeedBack record);
}