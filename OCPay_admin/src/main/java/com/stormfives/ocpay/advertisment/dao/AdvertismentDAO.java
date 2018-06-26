package com.stormfives.ocpay.advertisment.dao;

import com.stormfives.ocpay.advertisment.dao.entity.Advertisment;
import com.stormfives.ocpay.advertisment.dao.entity.AdvertismentExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdvertismentDAO {


    @Select("select * from advertisment where home_page_id in (SELECT id from home_page_content where type = #{type}) ORDER BY show_sort")
    List<Advertisment> selectByHomePageType(@Param("type") Integer type);
}