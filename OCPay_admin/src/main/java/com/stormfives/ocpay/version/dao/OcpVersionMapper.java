package com.stormfives.ocpay.version.dao;

import com.stormfives.ocpay.version.dao.entity.OcpVersion;
import com.stormfives.ocpay.version.dao.entity.OcpVersionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OcpVersionMapper {
    int countByExample(OcpVersionExample example);

    int deleteByExample(OcpVersionExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(OcpVersion record);

    int insertSelective(OcpVersion record);

    List<OcpVersion> selectByExample(OcpVersionExample example);

    OcpVersion selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") OcpVersion record, @Param("example") OcpVersionExample example);

    int updateByExample(@Param("record") OcpVersion record, @Param("example") OcpVersionExample example);

    int updateByPrimaryKeySelective(OcpVersion record);

    int updateByPrimaryKey(OcpVersion record);
}